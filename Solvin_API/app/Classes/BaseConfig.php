<?php

namespace App\Classes;

use App\Classes\Token;
use App\Classes\SCrypt;
use Carbon\Carbon;

trait BaseConfig
{
    protected $json_response;
    protected $table_user;

    protected $table_student;
    protected $table_mentor;
    protected $table_question;
    protected $table_solution;
    protected $table_comment;
    protected $table_transaction;
    protected $table_transaction_confirm;
    protected $table_bank;
    protected $table_subject;
    protected $table_material;
    protected $table_register_tmp;
    protected $table_notification;
    protected $table_package;
    protected $table_redeem_balance;
    protected $table_balance;
    protected $table_security_code;
    protected $table_admin;
    protected $table_feedback;
    protected $table_free_credit;
    protected $table_reset_password;
    protected $table_balance_bonus;
    protected $max_solution;
    protected $item_perpage;
    protected $auth;
    protected $simpleAuth;
    protected $deal_payment;

    protected $ERROR_EMAIL;
    protected $ERROR_PHONE;
    protected $ERROR_MEMBER_CODE;
    protected $ERROR_OLD_PASSWORD;
    protected $ERROR_VALIDATE_CODE;
    protected $ERROR_ACTIVE_PASSWORD;

    protected $ERROR_CREATE_QUESTION;
    protected $ERROR_EDIT_QUESTION;
    protected $ERROR_CREATE_SOLUTION;
    protected $ERROR_EDIT_SOLUTION;
    protected $ERROR_CREATE_COMMENT;
    protected $ERROR_EDIT_COMMENT;

    protected $ERROR_MOBILE;
    protected $ERROR_CODE;

    private $imagetypes;
    protected $scrypt;

    public function __construct()
    {
        $this->json_response['success'] = true;
        $this->json_response['data'] = new \stdClass();
        $this->json_response['message'] = 'success';

        $this->table_user = 'solvin_user';

        $this->table_student = 'solvin_student';
        $this->table_mentor = 'solvin_mentor';
        $this->table_question = 'solvin_question';
        $this->table_solution = 'solvin_solution';
        $this->table_comment = 'solvin_comment';
        $this->table_transaction = 'solvin_transaction';
        $this->table_transaction_confirm = 'solvin_transaction_confirm';
        $this->table_bank = 'solvin_bank';
        $this->table_subject = 'solvin_subject';
        $this->table_material = 'solvin_material';
        $this->table_register_tmp = 'solvin_register_tmp';
        $this->table_notification = 'solvin_notification';
        $this->table_package = 'solvin_package';
        $this->table_redeem_balance = 'solvin_redeem_balance';
        $this->table_balance = 'solvin_balance';
        $this->table_security_code = 'solvin_security_code';
        $this->table_admin = 'solvin_admin';
        $this->table_feedback = 'solvin_feedback';
        $this->table_free_credit = 'solvin_free_credit';
        $this->table_reset_password = 'solvin_reset_password';
        $this->table_balance_bonus = 'solvin_balance_bonus';

        $this->max_solution = 3;
        $this->item_perpage = 10;
        $this->auth = (new Token())->getAuth();

        $this->imagetypes = [IMAGETYPE_GIF, IMAGETYPE_JPEG, IMAGETYPE_PNG, IMAGETYPE_BMP];
        $this->simpleAuth = ['id', 'name', 'email', 'photo'];
        $this->deal_payment = env('DEAL_PAYMENT', 1000);

        $this->ERROR_EMAIL = 0;
        $this->ERROR_PHONE = 1;
        $this->ERROR_MEMBER_CODE = 2;
        $this->ERROR_OLD_PASSWORD = 3;
        $this->ERROR_VALIDATE_CODE = 4;
        $this->ERROR_ACTIVE_PASSWORD = 5;

        $this->ERROR_CREATE_QUESTION = 6;
        $this->ERROR_EDIT_QUESTION = 7;
        $this->ERROR_CREATE_SOLUTION = 8;
        $this->ERROR_EDIT_SOLUTION = 9;
        $this->ERROR_CREATE_COMMENT = 10;
        $this->ERROR_EDIT_COMMENT = 11;

        $this->ERROR_MOBILE = 12;
        $this->ERROR_CODE = 13;

        $this->scrypt = new SCrypt();
        Carbon::setLocale('id');
    }

    public function json_result()
    {
        if ($this->json_response['data'] == new \stdClass()) {
            unset($this->json_response['data']);
        }
        return $this->json_response;
    }

    public function isStudent()
    {
        $check = (new Token())->getType() == 'student';
        if ($check == false) {
            $this->response_unauthorization();
        }
        return $check;
    }

    public function isMentor()
    {
        $check = (new Token())->getType() == 'mentor';
        if (!$check) {
            $this->response_unauthorization();
        }
        return $check;
    }

    public function isAdmin()
    {
        $check = (new Token())->getType() == 'admin';
        if ($check == false) {
            $this->response_unauthorization();
        }
        return $check;
    }

    public function isGuest()
    {
        return (new Token())->getType() == 'guest';
    }

    public function isAuthorization()
    {
        if ($this->isGuest() || $this->isMentor() || $this->isStudent() || $this->isAdmin()) {
            $this->response_success();
            return true;
        }
        $this->response_unauthorization();
        return false;
    }

    public function getType()
    {
        return (new Token())->getType();
    }

    public function isImageValid($image)
    {
        $_valid = false;
        try {
            $_valid = in_array(exif_imagetype($image), $this->imagetypes);
        } catch (\ErrorException $e) {
            $_valid = false;
        }
        if (!$_valid) {
            $this->json_response['message'] = 'format gambar tidak di dukung';
            $this->json_response['success'] = false;
        }
        return $_valid;
    }

    public function getPhotoName($image, $id, $name, $path)
    {
        $fileName = time() . '-' . $id . '-' . $name . '.' . $image->getClientOriginalExtension();;
        $image->move(base_path() . '/public/images/' . $path . '/', $fileName);
        return $fileName;
    }

    public function getImageName($image, $path, $custom = '')
    {
        $fileName = time() . '-' . $this->auth->id . '-' . $custom . '.' . $image->getClientOriginalExtension();;
        $image->move(base_path() . '/public/images/' . $path . '/', $fileName);
        return $fileName;
    }

    public function deleteImage($image, $path)
    {
        $mFile = base_path() . '/public/images/' . $path . '/' . $image;
        if (!is_dir($mFile) && is_file($mFile)) {
            unlink($mFile);
        }
    }

    public function response_success()
    {
        $this->json_response['message'] = 'success';
        $this->json_response['success'] = true;
    }

    public function response_unauthorization()
    {
        $this->json_response['message'] = 'unauthorization';
        $this->json_response['success'] = false;
    }

    protected function getLatestVersionApp()
    {
        return env('VERSION_APP', '0.0.1');
    }

    protected function getLatestVersionMaterial()
    {
        return env('VERSION_MATERIAL', '0.0.1');
    }

    protected function getLatestVersionPackage()
    {
        return env('VERSION_PACKAGE', '0.0.1');
    }

    protected function getLatestVersionBank()
    {
        return env('VERSION_BANK', '0.0.1');
    }

    protected function getLatestVersionMobileNetwork()
    {
        return env('VERSION_MOBILE_NETWORK', '0.0.1');
    }

    protected function generateRandomString($length = 10)
    {
        return substr(str_shuffle(str_repeat($x = '0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ', ceil($length / strlen($x)))), 1, $length);
    }
}