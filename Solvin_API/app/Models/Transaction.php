<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Transaction extends Model
{
    protected $fillable = [
        'package_id','student_id','bank_id', 'mobile_network_id',
        'unique_code','status','payment_time','clear'
    ];

    protected $table =  'solvin_transaction';

    public function Student()
    {
        return $this->belongsTo('\App\Models\Student');
    }

    public function TransactionConfirm(){
        return $this->hasOne('\App\Models\TransactionConfirm');
    }

    public function Package_(){
        return $this->belongsTo('\App\Models\Package','package_id','id');
    }
}
