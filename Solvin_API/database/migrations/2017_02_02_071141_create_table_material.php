<?php

use Illuminate\Database\Schema\Blueprint;
use Illuminate\Database\Migrations\Migration;

class CreateTableMaterial extends Migration
{
    /**
     * Run the migrations.
     *
     * @return void
     */
    use \App\Classes\BaseConfig;

    public function up()
    {
        Schema::create($this->table_material, function (Blueprint $table) {
            $table->increments('id');
            $table->integer('subject_id', false, true);
            $table->string('name');
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
        Schema::drop($this->table_material);
    }
}