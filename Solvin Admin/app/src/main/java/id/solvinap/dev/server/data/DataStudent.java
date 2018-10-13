package id.solvinap.dev.server.data;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Erick Sumargo on 2/6/2017.
 */

public class DataStudent extends DataAuth implements Serializable {
    private int totalQuestion, totalComment, totalTransaction;
    private int totalFreeCredit;
    private int totalQuestionPending, totalQuestionDiscuss, totalQuestionComplete;
    private int totalTransactionPending, totalTransactionSuccess, totalTransactionCanceled;;
    private int credit;
    private boolean creditExpired;
    private String school;

    @SerializedName("member_code")
    private String membershipCode;

    @SerializedName("credit_timelife")
    private String creditTimelife;

    public boolean isCreditExpired() {
        return creditExpired;
    }

    public void setCreditExpired(boolean creditExpired) {
        this.creditExpired = creditExpired;
    }

    public int getTotalFreeCredit() {
        return totalFreeCredit;
    }

    public void setTotalFreeCredit(int totalFreeCredit) {
        this.totalFreeCredit = totalFreeCredit;
    }

    public int getTotalQuestionPending() {
        return totalQuestionPending;
    }

    public void setTotalQuestionPending(int totalQuestionPending) {
        this.totalQuestionPending = totalQuestionPending;
    }

    public int getTotalQuestionDiscuss() {
        return totalQuestionDiscuss;
    }

    public void setTotalQuestionDiscuss(int totalQuestionDiscuss) {
        this.totalQuestionDiscuss = totalQuestionDiscuss;
    }

    public int getTotalQuestionComplete() {
        return totalQuestionComplete;
    }

    public void setTotalQuestionComplete(int totalQuestionComplete) {
        this.totalQuestionComplete = totalQuestionComplete;
    }

    public int getTotalTransactionPending() {
        return totalTransactionPending;
    }

    public void setTotalTransactionPending(int totalTransactionPending) {
        this.totalTransactionPending = totalTransactionPending;
    }

    public int getTotalTransactionSuccess() {
        return totalTransactionSuccess;
    }

    public void setTotalTransactionSuccess(int totalTransactionSuccess) {
        this.totalTransactionSuccess = totalTransactionSuccess;
    }

    public int getTotalTransactionCanceled() {
        return totalTransactionCanceled;
    }

    public void setTotalTransactionCanceled(int totalTransactionCanceled) {
        this.totalTransactionCanceled = totalTransactionCanceled;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public void setMembershipCode(String membershipCode) {
        this.membershipCode = membershipCode;
    }

    public void setCredit(int credit) {
        this.credit = credit;
    }

    public void setCreditTimelife(String creditTimelife){this.creditTimelife = creditTimelife;}

    public void setTotalQuestion(int totalQuestion) {
        this.totalQuestion = totalQuestion;
    }

    public void setTotalComment(int totalComment) {
        this.totalComment = totalComment;
    }

    public void setTotalTransaction(int totalTransaction) {
        this.totalTransaction = totalTransaction;
    }

    public String getSchool() {
        return school;
    }

    public String getMembershipCode() {
        return membershipCode;
    }

    public int getCredit() {
        return credit;
    }

    public String getCreditTimelife(){return creditTimelife;}

    public int getTotalQuestion() {
        return totalQuestion;
    }

    public int getTotalComment() {
        return totalComment;
    }

    public int getTotalTransaction() {
        return totalTransaction;
    }
}