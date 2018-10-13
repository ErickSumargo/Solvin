<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class SecurityCode extends Model
{
    protected $fillable = [
        'code'
    ];
    protected $table =  'solvin_security_code';

}
