package id.solvinap.dev.server.data;

import id.solvinap.dev.view.helper.SCrypt;

import java.io.Serializable;

/**
 * Created by Erick Sumargo on 2/3/2017.
 */

public class DataReport implements Serializable {
    private String month, year,
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

    private SCrypt sCrypt;

    public DataReport() {
        sCrypt = SCrypt.getInstance();
    }

    public void setMonth(int month) {
        try {
            this.month = sCrypt.bytesToHex(sCrypt.encrypt(String.valueOf(month)));
        } catch (Exception e) {
        }
    }

    public int getMonth() {
        try {
            return Integer.parseInt(new String(SCrypt.getInstance().decrypt(month)).trim());
        } catch (Exception e) {
        }
        return 1;
    }

    public void setYear(int year) {
        try {
            this.year = sCrypt.bytesToHex(sCrypt.encrypt(String.valueOf(year)));
        } catch (Exception e) {
        }
    }

    public int getYear() {
        try {
            return Integer.parseInt(new String(SCrypt.getInstance().decrypt(year)).trim());
        } catch (Exception e) {
        }
        return 2017;
    }

    public void setStudent(int student) {
        try {
            this.student = sCrypt.bytesToHex(sCrypt.encrypt(String.valueOf(student)));
        } catch (Exception e) {
        }
    }

    public int getStudent() {
        try {
            return Integer.parseInt(new String(SCrypt.getInstance().decrypt(student)).trim());
        } catch (Exception e) {
        }
        return 0;
    }

    public void setMentor(int mentor) {
        try {
            this.mentor = sCrypt.bytesToHex(sCrypt.encrypt(String.valueOf(mentor)));
        } catch (Exception e) {
        }
    }

    public int getMentor() {
        try {
            return Integer.parseInt(new String(SCrypt.getInstance().decrypt(mentor)).trim());
        } catch (Exception e) {
        }
        return 0;
    }

    public void setIncome(int income) {
        try {
            this.income = sCrypt.bytesToHex(sCrypt.encrypt(String.valueOf(income)));
        } catch (Exception e) {
        }
    }

    public int getIncome() {
        try {
            return Integer.parseInt(new String(SCrypt.getInstance().decrypt(income)).trim());
        } catch (Exception e) {
        }
        return 0;
    }

    public void setBalanceRedeemed(int balanceRedeemed) {
        try {
            this.balanceRedeemed = sCrypt.bytesToHex(sCrypt.encrypt(String.valueOf(balanceRedeemed)));
        } catch (Exception e) {
        }
    }

    public int getBalanceRedeemed() {
        try {
            return Integer.parseInt(new String(SCrypt.getInstance().decrypt(balanceRedeemed)).trim());
        } catch (Exception e) {
        }
        return 0;
    }

    public void setMathematicsQuestionPending(int mathematicsQuestionPending) {
        try {
            this.mathematicsQuestionPending = sCrypt.bytesToHex(sCrypt.encrypt(String.valueOf(mathematicsQuestionPending)));
        } catch (Exception e) {
        }
    }

    public int getMathematicsQuestionPending() {
        try {
            return Integer.parseInt(new String(SCrypt.getInstance().decrypt(mathematicsQuestionPending)).trim());
        } catch (Exception e) {
        }
        return 0;
    }

    public void setMathematicsQuestionDiscuss(int mathematicsQuestionDiscuss) {
        try {
            this.mathematicsQuestionDiscuss = sCrypt.bytesToHex(sCrypt.encrypt(String.valueOf(mathematicsQuestionDiscuss)));
        } catch (Exception e) {
        }
    }

    public int getMathematicsQuestionDiscuss() {
        try {
            return Integer.parseInt(new String(SCrypt.getInstance().decrypt(mathematicsQuestionDiscuss)).trim());
        } catch (Exception e) {
        }
        return 0;
    }

    public void setMathematicsQuestionComplete(int mathematicsQuestionComplete) {
        try {
            this.mathematicsQuestionComplete = sCrypt.bytesToHex(sCrypt.encrypt(String.valueOf(mathematicsQuestionComplete)));
        } catch (Exception e) {
        }
    }

    public int getMathematicsQuestionComplete() {
        try {
            return Integer.parseInt(new String(SCrypt.getInstance().decrypt(mathematicsQuestionComplete)).trim());
        } catch (Exception e) {
        }
        return 0;
    }

    public void setPhysicsQuestionPending(int physicsQuestionPending) {
        try {
            this.physicsQuestionPending = sCrypt.bytesToHex(sCrypt.encrypt(String.valueOf(physicsQuestionPending)));
        } catch (Exception e) {
        }
    }

    public int getPhysicsQuestionPending() {
        try {
            return Integer.parseInt(new String(SCrypt.getInstance().decrypt(physicsQuestionPending)).trim());
        } catch (Exception e) {
        }
        return 0;
    }

    public void setPhysicsQuestionDiscuss(int physicsQuestionDiscuss) {
        try {
            this.physicsQuestionDiscuss = sCrypt.bytesToHex(sCrypt.encrypt(String.valueOf(physicsQuestionDiscuss)));
        } catch (Exception e) {
        }
    }

    public int getPhysicsQuestionDiscuss() {
        try {
            return Integer.parseInt(new String(SCrypt.getInstance().decrypt(physicsQuestionDiscuss)).trim());
        } catch (Exception e) {
        }
        return 0;
    }

    public void setPhysicsQuestionComplete(int physicsQuestionComplete) {
        try {
            this.physicsQuestionComplete = sCrypt.bytesToHex(sCrypt.encrypt(String.valueOf(physicsQuestionComplete)));
        } catch (Exception e) {
        }
    }

    public int getPhysicsQuestionComplete() {
        try {
            return Integer.parseInt(new String(SCrypt.getInstance().decrypt(physicsQuestionComplete)).trim());
        } catch (Exception e) {
        }
        return 0;
    }

    public void setMathematicsBestSolution(int mathematicsBestSolution) {
        try {
            this.mathematicsBestSolution = sCrypt.bytesToHex(sCrypt.encrypt(String.valueOf(mathematicsBestSolution)));
        } catch (Exception e) {
        }
    }

    public int getMathematicsBestSolution() {
        try {
            return Integer.parseInt(new String(SCrypt.getInstance().decrypt(mathematicsBestSolution)).trim());
        } catch (Exception e) {
        }
        return 0;
    }

    public void setMathematicsTotalSolution(int mathematicsTotalSolution) {
        try {
            this.mathematicsTotalSolution = sCrypt.bytesToHex(sCrypt.encrypt(String.valueOf(mathematicsTotalSolution)));
        } catch (Exception e) {
        }
    }

    public int getMathematicsTotalSolution() {
        try {
            return Integer.parseInt(new String(SCrypt.getInstance().decrypt(mathematicsTotalSolution)).trim());
        } catch (Exception e) {
        }
        return 0;
    }

    public void setPhysicsBestSolution(int physicsBestSolution) {
        try {
            this.physicsBestSolution = sCrypt.bytesToHex(sCrypt.encrypt(String.valueOf(physicsBestSolution)));
        } catch (Exception e) {
        }
    }

    public int getPhysicsBestSolution() {
        try {
            return Integer.parseInt(new String(SCrypt.getInstance().decrypt(physicsBestSolution)).trim());
        } catch (Exception e) {
        }
        return 0;
    }

    public void setPhysicsTotalSolution(int physicsTotalSolution) {
        try {
            this.physicsTotalSolution = sCrypt.bytesToHex(sCrypt.encrypt(String.valueOf(physicsTotalSolution)));
        } catch (Exception e) {
        }
    }

    public int getPhysicsTotalSolution() {
        try {
            return Integer.parseInt(new String(SCrypt.getInstance().decrypt(physicsTotalSolution)).trim());
        } catch (Exception e) {
        }
        return 0;
    }

    public void setMathematicsComment(int mathematicsComment) {
        try {
            this.mathematicsComment = sCrypt.bytesToHex(sCrypt.encrypt(String.valueOf(mathematicsComment)));
        } catch (Exception e) {
        }
    }

    public int getMathematicsComment() {
        try {
            return Integer.parseInt(new String(SCrypt.getInstance().decrypt(mathematicsComment)).trim());
        } catch (Exception e) {
        }
        return 0;
    }

    public void setPhysicsComment(int physicsComment) {
        try {
            this.physicsComment = sCrypt.bytesToHex(sCrypt.encrypt(String.valueOf(physicsComment)));
        } catch (Exception e) {
        }
    }

    public int getPhysicsComment() {
        try {
            return Integer.parseInt(new String(SCrypt.getInstance().decrypt(physicsComment)).trim());
        } catch (Exception e) {
        }
        return 0;
    }

    public void setTransactionPackage1Pending(int transactionPackage1Pending) {
        try {
            this.transactionPackage1Pending = sCrypt.bytesToHex(sCrypt.encrypt(String.valueOf(transactionPackage1Pending)));
        } catch (Exception e) {
        }
    }

    public int getTransactionPackage1Pending() {
        try {
            return Integer.parseInt(new String(SCrypt.getInstance().decrypt(transactionPackage1Pending)).trim());
        } catch (Exception e) {
        }
        return 0;
    }

    public void setTransactionPackage1Success(int transactionPackage1Success) {
        try {
            this.transactionPackage1Success = sCrypt.bytesToHex(sCrypt.encrypt(String.valueOf(transactionPackage1Success)));
        } catch (Exception e) {
        }
    }

    public int getTransactionPackage1Success() {
        try {
            return Integer.parseInt(new String(SCrypt.getInstance().decrypt(transactionPackage1Success)).trim());
        } catch (Exception e) {
        }
        return 0;
    }

    public void setTransactionPackage1Canceled(int transactionPackage1Canceled) {
        try {
            this.transactionPackage1Canceled = sCrypt.bytesToHex(sCrypt.encrypt(String.valueOf(transactionPackage1Canceled)));
        } catch (Exception e) {
        }
    }

    public int getTransactionPackage1Canceled() {
        try {
            return Integer.parseInt(new String(SCrypt.getInstance().decrypt(transactionPackage1Canceled)).trim());
        } catch (Exception e) {
        }
        return 0;
    }

    public void setTransactionPackage2Pending(int transactionPackage2Pending) {
        try {
            this.transactionPackage2Pending = sCrypt.bytesToHex(sCrypt.encrypt(String.valueOf(transactionPackage2Pending)));
        } catch (Exception e) {
        }
    }

    public int getTransactionPackage2Pending() {
        try {
            return Integer.parseInt(new String(SCrypt.getInstance().decrypt(transactionPackage2Pending)).trim());
        } catch (Exception e) {
        }
        return 0;
    }

    public void setTransactionPackage2Success(int transactionPackage2Success) {
        try {
            this.transactionPackage2Success = sCrypt.bytesToHex(sCrypt.encrypt(String.valueOf(transactionPackage2Success)));
        } catch (Exception e) {
        }
    }

    public int getTransactionPackage2Success() {
        try {
            return Integer.parseInt(new String(SCrypt.getInstance().decrypt(transactionPackage2Success)).trim());
        } catch (Exception e) {
        }
        return 0;
    }

    public void setTransactionPackage2Canceled(int transactionPackage2Canceled) {
        try {
            this.transactionPackage2Canceled = sCrypt.bytesToHex(sCrypt.encrypt(String.valueOf(transactionPackage2Canceled)));
        } catch (Exception e) {
        }
    }

    public int getTransactionPackage2Canceled() {
        try {
            return Integer.parseInt(new String(SCrypt.getInstance().decrypt(transactionPackage2Canceled)).trim());
        } catch (Exception e) {
        }
        return 0;
    }

    public void setTransactionPackage3Pending(int transactionPackage3Pending) {
        try {
            this.transactionPackage3Pending = sCrypt.bytesToHex(sCrypt.encrypt(String.valueOf(transactionPackage3Pending)));
        } catch (Exception e) {
        }
    }

    public int getTransactionPackage3Pending() {
        try {
            return Integer.parseInt(new String(SCrypt.getInstance().decrypt(transactionPackage3Pending)).trim());
        } catch (Exception e) {
        }
        return 0;
    }

    public void setTransactionPackage3Success(int transactionPackage3Success) {
        try {
            this.transactionPackage3Success = sCrypt.bytesToHex(sCrypt.encrypt(String.valueOf(transactionPackage3Success)));
        } catch (Exception e) {
        }
    }

    public int getTransactionPackage3Success() {
        try {
            return Integer.parseInt(new String(SCrypt.getInstance().decrypt(transactionPackage3Success)).trim());
        } catch (Exception e) {
        }
        return 0;
    }

    public void setTransactionPackage3Canceled(int transactionPackage3Canceled) {
        try {
            this.transactionPackage3Canceled = sCrypt.bytesToHex(sCrypt.encrypt(String.valueOf(transactionPackage3Canceled)));
        } catch (Exception e) {
        }
    }

    public int getTransactionPackage3Canceled() {
        try {
            return Integer.parseInt(new String(SCrypt.getInstance().decrypt(transactionPackage3Canceled)).trim());
        } catch (Exception e) {
        }
        return 0;
    }

    public void setRedeemBalancePending(int redeemBalancePending) {
        try {
            this.redeemBalancePending = sCrypt.bytesToHex(sCrypt.encrypt(String.valueOf(redeemBalancePending)));
        } catch (Exception e) {
        }
    }

    public int getRedeemBalancePending() {
        try {
            return Integer.parseInt(new String(SCrypt.getInstance().decrypt(redeemBalancePending)).trim());
        } catch (Exception e) {
        }
        return 0;
    }

    public void setRedeemBalanceSuccess(int redeemBalanceSuccess) {
        try {
            this.redeemBalanceSuccess = sCrypt.bytesToHex(sCrypt.encrypt(String.valueOf(redeemBalanceSuccess)));
        } catch (Exception e) {
        }
    }

    public int getRedeemBalanceSuccess() {
        try {
            return Integer.parseInt(new String(SCrypt.getInstance().decrypt(redeemBalanceSuccess)).trim());
        } catch (Exception e) {
        }
        return 0;
    }

    public void setRedeemBalanceCanceled(int redeemBalanceCanceled) {
        try {
            this.redeemBalanceCanceled = sCrypt.bytesToHex(sCrypt.encrypt(String.valueOf(redeemBalanceCanceled)));
        } catch (Exception e) {
        }
    }

    public int getRedeemBalanceCanceled() {
        try {
            return Integer.parseInt(new String(SCrypt.getInstance().decrypt(redeemBalanceCanceled)).trim());
        } catch (Exception e) {
        }
        return 0;
    }

    public void setFeedback(int feedback) {
        try {
            this.feedback = sCrypt.bytesToHex(sCrypt.encrypt(String.valueOf(feedback)));
        } catch (Exception e) {
        }
    }

    public int getFeedback() {
        try {
            return Integer.parseInt(new String(SCrypt.getInstance().decrypt(feedback)).trim());
        } catch (Exception e) {
        }
        return 0;
    }

    public void setFreeCredit(int freeCredit) {
        try {
            this.freeCredit = sCrypt.bytesToHex(sCrypt.encrypt(String.valueOf(freeCredit)));
        } catch (Exception e) {
        }
    }

    public int getFreeCredit() {
        try {
            return Integer.parseInt(new String(SCrypt.getInstance().decrypt(freeCredit)).trim());
        } catch (Exception e) {
        }
        return 0;
    }

    public void setBalanceBonus(int balanceBonus) {
        try {
            this.balanceBonus = sCrypt.bytesToHex(sCrypt.encrypt(String.valueOf(balanceBonus)));
        } catch (Exception e) {
        }
    }

    public int getBalanceBonus() {
        try {
            return Integer.parseInt(new String(SCrypt.getInstance().decrypt(balanceBonus)).trim());
        } catch (Exception e) {
        }
        return 0;
    }
}