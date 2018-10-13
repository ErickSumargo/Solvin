<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Comment extends Model
{
    protected $fillable = [
        'question_id','auth_id', 'auth_type', 'content','image', 'active'
    ];

    protected $table =  'solvin_comment';

    public function Question()
    {
        return $this->belongsTo('App\Models\Question');
    }
}
