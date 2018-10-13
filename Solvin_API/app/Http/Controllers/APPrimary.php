<?php

namespace App\Http\Controllers;

use App\Classes\Token;
use App\Models\Admin;
use App\Models\Material;
use App\Models\Package;
use App\Models\Subject;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Hash;

class APPrimary extends Controller
{
    use \App\Classes\BaseConfig;

    public function loadPrimaryData()
    {
        $data = new \stdClass();

        $subjects = Subject::all(['id', 'name']);
        foreach ($subjects as $s) {
            $s->material = Material::where([
                ['subject_id', '=', $s->id],
            ])->get(['id', 'name']);
        }

        $packages = Package::all('id', 'nominal', 'active', 'credit');
        $data->materialList = $subjects;
        $data->packageList = $packages;

        return array('message' => 'success', 'status' => 1, 'data' => $data);
    }

    public function login(Request $request)
    {
        $resultData = new \stdClass();
        $token = new Token();

        $email = $this->scrypt->decrypt($request['email']);
        $password = $this->scrypt->decrypt($request['password']);
        $firebase_token = $request['firebase_token'];

        if (empty($firebase_token)) {
            return array('message' => 'firebase token can\'t be null', 'success' => 0);
        } else {
            $admin = Admin::where('email', $email)->first();
            if ($admin == null) {
                $resultData->emailValid = false;
                $resultData->passwordValid = false;

                return array('message' => 'Email tidak terdaftar', 'status' => 0, 'resultData' => $resultData);
            } else {
                if (Hash::check($password, $admin->password)) {
                    $admin->firebase_token = $firebase_token;
                    $admin->save();

                    $resultData->dataAdmin = $admin;
                    $resultData->token = $token->getToken($admin);
                    $resultData->emailValid = true;
                    $resultData->passwordValid = true;

                    return array('message' => 'Login berhasil', 'status' => 1, 'resultData' => $resultData);
                } else {
                    $resultData->emailValid = true;
                    $resultData->passwordValid = false;

                    return array('message' => 'Password tidak cocok dengan email terdaftar', 'success' => 0, 'resultData' => $resultData);
                }
            }
        }
    }
}