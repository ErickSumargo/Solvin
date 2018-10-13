<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class RedeemBalance extends Model
{
    protected $fillable = [
        'mentor_id', 'redeem_code', 'balance',
        'status','date_agreement'
    ];
    protected $table =  'solvin_redeem_balance';

    public function Mentor()
    {
        return $this->belongsTo('App\Models\Mentor');
    }
}
