package id.solvinap.dev.realm.table;

import io.realm.RealmObject;

/**
 * Created by Erick Sumargo on 3/21/2017.
 */

public class TableReport extends RealmObject {
    private int month, year,
            student, mentor,
            income, balanceRedeemed,
            mathematicsQuestionPending, mathematicsQuestionDiscuss, mathematicsQuestionComplete,
            physicsQuestionPending, physicsQuestionDiscuss, physicsQuestionComplete,
            mathematicsBestSolution, mathematicsTotalSolution,
            physicsBestSolution, physicsTotalSolution,
            mathematicsComment, physicsComment,
            transactionPackage1Pending, transactionPackage1Success, transactionPackage1Canceled,
            transactionPackage2Pending, transactionPackage2Success, transactionPackage2Canceled,
            transactionPackage3Pending, transactionPackage3Success, transactionPackage3Canceled,
            redeemBalancePending, redeemBalanceSuccess, redeemBalanceCanceled,
            feedback, freeCredit, balanceBonus;

    public void setMonth(int month) {
        this.month = month;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setStudent(int student) {
        this.student = student;
    }

    public void setMentor(int mentor) {
        this.mentor = mentor;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public int getStudent() {
        return student;
    }

    public int getMentor() {
        return mentor;
    }

    public int getIncome() {
        return income;
    }

    public void setIncome(int income) {
        this.income = income;
    }

    public int getBalanceRedeemed() {
        return balanceRedeemed;
    }

    public void setBalanceRedeemed(int balanceRedeemed) {
        this.balanceRedeemed = balanceRedeemed;
    }

    public int getMathematicsQuestionPending() {
        return mathematicsQuestionPending;
    }

    public void setMathematicsQuestionPending(int mathematicsQuestionPending) {
        this.mathematicsQuestionPending = mathematicsQuestionPending;
    }

    public int getMathematicsQuestionDiscuss() {
        return mathematicsQuestionDiscuss;
    }

    public void setMathematicsQuestionDiscuss(int mathematicsQuestionDiscuss) {
        this.mathematicsQuestionDiscuss = mathematicsQuestionDiscuss;
    }

    public int getMathematicsQuestionComplete() {
        return mathematicsQuestionComplete;
    }

    public void setMathematicsQuestionComplete(int mathematicsQuestionComplete) {
        this.mathematicsQuestionComplete = mathematicsQuestionComplete;
    }

    public int getPhysicsQuestionPending() {
        return physicsQuestionPending;
    }

    public void setPhysicsQuestionPending(int physicsQuestionPending) {
        this.physicsQuestionPending = physicsQuestionPending;
    }

    public int getPhysicsQuestionDiscuss() {
        return physicsQuestionDiscuss;
    }

    public void setPhysicsQuestionDiscuss(int physicsQuestionDiscuss) {
        this.physicsQuestionDiscuss = physicsQuestionDiscuss;
    }

    public int getPhysicsQuestionComplete() {
        return physicsQuestionComplete;
    }

    public void setPhysicsQuestionComplete(int physicsQuestionComplete) {
        this.physicsQuestionComplete = physicsQuestionComplete;
    }

    public int getMathematicsBestSolution() {
        return mathematicsBestSolution;
    }

    public void setMathematicsBestSolution(int mathematicsBestSolution) {
        this.mathematicsBestSolution = mathematicsBestSolution;
    }

    public int getMathematicsTotalSolution() {
        return mathematicsTotalSolution;
    }

    public void setMathematicsTotalSolution(int mathematicsTotalSolution) {
        this.mathematicsTotalSolution = mathematicsTotalSolution;
    }

    public int getPhysicsBestSolution() {
        return physicsBestSolution;
    }

    public void setPhysicsBestSolution(int physicsBestSolution) {
        this.physicsBestSolution = physicsBestSolution;
    }

    public int getPhysicsTotalSolution() {
        return physicsTotalSolution;
    }

    public void setPhysicsTotalSolution(int physicsTotalSolution) {
        this.physicsTotalSolution = physicsTotalSolution;
    }

    public int getMathematicsComment() {
        return mathematicsComment;
    }

    public void setMathematicsComment(int mathematicsComment) {
        this.mathematicsComment = mathematicsComment;
    }

    public int getPhysicsComment() {
        return physicsComment;
    }

    public void setPhysicsComment(int physicsComment) {
        this.physicsComment = physicsComment;
    }

    public int getTransactionPackage1Pending() {
        return transactionPackage1Pending;
    }

    public void setTransactionPackage1Pending(int transactionPackage1Pending) {
        this.transactionPackage1Pending = transactionPackage1Pending;
    }

    public int getTransactionPackage1Success() {
        return transactionPackage1Success;
    }

    public void setTransactionPackage1Success(int transactionPackage1Success) {
        this.transactionPackage1Success = transactionPackage1Success;
    }

    public int getTransactionPackage1Canceled() {
        return transactionPackage1Canceled;
    }

    public void setTransactionPackage1Canceled(int transactionPackage1Canceled) {
        this.transactionPackage1Canceled = transactionPackage1Canceled;
    }

    public int getTransactionPackage2Pending() {
        return transactionPackage2Pending;
    }

    public void setTransactionPackage2Pending(int transactionPackage2Pending) {
        this.transactionPackage2Pending = transactionPackage2Pending;
    }

    public int getTransactionPackage2Success() {
        return transactionPackage2Success;
    }

    public void setTransactionPackage2Success(int transactionPackage2Success) {
        this.transactionPackage2Success = transactionPackage2Success;
    }

    public int getTransactionPackage2Canceled() {
        return transactionPackage2Canceled;
    }

    public void setTransactionPackage2Canceled(int transactionPackage2Canceled) {
        this.transactionPackage2Canceled = transactionPackage2Canceled;
    }

    public int getTransactionPackage3Pending() {
        return transactionPackage3Pending;
    }

    public void setTransactionPackage3Pending(int transactionPackage3Pending) {
        this.transactionPackage3Pending = transactionPackage3Pending;
    }

    public int getTransactionPackage3Success() {
        return transactionPackage3Success;
    }

    public void setTransactionPackage3Success(int transactionPackage3Success) {
        this.transactionPackage3Success = transactionPackage3Success;
    }

    public int getTransactionPackage3Canceled() {
        return transactionPackage3Canceled;
    }

    public void setTransactionPackage3Canceled(int transactionPackage3Canceled) {
        this.transactionPackage3Canceled = transactionPackage3Canceled;
    }

    public int getRedeemBalancePending() {
        return redeemBalancePending;
    }

    public void setRedeemBalancePending(int redeemBalancePending) {
        this.redeemBalancePending = redeemBalancePending;
    }

    public int getRedeemBalanceSuccess() {
        return redeemBalanceSuccess;
    }

    public void setRedeemBalanceSuccess(int redeemBalanceSuccess) {
        this.redeemBalanceSuccess = redeemBalanceSuccess;
    }

    public int getRedeemBalanceCanceled() {
        return redeemBalanceCanceled;
    }

    public void setRedeemBalanceCanceled(int redeemBalanceCanceled) {
        this.redeemBalanceCanceled = redeemBalanceCanceled;
    }

    public int getFeedback() {
        return feedback;
    }

    public void setFeedback(int feedback) {
        this.feedback = feedback;
    }

    public int getFreeCredit() {
        return freeCredit;
    }

    public void setFreeCredit(int freeCredit) {
        this.freeCredit = freeCredit;
    }

    public int getBalanceBonus() {
        return balanceBonus;
    }

    public void setBalanceBonus(int balanceBonus) {
        this.balanceBonus = balanceBonus;
    }
}