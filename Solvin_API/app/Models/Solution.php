<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Solution extends Model
{
    protected $fillable = [
        'mentor_id', 'content', 'image', 'question_id', 'best', 'active'
    ];

    protected $table = 'solvin_solution';

    public function Mentor()
    {
        return $this->belongsTo('\App\Models\Mentor', 'mentor_id', 'id');
    }

    public function Question()
    {
        return $this->belongsTo('App\Models\Question');
    }
}