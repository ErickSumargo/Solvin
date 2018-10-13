<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class FreeCredit extends Model
{
    protected $table = 'solvin_free_credit';

    protected $fillable = [
        'student_id'
    ];
}