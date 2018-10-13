<?php

use Illuminate\Database\Schema\Blueprint;
use Illuminate\Database\Migrations\Migration;

class CreateTableQuestion extends Migration
{
    /**
     * Run the migrations.
     *
     * @return void
     */
    use \App\Classes\BaseConfig;

    public function up()
    {
        Schema::create($this->table_question, function (Blueprint $table) {
            $table->increments('id');
            $table->integer('student_id',false,true);
            $table->integer('status',false,false)->default(0);
            $table->integer('material_id',false,true);
            $table->string('image')->nullable(false);
            $table->text('content')->nullable(false);
            $table->string('other')->nullable(false);
            $table->boolean('active')->default(true);
            $table->timestamps();
        });
    }

    /**
     * Reverse the migrations.
     *
     * @return void
     */
    public function down()
    {
        Schema::drop($this->table_question);
    }
}
