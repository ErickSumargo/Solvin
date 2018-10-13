<?php

namespace App\Console\Commands;

use App\Models\Student;
use Carbon\Carbon;
use Illuminate\Console\Command;

class FreeCreditBeta extends Command
{
    /**
     * The name and signature of the console command.
     *
     * @var string
     */
    protected $signature = 'student:freecredit';

    /**
     * The console command description.
     *
     * @var string
     */
    protected $description = 'Free credit for all student';

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
//        $students = Student::all();
//        foreach ($students as $s) {
//            $carbon = new Carbon();
//            $this->comment(PHP_EOL . $carbon . 'gift credit..' . PHP_EOL);
//            $this->comment($carbon . 'student_id : ' . $s->id . PHP_EOL);
//            $s->credit += 2;
//            $s->credit_timelife = Carbon::parse($s->credit_timelife)->addDays(1);
//            $s->save();
//        }
    }
}