<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class RegisterTmp extends Model
{
    protected $fillable = [
        'phone','code','status'
    ];
    protected $table =  'solvin_register_tmp';

}
