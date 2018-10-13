<?php

use Illuminate\Database\Schema\Blueprint;
use Illuminate\Database\Migrations\Migration;

class CreateTableTransactionconfirm extends Migration
{
    /**
     * Run the migrations.
     *
     * @return void
     */
    use \App\Classes\BaseConfig;

    public function up()
    {
        Schema::create($this->table_transaction_confirm, function (Blueprint $table) {
            $table->increments('id');
            $table->integer('transaction_id',false,true);
            $table->string('bank_account_owner');
            $table->string('bank_name');
            $table->string('bank_name_other');
            $table->string('image')->nullable(false);
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
        Schema::drop($this->table_transaction_confirm);
    }
}
