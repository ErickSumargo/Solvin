<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Student extends Model
{
    protected $fillable = [
        'name', 'email', 'password',
        'phone', 'photo', 'address', 'birth',
        'device_id', 'school', 'member_code',
        'credit', 'credit_timelife', 'active'
    ];

    /**
     * The attributes that should be hidden for arrays.
     *
     * @var array
     */
    protected $hidden = [
        'password', 'remember_token', 'firebase_token'
    ];


    protected $table = 'solvin_student';
}