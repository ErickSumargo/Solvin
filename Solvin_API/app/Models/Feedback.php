<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Feedback extends Model
{
    protected $table = 'solvin_feedback';

    protected $fillable = [
        'auth_id', 'auth_type', 'title', 'content', 'read'
    ];
}
