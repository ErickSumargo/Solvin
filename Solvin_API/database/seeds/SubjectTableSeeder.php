<?php

use Illuminate\Database\Seeder;

class SubjectTableSeeder extends Seeder
{
    /**
     * Run the database seeds.
     *
     * @return void
     */
    use \App\Classes\BaseConfig;

    public function run()
    {
        $subject = ['Matematika', 'Fisika'];
        foreach ($subject as $s) {
            DB::table($this->table_subject)->insert([
                'name' => $s,
                'created_at' => date('Y-m-d H:i:s'),
                'updated_at' => date('Y-m-d H:i:s')
            ]);
        }
    }
}