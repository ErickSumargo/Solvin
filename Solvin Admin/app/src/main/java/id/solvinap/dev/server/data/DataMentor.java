package id.solvinap.dev.server.data;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Erick Sumargo on 2/7/2017.
 */

public class DataMentor extends DataAuth implements Serializable {
    public int totalBestSolution, totalSolution, totalComment, totalRedeemBalance,
            totalRedeemBalancePending, totalRedeemBalanceSuccess, totalRedeemBalanceCanceled;

    public long totalBalance, totalBalanceBonus, totalBalanceRedeemed;
    public String workplace;

    @SerializedName("member_code")
    public String mentorshipCode;

    public String getMentorshipCode() {
        return mentorshipCode;
    }

    public void setMentorshipCode(String mentorshipCode) {
        this.mentorshipCode = mentorshipCode;
    }

    public String getWorkplace() {
        return workplace;
    }

    public void setWorkplace(String workplace) {
        this.workplace = workplace;
    }

    public void setTotalBalance(long totalBalance) {
        this.totalBalance = totalBalance;
    }

    public void setTotalBestSolution(int totalBestSolution) {
        this.totalBestSolution = totalBestSolution;
    }

    public void setTotalSolution(int totalSolution) {
        this.totalSolution = totalSolution;
    }

    public void setTotalComment(int totalComment) {
        this.totalComment = totalComment;
    }

    public void setTotalRedeemBalance(int totalRedeemBalance) {
        this.totalRedeemBalance = totalRedeemBalance;
    }

    public long getTotalBalance() {
        return totalBalance;
    }

    public long getTotalBalanceBonus() {
        return totalBalanceBonus;
    }

    public void setTotalBalanceBonus(long totalBalanceBonus) {
        this.totalBalanceBonus = totalBalanceBonus;
    }

    public int getTotalBestSolution() {
        return totalBestSolution;
    }

    public int getTotalSolution() {
        return totalSolution;
    }

    public int getTotalComment() {
        return totalComment;
    }

    public int getTotalRedeemBalance() {
        return totalRedeemBalance;
    }

    public int getTotalRedeemBalancePending() {
        return totalRedeemBalancePending;
    }

    public void setTotalRedeemBalancePending(int totalRedeemBalancePending) {
        this.totalRedeemBalancePending = totalRedeemBalancePending;
    }

    public int getTotalRedeemBalanceSuccess() {
        return totalRedeemBalanceSuccess;
    }

    public void setTotalRedeemBalanceSuccess(int totalRedeemBalanceSuccess) {
        this.totalRedeemBalanceSuccess = totalRedeemBalanceSuccess;
    }

    public int getTotalRedeemBalanceCanceled() {
        return totalRedeemBalanceCanceled;
    }

    public void setTotalRedeemBalanceCanceled(int totalRedeemBalanceCanceled) {
        this.totalRedeemBalanceCanceled = totalRedeemBalanceCanceled;
    }

    public long getTotalBalanceRedeemed() {
        return totalBalanceRedeemed;
    }

    public void setTotalBalanceRedeemed(long totalBalanceRedeemed) {
        this.totalBalanceRedeemed = totalBalanceRedeemed;
    }
}