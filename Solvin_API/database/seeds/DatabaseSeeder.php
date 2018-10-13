<?php

use Illuminate\Database\Seeder;

class DatabaseSeeder extends Seeder
{
    /**
     * Run the database seeds.
     *
     * @return void
     */
    public function run()
    {
//        $this->call(AdminTableSeeder::class);
        $this->call(SubjectTableSeeder::class);
        $this->call(MaterialTableSeeder::class);
        $this->call(PackageAndBankDummy::class);
    }
}
