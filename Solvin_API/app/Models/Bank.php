<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Bank extends Model
{
    protected $fillable = [
        'name','account_owner','account_number','logo'
    ];

    protected $table =  'solvin_bank';
}
