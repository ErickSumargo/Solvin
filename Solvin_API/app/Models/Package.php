<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Package extends Model
{
    protected $fillable = [
        'nominal','active','credit'
    ];
    protected $table =  'solvin_package';
}
