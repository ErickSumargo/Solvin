<?php

use Illuminate\Database\Schema\Blueprint;
use Illuminate\Database\Migrations\Migration;

class CreateTableSolution extends Migration
{
    /**
     * Run the migrations.
     *
     * @return void
     */
    use \App\Classes\BaseConfig;

    public function up()
    {
        Schema::create($this->table_solution, function (Blueprint $table) {
            $table->increments('id');
            $table->integer('mentor_id',false,true);
            $table->text('content');
            $table->string('image')->nullable(false);
            $table->integer('question_id',false,true);
            $table->boolean('best')->default(false);
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
        Schema::drop($this->table_solution);
    }
}
