<?php

use Illuminate\Database\Schema\Blueprint;
use Illuminate\Database\Migrations\Migration;

class CreateTableStudent extends Migration
{
    /**
     * Run the migrations.
     *
     * @return void
     */
    use \App\Classes\BaseConfig;

    public function up()
    {
        Schema::create($this->table_student, function (Blueprint $table) {
            $table->increments('id');
            $table->string('name');
            $table->string('email')->unique();
            $table->string('password');
            $table->string('phone')->unique();
            $table->string('photo')->nullable(false);
            $table->text('address')->nullable(false);
            $table->date('birth')->default('0000-00-00');
            $table->string('device_id')->nullable(false);
            $table->string('school')->nullable(false);
            $table->string('member_code')->nullable(false);
            $table->integer('credit')->default(0);
            $table->timestamp('credit_timelife')->nullable(true);
            $table->string("firebase_token")->nullable(true);
            $table->boolean('active')->default(true);
            $table->rememberToken();
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
        Schema::drop($this->table_student);
    }
}
