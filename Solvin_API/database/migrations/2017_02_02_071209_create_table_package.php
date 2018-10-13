<?php

use Illuminate\Database\Schema\Blueprint;
use Illuminate\Database\Migrations\Migration;

class CreateTablePackage extends Migration
{
    /**
     * Run the migrations.
     *
     * @return void
     */
    use \App\Classes\BaseConfig;

    public function up()
    {
        Schema::create($this->table_package, function (Blueprint $table) {
            $table->increments('id');
            $table->decimal('nominal',19,0);
            $table->integer('active',false,true);
            $table->integer('credit',false,true);
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
        Schema::drop($this->table_package);
    }
}
