<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class BalanceBonus extends Model
{
    protected $table = 'solvin_balance_bonus';

    protected $fillable = [
        'mentor_id', 'count', 'balance', 'deal_payment'
    ];
}