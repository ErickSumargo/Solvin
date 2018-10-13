package id.solvinap.dev.realm.helper;

import id.solvinap.dev.realm.table.TableReport;
import id.solvinap.dev.server.data.DataReport;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Erick Sumargo on 3/21/2017.
 */

public class RealmReport {
    //    HELPER
    private Realm realm;

    //    OBJECT
    private RealmResults<TableReport> records;
    private List<DataReport> reportList;

    public RealmReport(Realm realm) {
        this.realm = realm;
    }

    public void save(final List<DataReport> reportList) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                for (DataReport dataReport : reportList) {
                    TableReport record = new TableReport();

                    record.setMonth(dataReport.getMonth());
                    record.setYear(dataReport.getYear());

                    record.setStudent(dataReport.getStudent());
                    record.setMentor(dataReport.getMentor());

                    record.setIncome(dataReport.getIncome());
                    record.setBalanceRedeemed(dataReport.getBalanceRedeemed());

                    record.setMathematicsQuestionPending(dataReport.getMathematicsQuestionPending());
                    record.setMathematicsQuestionDiscuss(dataReport.getMathematicsQuestionDiscuss());
                    record.setMathematicsQuestionComplete(dataReport.getMathematicsQuestionComplete());

                    record.setPhysicsQuestionPending(dataReport.getPhysicsQuestionPending());
                    record.setPhysicsQuestionDiscuss(dataReport.getPhysicsQuestionDiscuss());
                    record.setPhysicsQuestionComplete(dataReport.getPhysicsQuestionComplete());

                    record.setMathematicsBestSolution(dataReport.getMathematicsBestSolution());
                    record.setMathematicsTotalSolution(dataReport.getMathematicsTotalSolution());
                    record.setPhysicsBestSolution(dataReport.getPhysicsBestSolution());
                    record.setPhysicsTotalSolution(dataReport.getPhysicsTotalSolution());

                    record.setMathematicsComment(dataReport.getMathematicsComment());
                    record.setPhysicsComment(dataReport.getPhysicsComment());

                    record.setTransactionPackage1Pending(dataReport.getTransactionPackage1Pending());
                    record.setTransactionPackage1Success(dataReport.getTransactionPackage1Success());
                    record.setTransactionPackage1Canceled(dataReport.getTransactionPackage1Canceled());

                    record.setTransactionPackage2Pending(dataReport.getTransactionPackage2Pending());
                    record.setTransactionPackage2Success(dataReport.getTransactionPackage2Success());
                    record.setTransactionPackage2Canceled(dataReport.getTransactionPackage2Canceled());

                    record.setTransactionPackage3Pending(dataReport.getTransactionPackage3Pending());
                    record.setTransactionPackage3Success(dataReport.getTransactionPackage3Success());
                    record.setTransactionPackage3Canceled(dataReport.getTransactionPackage3Canceled());

                    record.setRedeemBalancePending(dataReport.getRedeemBalancePending());
                    record.setRedeemBalanceSuccess(dataReport.getRedeemBalanceSuccess());
                    record.setRedeemBalanceCanceled(dataReport.getRedeemBalanceCanceled());

                    record.setFeedback(dataReport.getFeedback());
                    record.setFreeCredit(dataReport.getFreeCredit());
                    record.setBalanceBonus(dataReport.getBalanceBonus());

                    bgRealm.copyToRealm(record);
                }
            }
        }, new Realm.Transaction.Callback() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onError(Exception e) {
            }
        });
    }

    public List<DataReport> retrieve() {
        records = realm.where(TableReport.class).findAll();
        reportList = new ArrayList<>();

        for (TableReport record : records) {
            DataReport dataReport = new DataReport();

            dataReport.setMonth(record.getMonth());
            dataReport.setYear(record.getYear());

            dataReport.setStudent(record.getStudent());
            dataReport.setMentor(record.getMentor());

            dataReport.setIncome(record.getIncome());
            dataReport.setBalanceRedeemed(record.getBalanceRedeemed());

            dataReport.setMathematicsQuestionPending(record.getMathematicsQuestionPending());
            dataReport.setMathematicsQuestionDiscuss(record.getMathematicsQuestionDiscuss());
            dataReport.setMathematicsQuestionComplete(record.getMathematicsQuestionComplete());

            dataReport.setPhysicsQuestionPending(record.getPhysicsQuestionPending());
            dataReport.setPhysicsQuestionDiscuss(record.getPhysicsQuestionDiscuss());
            dataReport.setPhysicsQuestionComplete(record.getPhysicsQuestionComplete());

            dataReport.setMathematicsBestSolution(record.getMathematicsBestSolution());
            dataReport.setMathematicsTotalSolution(record.getMathematicsTotalSolution());
            dataReport.setPhysicsBestSolution(record.getPhysicsBestSolution());
            dataReport.setPhysicsTotalSolution(record.getPhysicsTotalSolution());

            dataReport.setMathematicsComment(record.getMathematicsComment());
            dataReport.setPhysicsComment(record.getPhysicsComment());

            dataReport.setTransactionPackage1Pending(record.getTransactionPackage1Pending());
            dataReport.setTransactionPackage1Success(record.getTransactionPackage1Success());
            dataReport.setTransactionPackage1Canceled(record.getTransactionPackage1Canceled());

            dataReport.setTransactionPackage2Pending(record.getTransactionPackage2Pending());
            dataReport.setTransactionPackage2Success(record.getTransactionPackage2Success());
            dataReport.setTransactionPackage2Canceled(record.getTransactionPackage2Canceled());

            dataReport.setTransactionPackage3Pending(record.getTransactionPackage3Pending());
            dataReport.setTransactionPackage3Success(record.getTransactionPackage3Success());
            dataReport.setTransactionPackage3Canceled(record.getTransactionPackage3Canceled());

            dataReport.setRedeemBalancePending(record.getRedeemBalancePending());
            dataReport.setRedeemBalanceSuccess(record.getRedeemBalanceSuccess());
            dataReport.setRedeemBalanceCanceled(record.getRedeemBalanceCanceled());

            dataReport.setFeedback(record.getFeedback());
            dataReport.setFreeCredit(record.getFreeCredit());
            dataReport.setBalanceBonus(record.getBalanceBonus());

            reportList.add(dataReport);
        }
        return reportList;
    }

    public void clear() {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.clear(TableReport.class);
            }
        });
    }
}