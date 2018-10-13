<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Mentor extends Model
{
    protected $fillable = [
        'name', 'email', 'password',
        'phone', 'photo', 'address', 'birth',
        'device_id', 'workplace', 'member_code', 'active'
    ];

    /**
     * The attributes that should be hidden for arrays.
     *
     * @var array
     */
    protected $hidden = [
        'password', 'security_code', 'remember_token', 'firebase_token'
    ];

    protected $table = 'solvin_mentor';

    public function Solution()
    {
        return $this->hasMany('App\Models\Solution');
    }

    public function Feedback()
    {
        return $this->hasMany('App\Models\Feedback');
    }
}