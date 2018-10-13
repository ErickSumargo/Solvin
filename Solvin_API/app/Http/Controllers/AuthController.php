<?php

namespace App\Http\Controllers;

use App\Classes\BaseConfig;
use App\Classes\Firebase;
use App\Classes\Helper;
use App\Classes\Token;
use App\Models\BalanceBonus;
use App\Models\Feedback;
use App\Models\FreeCredit;
use App\Models\Mentor;
use App\Models\Notification;
use App\Models\RedeemBalance;
use App\Models\RegisterTmp;
use App\Models\ResetPassword;
use App\Models\Solution;
use App\Models\Student;
use Carbon\Carbon;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Hash;

class AuthController extends Controller
{
    use BaseConfig;

    public function registerStepOne(Request $req)
    {
        if (!empty($req['phone'])) {
            $findPhone = Student::where('phone', $this->scrypt->decrypt($req['phone']))->first();

            if ($findPhone == null) {
                $refindPhone = RegisterTmp::where('phone', $this->scrypt->decrypt($req['phone']))->first();
                $random = rand(0, 10000);
                $random = $random . "";
                if (strlen($random) < 5) {
                    $nol = "";
                    for ($i = 0; $i < 5 - strlen($random); $i++) {
                        $nol .= "0";
                    }
                    $random = $nol . $random;
                }
                $this->json_response['message'] = 'berhasil mendapatkan register code';

                if ($refindPhone == null) {
                    $newUserRegisterStepOne = new RegisterTmp();
                    $newUserRegisterStepOne->phone = $this->scrypt->decrypt($req['phone']);
                    $newUserRegisterStepOne->code = $random;

                    $phone = "+62" . ltrim($this->scrypt->decrypt($req['phone']), "0");
                    Helper::sendSMS(['code' => $random, 'to' => $phone]);

                    $newUserRegisterStepOne->save();

                } else {
                    if ($refindPhone->status == 1) {
                        $this->json_response['message'] = "skip-1-2";
                    } else {
                        $refindPhone->code = $random;
                        $refindPhone->status = 0;

                        $phone = "+62" . ltrim($refindPhone->phone, "0");
                        Helper::sendSMS(['code' => $random, 'to' => $phone]);
                        $refindPhone->save();
                    }
                }
            } else {
                $this->json_response['error'][] = [
                    'message' => 'No. HP yang dimasukkan telah terdaftar sebelumnya',
                    'type' => $this->ERROR_MOBILE
                ];
                $this->json_response['success'] = false;
            }
        } else {
            $this->json_response['message'] = 'bad request';
            $this->json_response['success'] = false;
        }
        return $this->json_result();
    }

    public function registerStepTwo(Request $req)
    {
        if (!empty($req['phone']) && !empty($req['code'])) {
            $findPhone = RegisterTmp::where('phone', $this->scrypt->decrypt($req['phone']))->where('code', $req['code'])->first();
            if ($findPhone != null) {
                $findPhone->status = 1;
                $findPhone->save();
                $this->json_response['message'] = "phone terverifikasi";
            } else {
                $this->json_response['error'][] = [
                    'message' => 'Kode verifikasi yang dimasukkan tidak sesuai dengan yang kami kirimkan',
                    'type' => $this->ERROR_CODE
                ];
                $this->json_response['success'] = false;
            }
        } else {
            $this->json_response['message'] = "bad request";
            $this->json_response['success'] = false;
        }
        return $this->json_result();
    }

    public function registerStepThree(Request $req)
    {
        $token = new Token();
        if (empty($req['firebase'])) {
            $this->json_response['message'] = "firebase token can't be null";
            $this->json_response['success'] = false;
            return $this->json_result();
        }
        if (empty($req['email']) || empty($req['password']) || empty($req['name']) || empty($req['phone'])) {
            $this->json_response['message'] = "bad request";
            $this->json_response['success'] = false;
        } else {
            $findPhone = RegisterTmp::where('phone', $this->scrypt->decrypt($req['phone']))->where('status', 1)->first();
            if ($findPhone != null) {
                $findPhoneStudent = Student::where('phone', $this->scrypt->decrypt($req['phone']))->first();
                if ($findPhoneStudent == null) {
                    $tmpStudent = Student::where([
                        ['email', '=', $req['email']]
                    ])->first();
                    $tmpMentor = Mentor::where([
                        ['email', '=', $req['email']]
                    ])->first();
                    if ($tmpStudent != null || $tmpMentor != null) {
                        $this->json_response['error'][] = [
                            'message' => 'Email yang dimasukkan telah terdaftar sebelumnya',
                            'type' => $this->ERROR_EMAIL
                        ];
                        $this->json_response['success'] = false;
                        return $this->json_result();
                    }

                    $bonus_credit_member = 0;
                    $bonus_day_member = 0;
                    if (isset($req['member_code']) && !empty(trim($req['member_code']))) {
                        $isExist = false;
                        $studentMemberCode = Student::where([
                            ['member_code', '=', $req['member_code']]
                        ])->first();

                        $isExist = $studentMemberCode != null ? true : false;
                        if ($isExist) {
                            $bonus_credit_member = 1;
                            $bonus_day_member = 1;

                            if (Carbon::parse($studentMemberCode->credit_timelife) < Carbon::now()) {
                                $studentMemberCode->credit = 1;
                                $studentMemberCode->credit_timelife = Carbon::now()->addDays($bonus_day_member)->__toString();
                            } else {
                                $studentMemberCode->credit += $bonus_credit_member;
                                $studentMemberCode->credit_timelife = Carbon::parse($studentMemberCode->credit_timelife)->addDays($bonus_day_member)->__toString();
                            }
                            $studentMemberCode->save();

                            $freeCredit = new FreeCredit();
                            $freeCredit->student_id = $studentMemberCode->id;
                            $freeCredit->save();
                        }

                        if (!$isExist) {
                            $this->json_response['success'] = false;
                            $this->json_response['message'] = 'gagal membuat akun';
                            $this->json_response['error'][] = [
                                'message' => 'Kode membership yang dimasukkan tidak valid',
                                'type' => $this->ERROR_MEMBER_CODE
                            ];
                            return $this->json_result();
                        }
                    }

                    $newStudent = new Student();
                    $newStudent->email = $req['email'];
                    $newStudent->password = Hash::make($this->scrypt->decrypt($req['password']));
                    $newStudent->name = ucwords(strtolower($req['name']));
                    $newStudent->phone = $this->scrypt->decrypt($req['phone']);
                    $newStudent->firebase_token = $req['firebase'];
                    $newStudent->member_code = "-";
                    $newStudent->device_id = $req['device_id'];
                    $newStudent->credit = 2; // Default setelah register.
                    $newStudent->credit_timelife = (new Carbon())->addDays(30)->__toString();
                    $newStudent->save();

                    $_student = Student::where("email", $req['email'])->first();
                    $strId = str_pad($_student->id, 4, "0", STR_PAD_LEFT);
                    $name_part = explode(' ', $_student->name);
                    $member_code = '';
                    if (strlen($name_part[0]) >= 3) {
                        $member_code = strtoupper(substr($name_part[0], 0, 3)) . $strId;
                    } else {
                        for ($i = 0; $i < count($name_part); $i++) {
                            $member_code .= strtoupper(substr($name_part[$i], 0, 1));
                        }
                        $member_code .= $strId;
                    }

                    $_student->member_code = $member_code;
                    $_student->save();

                    $_student->auth_type = 'student';
                    if (isset($req['member_code']) && !empty(trim($req['member_code'])) && $isExist) {

                        $this->pushNotificationFreeCredit($studentMemberCode, $_student, $freeCredit);
                    }

                    $resultData = new \stdClass();
                    $resultData->auth = $_student;
                    $resultData->token = $token->getToken($_student);

                    $this->json_response['data'] = $resultData;
                } else {
                    $this->json_response['message'] = "nomor sudah dipakai";
                    $this->json_response['success'] = false;
                }
            } else {
                $this->json_response['message'] = "nomor tidak ada atau belum di verifikasi";
                $this->json_response['success'] = false;
            }
        }
        return $this->json_result();
    }

    public function login(Request $req)
    {
        $token = new Token();
        $resultData = new \stdClass();
        if (empty($req['firebase'])) {
            $this->json_response['message'] = 'firebase token can\'t be null';
            $this->json_response['success'] = false;
            return $this->json_result();
        }
        if (empty($req['email']) || empty($req['password'])) {
            $this->json_response['message'] = 'Harap masukkan email dan password terdaftar anda';
            $this->json_response['success'] = false;
        } else {
            $mentor = Mentor::where('email', $req['email'])->first();
            $student = Student::where('email', $req['email'])->first();

            if ($mentor != null) {
                if (Hash::check($this->scrypt->decrypt($req['password']), $mentor->password)) {
                    $mentor->device_id = $req['device_id'];
                    $mentor->firebase_token = $req['firebase'];
                    $mentor->save();

                    $resultData->auth = $mentor;
                    $resultData->token = $token->getToken($mentor);

                    $solution = Solution::where([
                        ['mentor_id', '=', $mentor->id]
                    ])->get(['best']);
                    if ($solution == null) {
                        $resultData->auth->solution_count = $this->scrypt->encrypt(0);
                        $resultData->auth->best_count = $this->scrypt->encrypt(0);
                    } else {
                        $best = 0;
                        foreach ($solution as $s) {
                            if ($s->best) {
                                $best++;
                            }
                        }
                        $resultData->auth->solution_count = $this->scrypt->encrypt(count($solution));
                        $resultData->auth->best_count = $this->scrypt->encrypt($best);
                    }

                    $totalBalance = Solution::where([
                        ['mentor_id', '=', $mentor->id],
                        ['best', '=', '1']
                    ])->join($this->table_balance, $this->table_solution . '.id', '=', $this->table_balance . '.solution_id')
                        ->get(['deal_payment'])
                        ->sum('deal_payment');
                    if ($totalBalance == null) {
                        $totalBalance = 0;
                    }

                    $balanceBonus = BalanceBonus::where('mentor_id', '=', $mentor->id)
                        ->get(['balance'])
                        ->sum('balance');
                    if ($balanceBonus == null) {
                        $balanceBonus = 0;
                    }

                    $balanceRedeemed = RedeemBalance::where([
                        ['mentor_id', '=', $mentor->id],
                        ['status', '=', 1]
                    ])
                        ->sum('balance');
                    if ($balanceRedeemed == null) {
                        $balanceRedeemed = 0;
                    }

                    $balances = $totalBalance + $balanceBonus - $balanceRedeemed;
                    $resultData->auth->balance_amount = $this->scrypt->encrypt($balances);

                    $mentor->auth_type = 'mentor';

                    $this->json_response['message'] = 'berhasil login';
                    $this->json_response['data'] = $resultData;
                } else {
                    $this->json_response['error'][] = [
                        'message' => 'Password tidak cocok dengan email terdaftar',
                        'type' => $this->ERROR_ACTIVE_PASSWORD
                    ];
                    $this->json_response['success'] = false;
                    return $this->json_result();
                }
            } else if ($student != null) {
                if (Hash::check($this->scrypt->decrypt($req['password']), $student->password)) {
                    $student->device_id = $req['device_id'];
                    $student->firebase_token = $req['firebase'];
                    $student->save();
                    $student->auth_type = 'student';
                    $student->credit = $this->scrypt->encrypt($student->credit);
                    $student->credit_timelife = $this->scrypt->encrypt($student->credit_timelife);

                    $resultData->auth = $student;
                    $resultData->token = $token->getToken($student);

                    $this->json_response['message'] = 'berhasil login';
                    $this->json_response['data'] = $resultData;
                } else {
                    $this->json_response['error'][] = [
                        'message' => 'Password tidak cocok dengan email terdaftar',
                        'type' => $this->ERROR_ACTIVE_PASSWORD
                    ];
                    $this->json_response['success'] = false;
                    return $this->json_result();
                }
            } else {
                $this->json_response['error'][] = [
                    'message' => 'Email tidak terdaftar',
                    'type' => $this->ERROR_EMAIL
                ];
                $this->json_response['success'] = false;
                return $this->json_result();
            }
        }
        return $this->json_result();
    }

    public function profile($auth_type = null, $auth_id = 0)
    {
        if ($this->isGuest()) {
            $this->json_response['message'] = 'Unauthorized';
            $this->json_response['success'] = false;
            return $this->json_result();
        }
        $resultData = new \stdClass();
        $auth = $auth_type == 'student' ? Student::find($auth_id) : Mentor::find($auth_id);

        if ($auth == null) {
            $this->json_response['message'] = $auth_type . ' not found';
            $this->json_response['success'] = false;
            return $this->json_result();
        }
        $auth->join_time = $auth->created_at->diffForHumans();
        if ($auth->birth == '0000-00-00') {
            $auth->age = -1;
        } else {
            $auth->age = Carbon::now()->diffInYears(Carbon::parse($auth->birth));
        }

        if ($auth_type == 'mentor') {
            $solution = Solution::where([
                ['mentor_id', '=', $auth_id]
            ])->get(['best']);
            if ($solution == null) {
                $auth->solution_count = $this->scrypt->encrypt(0);
                $auth->best_count = $this->scrypt->encrypt(0);
            } else {
                $best = 0;
                foreach ($solution as $s) {
                    if ($s->best) {
                        $best++;
                    }
                }
                $auth->solution_count = $this->scrypt->encrypt(count($solution));
                $auth->best_count = $this->scrypt->encrypt($best);
            }
        }
        if ($auth_type == $this->getType() && $auth_id == $this->auth->id) {
            $token = new Token();
            if ($this->getType() == 'student') {
                $now = new Carbon();
                $credit_timelife = Carbon::parse($auth->credit_timelife);
                if ($now > $credit_timelife) {
                    $auth->credit_expired = 'true';
                } else {
                    $auth->credit_expired = 'false';
                }
                $auth->credit = $this->scrypt->encrypt($auth->credit);
                $auth->credit_timelife = $this->scrypt->encrypt($auth->credit_timelife);
            }
            if ($this->getType() == 'mentor') {
                $totalBalance = Solution::where([
                    ['mentor_id', '=', $auth_id],
                    ['best', '=', '1']
                ])->join($this->table_balance, $this->table_solution . '.id', '=', $this->table_balance . '.solution_id')
                    ->get(['deal_payment'])
                    ->sum('deal_payment');
                if ($totalBalance == null) {
                    $totalBalance = 0;
                }

                $balanceBonus = BalanceBonus::where('mentor_id', '=', $auth_id)
                    ->get(['balance'])
                    ->sum('balance');
                if ($balanceBonus == null) {
                    $balanceBonus = 0;
                }

                $balanceRedeemed = RedeemBalance::where([
                    ['mentor_id', '=', $auth_id],
                    ['status', '=', 1]
                ])
                    ->sum('balance');
                if ($balanceRedeemed == null) {
                    $balanceRedeemed = 0;
                }

                $balances = $totalBalance + $balanceBonus - $balanceRedeemed;
                $auth->balance_amount = $this->scrypt->encrypt($balances);
            }
            $resultData->token = $token->getToken($auth);
        }
        $auth->auth_type = $auth_type;
        $resultData->auth = $auth;
        $this->json_response['data'] = $resultData;

        return $this->json_result();
    }

    #param (change_image,*image)
    public function changePhoto(Request $req)
    {
        if ($this->isGuest()) {
            $this->json_response['message'] = 'Unauthorized';
            $this->json_response['success'] = false;
            return $this->json_result();
        }
        /**
         * check & upload image
         */
        if (isset($req['change_image'])) {
            $change_image = $req['change_image'];
            if ($change_image == "true") {
                $tmp_file = $this->auth->photo;

                if ($req->file('image') != null && !empty($req->file("image")->getClientOriginalName())) {
                    if ($this->isImageValid($req->file('image'))) {
                        $this->auth->photo = $this->getImageName($req->file('image'), $this->getType());
                        $this->json_response['message'] = 'success update photo';
                    } else {
                        return $this->json_result();
                    }
                } else {
                    $this->auth->photo = null;
                }
                $this->deleteImage($tmp_file, $this->getType());
            }
        } else {
            $this->json_response['message'] = 'change_image required';
            $this->json_response['success'] = false;
        }
        $this->auth->save();
        return $this->json_result();
    }

    #param (old_password,new_password)
    public function changePassword(Request $req)
    {
        if ($this->isGuest()) {
            $this->json_response['message'] = 'Unauthorized';
            $this->json_response['success'] = false;
            return $this->json_result();
        }
        if (Hash::check($this->scrypt->decrypt($req['old_password']), $this->auth->password)) {
            $this->auth->password = Hash::make($this->scrypt->decrypt($req['new_password']));
            $this->auth->save();
        } else {
            $this->json_response['message'] = 'terdapat kesalahan';
            $this->json_response['error'][] = [
                'messsage' => 'Password yang dimasukkan tidak sesuai dengan password aktif anda saat ini',
                'type' => $this->ERROR_ACTIVE_PASSWORD
            ];
            $this->json_response['success'] = false;
        }
        return $this->json_result();
    }

    #param (email,name,birth,address,other{workplace,school}
    #(change_image,*image)
    public function update(Request $req)
    {
        if ($this->isGuest()) {
            $this->json_response['message'] = 'Unauthorized';
            $this->json_response['success'] = false;
            return $this->json_result();
        }

        $token = new Token();

        if (isset($req['email'])) {
            $tmpStudent = Student::where('email', $req['email'])->first();
            $tmpMentor = Mentor::where('email', $req['email'])->first();
            if ($this->auth->email != $req['email'] && ($tmpStudent != null || $tmpMentor != null)) {
                $this->json_response['error'][] = [
                    'message' => 'Email yang dimasukkan telah terdaftar sebelumnya',
                    'type' => $this->ERROR_EMAIL
                ];
                $this->json_response['success'] = false;
                return $this->json_result();
            }
            $this->auth->email = $req['email'];
        }
        if (isset($req['name'])) {
            $this->auth->name = ucwords(strtolower($req['name']));
        }
        if (empty($this->auth->email) || empty($this->auth->name)) {
            $this->json_response['message'] = 'email atau nama tidak boleh kosong';
            $this->json_response['success'] = false;
            return $this->json_result();
        }
        if (empty($req['phone'])) {
            $this->json_response['message'] = 'no. hp tidak boleh kosong';
            $this->json_response['success'] = false;
            return $this->json_result();
        }
        $this->auth->phone = $this->scrypt->decrypt($req['phone']);
        if (isset($req['birth'])) {
            if ($req['birth'] == '0000-00-00') {
                $this->auth->birth = $req['birth'];
            } else {
                $this->auth->birth = date('Y-m-d', strtotime($req['birth']));
            }
        }
        if (isset($req['address'])) {
            $this->auth->address = $req['address'];
        }
        if ($this->getType() == 'mentor') {
            if (isset($req['other'])) {
                $this->auth->workplace = $req['other'];
            }
        } else {
            if (isset($req['other'])) {
                $this->auth->school = $req['other'];
            }
        }

        /**
         * check & upload image
         */
        if (isset($req['change_image'])) {
            $change_image = $req['change_image'];
            if ($change_image == "true") {
                $tmp_file = $this->auth->photo;

                if ($req->file('image') != null && !empty($req->file("image")->getClientOriginalName())) {
                    if ($this->isImageValid($req->file('image'))) {
                        $this->auth->photo = $this->getImageName($req->file('image'), $this->getType());
                        $this->json_response['message'] = 'success update photo';
                    } else {
                        return $this->json_result();
                    }
                } else {
                    $this->auth->photo = null;
                }
                $this->deleteImage($tmp_file, $this->getType());
            }
        }
        $this->auth->save();
        $resultData = new \stdClass();
        $resultData->token = $token->getToken($this->auth);
        $this->json_response['data'] = $resultData;
        return $this->json_result();
    }

    #param (*page)
    public function notification(Request $req)
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
            ->orderBy('created_at', 'desc')
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
            ->limit($this->item_perpage)
            ->get(
                [$this->table_notification . '.*',
                    DB::raw("IF(" . $this->table_notification . ".sender_type='student'," . $this->table_student . ".photo,IF(" . $this->table_notification . ".`sender_type`='mentor'," . $this->table_mentor . ".photo,'admin.jpg')) AS photo"),
                    DB::raw("IF(" . $this->table_notification . ".sender_type='student'," . $this->table_student . ".name,IF(" . $this->table_notification . ".`sender_type`='mentor'," . $this->table_mentor . ".name,'admin')) AS sender_name")
                ]
            );

        $resultData = new \stdClass();
        $resultData->notifications = $notification;

        $this->json_response['data'] = $resultData;
        return $this->json_result();
    }

    public function countNotification(Request $req)
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

    public function actionNotification(Request $req)
    {
        if ($this->isGuest()) {
            $this->json_response['message'] = 'Unauthorized';
            $this->json_response['success'] = false;
            return $this->json_result();
        }
        if (!isset($req['notification_id'])) {

            $this->json_response['message'] = 'notification_id required';
            $this->json_response['success'] = false;
            return $this->json_result();

        }

        $notification = Notification::find($req['notification_id']);

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

    public function resetPasswordStepOne(Request $req)
    {
        if (!isset($req['phone'])) {
            $this->json_response['message'] = 'phone required.';
            $this->json_response['success'] = false;
            return $this->json_result();
        }
        $student = Student::where([
            ['phone', '=', $this->scrypt->decrypt($req['phone'])]
        ])->first();

        $mentor = Mentor::where([
            ['phone', '=', $this->scrypt->decrypt($req['phone'])]
        ])->first();

        $auth = $student != null ? $student : $mentor;
        $auth_type = $student != null ? 'student' : 'mentor';
        if ($auth != null) {
            $random = rand(0, 10000);
            $random = $random . "";
            if (strlen($random) < 5) {
                $nol = "";
                for ($i = 0; $i < 5 - strlen($random); $i++) {
                    $nol .= "0";
                }
                $random = $nol . $random;
            }
            $resetPassword = new ResetPassword();
            $resetPassword->auth_id = $auth->id;
            $resetPassword->auth_type = $auth_type;
            $resetPassword->phone = $this->scrypt->decrypt($req['phone']);
            $resetPassword->code = $random;

            $phone = "+62" . ltrim($this->scrypt->decrypt($req['phone']), "0");
//            Helper::sendSMS(['code' => $random, 'to' => $phone]);
            $resetPassword->save();

        } else {
            $this->json_response['message'] = 'No. hp yang dimasukkan tidak terdaftar';
            $this->json_response['success'] = false;
        }
        return $this->json_result();
    }

    public function resetPasswordStepTwo(Request $req)
    {
        $params = ['phone', 'code'];
        foreach ($params as $p) {
            if (!isset($req[$p])) {
                $this->json_response['success'] = false;
                $this->json_response['message'] = $p . ' is required';
                return $this->json_result();
            }
        }
        $resetPassword = ResetPassword::where([
            ['phone', '=', $this->scrypt->decrypt($req['phone'])],
            ['code', '=', $req['code']],
            ['status', '<', 2]
        ])->first();

        if ($resetPassword != null) {
            $token = $this->generateRandomString(48);
            $this->json_response['message'] = $token;
            $resetPassword->status = 2;
            $resetPassword->token = $token;
            $resetPassword->save();
        } else {
            $this->json_response['message'] = 'invalid';
        }
        return $this->json_result();
    }

    public function resetPasswordStepThree(Request $req)
    {
        $params = ['phone', 'token', 'password'];
        foreach ($params as $p) {
            if (!isset($req[$p])) {
                $this->json_response['success'] = false;
                $this->json_response['message'] = $p . ' is required';
                return $this->json_result();
            }
        }

        $resetPassword = ResetPassword::where([
            ['phone', '=', $this->scrypt->decrypt($req['phone'])],
            ['token', '=', $req['token']],
            ['status', '<', 3]
        ])->first();

        if ($resetPassword != null) {
            $resetPassword->status = 3;
            $auth = $resetPassword->auth_type == 'student' ? Student::find($resetPassword->auth_id) : Mentor::find($resetPassword->auth_id);
            if ($auth == null) {
                $this->json_response['success'] = false;
                $this->json_response['message'] = 'student/mentor tidak ditemukan';
                return $this->json_result();
            }
            $auth->password = Hash::make($this->scrypt->decrypt($req['password']));
            $auth->save();
            $resetPassword->save();
        } else {
            $this->json_response['success'] = false;
            $this->json_response['message'] = 'token salah';
        }
        return $this->json_result();
    }

    public function search(Request $req)
    {
        if ($this->isGuest()) {
            $this->response_unauthorization();
            return $this->json_result();
        }
        if (!isset($req['q'])) {
            $this->json_response['message'] = 'param q is required';
            return $this->json_result();
        }
        $query = $req['q'];
        $uQ = strlen($query) > 1 ? '%' . $query : $query;

        $resultStudent = Student::where($this->table_student . '.name', 'LIKE', "" . $uQ . "%")
            ->leftJoin($this->table_question, $this->table_student . '.id', '=', $this->table_question . '.student_id')
            ->groupBy($this->table_student . '.name')
            ->get([
                $this->table_student . '.id',
                $this->table_student . '.name',
                $this->table_student . '.email',
                $this->table_student . '.photo',
                $this->table_student . '.created_at',
                DB::raw('COUNT(' . $this->table_question . '.id) as question_count'),
                DB::raw('"student" as auth_type'),
            ]);
        foreach ($resultStudent as $student) {
            $student->join_time = $student->created_at->diffForHumans();
        }

        $resultMentor = Mentor::where($this->table_mentor . '.name', 'LIKE', $uQ . '%')
            ->leftJoin($this->table_solution, $this->table_mentor . '.id', '=', $this->table_solution . '.mentor_id')
            ->groupBy($this->table_mentor . '.name')
            ->get([
                $this->table_mentor . '.id',
                $this->table_mentor . '.name',
                $this->table_mentor . '.email',
                $this->table_mentor . '.photo',
                $this->table_mentor . '.created_at',
                DB::raw('(SELECT count(id) from ' . $this->table_solution . ' WHERE ' . $this->table_mentor . '.id = ' . $this->table_solution . '.mentor_id
                AND ' . $this->table_solution . '.best = 1) as best_count'),
                DB::raw('COUNT(' . $this->table_solution . '.id) as solution_count'),
                DB::raw('"mentor" as auth_type')
            ]);
        foreach ($resultMentor as $mentor) {
            $mentor->join_time = $mentor->created_at->diffForHumans();
            $mentor->solution_count = $this->scrypt->encrypt($mentor->solution_count);
            $mentor->best_count = $this->scrypt->encrypt($mentor->best_count);
        }

        $pureArrStudent = array_values((array)$resultStudent)[0];
        $pureArrMentor = array_values((array)$resultMentor)[0];
        $merged = array_merge($pureArrStudent, $pureArrMentor);
        $this->json_response['data'] = $merged;
        return $this->json_result();
    }

    public function sendFeedback(Request $req)
    {
        if (!$this->isStudent() && !$this->isMentor()) {
            $this->json_response['success'] = false;
            return $this->json_result();
        }

        $feedback = new Feedback();
        $feedback->auth_id = $this->auth->id;
        $feedback->auth_type = $this->getType();
        $feedback->title = $req['title'];
        $feedback->content = $req['content'];
        $feedback->save();

        $this->json_response['success'] = true;
        $this->json_response['message'] = 'Feedback telah disampaikan';

        return $this->json_result();
    }

    private function pushNotificationFreeCredit($owner_member_code, $new_user, $free_credit_obj)
    {
        $credit_timelife = Carbon::parse($owner_member_code->credit_timelife);
        $_content = "Penambahan +1 kredit tanya dan berlaku s/d " . $credit_timelife->day . ' ' . $credit_timelife->format('F') . ' ' . $credit_timelife->year
            . ' pada ' . $credit_timelife->hour . '.' . $credit_timelife->minute . ' WIB';

        Firebase::sendPushNotification([
            "type" => "other",
            "auth_id" => $owner_member_code->id,
            "auth_type" => 'student',
            "sender_id" => $new_user->id,
            "sender_type" => 'student',
            "subject_id" => $free_credit_obj->id,
            "subject_type" => "free_credit",
            "content" => $_content], $_content);
    }
}
