<?php

use Illuminate\Database\Schema\Blueprint;
use Illuminate\Database\Migrations\Migration;

class CreateTableNotification extends Migration
{
    /**
     * Run the migrations.
     *
     * @return void
     */
    use \App\Classes\BaseConfig;

    public function up()
    {
        Schema::create($this->table_notification, function (Blueprint $table) {
            $table->increments('id');
            $table->integer('auth_id',false,true);
            $table->integer('subject_id',false,true);
            $table->smallInteger('status',false,false)->default(0);
            $table->string('type');
            $table->string('auth_type');
            $table->string('subject_type');
            $table->text('content');
            $table->integer('sender_id',false,true);
            $table->string('sender_type');
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
        Schema::drop($this->table_notification);
    }
}
