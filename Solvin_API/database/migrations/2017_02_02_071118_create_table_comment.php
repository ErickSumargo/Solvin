<?php

use Illuminate\Database\Schema\Blueprint;
use Illuminate\Database\Migrations\Migration;

class CreateTableComment extends Migration
{
    /**
     * Run the migrations.
     *
     * @return void
     */
    use \App\Classes\BaseConfig;

    public function up()
    {
        Schema::create($this->table_comment, function (Blueprint $table) {
            $table->increments('id');
            $table->integer('question_id',false,true);
            $table->integer('auth_id',false,true);
            $table->string('auth_type');
            $table->text('content')->nullable(false);
            $table->string('image');
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
        Schema::drop($this->table_comment);
    }
}
