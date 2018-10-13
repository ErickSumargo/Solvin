<?php

namespace App\Models;

use App\Classes\BaseConfig;
use Illuminate\Database\Eloquent\Model;
use Illuminate\Support\Facades\DB;

class Question extends Model
{
    protected $fillable = [
        'id', 'student_id', 'status', 'material_id',
        'image', 'content', 'other', 'active'
    ];
    protected $hidden = [
        'question_id',
    ];
    protected $table = 'solvin_question';

    use BaseConfig;

    public function Student()
    {
        return $this->belongsTo('\App\Models\Student', 'student_id', 'id');
    }

    public function Comments()
    {
        $_relation = $this->hasMany('\App\Models\Comment', 'question_id')->orderBy($this->table_comment . '.created_at', 'asc');
        return $_relation;
    }

    public function Solutions()
    {
        return $this->hasMany('\App\Models\Solution', 'question_id')->orderBy('created_at', 'asc');
    }

    public function Material()
    {
        return $this->belongsTo('\App\Models\Material', 'material_id', 'id');
    }
}