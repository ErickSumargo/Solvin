<?php
/**
 * Created by PhpStorm.
 * User: edinofri
 * Date: 29/01/2017
 * Time: 16:52
 */

namespace App\Classes;

use Twilio\Rest\Client;

class Helper
{
    public static function sendSMS($arr)
    {
        $sid = 'AC10dfab1c333fdc7279dba74f41cdfc4b';
        $token = 'd043710b676ac916787b6661973ae8b3';
        $client = new Client($sid, $token);
        $arr['to'] =
        $sms = $client->messages->create(
            $arr['to'],
            array(
                "from" => '+16172997317',
                "body" => 'Welcome to Solvin. Kode verifikasi: ' . $arr['code']
            )
        );
        return $sms->errorCode;
    }

    public static function getASCIImod100($str)
    {
        $sum = 0;
        for ($i = 0; $i < count($str); $i++) {
            if (Helper::charAt($str, $i) != ' ') {
                $sum += ord(Helper::charAt($str, $i));
            }
        }
        return $sum;
    }

    private static function charAt($str, $pos)
    {
        return $str{$pos};
    }
}