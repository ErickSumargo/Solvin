<?php

namespace App\Http\Controllers;

use App\Models\Comment;
use App\Models\FreeCredit;
use App\Models\Material;
use App\Models\Mentor;
use App\Models\Question;
use App\Models\RedeemBalance;
use App\Models\BalanceBonus;
use App\Models\Solution;
use App\Models\Student;
use App\Models\Transaction;
use Carbon\Carbon;
use Illuminate\Http\Request;

class APDetail extends Controller
{
    use \App\Classes\BaseConfig;

    public function loadStudentProfile(Request $request)
    {
        if ($this->isGuest()) {
            $this->json_response['message'] = 'Unauthorized';
            $this->json_response['success'] = false;
            return $this->json_result();
        }
        $id = $request['id'];

        $student = Student::select('*')
            ->where('id', '=', $id)
            ->first();

        $totalFreeCredit = FreeCredit::where('student_id', '=', $id)
            ->count();

        $totalQuestionPending = Question::select('student_id')
            ->where([
                ['student_id', '=', $id],
                ['status', '=', 0]
            ])
            ->count();

        $totalQuestionDiscuss = Question::select('student_id')
            ->where([
                ['student_id', '=', $id],
                ['status', '=', 1]
            ])
            ->count();

        $totalQuestionComplete = Question::select('student_id')
            ->where([
                ['student_id', '=', $id],
                ['status', '=', 2]
            ])
            ->count();

        $totalTransactionPending = Transaction::select('student_id')
            ->where([
                ['student_id', '=', $id],
                ['status', '=', 0]
            ])
            ->count();

        $totalTransactionSuccess = Transaction::select('student_id')
            ->where([
                ['student_id', '=', $id],
                ['status', '=', 1]
            ])
            ->count();

        $totalTransactionCanceled = Transaction::select('student_id')
            ->where([
                ['student_id', '=', $id],
                ['status', '=', 2]
            ])
            ->count();

        $totalComment = Comment::select('auth_id')
            ->where([
                ['auth_id', '=', $id],
                ['auth_type', '=', 'student']
            ])
            ->count();

        $credit_timelife = Carbon::parse($student->credit_timelife);
        if (Carbon::now() > $credit_timelife) {
            $student->creditExpired = true;
        } else {
            $student->creditExpired = false;
        }

        $student->totalFreeCredit = $totalFreeCredit;

        $student->totalQuestionPending = $totalQuestionPending;
        $student->totalQuestionDiscuss = $totalQuestionDiscuss;
        $student->totalQuestionComplete = $totalQuestionComplete;

        $student->totalTransactionPending = $totalTransactionPending;
        $student->totalTransactionSuccess = $totalTransactionSuccess;
        $student->totalTransactionCanceled = $totalTransactionCanceled;

        $student->totalComment = $totalComment;

        if ($student == null) {
            return array('message' => 'failed', 'status' => 0, 'student' => null);
        } else {
            return array('message' => 'success', 'status' => 1, 'student' => $student);
        }
    }

    public function loadQuestionList(Request $request, $last_id = 0)
    {
        if ($this->isGuest()) {
            $this->json_response['message'] = 'Unauthorized';
            $this->json_response['success'] = false;
            return $this->json_result();
        }
        if ($last_id == 0) {
            $last_id = Question::max('id') + 1;
        }
        $student_id = $request['student_id'];
        $questionList = Question::with(
            array(
                'Material' => function ($query) {
                    $query->select(['id', 'name', 'subject_id']);
                },
                'Material.Subject' => function ($query) {
                    $query->select(['id', 'name']);
                }
            )
        )
            ->select('*')
            ->where([
                ['id', '<', $last_id],
                ['student_id', '=', $student_id]
            ])
            ->orderBy('created_at', 'desc')
            ->limit($this->item_perpage)
            ->get();

        if ($questionList == null) {
            return array('message' => 'failed', 'status' => 0, 'questionList' => null);
        } else {
            $max_id = Question::where('student_id', '=', $student_id)->max('id');
            return array('message' => 'success', 'status' => 1, 'questionList' => $questionList, 'max_id' => $this->scrypt->encrypt($max_id == null ? 0 : $max_id));
        }
    }

    public function editQuestion(Request $request)
    {
        if ($this->isGuest()) {
            $this->json_response['message'] = 'Unauthorized';
            $this->json_response['success'] = false;
            return $this->json_result();
        }
        $question = Question::where('id', '=', $request['id'])
            ->first();

        $material = Material::find($request['material_id']);

        $question->content = $request['content'];
        $question->material_id = $material->id;
        $question->other = $request['other'];

        $change_image = $request['change_image'];
        if ($change_image == 'true') {
            $temp = $question->image;
            if ($request->file('image') != null && !empty($request->file('image')->getClientOriginalName())) {
                if ($this->isImageValid($request->file('image'))) {
                    $question->image = $this->getImageName($request->file('image'), 'question', $material->id);
                } else {
                    return array('message' => 'failed', 'status' => 0);
                }
            } else {
                $question->image = null;
            }
            $this->deleteImage($temp, 'question');
        }

        $question->save();

        return array('message' => 'success', 'status' => 1);
    }

    public function blockQuestion(Request $request)
    {
        if ($this->isGuest()) {
            $this->json_response['message'] = 'Unauthorized';
            $this->json_response['success'] = false;
            return $this->json_result();
        }
        $id = $request['id'];

        $question = Question::find($id);
        $question->active = 0;
        $question->save();

        return array('message' => 'success', 'status' => 1);
    }

    public function loadTransactionList(Request $request, $last_id = 0)
    {
        if ($this->isGuest()) {
            $this->json_response['message'] = 'Unauthorized';
            $this->json_response['success'] = false;
            return $this->json_result();
        }
        if ($last_id == 0) {
            $last_id = Transaction::max('id') + 1;
        }
        $student_id = $request['student_id'];
        $transactionList = Transaction::with(
            array(
                'Package_' => function ($query) {
                    $query->select(['id', 'nominal', 'credit', 'active']);
                }
            )
        )
            ->select('*')
            ->where([
                ['id', '<', $last_id],
                ['student_id', '=', $student_id]
            ])
            ->orderBy('created_at', 'desc')
            ->limit($this->item_perpage)
            ->get();

        if ($transactionList == null) {
            return array('message' => 'failed', 'status' => 0, 'transactionList' => null);
        } else {
            return array('message' => 'success', 'status' => 1, 'transactionList' => $transactionList);
        }
    }

    public function loadMentorProfile(Request $request)
    {
        if ($this->isGuest()) {
            $this->json_response['message'] = 'Unauthorized';
            $this->json_response['success'] = false;
            return $this->json_result();
        }
        $id = $request['id'];

        $mentor = Mentor::select('*')
            ->where('id', '=', $id)
            ->first();

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

        $totalBalanceRedeemed = RedeemBalance::select('mentor_id')
            ->where([
                ['mentor_id', '=', $id],
                ['status', '=', 1]
            ])
            ->sum('balance');

        $totalBestSolution = Solution::where([
            ['mentor_id', '=', $mentor->id],
            ['best', '=', 1]
        ])
            ->count();

        $totalSolution = Solution::where('mentor_id', '=', $mentor->id)
            ->count();

        $totalRedeemBalancePending = RedeemBalance::select('mentor_id')
            ->where([
                ['mentor_id', '=', $id],
                ['status', '=', 0]
            ])
            ->count();

        $totalRedeemBalanceSuccess = RedeemBalance::select('mentor_id')
            ->where([
                ['mentor_id', '=', $id],
                ['status', '=', 1]
            ])
            ->count();

        $totalRedeemBalanceCanceled = RedeemBalance::select('mentor_id')
            ->where([
                ['mentor_id', '=', $id],
                ['status', '=', 2]
            ])
            ->count();

        $totalComment = Comment::select('auth_id')
            ->where([
                ['auth_id', '=', $id],
                ['auth_type', '=', 'mentor']
            ])
            ->count();


        $mentor->totalBalance = $totalBalance;
        $mentor->totalBalanceBonus = $totalBalanceBonus;
        $mentor->totalBalanceRedeemed = $totalBalanceRedeemed;

        $mentor->totalBestSolution = $totalBestSolution;
        $mentor->totalSolution = $totalSolution;

        $mentor->totalRedeemBalancePending = $totalRedeemBalancePending;
        $mentor->totalRedeemBalanceSuccess = $totalRedeemBalanceSuccess;
        $mentor->totalRedeemBalanceCanceled = $totalRedeemBalanceCanceled;

        $mentor->totalComment = $totalComment;

        if ($mentor == null) {
            return array('message' => 'failed', 'status' => 0, 'mentor' => null);
        } else {
            return array('message' => 'success', 'status' => 1, 'mentor' => $mentor);
        }
    }

    public function changePhoto(Request $request)
    {
        if ($this->isGuest()) {
            $this->json_response['message'] = 'Unauthorized';
            $this->json_response['success'] = false;
            return $this->json_result();
        }

        $mentor = Mentor::find($request['id']);
        $name_parts = explode(' ', $mentor->name);
        $name = '';
        foreach ($name_parts as $name_part) {
            $name .= strtolower($name_part) . '_';
        }
        $name = substr_replace($name, "", -1);

        if (isset($request['image'])) {
            $temp = $mentor->photo;
            if ($request->file('image') != null && !empty($request->file("image")->getClientOriginalName())) {
                if ($this->isImageValid($request->file('image'))) {
                    $mentor->photo = $this->getPhotoName($request->file('image'), $mentor->id, $name, 'mentor');
                } else {
                    return array('message' => 'failed', 'status' => 0);
                }
            } else {
                $mentor->photo = null;
            }
            $this->deleteImage($temp, 'mentor');
        }
        $mentor->save();
        return array('message' => 'success', 'status' => 1);
    }

    public function loadSolutionList(Request $request, $last_id = 0)
    {
        if ($this->isGuest()) {
            $this->json_response['message'] = 'Unauthorized';
            $this->json_response['success'] = false;
            return $this->json_result();
        }
        if ($last_id == 0) {
            $last_id = Solution::max('id') + 1;
        }
        $mentor_id = $request['mentor_id'];
        $solutionList = Solution::with(
            array(
                'Question' => function ($query) {
                    $query->select('*');
                },
                'Question.Student' => function ($query) {
                    $query->select('id', 'name', 'photo');
                },
                'Question.Material' => function ($query) {
                    $query->select(['id', 'name', 'subject_id']);
                },
                'Question.Material.Subject' => function ($query) {
                    $query->select(['id', 'name']);
                }
            )
        )
            ->select('*')
            ->where([
                ['id', '<', $last_id],
                ['mentor_id', '=', $mentor_id]
            ])
            ->orderBy('created_at', 'desc')
            ->limit($this->item_perpage)
            ->get();

        if ($solutionList == null) {
            return array('message' => 'failed', 'status' => 0, 'solutionList' => null);
        } else {
            return array('message' => 'success', 'status' => 1, 'solutionList' => $solutionList);
        }
    }

    public function editSolution(Request $request)
    {
        if ($this->isGuest()) {
            $this->json_response['message'] = 'Unauthorized';
            $this->json_response['success'] = false;
            return $this->json_result();
        }
        $solution = Solution::where('id', '=', $request['id'])
            ->first();
        $solution->content = $request['content'];

        $change_image = $request['change_image'];
        if ($change_image == 'true') {
            $temp = $solution->image;
            if ($request->file('image') != null && !empty($request->file('image')->getClientOriginalName())) {
                if ($this->isImageValid($request->file('image'))) {
                    $solution->image = $this->getImageName($request->file('image'), 'solution', null);
                } else {
                    return array('message' => 'failed', 'status' => 0);
                }
            } else {
                $solution->image = null;
            }
            $this->deleteImage($temp, 'solution');
        }

        $solution->save();

        return array('message' => 'success', 'status' => 1);
    }

    public function blockSolution(Request $request)
    {
        if ($this->isGuest()) {
            $this->json_response['message'] = 'Unauthorized';
            $this->json_response['success'] = false;
            return $this->json_result();
        }
        $id = $request['id'];

        $solution = Solution::find($id);
        $solution->active = 0;
        $solution->save();

        return array('message' => 'success', 'status' => 1);
    }

    public function loadRedeemBalanceList(Request $request, $last_id = 0)
    {
        if ($this->isGuest()) {
            $this->json_response['message'] = 'Unauthorized';
            $this->json_response['success'] = false;
            return $this->json_result();
        }
        if ($last_id == 0) {
            $last_id = RedeemBalance::max('id') + 1;
        }
        $mentor_id = $request['mentor_id'];
        $redeemBalanceList = RedeemBalance::select('*')
            ->where([
                ['id', '<', $last_id],
                ['mentor_id', '=', $mentor_id]
            ])
            ->orderBy('created_at', 'desc')
            ->limit($this->item_perpage)
            ->get();

        if ($redeemBalanceList == null) {
            return array('message' => 'failed', 'status' => 0, 'redeemBalanceList' => null);
        } else {
            return array('message' => 'success', 'status' => 1, 'redeemBalanceList' => $redeemBalanceList);
        }
    }

    public function loadBalanceBonusList(Request $request, $last_id = 0)
    {
        if ($this->isGuest()) {
            $this->json_response['message'] = 'Unauthorized';
            $this->json_response['success'] = false;
            return $this->json_result();
        }
        if ($last_id == 0) {
            $last_id = BalanceBonus::max('id') + 1;
        }
        $mentor_id = $request['mentor_id'];
        $balanceBonusList = BalanceBonus::select('*')
            ->where([
                ['id', '<', $last_id],
                ['mentor_id', '=', $mentor_id]
            ])
            ->orderBy('created_at', 'desc')
            ->limit($this->item_perpage)
            ->get();

        if ($balanceBonusList == null) {
            return array('message' => 'failed', 'status' => 0, 'balanceBonusList' => null);
        } else {
            return array('message' => 'success', 'status' => 1, 'balanceBonusList' => $balanceBonusList);
        }
    }

    public function loadCommentList(Request $request, $last_id = 0)
    {
        if ($this->isGuest()) {
            $this->json_response['message'] = 'Unauthorized';
            $this->json_response['success'] = false;
            return $this->json_result();
        }
        if ($last_id == 0) {
            $last_id = Comment::max('id') + 1;
        }
        $auth_id = $request['auth_id'];
        $auth_type = $request['auth_type'];
        $commentList = Comment::with(
            array(
                'Question' => function ($query) {
                    $query->select(['id', 'material_id']);
                },
                'Question.Material' => function ($query) {
                    $query->select(['id', 'name', 'subject_id']);
                },
                'Question.Material.Subject' => function ($query) {
                    $query->select(['id', 'name']);
                }
            )
        )
            ->select('*')
            ->where([
                ['id', '<', $last_id],
                ['auth_id', '=', $auth_id],
                ['auth_type', '=', $auth_type]
            ])
            ->orderBy('created_at', 'desc')
            ->limit($this->item_perpage)
            ->get();

        if ($commentList == null) {
            return array('message' => 'failed', 'status' => 0, 'commentList' => null);
        } else {
            return array('message' => 'success', 'status' => 1, 'commentList' => $commentList);
        }
    }

    public function editComment(Request $request)
    {
        if ($this->isGuest()) {
            $this->json_response['message'] = 'Unauthorized';
            $this->json_response['success'] = false;
            return $this->json_result();
        }
        $comment = Comment::where([
            ['id', '=', $request['id']],
            ['auth_id', '=', $request['auth_id']],
            ['auth_type', '=', $request['auth_type']]
        ])
            ->first();
        $comment->content = $request['content'];

        $change_image = $request['change_image'];
        if ($change_image == 'true') {
            $temp = $comment->image;
            if ($request->file('image') != null && !empty($request->file('image')->getClientOriginalName())) {
                if ($this->isImageValid($request->file('image'))) {
                    $comment->image = $this->getImageName($request->file('image'), 'comment', null);
                } else {
                    return array('message' => 'failed', 'status' => 0);
                }
            } else {
                $comment->image = null;
            }
            $this->deleteImage($temp, 'comment');
        }

        $comment->save();

        return array('message' => 'success', 'status' => 1);
    }

    public function blockComment(Request $request)
    {
        if ($this->isGuest()) {
            $this->json_response['message'] = 'Unauthorized';
            $this->json_response['success'] = false;
            return $this->json_result();
        }
        $id = $request['id'];

        $comment = Comment::find($id);
        $comment->active = 0;
        $comment->save();

        return array('message' => 'success', 'status' => 1);
    }

    public function blockAuth(Request $request)
    {
        if ($this->isGuest()) {
            $this->json_response['message'] = 'Unauthorized';
            $this->json_response['success'] = false;
            return $this->json_result();
        }
        $id = $request['id'];
        $auth_type = $request['auth_type'];

        if ($auth_type == 'student') {
            $student = Student::find($id);
            if ($student == null) {
                return array('message' => 'failed', 'status' => 0);
            } else {
                $student->active = 0;
                $student->save();
                return array('message' => 'success', 'status' => 1);
            }
        } else if ($auth_type == 'mentor') {
            $mentor = Mentor::find($id);
            if ($mentor == null) {
                return array('message' => 'failed', 'status' => 0);
            } else {
                $mentor->active = 0;
                $mentor->save();
                return array('message' => 'success', 'status' => 1);
            }
        }
    }
}