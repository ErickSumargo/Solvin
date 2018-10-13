<?php

namespace App\Console;

use App\Console\Commands\BalanceBonusJob;
use App\Console\Commands\FreeCreditBeta;
use Illuminate\Console\Scheduling\Schedule;
use App\Console\Commands\ExpiredTransaction;
use Illuminate\Foundation\Console\Kernel as ConsoleKernel;

class Kernel extends ConsoleKernel
{
    /**
     * The Artisan commands provided by your application.
     *
     * @var array
     */
    protected $commands = [
        Commands\ExpiredTransaction::class,
        Commands\FreeCreditBeta::class,
        Commands\BalanceBonusJob::class
    ];

    /**
     * Define the application's command schedule.
     *
     * @param  \Illuminate\Console\Scheduling\Schedule $schedule
     * @return void
     */
    protected function schedule(Schedule $schedule)
    {
//         $schedule->command('inspire')
//                  ->hourly();
//
//        $schedule->command(ExpiredTransaction::class)
//            ->everyMinute()
//            ->timezone('Asia/Jakarta');
//
//        $schedule->command(FreeCreditBeta::class)
//            ->daily()
//            ->timezone('Asia/Jakarta');
//
//        $schedule->command(BalanceBonusJob::class)
//            ->daily()
//            ->timezone('Asia/Jakarta');
    }
}