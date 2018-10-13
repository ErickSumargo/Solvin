<?php

namespace App\Http\Controllers;

use App\Models\BalanceBonus;
use App\Models\Comment;
use App\Models\Feedback;
use App\Models\FreeCredit;
use App\Models\Mentor;
use App\Models\Question;
use App\Models\RedeemBalance;
use App\Models\Solution;
use App\Models\Student;
use App\Models\Transaction;
use Carbon\Carbon;
use Illuminate\Http\Request;

class APReport extends Controller
{
    use \App\Classes\BaseConfig;

    private $now;
    private $start, $end;

    private $student, $mentor;
    private $income, $balance_redeemed;
    private $mathematics_question_pending, $mathematics_question_discuss, $mathematics_question_complete;
    private $physics_question_pending, $physics_question_discuss, $physics_question_complete;
    private $mathematics_best_solution, $mathematics_total_solution;
    private $physics_best_solution, $physics_total_solution;
    private $mathematics_comment, $physics_comment;
    private $transaction_package_1_pending, $transaction_package_1_success, $transaction_package_1_canceled;
    private $transaction_package_2_pending, $transaction_package_2_success, $transaction_package_2_canceled;
    private $transaction_package_3_pending, $transaction_package_3_success, $transaction_package_3_canceled;
    private $redeem_balance_pending, $redeem_balance_success, $redeem_balance_canceled;
    private $feedback, $free_credit, $balance_bonus;

    private $months = ['January', 'February', 'March', 'April', 'May', 'June',
        'July', 'August', 'September', 'October', 'November', 'December'];

    private function setRangeDate($month, $year)
    {
        $this->start = new Carbon('first day of ' . $this->months[$month - 1] . ' ' . $year);
        $this->start->hour = 0;
        $this->start->minute = 0;
        $this->start->second = 0;

        $this->end = new Carbon('last day of ' . $this->months[$month - 1] . ' ' . $year);
        $this->end->hour = 23;
        $this->end->minute = 59;
        $this->end->second = 59;
    }

    private function getRangeMonth($request_month, $current_month, $request_year, $current_year)
    {
        if ($current_month < $request_month) {
            $current_month += 12;
            $current_year--;
        }
        return ($current_year - $request_year) * 12 + ($current_month - $request_month);
    }

    public function getMonthlyReport(Request $request)
    {
        if ($this->isGuest()) {
            $this->json_response['message'] = 'Unauthorized';
            $this->json_response['success'] = false;
            return $this->json_result();
        }

        $request_month = $request['month'];
        $request_year = $request['year'];

        $reportList = null;
        $this->now = new Carbon();
        $range_month = $this->getRangeMonth($request_month, $this->now->month, $request_year, $this->now->year);
        if ($range_month <= 0) {
            return array('message' => 'Tidak ada laporan bulanan terbaru', 'status' => 0, 'currentMonth' => $this->now->month,
                'currentYear' => $this->now->year, 'reportList' => $reportList);
        } else {
            $i = 0;
            while ($i < $range_month) {
                $report = new \stdClass();
                $this->setRangeDate($request_month, $request_year);

                $this->getStudent();
                $this->getMentor();

                $this->getIncome();
                $this->getBalanceRedeemed();

                $this->getMathematicsQuestion();
                $this->getPhysicsQuestion();

                $this->getMathematicsSolution();
                $this->getPhysicsSolution();

                $this->getMathematicsComment();
                $this->getPhysicsComment();

                $this->getTransactionPackage1();
                $this->getTransactionPackage2();
                $this->getTransactionPackage3();

                $this->getRedeemBalance();

                $this->getFeedback();
                $this->getFreeCredit();
                $this->getBalanceBonus();

                $report->month = $this->scrypt->encrypt($this->start->month);
                $report->year = $this->scrypt->encrypt($this->start->year);

                $report->student = $this->student != null ? $this->scrypt->encrypt($this->student) : 0;
                $report->mentor = $this->mentor != null ? $this->scrypt->encrypt($this->mentor) : 0;

                $report->income = $this->income != null ? $this->scrypt->encrypt($this->income) : 0;
                $report->balanceRedeemed = $this->balance_redeemed != null ? $this->scrypt->encrypt($this->balance_redeemed) : 0;

                $report->mathematicsQuestionPending = $this->mathematics_question_pending != null ? $this->scrypt->encrypt($this->mathematics_question_pending) : 0;
                $report->mathematicsQuestionDiscuss = $this->mathematics_question_discuss != null ? $this->scrypt->encrypt($this->mathematics_question_discuss) : 0;
                $report->mathematicsQuestionComplete = $this->mathematics_question_complete != null ? $this->scrypt->encrypt($this->mathematics_question_complete) : 0;

                $report->physicsQuestionPending = $this->physics_question_pending != null ? $this->scrypt->encrypt($this->physics_question_pending) : 0;
                $report->physicsQuestionDiscuss = $this->physics_question_discuss != null ? $this->scrypt->encrypt($this->physics_question_discuss) : 0;
                $report->physicsQuestionComplete = $this->physics_question_complete != null ? $this->scrypt->encrypt($this->physics_question_complete) : 0;

                $report->mathematicsBestSolution = $this->mathematics_best_solution != null ? $this->scrypt->encrypt($this->mathematics_best_solution) : 0;
                $report->mathematicsTotalSolution = $this->mathematics_total_solution != null ? $this->scrypt->encrypt($this->mathematics_total_solution) : 0;
                $report->physicsBestSolution = $this->physics_best_solution != null ? $this->scrypt->encrypt($this->physics_best_solution) : 0;
                $report->physicsTotalSolution = $this->physics_total_solution != null ? $this->scrypt->encrypt($this->physics_total_solution) : 0;

                $report->mathematicsComment = $this->mathematics_comment != null ? $this->scrypt->encrypt($this->mathematics_comment) : 0;
                $report->physicsComment = $this->physics_comment != null ? $this->scrypt->encrypt($this->physics_comment) : 0;

                $report->transactionPackage1Pending = $this->transaction_package_1_pending != null ? $this->scrypt->encrypt($this->transaction_package_1_pending) : 0;
                $report->transactionPackage1Success = $this->transaction_package_1_success != null ? $this->scrypt->encrypt($this->transaction_package_1_success) : 0;
                $report->transactionPackage1Canceled = $this->transaction_package_1_canceled != null ? $this->scrypt->encrypt($this->transaction_package_1_canceled) : 0;

                $report->transactionPackage2Pending = $this->transaction_package_2_pending != null ? $this->scrypt->encrypt($this->transaction_package_2_pending) : 0;
                $report->transactionPackage2Success = $this->transaction_package_2_success != null ? $this->scrypt->encrypt($this->transaction_package_2_success) : 0;
                $report->transactionPackage2Canceled = $this->transaction_package_2_canceled != null ? $this->scrypt->encrypt($this->transaction_package_2_canceled) : 0;

                $report->transactionPackage3Pending = $this->transaction_package_3_pending != null ? $this->scrypt->encrypt($this->transaction_package_3_pending) : 0;
                $report->transactionPackage3Success = $this->transaction_package_3_success != null ? $this->scrypt->encrypt($this->transaction_package_3_success) : 0;
                $report->transactionPackage3Canceled = $this->transaction_package_3_canceled != null ? $this->scrypt->encrypt($this->transaction_package_3_canceled) : 0;

                $report->redeemBalancePending = $this->redeem_balance_pending != null ? $this->scrypt->encrypt($this->redeem_balance_pending) : 0;
                $report->redeemBalanceSuccess = $this->redeem_balance_success != null ? $this->scrypt->encrypt($this->redeem_balance_success) : 0;
                $report->redeemBalanceCanceled = $this->redeem_balance_canceled != null ? $this->scrypt->encrypt($this->redeem_balance_canceled) : 0;

                $report->feedback = $this->feedback != null ? $this->scrypt->encrypt($this->feedback) : 0;
                $report->freeCredit = $this->free_credit != null ? $this->scrypt->encrypt($this->free_credit) : 0;
                $report->balanceBonus = $this->balance_bonus != null ? $this->scrypt->encrypt($this->balance_bonus) : 0;
                $report = array($report);

                if ($i == 0) {
                    $reportList = $report;
                } else {
                    $reportList = array_merge($reportList, $report);
                }
                if ($request_month == 12) {
                    $request_month = 1;
                    $request_year++;
                } else {
                    $request_month++;
                }
                $i++;
            }
            return array('message' => 'success', 'status' => 1, 'currentMonth' => $this->now->month,
                'currentYear' => $this->now->year, 'reportList' => $reportList);
        }
    }

    private function getStudent()
    {
        $this->student = Student::whereBetween('created_at', array($this->start, $this->end))
            ->count();
    }

    private function getMentor()
    {
        $this->mentor = Mentor::whereBetween('created_at', array($this->start, $this->end))
            ->count();
    }

    private function getIncome()
    {
        $nominal = 0;
        $unique_code = 0;

        $transactions = Transaction::with(
            array(
                'Package_' => function ($query) {
                    $query->select('id', 'nominal');
                }
            )
        )
            ->select('id', 'package_id', 'unique_code')
            ->where('status', '=', 1)
            ->whereBetween('created_at', array($this->start, $this->end))
            ->get();

        foreach ($transactions as $transaction) {
            $nominal += $transaction->package_->nominal;
            $unique_code += $transaction->unique_code;
        }

        $this->income = $nominal + $unique_code;
    }

    private function getBalanceRedeemed()
    {
        $this->balance_redeemed = RedeemBalance::where('status', '=', 1)
            ->whereBetween('created_at', array($this->start, $this->end))
            ->sum('balance');
    }

    private function getMathematicsQuestion()
    {
        $this->mathematics_question_pending = Question::join(
            $this->table_material, $this->table_question . '.material_id', '=', $this->table_material . '.id')
            ->where([
                [$this->table_question . '.status', '=', 0],
                [$this->table_material . '.subject_id', '=', 1]
            ])
            ->whereBetween($this->table_question . '.created_at', array($this->start, $this->end))
            ->count();

        $this->mathematics_question_discuss = Question::join(
            $this->table_material, $this->table_question . '.material_id', '=', $this->table_material . '.id')
            ->where([
                [$this->table_question . '.status', '=', 1],
                [$this->table_material . '.subject_id', '=', 1]
            ])
            ->whereBetween($this->table_question . '.created_at', array($this->start, $this->end))
            ->count();

        $this->mathematics_question_complete = Question::join(
            $this->table_material, $this->table_question . '.material_id', '=', $this->table_material . '.id')
            ->where([
                [$this->table_question . '.status', '=', 2],
                [$this->table_material . '.subject_id', '=', 1]
            ])
            ->whereBetween($this->table_question . '.created_at', array($this->start, $this->end))
            ->count();
    }

    private function getPhysicsQuestion()
    {
        $this->physics_question_pending = Question::join(
            $this->table_material, $this->table_question . '.material_id', '=', $this->table_material . '.id')
            ->where([
                [$this->table_question . '.status', '=', 0],
                [$this->table_material . '.subject_id', '=', 2]
            ])
            ->whereBetween($this->table_question . '.created_at', array($this->start, $this->end))
            ->count();

        $this->physics_question_discuss = Question::join(
            $this->table_material, $this->table_question . '.material_id', '=', $this->table_material . '.id')
            ->where([
                [$this->table_question . '.status', '=', 1],
                [$this->table_material . '.subject_id', '=', 2]
            ])
            ->whereBetween($this->table_question . '.created_at', array($this->start, $this->end))
            ->count();

        $this->physics_question_complete = Question::join(
            $this->table_material, $this->table_question . '.material_id', '=', $this->table_material . '.id')
            ->where([
                [$this->table_question . '.status', '=', 2],
                [$this->table_material . '.subject_id', '=', 2]
            ])
            ->whereBetween($this->table_question . '.created_at', array($this->start, $this->end))
            ->count();
    }

    private function getMathematicsSolution()
    {
        $this->mathematics_best_solution = Solution::join(
            $this->table_question, $this->table_solution . '.question_id', '=', $this->table_question . '.id')
            ->join(
                $this->table_material, $this->table_question . '.material_id', '=', $this->table_material . '.id')
            ->where([
                [$this->table_solution . '.best', '=', 1],
                [$this->table_material . '.subject_id', '=', 1]
            ])
            ->whereBetween($this->table_question . '.created_at', array($this->start, $this->end))
            ->count();

        $this->mathematics_total_solution = Solution::join(
            $this->table_question, $this->table_solution . '.question_id', '=', $this->table_question . '.id')
            ->join(
                $this->table_material, $this->table_question . '.material_id', '=', $this->table_material . '.id')
            ->where($this->table_material . '.subject_id', '=', 1)
            ->whereBetween($this->table_question . '.created_at', array($this->start, $this->end))
            ->count();
    }

    private function getPhysicsSolution()
    {
        $this->physics_best_solution = Solution::join(
            $this->table_question, $this->table_solution . '.question_id', '=', $this->table_question . '.id')
            ->join(
                $this->table_material, $this->table_question . '.material_id', '=', $this->table_material . '.id')
            ->where([
                [$this->table_solution . '.best', '=', 1],
                [$this->table_material . '.subject_id', '=', 2]
            ])
            ->whereBetween($this->table_question . '.created_at', array($this->start, $this->end))
            ->count();

        $this->physics_total_solution = Solution::join(
            $this->table_question, $this->table_solution . '.question_id', '=', $this->table_question . '.id')
            ->join(
                $this->table_material, $this->table_question . '.material_id', '=', $this->table_material . '.id')
            ->where($this->table_material . '.subject_id', '=', 2)
            ->whereBetween($this->table_question . '.created_at', array($this->start, $this->end))
            ->count();
    }

    private function getMathematicsComment()
    {
        $this->mathematics_comment = Comment::join(
            $this->table_question, $this->table_comment . '.question_id', '=', $this->table_question . '.id')
            ->join(
                $this->table_material, $this->table_question . '.material_id', '=', $this->table_material . '.id')
            ->where($this->table_material . '.subject_id', '=', 1)
            ->whereBetween($this->table_comment . '.created_at', array($this->start, $this->end))
            ->count();
    }

    private function getPhysicsComment()
    {
        $this->physics_comment = Comment::join(
            $this->table_question, $this->table_comment . '.question_id', '=', $this->table_question . '.id')
            ->join(
                $this->table_material, $this->table_question . '.material_id', '=', $this->table_material . '.id')
            ->where($this->table_material . '.subject_id', '=', 2)
            ->whereBetween($this->table_question . '.created_at', array($this->start, $this->end))
            ->count();
    }

    private function getTransactionPackage1()
    {
        $this->transaction_package_1_pending = Transaction::where([
            ['package_id', '=', 1],
            ['status', '=', 0]
        ])
            ->whereBetween('created_at', array($this->start, $this->end))
            ->count();

        $this->transaction_package_1_success = Transaction::where([
            ['package_id', '=', 1],
            ['status', '=', 1]
        ])
            ->whereBetween('created_at', array($this->start, $this->end))
            ->count();

        $this->transaction_package_1_canceled = Transaction::where([
            ['package_id', '=', 1],
            ['status', '=', 2]
        ])
            ->whereBetween('created_at', array($this->start, $this->end))
            ->count();
    }

    private function getTransactionPackage2()
    {
        $this->transaction_package_2_pending = Transaction::where([
            ['package_id', '=', 2],
            ['status', '=', 0]
        ])
            ->whereBetween('created_at', array($this->start, $this->end))
            ->count();

        $this->transaction_package_2_success = Transaction::where([
            ['package_id', '=', 2],
            ['status', '=', 1]
        ])
            ->whereBetween('created_at', array($this->start, $this->end))
            ->count();

        $this->transaction_package_2_canceled = Transaction::where([
            ['package_id', '=', 2],
            ['status', '=', 2]
        ])
            ->whereBetween('created_at', array($this->start, $this->end))
            ->count();
    }

    private function getTransactionPackage3()
    {
        $this->transaction_package_3_pending = Transaction::where([
            ['package_id', '=', 3],
            ['status', '=', 0]
        ])
            ->whereBetween('created_at', array($this->start, $this->end))
            ->count();

        $this->transaction_package_3_success = Transaction::where([
            ['package_id', '=', 3],
            ['status', '=', 1]
        ])
            ->whereBetween('created_at', array($this->start, $this->end))
            ->count();

        $this->transaction_package_3_canceled = Transaction::where([
            ['package_id', '=', 3],
            ['status', '=', 2]
        ])
            ->whereBetween('created_at', array($this->start, $this->end))
            ->count();
    }

    private function getRedeemBalance()
    {
        $this->redeem_balance_pending = RedeemBalance::where('status', '=', 0)
            ->whereBetween('created_at', array($this->start, $this->end))
            ->count();

        $this->redeem_balance_success = RedeemBalance::where('status', '=', 1)
            ->whereBetween('created_at', array($this->start, $this->end))
            ->count();

        $this->redeem_balance_canceled = RedeemBalance::where('status', '=', 2)
            ->whereBetween('created_at', array($this->start, $this->end))
            ->count();
    }

    private function getFeedback()
    {
        $this->feedback = Feedback::whereBetween('created_at', array($this->start, $this->end))
            ->count();
    }

    private function getFreeCredit()
    {
        $this->free_credit = FreeCredit::whereBetween('created_at', array($this->start, $this->end))
            ->count();
    }

    private function getBalanceBonus()
    {
        $this->balance_bonus = BalanceBonus::whereBetween('created_at', array($this->start, $this->end))
            ->sum('balance');
    }
}