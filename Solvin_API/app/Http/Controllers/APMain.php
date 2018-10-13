<?php

namespace App\Http\Controllers;

use App\Classes\Firebase;
use App\Models\Admin;
use App\Models\BalanceBonus;
use App\Models\Comment;
use App\Models\Balance;
use App\Models\Feedback;
use App\Models\Mentor;
use App\Models\Notification;
use App\Models\Package;
use App\Models\Question;
use App\Models\RedeemBalance;
use App\Models\Solution;
use App\Models\Student;
use App\Models\Transaction;
use Carbon\Carbon;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Hash;

class APMain extends Controller
{
    use \App\Classes\BaseConfig;

    public function getCumulativeRecord()
    {
        if ($this->isGuest()) {
            $this->json_response['message'] = 'Unauthorized';
            $this->json_response['success'] = false;
            return $this->json_result();
        }
        $cumulativeRecord = new \stdClass();

        $nominal = 0;
        $unique_code = 0;

        $totalStudent = Student::count();
        if ($totalStudent == null) {
            $totalStudent = 0;
        }
        $totalMentor = Mentor::count();
        if ($totalMentor == null) {
            $totalMentor = 0;
        }

        $transactionList = Transaction::with(
            array(
                'Package_' => function ($query) {
                    $query->select('id', 'nominal');
                }
            )
        )
            ->select('id', 'package_id', 'unique_code')
            ->where('status', '=', 1)
            ->get();

        foreach ($transactionList as $transaction) {
            $nominal += $transaction->package_->nominal;
            $unique_code += $transaction->unique_code;
        }
        $totalIncome = $nominal + $unique_code;
        if ($totalIncome == null) {
            $totalIncome = 0;
        }

        $totalBalanceRedeemed = RedeemBalance::where('status', '=', 1)
            ->sum('balance');
        if ($totalBalanceRedeemed == null) {
            $totalBalanceRedeemed = 0;
        }

        $cumulativeRecord->totalStudent = $this->scrypt->encrypt($totalStudent);
        $cumulativeRecord->totalMentor = $this->scrypt->encrypt($totalMentor);
        $cumulativeRecord->totalIncome = $this->scrypt->encrypt($totalIncome);
        $cumulativeRecord->totalBalanceRedeemed = $this->scrypt->encrypt($totalBalanceRedeemed);

        return array('message' => 'success', 'status' => 1, 'cumulativeRecord' => $cumulativeRecord);
    }

    public function loadAuthList(Request $request)
    {
        if ($this->isGuest()) {
            $this->json_response['message'] = 'Unauthorized';
            $this->json_response['success'] = false;
            return $this->json_result();
        }
        $query = $request['query'];

        if (strlen($query) == 0) {
            return array('message' => 'failed', 'status' => 0, 'authList' => null);
        } else {
            $pattern = strlen($query) == 1 ? $query . '%' : '%' . $query . '%';
            $students = Student::select('*')
                ->where('name', 'like', $pattern)
                ->orderBy('created_at', 'desc')
                ->get();
            foreach ($students as $student) {
                $auth_total_question = Question::where('student_id', '=', $student->id)
                    ->count();

                if ($student->birth == '0000-00-00') {
                    $student->age = -1;
                } else {
                    $student->age = Carbon::now()->diffInYears(Carbon::parse($student->birth));
                }
                $student->auth_type = 'student';
                $student->auth_total_question = $auth_total_question;
            }

            $mentors = Mentor::select('*')
                ->where('name', 'like', $pattern)
                ->orderBy('created_at', 'desc')
                ->get();
            foreach ($mentors as $mentor) {
                $auth_total_best_solution = Solution::where([
                    ['mentor_id', '=', $mentor->id],
                    ['best', '=', 1]
                ])
                    ->count();

                $auth_total_solution = Solution::where('mentor_id', '=', $mentor->id)
                    ->count();

                if ($mentor->birth == '0000-00-00') {
                    $mentor->age = -1;
                } else {
                    $mentor->age = Carbon::now()->diffInYears(Carbon::parse($mentor->birth));
                }
                $mentor->auth_type = 'mentor';
                $mentor->auth_total_best_solution = $auth_total_best_solution;
                $mentor->auth_total_solution = $auth_total_solution;
            }

            $arr_students = array_values((array)$students)[0];
            $arr_mentors = array_values((array)$mentors)[0];
            $auths = array_merge($arr_students, $arr_mentors);

            return array('message' => 'success', 'status' => 1, 'authList' => $auths);
        }
    }

    public function loadStudentList($last_id = 0)
    {
        if ($this->isGuest()) {
            $this->json_response['message'] = 'Unauthorized';
            $this->json_response['success'] = false;
            return $this->json_result();
        }
        if ($last_id == 0) {
            $last_id = Student::max('id') + 1;
        }
        $students = Student::select('*')
            ->where('id', '<', $last_id)
            ->orderBy('created_at', 'desc')
            ->limit($this->item_perpage)
            ->get();

        foreach ($students as $student) {
            if ($student->birth == '0000-00-00') {
                $student->age = -1;
            } else {
                $student->age = Carbon::now()->diffInYears(Carbon::parse($student->birth));
            }

            $credit_timelife = Carbon::parse($student->credit_timelife);
            if (Carbon::now() > $credit_timelife) {
                $student->creditExpired = true;
            } else {
                $student->creditExpired = false;
            }

            $totalQuestion = Question::where('student_id', '=', $student->id)
                ->count();

            $totalComment = Comment::where([
                ['auth_id', '=', $student->id],
                ['auth_type', '=', 'student']
            ])
                ->count();

            $totalTransaction = Transaction::where('student_id', '=', $student->id)
                ->count();

            $student->totalQuestion = $totalQuestion;
            $student->totalComment = $totalComment;
            $student->totalTransaction = $totalTransaction;
        }

        if ($students == null) {
            return array('message' => 'failed', 'status' => 0, 'studentList' => null);
        } else {
            return array('message' => 'success', 'status' => 1, 'studentList' => $students);
        }
    }

    public function loadMentorList($last_id = 0)
    {
        if ($this->isGuest()) {
            $this->json_response['message'] = 'Unauthorized';
            $this->json_response['success'] = false;
            return $this->json_result();
        }
        if ($last_id == 0) {
            $last_id = Mentor::max('id') + 1;
        }

        $mentors = Mentor::select('*')
            ->where('id', '<', $last_id)
            ->orderBy('created_at', 'desc')
            ->limit($this->item_perpage)
            ->get();

        foreach ($mentors as $mentor) {
            if ($mentor->birth == '0000-00-00') {
                $mentor->age = -1;
            } else {
                $mentor->age = Carbon::now()->diffInYears(Carbon::parse($mentor->birth));
            }

            $totalBalance = Solution::where([
                ['mentor_id', '=', $mentor->id],
                ['best', '=', '1']
            ])->join($this->table_balance, $this->table_solution . '.id', '=', $this->table_balance . '.solution_id')
                ->get(['deal_payment'])
                ->sum('deal_payment');

            $totalBalanceBonus = BalanceBonus::where('mentor_id', '=', $mentor->id)
                ->get(['balance'])
                ->sum('balance');
            if ($totalBalanceBonus == null) {
                $totalBalanceBonus = 0;
            }

            $totalBestSolution = Solution::where([
                ['mentor_id', '=', $mentor->id],
                ['best', '=', 1]
            ])
                ->count();

            $totalSolution = Solution::where('mentor_id', '=', $mentor->id)
                ->count();

            $totalComment = Comment::where([
                ['auth_id', '=', $mentor->id],
                ['auth_type', '=', 'mentor']
            ])
                ->count();

            $totalRedeemBalance = RedeemBalance::where('mentor_id', '=', $mentor->id)
                ->count();

            $mentor->totalBalance = $totalBalance;
            $mentor->totalBalanceBonus = $totalBalanceBonus;
            $mentor->totalBestSolution = $totalBestSolution;
            $mentor->totalSolution = $totalSolution;
            $mentor->totalComment = $totalComment;
            $mentor->totalRedeemBalance = $totalRedeemBalance;
        }

        if ($mentors == null) {
            return array('message' => 'failed', 'status' => 0, 'mentorList' => null);
        } else {
            return array('message' => 'success', 'status' => 1, 'mentorList' => $mentors);
        }
    }

    public function registerMentor(Request $request)
    {
        if ($this->isGuest()) {
            $this->json_response['message'] = 'Unauthorized';
            $this->json_response['success'] = false;
            return $this->json_result();
        }
        $mentor = new Mentor();
        $mentor->name = ucwords(strtolower($request['name']));
        $mentor->email = $request['email'];

        $name_parts = explode(' ', $request['name']);
        $name = '';
        foreach ($name_parts as $name_part) {
            $name .= strtolower($name_part) . '_';
        }
        $name = substr_replace($name, "", -1);
        $mentor->password = Hash::make($name);
        $mentor->phone = $request['phone'];
        $mentor->save();

        $strId = str_pad($mentor->id, 4, "0", STR_PAD_LEFT);
        $member_code = '';
        if (strlen($name_parts[0]) >= 3) {
            $member_code = strtoupper(substr($name_parts[0], 0, 3)) . $strId;
        } else {
            for ($i = 0; $i < count($name_parts); $i++) {
                $member_code .= strtoupper(substr($name_parts[$i], 0, 1));
            }
            $member_code .= $strId;
        }

        $mentor->member_code = $member_code;
        $mentor->photo = $this->getPhotoName($request->file('image'), $mentor->id, $name, 'mentor');
        $mentor->security_code = $this->generateSecurityCode();
        $mentor->save();

        return array('message' => 'success', 'status' => 1, 'security_code' => $this->scrypt->encrypt($mentor->security_code));
    }

    private function generateSecurityCode()
    {
        $chars = '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz';
        $newCode = '';

        $mentor = $this->regenerateSecurityCode($chars, $newCode);
        while ($mentor != null) {
            $mentor = $this->regenerateSecurityCode($chars, $newCode);
        }
        return $newCode;
    }

    private function regenerateSecurityCode($chars, &$newCode)
    {
        $newCode = '';
        for ($i = 0; $i < 10; $i++) {
            $newCode .= $chars[rand(0, strlen($chars) - 1)];
        }

        $mentor = Mentor::where('security_code', '=', $newCode)
            ->first();
        return $mentor;
    }

    public function loadNotificationList(Request $req)
    {
        if ($this->isGuest()) {
            $this->json_response['message'] = 'Unauthorized';
            $this->json_response['success'] = false;
            return $this->json_result();
        }
        $last_id = 0;
        if (!isset($req['last_id'])) {
            $last_id = 0;
        } else {
            $last_id = $req['last_id'];
        }
        if ($last_id == 0) {
            $last_id = Notification::max('id') + 1;
        }
        $notification = Notification::where([
            [$this->table_notification . '.id', '<', $last_id],
            ['auth_id', '=', $this->auth->id],
            ['auth_type', '=', $this->getType()]
        ])
            ->leftJoin($this->table_student, function ($join) {
                $join->on($this->table_student . '.id', '=', 'sender_id');
                $join->on('sender_type', '=', DB::raw("'student'"));
            })
            ->leftJoin($this->table_mentor, function ($join) {
                $join->on($this->table_mentor . '.id', '=', 'sender_id');
                $join->on('sender_type', '=', DB::raw("'mentor'"));
            })
            ->leftJoin($this->table_admin, function ($join) {
                $join->on($this->table_admin . '.id', '=', 'sender_id');
                $join->on('sender_type', '=', DB::raw("'admin'"));
            })
            ->orderBy('created_at', 'desc')
            ->limit($this->item_perpage)
            ->get(
                [$this->table_notification . '.*',
                    DB::raw("IF(" . $this->table_notification . ".sender_type='student'," . $this->table_student . ".photo,IF(" . $this->table_notification . ".`sender_type`='mentor'," . $this->table_mentor . ".photo,'admin.jpg')) AS photo"),
                    DB::raw("IF(" . $this->table_notification . ".sender_type='student'," . $this->table_student . ".name,IF(" . $this->table_notification . ".`sender_type`='mentor'," . $this->table_mentor . ".name,'admin')) AS sender_name")
                ]
            );

        $resultData = new \stdClass();
        $resultData->notificationList = $notification;

        $this->json_response['data'] = $resultData;
        return $this->json_result();
    }

    public function getNotificationCount()
    {
        if ($this->isGuest()) {
            $this->json_response['message'] = 'Unauthorized';
            $this->json_response['success'] = false;
            return $this->json_result();
        }

        $resultData = new \stdClass();
        $resultData->count = Notification::where([
            ['auth_id', '=', $this->auth->id],
            ['auth_type', '=', $this->getType()],
            ['status', '=', 0]
        ])->count();

        $this->json_response['data'] = $resultData;
        return $this->json_result();
    }

    public function setNotificationReadStatus(Request $request)
    {
        if ($this->isGuest()) {
            $this->json_response['message'] = 'Unauthorized';
            $this->json_response['success'] = false;
            return $this->json_result();
        }
        if (!isset($request['id'])) {
            $this->json_response['message'] = 'notification_id required';
            $this->json_response['success'] = false;
            return $this->json_result();
        }

        $notification = Notification::find($request['id']);
        if ($notification == null) {
            $this->json_response['message'] = 'notification_id required';
            $this->json_response['success'] = false;
            return $this->json_result();

        }
        if (($this->getType() == $notification->auth_type) && ($this->auth->id == $notification->auth_id)) {
            $notification->status = 1;
            $notification->save();
        } else {
            $this->json_response['message'] = 'notifikasi bukan milik anda';
            $this->json_response['success'] = false;
        }
        return $this->json_result();
    }

    public function loadPriorityQuestionList($last_id = 0)
    {
        if ($this->isGuest()) {
            $this->json_response['message'] = 'Unauthorized';
            $this->json_response['success'] = false;
            return $this->json_result();
        }
        if ($last_id == 0) {
            $last_id = Question::max('id') + 1;
        }
        $questions = Question::with(
            array(
                'Student' => function ($query) {
                    $query->select('id', 'name', 'photo');
                },
                'Material' => function ($query) {
                    $query->select(['id', 'name', 'subject_id']);
                },
                'Material.Subject' => function ($query) {
                    $query->select(['id', 'name']);
                }
            )
        )
            ->join($this->table_solution, $this->table_question . '.id', '=', $this->table_solution . '.question_id')
            ->select($this->table_question . '.*')
            ->where([
                [$this->table_question . '.id', '<', $last_id],
                ['status', '=', 1],
                [$this->table_solution . '.created_at', '<=', Carbon::now()->subDays(2)]
            ])
            ->orderBy('created_at', 'desc')
            ->limit($this->item_perpage)
            ->get();

        return array('message' => 'success', 'status' => 1, 'questionList' => $questions);
    }

    public function loadPriorityQuestionDetail(Request $request)
    {
        if ($this->isGuest()) {
            $this->json_response['message'] = 'Unauthorized';
            $this->json_response['success'] = false;
            return $this->json_result();
        }
        $question_id = $request['question_id'];

        $solutionList = Solution::with(
            array(
                'Mentor' => function ($query) {
                    $query->select('id', 'name', 'photo');
                }
            )
        )
            ->select('*')
            ->where('question_id', '=', $question_id)
            ->get();

        return array('message' => 'success', 'status' => 1, 'solutionList' => $solutionList);
    }

    public function voteBestSolution(Request $request)
    {
        if ($this->isGuest()) {
            $this->json_response['message'] = 'Unauthorized';
            $this->json_response['success'] = false;
            return $this->json_result();
        }
        $question_id = $request['question_id'];
        $solution_id = $request['solution_id'];
        $mentor_id = $request['mentor_id'];

        $question = Question::where('id', '=', $question_id)
            ->first();
        $solution = Solution::where([
            ['id', '=', $solution_id],
            ['mentor_id', '=', $mentor_id]
        ])
            ->first();
        if ($question == null) {
            return array('message' => 'failed', 'status' => 0);
        }
        if ($solution == null) {
            return array('message' => 'failed', 'status' => 0);
        }

        $question->status = 2;
        $solution->best = 1;

        $balance = new Balance();
        $balance->solution_id = $solution->id;
        $balance->deal_payment = $this->deal_payment;
        $balance->save();

        $question->save();
        $solution->save();

        /*
         * push notification
         */
        $this->pushNotificationChooseTheBest($question, $solution);
        return array('message' => 'success', 'status' => 1);
    }

    public function loadPriorityTransactionList($last_id = 0)
    {
        if ($this->isGuest()) {
            $this->json_response['message'] = 'Unauthorized';
            $this->json_response['success'] = false;
            return $this->json_result();
        }
        if ($last_id == 0) {
            $last_id = Transaction::max('id') + 1;
        }
        $transactionList = Transaction::with(
            array(
                'Student' => function ($query) {
                    $query->select('id', 'name', 'photo');
                },
                'Package_' => function ($query) {
                    $query->select(['id', 'nominal', 'credit', 'active']);
                },
                'TransactionConfirm' => function ($query) {
                    $query->select('*');
                }
            )
        )
            ->select('*')
            ->where([
                ['id', '<', $last_id],
                ['status', '=', 0]
            ])
            ->orderBy('created_at', 'desc')
            ->limit($this->item_perpage)
            ->get();

        return array('message' => 'success', 'status' => 1, 'transactionList' => $transactionList);
    }

    #param (transaction_id)
    public function setTransactionAction(Request $request)
    {
        if ($this->isGuest()) {
            $this->json_response['message'] = 'Unauthorized';
            $this->json_response['success'] = false;
            return $this->json_result();
        }
        $transaction = Transaction::find($request['transaction_id']);
        if ($transaction == null) {
            $this->json_response['message'] = 'Error transaction not found';
            $this->json_response['success'] = false;
            return $this->json_result();
        }
        $student = Student::find($transaction->student_id);
        if ($student == null) {
            $this->json_response['message'] = 'Error student not found';
            $this->json_response['success'] = false;
            return $this->json_result();
        }
        $package = Package::find($transaction->package_id);
        if ($package == null) {
            $this->json_response['message'] = 'Error package not found';
            $this->json_response['success'] = false;
            return $this->json_result();
        }
        if ($request['action'] == 0) {
            $transaction->status = 2;
            $transaction->save();

            $this->pushNotificationCancelPurchase($transaction, $student, $package);
            return $this->json_result();
        } elseif ($request['action'] == 1) {
            $transaction->status = 1;
            $credit_timelife = Carbon::parse($student->credit_timelife);
            if ($credit_timelife < Carbon::now()) {
                $student->credit = $package->credit;
                $student->credit_timelife = Carbon::now()->addDays($package->active)->__toString();
            } else {
                $student->credit += $package->credit;
                $student->credit_timelife = $credit_timelife->addDays($package->active)->__toString();
            }

            $student->save();
            $transaction->save();

            $this->pushNotificationAcceptPurchase($transaction, $student, $package);
            return $this->json_result();
        }
    }

    public function loadPriorityRedeemBalanceList($last_id = 0)
    {
        if ($this->isGuest()) {
            $this->json_response['message'] = 'Unauthorized';
            $this->json_response['success'] = false;
            return $this->json_result();
        }
        if ($last_id == 0) {
            $last_id = RedeemBalance::max('id') + 1;
        }
        $redeemBalanceList = RedeemBalance::with(
            array(
                'Mentor' => function ($query) {
                    $query->select('id', 'name', 'photo');
                }
            )
        )
            ->select('*')
            ->where([
                ['id', '<', $last_id],
                ['status', '=', 0]
            ])
            ->orderBy('created_at', 'desc')
            ->limit($this->item_perpage)
            ->get();

        return array('message' => 'success', 'status' => 1, 'redeemBalanceList' => $redeemBalanceList);
    }

    public function setRedeemBalanceAction(Request $request)
    {
        if ($this->isGuest()) {
            $this->json_response['message'] = 'Unauthorized';
            $this->json_response['success'] = false;
            return $this->json_result();
        }
        $id = $request['id'];
        $mentor_id = $request['mentor_id'];
        $action = $request['action'];

        $redeemBalance = RedeemBalance::where([
            ['id', '=', $id],
            ['mentor_id', '=', $mentor_id],
        ])
            ->first();

        if ($redeemBalance == null) {
            return array('message' => 'failed', 'status' => 0);
        } else {
            if ($action == 0) {
                $redeemBalance->date_agreement = date('Y-m-d H:i:s');
                $redeemBalance->status = 2;

                $this->pushNotificationCancelRedeemBalance($redeemBalance, $mentor_id);
            } elseif ($action == 1) {
                $redeemBalance->date_agreement = date('Y-m-d H:i:s');
                $redeemBalance->status = 1;

                $this->pushNotificationAcceptRedeemBalance($redeemBalance, $mentor_id);
            }
            $redeemBalance->save();

            return array('message' => 'success', 'status' => 1);
        }
    }

    public function loadFeedbackList($last_id = 0)
    {
        if ($this->isGuest()) {
            $this->json_response['message'] = 'Unauthorized';
            $this->json_response['success'] = false;
            return $this->json_result();
        }
        if ($last_id == 0) {
            $last_id = Feedback::max('id') + 1;
        }
        $feedbackList = Feedback::select('*')
            ->where('id', '<', $last_id)
            ->orderBy('created_at', 'desc')
            ->limit($this->item_perpage)
            ->get();

        foreach ($feedbackList as $feedback) {
            if ($feedback->auth_type == 'student') {
                $feedback->auth = Student::where([
                    ['id', '=', $feedback->auth_id]
                ])
                    ->first(['id', 'name', 'photo']);
            } else if ($feedback->auth_type == 'mentor') {
                $feedback->auth = Mentor::where([
                    ['id', '=', $feedback->auth_id]
                ])
                    ->first(['id', 'name', 'photo']);
            }
        }
        return array('message' => 'success', 'status' => 1, 'feedbackList' => $feedbackList);
    }

    public function setFeedbackReadStatus(Request $request)
    {
        if ($this->isGuest()) {
            $this->json_response['message'] = 'Unauthorized';
            $this->json_response['success'] = false;
            return $this->json_result();
        }
        $feedback = Feedback::find($request['id']);
        $feedback->read = 1;
        $feedback->save();

        return array('message' => 'success', 'status' => 1);
    }

    private function pushNotificationChooseTheBest($question, $solution)
    {
        $_content = "Tim Solvin menandai solusi anda sebagai yang terbaik";

        Firebase::sendPushNotification([
            "type" => "solution",
            "auth_id" => $solution->mentor_id,
            "auth_type" => "mentor",
            "sender_id" => $question->student_id,
            "sender_type" => 'admin',
            "subject_id" => $question->id,
            "subject_type" => "question",
            "content" => $_content], $_content);
    }

    private function pushNotificationAcceptPurchase($transaction, $student, $package)
    {
        $carbon = Carbon::parse($student->credit_timelife);
        $until_date = $carbon->addDays($package->active);
        $month = ['Januari', 'Februari', 'Maret', 'April', 'Mei', 'Juni', 'Juli', 'Agustus', 'September', 'Oktober', 'November', 'Desember'];
        $date_format = $until_date->day . ' ' . $month[$until_date->month - 1] . ' ' . $until_date->year;

        $_content = "Paket Rp. " . number_format($package->nominal, 0, '', '.') . " telah berhasil diaktifkan dan berlaku s/d tanggal " . $date_format;

        Firebase::sendPushNotification([
            "type" => "transaction",
            "auth_id" => $student->id,
            "auth_type" => "student",
            "sender_id" => 1,
            "sender_type" => 'admin',
            "sender_photo" => '',
            "subject_id" => $transaction->id,
            "subject_type" => "transaction",
            "content" => $_content], $_content);
    }

    private function pushNotificationCancelPurchase($transaction, $student, $package)
    {
        $_content = "Admin menolak permohonan pembelian paket pertanyaan seharga Rp. " . number_format($package->nominal + $transaction->unique_code, 0, '', '.') . " dengan alasan tertentu. Mohon hubungi admin terkait via email untuk memahami alasan penolakan tersebut";

        Firebase::sendPushNotification([
            "type" => "transaction",
            "auth_id" => $student->id,
            "auth_type" => "student",
            "sender_id" => 1,
            "sender_type" => 'admin',
            "sender_photo" => '',
            "subject_id" => $transaction->id,
            "subject_type" => "transaction",
            "content" => $_content], $_content);
    }

    private function pushNotificationAcceptRedeemBalance($redeem_balance, $mentor_id)
    {
        $_content = "Permohonan penebusan saldo sebesar Rp. " . number_format($redeem_balance->balance, 0, '', '.') . " telah berhasil diproses";

        Firebase::sendPushNotification([
            "type" => "redeem_balance",
            "auth_id" => $mentor_id,
            "auth_type" => "mentor",
            "sender_id" => 1,
            "sender_type" => 'admin',
            "subject_id" => $redeem_balance->id,
            "subject_type" => "redeem_balance",
            "content" => $_content], $_content);
    }

    private function pushNotificationCancelRedeemBalance($redeem_balance, $mentor_id)
    {
        $_content = "Admin menolak permohonan penebusan saldo sebesar Rp. " . number_format($redeem_balance->balance, 0, '', '.') . " dengan alasan tertentu. Mohon hubungi admin terkait via email untuk memahami alasan penolakan tersebut";

        Firebase::sendPushNotification([
            "type" => "redeem_balance",
            "auth_id" => $mentor_id,
            "auth_type" => "mentor",
            "sender_id" => 1,
            "sender_type" => 'admin',
            "subject_id" => $redeem_balance->id,
            "subject_type" => "redeem_balance",
            "content" => $_content], $_content);
    }

    public function test()
    {
//        $mentor = Mentor::find(1);
//        $_content = 'test';
//        Firebase::sendPushNotification([
//            "type" => "other",
//            "auth_id" => 1,
//            "auth_type" => 'mentor',
//            "sender_id" => $mentor->id,
//            "sender_type" => 'mentor',
//            "photo" => $mentor->photo,
//            "subject_id" => 1,
//            "subject_type" => "other",
//            "content" => $_content], $_content);

        $student = Student::find(5);
        $_content = 'test';
        Firebase::sendPushNotification([
            "type" => "other",
            "auth_id" => 1,
            "auth_type" => 'mentor',
            "sender_id" => $student->id,
            "sender_type" => 'admin',
            "sender_photo" => $student->photo,
            "subject_id" => 1,
            "subject_type" => "other",
            "content" => $_content], $_content);
    }
}