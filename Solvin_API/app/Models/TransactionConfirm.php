<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class TransactionConfirm extends Model
{
    protected $fillable = [
        'transaction_id', 'bank_account_owner',
        'bank_name', 'bank_name_other', 'mobile_number_owner', 'image'
    ];
    protected $table = 'solvin_transaction_confirm';

    public function Transaction()
    {
        return $this->belongsTo('App\Models\Transaction');
    }
}