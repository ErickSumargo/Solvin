<?php

namespace App\Console\Commands;

use App\Classes\Firebase;
use App\Models\BalanceBonus;
use App\Models\Mentor;
use App\Models\Solution;
use Carbon\Carbon;
use Illuminate\Console\Command;

class BalanceBonusJob extends Command
{
    use \App\Classes\BaseConfig;
    /**
     * The name and signature of the console command.
     *
     * @var string
     */
    protected $signature = 'balance:bonus';

    /**
     * The console command description.
     *
     * @var string
     */
    protected $description = 'Give mentor bonus balance everyday';

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
        $start = Carbon::yesterday();
        $start->hour = 0;
        $start->minute = 0;
        $start->second = 0;

        $end = Carbon::yesterday();
        $end->hour = 23;
        $end->minute = 59;
        $end->second = 59;

        $mentors = Mentor::select('id')->get();
        foreach ($mentors as $mentor) {
            $total = Solution::where([
                ['mentor_id', '=', $mentor->id],
                ['best', '=', '1']
            ])
                ->join($this->table_balance, $this->table_solution . '.id', '=', $this->table_balance . '.solution_id')
                ->whereBetween($this->table_balance . '.created_at', array($start, $end))
                ->count();
            if ($total >= 5) {
                $balanceBonus = new BalanceBonus();
                $balanceBonus->mentor_id = $mentor->id;
                $balanceBonus->balance = ($total / 2) * 1000;
                $balanceBonus->save();

                $this->pushNotificationBonusBalance($balanceBonus, $total, $mentor->id);
            }
        }
    }

    private function pushNotificationBonusBalance($balance_bonus, $count, $mentor_id)
    {
        $yesterday = Carbon::yesterday();
        $_content = "Solvin memberikan bonus saldo sebesar Rp. " . number_format($balance_bonus->balance, 0, '', '.')
            . " atas pencapaian " . $count . " buah solusi terbaik pada tanggal " . $yesterday->day . ' ' . $yesterday->format('F') . ' ' . $yesterday->year;

        Firebase::sendPushNotification([
            "type" => "other",
            "auth_id" => $mentor_id,
            "auth_type" => "mentor",
            "sender_id" => 1,
            "sender_type" => 'admin',
            "subject_id" => $balance_bonus->id,
            "subject_type" => "balance_bonus",
            "content" => $_content], $_content);
    }
}