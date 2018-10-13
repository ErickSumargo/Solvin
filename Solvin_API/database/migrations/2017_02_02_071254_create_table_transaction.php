<?php

use Illuminate\Database\Schema\Blueprint;
use Illuminate\Database\Migrations\Migration;

class CreateTableTransaction extends Migration
{
    /**
     * Run the migrations.
     *
     * @return void
     */
    use \App\Classes\BaseConfig;

    public function up()
    {
        Schema::create($this->table_transaction, function (Blueprint $table) {
            $table->increments('id');
            $table->integer('package_id',false,true);
            $table->integer('student_id',false,true);
            $table->integer('bank_id',false,true);
            $table->string('unique_code');
            $table->smallInteger('status',false,false)->default(0);
            $table->string('payment_time');
            $table->boolean('clear')->default(false);
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
        Schema::drop($this->table_transaction);
    }
}
