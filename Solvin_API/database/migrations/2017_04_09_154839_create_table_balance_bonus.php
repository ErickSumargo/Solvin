<?php

use Illuminate\Database\Schema\Blueprint;
use Illuminate\Database\Migrations\Migration;

class CreateTableBalanceBonus extends Migration
{
    /**
     * Run the migrations.
     *
     * @return void
     */
    use \App\Classes\BaseConfig;

    public function up()
    {
        Schema::create($this->table_balance_bonus, function (Blueprint $table) {
            $table->increments('id');
            $table->integer('mentor_id', false, true);
            $table->integer('count');
            $table->decimal('balance', 19, 0);
            $table->decimal('deal_payment', 19, 0);
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
        Schema::drop($this->table_balance_bonus);
    }
}