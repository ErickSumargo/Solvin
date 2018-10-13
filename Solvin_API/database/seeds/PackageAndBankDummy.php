<?php

use Illuminate\Database\Seeder;

class PackageAndBankDummy extends Seeder
{
    /**
     * Run the database seeds.
     *
     * @return void
     */
    use \App\Classes\BaseConfig;

    public function run()
    {
        $packages = [
            [
                'nominal' => 20000,
                'active' => 7,
                'credit' => 10
            ],
            [
                'nominal' => 50000,
                'active' => 14,
                'credit' => 25
            ],
            [
                'nominal' => 80000,
                'active' => 30,
                'credit' => 40
            ]
        ];
        $banks = [
            [
                'name' => 'BCA',
                'account_owner' => 'Erick Sumargo',
                'account_number' => '8430202406'
            ],
            [
                'name' => 'BNI',
                'account_owner' => 'Erick Sumargo',
                'account_number' => '503602787'
            ],
        ];

        foreach ($packages as $p) {
            DB::table($this->table_package)->insert([
                'nominal' => $p['nominal'],
                'active' => $p['active'],
                'credit' => $p['credit'],
                'created_at' => date('Y-m-d H:i:s'),
                'updated_at' => date('Y-m-d H:i:s')
            ]);
        }

        foreach ($banks as $b) {
            DB::table($this->table_bank)->insert([
                'name' => $b['name'],
                'account_owner' => $b['account_owner'],
                'account_number' => $b['account_number'],
                'created_at' => date('Y-m-d H:i:s'),
                'updated_at' => date('Y-m-d H:i:s')
            ]);
        }
    }
}