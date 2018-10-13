<?php

use Illuminate\Database\Schema\Blueprint;
use Illuminate\Database\Migrations\Migration;

class CreateTableFeedback extends Migration
{
    /**
     * Run the migrations.
     *
     * @return void
     */
    use \App\Classes\BaseConfig;

    public function up()
    {
        Schema::create($this->table_feedback, function (Blueprint $table) {
            $table->increments('id');
            $table->integer('auth_id', false, true);
            $table->string('auth_type');
            $table->text('title')->nullable(false);
            $table->text('content')->nullable(false);
            $table->smallInteger('read', false, true)->default(0);
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
        Schema::drop($this->table_feedback);
    }
}
