<?php
/**
 * Created by PhpStorm.
 * User: edinofri
 * Date: 03/02/2017
 * Time: 23:04
 */

namespace App\Classes;

use App\Models\Admin;
use App\Models\Mentor;
use App\Models\Student;
use App\Models\Notification;

class Firebase // ini bukan controller just a class
{

    public static function sendPushNotification($_data, $_content, $p = 0, $mode = 0)
    {
        Notification::Create($_data); // buat record baru di table Notification
        // ngecheck type user nya (multi auth) user / mentor + masukin object user/mentor
        if ($_data['auth_type'] == 'admin') {
            $auth = Admin::find($_data['auth_id']);
        } elseif ($_data['auth_type'] == 'student') {
            $auth = Student::find($_data['auth_id']);
        } elseif ($_data['auth_type'] == 'mentor') {
            $auth = Mentor::find($_data['auth_id']);
        }
        // yg aku kirim nanti cuma jumlah notif nya. jadi aku hitung jumlah notif yg belum dibaca (status = 1) sesuai
        // condition (user_type,id)
        $count_notification = Notification::where('auth_id', $auth->id)->where('auth_type', $_data['auth_type'])->where('status', 0)->count();
        // user/mentor udah didapat & jumlah notif sudah didapat saatnya kirim token.
        // aku udah isi token firebase saat login & register(android)

        $sender_name = 'admin';
        if ($_data['sender_type'] == 'student') {
            $sender_name = Student::find($_data['sender_id'])->name;
        } else if ($_data['sender_type'] == 'mentor') {
            $sender_name = Mentor::find($_data['sender_id'])->name;
        }
        $data = Array(
            'to' => $auth->firebase_token, // ambil token dari object user/mentor
            'notification' => array(
                'title' => $mode == 0 ? 'Solvin' : 'Solvin AP',
                'body' => $_content // pesan yg akan ku kirim
            ),
            'data' => array(
                'count' => $count_notification, //data yg akan ku bawa di android nanti via eventbus (service->main treadh seperti activity & dll)
                'title' => $mode == 0 ? 'Solvin' : 'Solvin AP',
                'subject_type' => $_data['subject_type'],
                'subject_id' => $_data['subject_id'],
                'auth_id' => $_data['auth_id'],
                'auth_type' => $_data['auth_type'],
                'body' => $_data['content'],
                'sender_id' => $_data['sender_id'],
                'sender_name' => $sender_name,
                'sender_type' => $_data['sender_type'],
                'sender_photo' => $_data['sender_photo'],
                'priority' => $p
            )
        );
        // token api firebase
        $key_firebase = 'AAAAk1JCcFw:APA91bERue2G-M6gumg2pxiVsJ3BJzLwoXjR8pvGidcZSDqEB5t3WFPfB2eXg9KBcB_3YjIlQD4ivUt5TOr6oGnHQawlcq7xtWHE8eZPQdCAaD-QybxU0uaf1d9ZGq06OYCnGC4NaVZ5p7jFPc-lDWixuke0_NlkXw';

        // sisanya cuma curl (sama kayak postman gunanya)
        $url = 'https://fcm.googleapis.com/fcm/send';
        $headers = array(
            'Authorization: key=' . $key_firebase,
            'Content-Type: application/json'
        );

        $ch = curl_init();
        //Setting the curl url
        curl_setopt($ch, CURLOPT_URL, $url);

        //setting the method as post
        curl_setopt($ch, CURLOPT_POST, true);

        //adding headers
        curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);

        //disabling ssl support
        curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);

        // adding the data in json format
        curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($data));

        $result = curl_exec($ch);
        if ($result == false) {
            $result = 'Curl failed: ' . curl_error($ch);
        }
        curl_close($ch);
        return $result;
        // selesai , any question?
    }
}