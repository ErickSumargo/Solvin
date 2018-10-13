<?php

namespace App\Console\Commands;

use App\Classes\BaseConfig;
use App\Models\Transaction;
use Carbon\Carbon;
use Illuminate\Console\Command;
use Illuminate\Support\Facades\DB;

class ExpiredTransaction extends Command
{
    use BaseConfig;
    /**
     * The name and signature of the console command.
     *
     * @var string
     */
    protected $signature = 'expired:transaction';

    /**
     * The console command description.
     *
     * @var string
     */
    protected $description = 'Make all transaction expired';

    /**
     * Create a new command instance.
     *
     * @return void
     */
    public function __construct()
    {
        parent::__construct();
    }

    /**
     * Execute the console command.
     *
     * @return mixed
     */
    public function handle()
    {
        $transactions = Transaction::leftJoin(
            'solvin_transaction_confirm', 'solvin_transaction.id', '=', 'solvin_transaction_confirm.transaction_id'
        )->where([
            ['status', '=', 0],
            ['clear', '=', 0],
        ])
            ->whereNull('transaction_id')// check udah confirm belum
            ->get(['solvin_transaction.*']);


        foreach ($transactions as $t) {
            $carbon = new Carbon();
            $waktu = Carbon::parse($t->payment_time);
            $this->comment(PHP_EOL . $carbon . 'checking..' . PHP_EOL);
            $this->comment($carbon . 'transcation_id : ' . $t->id . PHP_EOL);

            if ($carbon->diffInSeconds($waktu) > (60 * 60 * 24)) {
                $carbon = new Carbon();
                $this->comment($carbon . 'expired = true' . PHP_EOL);
                $t->status = 3;
                $t->clear = 0;
                $t->save();
            }
        }
    }
}