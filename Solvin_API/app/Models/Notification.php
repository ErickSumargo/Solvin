<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Notification extends Model
{
    protected $fillable = [
        'auth_id','subject_id','status'
        ,'type','auth_type','subject_type',
        'content','sender_id','sender_type'
    ];

    protected $table =  'solvin_notification';
}
