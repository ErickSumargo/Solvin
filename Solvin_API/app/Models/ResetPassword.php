<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class ResetPassword extends Model
{
    protected $fillable = [
        'phone', 'status', 'code', 'auth_id', 'auth_type', 'token'
    ];
    protected $table = 'solvin_reset_password';
}