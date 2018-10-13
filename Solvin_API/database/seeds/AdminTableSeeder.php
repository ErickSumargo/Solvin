<?php

use Illuminate\Database\Seeder;

class AdminTableSeeder extends Seeder
{
    /**
     * Run the database seeds.
     *
     * @return void
     */
    use \App\Classes\BaseConfig;

    public function run()
    {
        DB::table($this->table_admin)->insert([
            'name' => 'admin',
            'email' => 'admin@solvin.id',
            'password' => '$2y$10$r2rSEU010cvsY63xNNuAkOAo44jU8jkiAFDsZOe/.HSM/SjLp8YQi',
            'created_at' => date('Y-m-d H:i:s'),
            'updated_at' => date('Y-m-d H:i:s')
        ]);
    }
}