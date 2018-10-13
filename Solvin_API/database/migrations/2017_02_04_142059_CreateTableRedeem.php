<?php

use Illuminate\Database\Schema\Blueprint;
use Illuminate\Database\Migrations\Migration;

class CreateTableRedeem extends Migration
{
    /**
     * Run the migrations.
     *
     * @return void
     */
    use \App\Classes\BaseConfig;
    public function up()
    {
        Schema::create($this->table_redeem_balance, function (Blueprint $table) {
            $table->increments('id');
            $table->integer('mentor_id',false,true);
            $table->string('redeem_code');
            $table->decimal('balance',19,0);
            $table->smallInteger('status',false,true)->default(0);
            $table->timestamp('date_agreement')->nullable(true);
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
        Schema::drop($this->table_redeem_balance);
    }
}
