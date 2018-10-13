<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Subject extends Model
{
    protected $fillable = [
        'name'
    ];

    protected $table = 'solvin_subject';

    public function Material()
    {
        return $this->hasMany('\App\Models\Material', 'subject_id');
    }
}