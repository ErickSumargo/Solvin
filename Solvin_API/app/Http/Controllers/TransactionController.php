<?php

namespace App\Http\Controllers;

use App\Classes\BaseConfig;
use App\Classes\Firebase;
use App\Models\Balance;
use App\Models\BalanceBonus;
use App\Models\Bank;
use App\Models\Mentor;
use App\Models\MobileNetwork;
use App\Models\Package;
use App\Models\RedeemBalance;
use App\Models\Student;
use App\Models\Solution;
use App\Models\Transaction;
use App\Models\TransactionConfirm;
use Carbon\Carbon;
use Illuminate\Http\Request;

class TransactionController extends Controller
{
    use BaseConfig;
    private $now, $start, $end;
    private $months = ['January', 'February', 'March', 'April', 'May', 'June',
        'July', 'August', 'September', 'October', 'November', 'December'];

    #param (package_id,bank_id,unique_code)
    public function purchase(Request $req)
    {
        if (!$this->isStudent()) {
            return $this->json_result();
        }
        $package = Package::find($this->scrypt->decrypt($req['package_id']));
        if ($package == null) {
            $this->json_response['message'] = 'Error paket not found';
            $this->json_response['success'] = false;
            return $this->json_result();
        }
        $bank = Bank::find($this->scrypt->decrypt($req['bank_id']));
        $mobileNetwork = MobileNetwork::find($this->scrypt->decrypt($req['mobile_network_id']));
        if ($bank == null && $mobileNetwork == null) {
            $this->json_response['message'] = 'Error media transaction not found';
            $this->json_response['success'] = false;
            return $this->json_result();
        }

        $lastTransaction = Transaction::where('student_id', '=', $this->auth->id)
            ->where('status', '=', '0')->first();

        $isCanTransaction = count($lastTransaction) == 0 ? true : false;

        $resultData = new \stdClass();
        if ($isCanTransaction) {
            $transaction = new Transaction();
            $transaction->student_id = $this->auth->id;
            $transaction->package_id = $package->id;
            if ($bank != null) {
                $transaction->bank_id = $bank->id;
                $transaction->mobile_network_id = 0;
            } else if ($mobileNetwork != null) {
                $transaction->bank_id = 0;
                $transaction->mobile_network_id = $mobileNetwork->id;
            }
            $transaction->status = 0;
            $transaction->payment_time = date('Y-m-d H:i:s', strtotime("+1 day"));;
            $transaction->unique_code = $this->scrypt->decrypt($req['unique_code']);
            $transaction->save();

//            $this->pushNotificationPurchasePackage($transaction, $package);

            $transaction->package_id = $this->scrypt->encrypt($transaction->package_id);
            $transaction->bank_id = $this->scrypt->encrypt($transaction->bank_id);
            $transaction->mobile_network_id = $this->scrypt->encrypt($transaction->mobile_network_id);
            $transaction->unique_code = $this->scrypt->encrypt($transaction->unique_code);
            $transaction->status = $this->scrypt->encrypt($transaction->status);
            $resultData->transaction = $transaction;
        } else {
            $this->json_response['success'] = false;
            $this->json_response['message'] = 'Ada transaksi yg belum selesai';

            $lastTransaction->package_id = $this->scrypt->encrypt($lastTransaction->package_id);
            $lastTransaction->bank_id = $this->scrypt->encrypt($lastTransaction->bank_id);
            $lastTransaction->mobile_network_id = $this->scrypt->encrypt($lastTransaction->mobile_network_id);
            $lastTransaction->unique_code = $this->scrypt->encrypt($lastTransaction->unique_code);
            $lastTransaction->status = $this->scrypt->encrypt($lastTransaction->status);

            $resultData->transaction = $lastTransaction;
        }

        $this->json_response["data"] = $resultData;
        return $this->json_response;
    }

    #param (transaction_id,bank_name,bank_name_other,bank_account_owner,*image)
    public function confirmPurchase(Request $req)
    {
        if (!$this->isStudent()) {
            return $this->json_result();
        }
        $transaction = Transaction::find($req['transaction_id']);
        if ($transaction == null) {
            $this->json_response['message'] = 'Error transaction not found';
            $this->json_response['success'] = false;
            return $this->json_result();
        }
        if (!isset($req['bank_account_owner']) && !isset($req['mobile_number_owner'])) {
            $this->json_response['message'] = 'required media transfer owner';
            $this->json_response['success'] = false;
            return $this->json_result();
        }
        if (!isset($req['bank_name'])) {
            $this->json_response['message'] = 'required bank_name';
            $this->json_response['success'] = false;
            return $this->json_result();
        }
        $bank_account_owner = $req['bank_account_owner'];
        $bank_name_other = $req['bank_name_other'];
        $bank_name = $req['bank_name'];
        $mobile_number_owner = $req['mobile_number_owner'];

        $transactionConfirm = TransactionConfirm::where('transaction_id', '=', $transaction->id)->first();
        if ($transactionConfirm == null) {
            $transactionConfirm = new TransactionConfirm();
        }
        $transactionConfirm->transaction_id = $transaction->id;
        $transactionConfirm->bank_account_owner = $bank_account_owner;
        $transactionConfirm->bank_name = $bank_name;
        $transactionConfirm->bank_name_other = $bank_name_other;
        $transactionConfirm->mobile_number_owner = $mobile_number_owner;

        $tmp_file = $transactionConfirm->image;
        if ($req->file('image') != null && !empty($req->file("image")->getClientOriginalName())) {
            if ($this->isImageValid($req->file('image'))) {
                $transactionConfirm->image = $this->getImageName($req->file('image'), 'transactionconfirm', $transaction->id);
            } else {
                return $this->json_result();
            }
        } else {
            $transactionConfirm->image = null;
        }
        $this->deleteImage($tmp_file, 'transactionconfirm');

        $package = Package::find($transaction->package_id);
        $transactionConfirm->save();

        $student = Student::find($transaction->student_id);
        $this->pushNotificationConfirmTransaction($transaction, $transactionConfirm, $package, $student);
        return $this->json_result();
    }

    public function checkLastPurchase()
    {
        if (!$this->isStudent()) {
            return $this->json_result();
        }
        $transaction = Transaction::where('student_id', '=', $this->auth->id)
            ->where('clear', '=', 0)->first();

        if ($transaction != null) {
            $payment_created = date("Y-m-d H:i:s", strtotime($transaction->created_at));
            $payment_now = date("Y-m-d H:i:s");
            $selisih = (strtotime($payment_now) - strtotime($payment_created) - 86400);
            $isActive = $selisih > 0 ? false : true;
            if (!$isActive) {
                $transaction->status = 2;
                $transaction->save();
            }

            $transaction->package_id = $this->scrypt->encrypt($transaction->package_id);
            $transaction->bank_id = $this->scrypt->encrypt($transaction->bank_id);
            $transaction->mobile_network_id = $this->scrypt->encrypt($transaction->mobile_network_id);
            $transaction->unique_code = $this->scrypt->encrypt($transaction->unique_code);
            $transaction->status = $this->scrypt->encrypt($transaction->status);

            $resultData = new \stdClass();
            $resultData->transaction = $transaction;

            $this->json_response['success'] = false;
            $this->json_response['data'] = $resultData;
            $this->json_response['message'] = 'Ada transaksi yang belum selesai';
        } else {
            $this->json_response['message'] = 'Anda bisa melanjutkan membeli paket';
        }
        return $this->json_result();
    }

    public function detailPurchase(Request $req)
    {
        if (!$this->isStudent()) {
            return $this->json_result();
        }
        if (!isset($req['id'])) {
            $this->json_response['message'] = "id is required";
            $this->json_response['success'] = false;
            return $this->json_result();
        }
        $transaction = Transaction::where('student_id', '=', $this->auth->id)
            ->where('id', '=', $req['id'])->first();

        if ($transaction != null) {
            $resultData = new \stdClass();

            $transaction->package_id = $this->scrypt->encrypt($transaction->package_id);
            $transaction->bank_id = $this->scrypt->encrypt($transaction->bank_id);
            $transaction->mobile_network_id = $this->scrypt->encrypt($transaction->mobile_network_id);
            $transaction->unique_code = $this->scrypt->encrypt($transaction->unique_code);
            $transaction->status = $this->scrypt->encrypt($transaction->status);
            $resultData->transaction = $transaction;

            $this->json_response['success'] = false;
            $this->json_response['data'] = $resultData;
            $this->json_response['message'] = 'Ada transaksi yang belum selesai';
        } else {
            $this->json_response['message'] = 'Anda bisa melanjutkan membeli paket';
        }
        return $this->json_result();
    }

    #param (transaction_id)
    public function actionPurchase(Request $req)
    {
        if (!$this->isStudent()) {
            return $this->json_result();
        }
        $transaction = Transaction::find($req['transaction_id']);
        if ($transaction == null) {
            $this->json_response['success'] = false;
            $this->json_response['message'] = 'transaksi tidak ditemukan';
            return $this->json_result();
        }
        if (!isset($req['action'])) {
            $this->json_response['success'] = false;
            $this->json_response['message'] = 'action required';
            return $this->json_result();
        }
        if ($req['action'] == 'abort') {
            $transaction->status = 2;
        }
        $transaction->clear = 1;
        $transaction->save();
        return $this->json_response;
    }

    public function historyPurchase($last_id = 0)
    {
        if (!$this->isStudent()) {
            return $this->json_result();
        }

        if ($last_id == 0) {
            $last_id = Transaction::max('id') + 1;
        }
        $transaction = Transaction::with(
            array(
                'Package_' => function ($query) {
                    $query->select(["id", "nominal", "active", "credit"]);
                }
            ))
            ->where([
                ['id', '<', $last_id],
                ['student_id', '=', $this->auth->id]
            ])
            ->orderBy('created_at', 'desc')
            ->limit($this->item_perpage)
            ->get();

        foreach ($transaction as $t) {
            $t->status = $this->scrypt->encrypt($t->status);
            $t->package_id = $this->scrypt->encrypt($t->package_id);
            $t->unique_code = $this->scrypt->encrypt($t->unique_code);
        }

        $resultData = new \stdClass();
        $resultData->transactions = $transaction;
        $this->json_response['data'] = $resultData;

        return $this->json_result();
    }

    #param (security_code,balance)
    public function redeem(Request $req)
    {
        if (!$this->isMentor()) {
            return $this->json_result();
        }

        if (!isset($req['security_code'])) {
            $this->json_response['success'] = false;
            $this->json_response['message'] = 'security_code required';
            return $this->json_result();
        }

        if (!isset($req['balance'])) {
            $this->json_response['success'] = false;
            $this->json_response['message'] = 'balance required';
            return $this->json_result();
        }
        if ($this->scrypt->decrypt($req['balance']) < 20000 && $this->scrypt->decrypt($req['balance']) >= 1000000) {
            $this->json_response['success'] = false;
            $this->json_response['message'] = 'balance invalid format';
            return $this->json_result();
        }

        //  Check if request exceeds current balance
        $totalBalance = Solution::where([
            ['mentor_id', '=', $this->auth->id],
            ['best', '=', '1']
        ])->join($this->table_balance, $this->table_solution . '.id', '=', $this->table_balance . '.solution_id')
            ->get(['deal_payment'])
            ->sum('deal_payment');
        if ($totalBalance == null) {
            $totalBalance = 0;
        }

        $balanceBonus = BalanceBonus::where('mentor_id', '=', $this->auth->id)
            ->get(['balance'])
            ->sum('balance');
        if ($balanceBonus == null) {
            $balanceBonus = 0;
        }

        $balanceRedeemed = RedeemBalance::where([
            ['mentor_id', '=', $this->auth->id],
            ['status', '=', 1]
        ])
            ->sum('balance');
        if ($balanceRedeemed == null) {
            $balanceRedeemed = 0;
        }

        if ($this->scrypt->decrypt($req['balance']) > ($totalBalance + $balanceBonus - $balanceRedeemed)) {
            $this->json_response['success'] = false;
            $this->json_response['message'] = 'request exceeds current balance';
            return $this->json_result();
        }

        $mentor = Mentor::where([
            ['id', '=', $this->auth->id],
            ['security_code', '=', $this->scrypt->decrypt($req['security_code'])]
        ])
            ->first();
        if ($mentor != null) {
            $redeem = new RedeemBalance();
            $redeem->mentor_id = $this->auth->id;
            $redeem->balance = $this->scrypt->decrypt($req['balance']);
            $redeem->save();
            $strId = str_pad($redeem->id, 4, "0", STR_PAD_LEFT);
            $redeem->redeem_code = 'SOL.' . $strId;
            $redeem->save();

            $this->pushNotificationRedeemBalance($redeem);

            $this->json_response['success'] = true;
            $this->json_response['message'] = 'Permohonan penebusan saldo telah diajukan';
        } else {
            $this->json_response['success'] = false;
            $this->json_response['message'] = 'Kode pengaman anda salah. Silahkan coba lagi';
        }
        return $this->json_result();
    }

    #param (redeem_id,action)
    public function acceptRedeem(Request $req)
    {
        $redeem = RedeemBalance::find($req['redeem_id']);
        if ($redeem == null) {
            $this->json_response['success'] = false;
            $this->json_response['message'] = 'permintaan redeem tidak ditemukan';
            return $this->json_result();
        }
        if (!isset($req['action'])) {
            $this->json_response['success'] = false;
            $this->json_response['message'] = 'action required';
            return $this->json_result();
        }
        $action = $req['action'];

        if ($action == 'accept') {
            $redeem->date_agreement = date('Y-m-d H:i:s');
            $redeem->status = 1;

            $balances = Balance::where([
                ['status', '=', 0]
            ])->join($this->table_solution, $this->table_balance . '.solution_id', '=', $this->table_solution . '.id')
                ->where([
                    ['mentor_id', '=', $redeem->mentor_id],
                    ['best', '=', '1']
                ])->get(['deal_payment', $this->table_balance . '.id']);
            $balance = 0;
            foreach ($balances as $b) {
                if ($balance != $redeem->balance) {
                    $balance += $b->deal_payment;
                    $b->status = 1;
                    $b->save();
                } else {
                    break;
                }
            }
            $this->json_response['message'] = 'berhasil redeem';
        } else if ($action == 'abort') {
            $redeem->status = 2;
            $this->json_response['message'] = 'permohonan redeem dibatalkan';
        }
        $redeem->save();
        return $this->json_result();
    }

    public function historyRedeem($last_id = 0)
    {
        if (!$this->isMentor()) {
            return $this->json_result();
        }

        if ($last_id == 0) {
            $last_id = RedeemBalance::max('id') + 1;
        }
        $redeem_balance = RedeemBalance::where([
            ['id', '<', $last_id],
            ['mentor_id', '=', $this->auth->id]
        ])
            ->orderBy('created_at', 'desc')
            ->limit($this->item_perpage)
            ->get();

        foreach ($redeem_balance as $rb) {
            $rb->balance = $this->scrypt->encrypt($rb->balance);
            $rb->status = $this->scrypt->encrypt($rb->status);
            $rb->date_agreement = $this->scrypt->encrypt($rb->date_agreement);
        }

        $resultData = new \stdClass();
        $resultData->redeemBalance = $redeem_balance;
        $this->json_response['data'] = $resultData;

        return $this->json_result();
    }

    public function summaryRedeem()
    {
        $resultData = new \stdClass();

        $totalBalance = Solution::where([
            ['mentor_id', '=', $this->auth->id],
            ['best', '=', '1']
        ])->join($this->table_balance, $this->table_solution . '.id', '=', $this->table_balance . '.solution_id')
            ->get(['deal_payment'])
            ->sum('deal_payment');
        if ($totalBalance == null) {
            $totalBalance = 0;
        }

        $balanceBonus = BalanceBonus::where('mentor_id', '=', $this->auth->id)
            ->get(['balance'])
            ->sum('balance');
        if ($balanceBonus == null) {
            $balanceBonus = 0;
        }

        $balanceRedeemed = RedeemBalance::where([
            ['mentor_id', '=', $this->auth->id],
            ['status', '=', 1]
        ])
            ->sum('balance');
        if ($balanceRedeemed == null) {
            $balanceRedeemed = 0;
        }

        $resultData->totalBalance = $this->scrypt->encrypt($totalBalance);
        $resultData->balanceBonus = $this->scrypt->encrypt($balanceBonus);
        $resultData->balanceRedeemed = $this->scrypt->encrypt($balanceRedeemed);
        $this->json_response['data'] = $resultData;

        return $this->json_response;
    }

    public function monthlyBalance($page = 1)
    {
        if (!$this->isMentor()) {
            return $this->json_result();
        }

        $monthlyBalanceList = array();
        $join_date = Mentor::where('id', '=', $this->auth->id)
            ->select('created_at')
            ->first();

        $this->now = new Carbon();
        $month = $this->now->month;
        $year = $this->now->year;

        $offset = ($page - 1) * $this->item_perpage;
        for ($i = 0; $i < $offset; $i++) {
            if ($month - 1 == 0) {
                $month = 12;
                $year--;
            } else {
                $month--;
            }
            if ($month <= $join_date->created_at->month && $year <= $join_date->created_at->year) {
                break;
            }
        }
        if ($month <= $join_date->created_at->month && $year <= $join_date->created_at->year) {
            $this->json_response['data'] = $monthlyBalanceList;
            return $this->json_result();
        }

        $limit = $offset + $this->item_perpage;
        while ($offset < $limit) {
            $offset++;
            if ($month - 1 == 0) {
                $month = 12;
                $year--;
            } else {
                $month -= 1;
            }
            $this->setRangeDate($month, $year);

            $best_vote = Solution::where([
                ['mentor_id', '=', $this->auth->id],
                ['best', '=', 1]
            ])
                ->whereBetween($this->table_solution . '.created_at', array($this->start, $this->end))
                ->count();
            if ($best_vote == null) {
                $best_vote = 0;
            }

            $balance = Solution::where([
                ['mentor_id', '=', $this->auth->id],
                ['best', '=', 1]
            ])
                ->join($this->table_balance, $this->table_solution . '.id', '=', $this->table_balance . '.solution_id')
                ->whereBetween($this->table_solution . '.created_at', array($this->start, $this->end))
                ->get(['deal_payment'])
                ->sum('deal_payment');
            if ($balance == null) {
                $balance = 0;
            }

            $balance_bonus = BalanceBonus::where('mentor_id', '=', $this->auth->id)
                ->whereBetween('created_at', array($this->start, $this->end))
                ->get(['balance'])
                ->sum('balance');
            if ($balance_bonus == null) {
                $balance_bonus = 0;
            }

            $monthlyBalance = new \stdClass();
            $monthlyBalance->dateStart = $this->start->format('d F Y');
            $monthlyBalance->dateEnd = $this->end->format('d F Y');

            $monthlyBalance->bestVote = $this->scrypt->encrypt($best_vote);
            $monthlyBalance->balance = $this->scrypt->encrypt($balance);
            $monthlyBalance->balanceBonus = $this->scrypt->encrypt($balance_bonus);
            $monthlyBalance = array($monthlyBalance);
            $monthlyBalanceList = array_merge($monthlyBalanceList, $monthlyBalance);

            if ($month <= $join_date->created_at->month && $year <= $join_date->created_at->year) {
                break;
            }
        }

        $this->json_response['data'] = $monthlyBalanceList;
        return $this->json_result();
    }

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

    private function pushNotificationPurchasePackage($transaction, $package)
    {
        $_content = Student::find($transaction->student_id)->name . " mengajukan permohonan pembelian paket pertanyaan seharga Rp. " .
            number_format($package->nominal + $transaction->unique_code, 0, '', '.') . ',-';

        Firebase::sendPushNotification([
            "type" => "transaction",
            "auth_id" => 1,
            "auth_type" => "admin",
            "sender_id" => $this->auth->id,
            "sender_type" => 'student',
            "subject_id" => $transaction->id,
            "subject_type" => "transaction",
            "content" => "%s mengajukan permohonan pembelian paket pertanyaan seharga Rp. " .
                number_format($package->nominal + $transaction->unique_code, 0, '', '.') . ',-'], $_content, 0, 1);
    }

    private function pushNotificationConfirmTransaction($transaction, $transaction_confirm, $package, $student)
    {
        $_content = Student::find($transaction->student_id)->name . " mengirimkan info konfirmasi pembayaran atas paket seharga Rp. " .
            number_format($package->nominal + $transaction->unique_code, 0, '', '.') . ',-';

        Firebase::sendPushNotification([
            "type" => "transaction_confirm",
            "auth_id" => 1,
            "auth_type" => "admin",
            "sender_id" => $this->auth->id,
            "sender_photo" => $student->photo,
            "sender_type" => 'student',
            "subject_id" => $transaction_confirm->id,
            "subject_type" => "transaction",
            "content" => "%s mengirimkan info konfirmasi pembayaran atas paket seharga Rp. " .
                number_format($package->nominal + $transaction->unique_code, 0, '', '.') . ',-'], $_content, 0, 1);
    }

    private function pushNotificationRedeemBalance($redeem_balance)
    {
        $_content = Mentor::find($redeem_balance->mentor_id)->name . " mengajukan permohonan penebusan saldo sebesar Rp. " .
            number_format($redeem_balance->balance, 0, '', '.') . ',-';

        Firebase::sendPushNotification([
            "type" => "redeem_balance",
            "auth_id" => 1,
            "auth_type" => "admin",
            "sender_id" => $this->auth->id,
            "sender_type" => 'mentor',
            "subject_id" => $redeem_balance->id,
            "subject_type" => "redeem_balance",
            "content" => "%s mengajukan permohonan penebusan saldo sebesar Rp. " .
                number_format($redeem_balance->balance, 0, '', '.') . ',-'], $_content, 0, 1);
    }
}