<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Material extends Model
{
    protected $fillable = [
        'name', 'subject_id'
    ];

    protected $table = 'solvin_material';

    public function Subject()
    {
        return $this->belongsTo('\App\Models\Subject', 'subject_id', 'id');
    }
}