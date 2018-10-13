<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class MobileNetwork extends Model
{
    protected $fillable = [
        'name', 'number'
    ];

    protected $table = 'solvin_mobile_network';
}