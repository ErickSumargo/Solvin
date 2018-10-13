package id.solvinap.dev.server.data;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Erick Sumargo on 4/11/2017.
 */

public class DataBalanceBonus {
    private int id, count;
    private long balance;

    @SerializedName("mentor_id")
    private int mentorId;

    @SerializedName("deal_payment")
    private long dealPayment;

    @SerializedName("created_at")
    private String createdAt;

    private void setId(int id) {
        this.id = id;
    }

    private void setMentorId(int mentorId) {
        this.mentorId = mentorId;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setDealPayment(long dealPayment) {
        this.dealPayment = dealPayment;
    }

    private void setBalance(long balance) {
        this.balance = balance;
    }

    private void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public int getMentorId() {
        return mentorId;
    }

    public int getCount() {
        return count;
    }

    public long getDealPayment() {
        return dealPayment;
    }

    public long getBalance() {
        return balance;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}