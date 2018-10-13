<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Balance extends Model
{
    protected $fillable = [
        'solution_id', 'deal_payment'
    ];

    protected $table =  'solvin_balance';

}
