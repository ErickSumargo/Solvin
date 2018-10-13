package id.solvinap.dev.view.adapter;

import android.content.Context;
import android.support.transition.TransitionManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;


import id.solvinap.dev.R;
import id.solvinap.dev.server.data.DataPackage;
import id.solvinap.dev.server.data.DataReport;
import id.solvinap.dev.server.helper.Session;
import id.solvinap.dev.view.helper.Tool;

import java.util.List;

/**
 * Created by Erick Sumargo on 2/3/2017.
 */

public class ReportViewAdapter extends RecyclerView.Adapter<ReportViewAdapter.ViewHolder> {
    //    VIEW
    private View itemView;

    //    HELPER
    private Context context;

    private Animation animation;

    //    OBJECT
    private List<DataReport> reportList;
    private List<DataPackage> packageList;

    //    VARIABLE
    private int expandedPosition = -1;
    public int lastAnimatedPosition = -1;
    private String[] months = {"Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus",
            "September", "Oktober", "November", "Desember"};
    private String incomeValue, balanceRedeemedValue, profitValue, balanceBonusValue;

    public ReportViewAdapter(List<DataReport> reportList, Context context) {
        this.reportList = reportList;
        packageList = Session.getInstance(context).getPackageList();
    }

    public void updateList(List<DataReport> dataReportList) {
        this.reportList.clear();
        this.reportList.addAll(dataReportList);

        notifyDataSetChanged();
    }

    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastAnimatedPosition) {
            animation = AnimationUtils.loadAnimation(context, R.anim.load_feed);
            viewToAnimate.startAnimation(animation);

            lastAnimatedPosition = position;
        }
    }

    private void expandContent(ViewHolder holder) {
        TransitionManager.beginDelayedTransition(holder.card);
        holder.contentContainer.setVisibility(View.VISIBLE);
    }

    private void collapseContent(ViewHolder holder) {
        holder.contentContainer.setVisibility(View.GONE);
    }

    @Override
    public ReportViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.report_list, parent, false);

        return new ReportViewAdapter.ViewHolder(itemView);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CardView card;
        private LinearLayout contentContainer;
        private TextView monthly,
                student, mentor,
                income, balanceRedeemed, profit,
                mathematicsQuestion, mathematicsQuestionPending, mathematicsQuestionDiscussion, mathematicsQuestionComplete,
                physicsQuestion, physicsQuestionPending, physicsQuestionDiscussion, physicsQuestionComplete,
                mathematicsTotalSolution, mathematicsBestSolution,
                physicsTotalSolution, physicsBestSolution,
                mathematicsComment, physicsComment,
                transactionPackage1, transactionPackage1Pending, transactionPackage1Canceled, transactionPackage1Actived,
                transactionPackage2, transactionPackage2Pending, transactionPackage2Canceled, transactionPackage2Actived,
                transactionPackage3, transactionPackage3Pending, transactionPackage3Canceled, transactionPackage3Actived,
                transactionRedeemBalance, transactionRedeemBalancePending, transactionRedeemBalanceCanceled, transactionRedeemBalanceActived,
                feedback, extraCredit, balanceBonus;
        private ImageButton toggleContent;

        private boolean expanded = false;

        public ViewHolder(View view) {
            super(view);
            context = view.getContext();

            card = (CardView) view.findViewById(R.id.report_card);
            contentContainer = (LinearLayout) view.findViewById(R.id.report_content_container);

            //  Header
            monthly = (TextView) view.findViewById(R.id.report_monthly);
            toggleContent = (ImageButton) view.findViewById(R.id.report_toggle_content);

            //  DataAuth
            student = (TextView) view.findViewById(R.id.report_total_student);
            mentor = (TextView) view.findViewById(R.id.report_total_mentor);

            //  Financial
            income = (TextView) view.findViewById(R.id.report_total_income);
            balanceRedeemed = (TextView) view.findViewById(R.id.report_total_balance_redeemed);
            profit = (TextView) view.findViewById(R.id.report_total_profit);

            //  Activity
            mathematicsQuestion = (TextView) view.findViewById(R.id.report_total_mathematics_question);
            mathematicsQuestionPending = (TextView) view.findViewById(R.id.report_mathematics_question_pending);
            mathematicsQuestionDiscussion = (TextView) view.findViewById(R.id.report_mathematics_question_discussion);
            mathematicsQuestionComplete = (TextView) view.findViewById(R.id.report_mathematics_question_complete);

            physicsQuestion = (TextView) view.findViewById(R.id.report_total_physics_question);
            physicsQuestionPending = (TextView) view.findViewById(R.id.report_physics_question_pending);
            physicsQuestionDiscussion = (TextView) view.findViewById(R.id.report_physics_question_discussion);
            physicsQuestionComplete = (TextView) view.findViewById(R.id.report_physics_question_complete);

            mathematicsTotalSolution = (TextView) view.findViewById(R.id.report_mathematics_total_solution);
            mathematicsBestSolution = (TextView) view.findViewById(R.id.report_mathematics_best_solution);
            physicsTotalSolution = (TextView) view.findViewById(R.id.report_physics_total_solution);
            physicsBestSolution = (TextView) view.findViewById(R.id.report_physics_best_solution);

            mathematicsComment = (TextView) view.findViewById(R.id.report_mathematics_total_comment);
            physicsComment = (TextView) view.findViewById(R.id.report_physics_total_comment);

            //  Transaction
            transactionPackage1 = (TextView) view.findViewById(R.id.report_transaction_package_1);
            transactionPackage1Pending = (TextView) view.findViewById(R.id.report_transaction_package_1_pending);
            transactionPackage1Canceled = (TextView) view.findViewById(R.id.report_transaction_package_1_canceled);
            transactionPackage1Actived = (TextView) view.findViewById(R.id.report_transaction_package_1_actived);

            transactionPackage2 = (TextView) view.findViewById(R.id.report_transaction_package_2);
            transactionPackage2Pending = (TextView) view.findViewById(R.id.report_transaction_package_2_pending);
            transactionPackage2Canceled = (TextView) view.findViewById(R.id.report_transaction_package_2_canceled);
            transactionPackage2Actived = (TextView) view.findViewById(R.id.report_transaction_package_2_actived);

            transactionPackage3 = (TextView) view.findViewById(R.id.report_transaction_package_3);
            transactionPackage3Pending = (TextView) view.findViewById(R.id.report_transaction_package_3_pending);
            transactionPackage3Canceled = (TextView) view.findViewById(R.id.report_transaction_package_3_canceled);
            transactionPackage3Actived = (TextView) view.findViewById(R.id.report_transaction_package_3_actived);

            transactionRedeemBalance = (TextView) view.findViewById(R.id.report_transaction_redeem_balance);
            transactionRedeemBalancePending = (TextView) view.findViewById(R.id.report_transaction_redeem_balance_pending);
            transactionRedeemBalanceCanceled = (TextView) view.findViewById(R.id.report_transaction_redeem_balance_canceled);
            transactionRedeemBalanceActived = (TextView) view.findViewById(R.id.report_transaction_redeem_balance_actived);

            //  Other
            feedback = (TextView) view.findViewById(R.id.report_total_feedback);
            extraCredit = (TextView) view.findViewById(R.id.report_total_extra_credit);
            balanceBonus = (TextView) view.findViewById(R.id.report_total_balance_bonus);
        }
    }

    @Override
    public void onBindViewHolder(final ReportViewAdapter.ViewHolder holder, int position) {
        final DataReport dataReport = reportList.get(reportList.size() - position - 1);
        holder.monthly.setText("Bulanan: " + months[dataReport.getMonth() - 1] + " " + dataReport.getYear());

        if (holder.getAdapterPosition() == expandedPosition) {
            holder.contentContainer.setVisibility(View.VISIBLE);
        } else {
            holder.contentContainer.setVisibility(View.GONE);
        }
        holder.student.setText(String.valueOf(dataReport.getStudent()));
        holder.mentor.setText(String.valueOf(dataReport.getMentor()));

        incomeValue = Tool.getInstance(context).convertRpCurrency(dataReport.getIncome());
        balanceRedeemedValue = Tool.getInstance(context).convertRpCurrency(dataReport.getBalanceRedeemed());
        profitValue = Tool.getInstance(context).convertRpCurrency(dataReport.getIncome() - dataReport.getBalanceRedeemed());

        holder.income.setText(incomeValue.substring(4, incomeValue.length()));
        holder.balanceRedeemed.setText(balanceRedeemedValue.substring(4, balanceRedeemedValue.length()));
        if (profitValue.charAt(0) != '-') {
            holder.profit.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            holder.profit.setText(profitValue.substring(4, profitValue.length()));
        } else {
            holder.profit.setTextColor(context.getResources().getColor(R.color.colorCancel));
            holder.profit.setText(profitValue.substring(5, profitValue.length()));
        }

        holder.mathematicsQuestion.setText(String.valueOf(dataReport.getMathematicsQuestionPending() +
                dataReport.getMathematicsQuestionDiscuss() + dataReport.getMathematicsQuestionComplete()));
        holder.mathematicsQuestionPending.setText(String.valueOf(dataReport.getMathematicsQuestionPending()));
        holder.mathematicsQuestionDiscussion.setText(String.valueOf(dataReport.getMathematicsQuestionDiscuss()));
        holder.mathematicsQuestionComplete.setText(String.valueOf(dataReport.getMathematicsQuestionComplete()));

        holder.physicsQuestion.setText(String.valueOf(dataReport.getPhysicsQuestionPending() +
                dataReport.getPhysicsQuestionDiscuss() + dataReport.getPhysicsQuestionComplete()));
        holder.physicsQuestionPending.setText(String.valueOf(dataReport.getPhysicsQuestionPending()));
        holder.physicsQuestionDiscussion.setText(String.valueOf(dataReport.getPhysicsQuestionDiscuss()));
        holder.physicsQuestionComplete.setText(String.valueOf(dataReport.getPhysicsQuestionComplete()));

        holder.mathematicsTotalSolution.setText(String.valueOf(dataReport.getMathematicsTotalSolution()));
        holder.mathematicsBestSolution.setText(String.valueOf(dataReport.getMathematicsBestSolution()));
        holder.physicsTotalSolution.setText(String.valueOf(dataReport.getPhysicsTotalSolution()));
        holder.physicsBestSolution.setText(String.valueOf(dataReport.getPhysicsBestSolution()));

        holder.mathematicsComment.setText(String.valueOf(dataReport.getMathematicsComment()));
        holder.physicsComment.setText(String.valueOf(dataReport.getPhysicsComment()));

        holder.transactionPackage1.setText(Tool.getInstance(context).convertRpCurrency(packageList.get(0).getNominal()) +
                " (" + packageList.get(0).getCredit() + " SKT)");
        holder.transactionPackage1Pending.setText(String.valueOf(dataReport.getTransactionPackage1Pending()));
        holder.transactionPackage1Canceled.setText(String.valueOf(dataReport.getTransactionPackage1Canceled()));
        holder.transactionPackage1Actived.setText(String.valueOf(dataReport.getTransactionPackage2Success()));

        holder.transactionPackage2.setText(Tool.getInstance(context).convertRpCurrency(packageList.get(1).getNominal()) +
                " (" + packageList.get(1).getCredit() + " SKT)");
        holder.transactionPackage2Pending.setText(String.valueOf(dataReport.getTransactionPackage2Pending()));
        holder.transactionPackage2Canceled.setText(String.valueOf(dataReport.getTransactionPackage2Canceled()));
        holder.transactionPackage2Actived.setText(String.valueOf(dataReport.getTransactionPackage2Success()));

        holder.transactionPackage3.setText(Tool.getInstance(context).convertRpCurrency(packageList.get(2).getNominal()) +
                " (" + packageList.get(2).getCredit() + " SKT)");
        holder.transactionPackage3Pending.setText(String.valueOf(dataReport.getTransactionPackage3Pending()));
        holder.transactionPackage3Canceled.setText(String.valueOf(dataReport.getTransactionPackage3Canceled()));
        holder.transactionPackage3Actived.setText(String.valueOf(dataReport.getTransactionPackage3Success()));

        holder.transactionRedeemBalance.setText(String.valueOf(dataReport.getRedeemBalancePending() +
                dataReport.getRedeemBalanceCanceled() + dataReport.getRedeemBalanceSuccess()));
        holder.transactionRedeemBalancePending.setText(String.valueOf(dataReport.getRedeemBalancePending()));
        holder.transactionRedeemBalanceCanceled.setText(String.valueOf(dataReport.getRedeemBalanceCanceled()));
        holder.transactionRedeemBalanceActived.setText(String.valueOf(dataReport.getRedeemBalanceSuccess()));

        holder.feedback.setText(String.valueOf(dataReport.getFeedback()));
        holder.extraCredit.setText(String.valueOf(dataReport.getFreeCredit()));

        balanceBonusValue = Tool.getInstance(context).convertRpCurrency(dataReport.getBalanceBonus());
        holder.balanceBonus.setText(balanceBonusValue.substring(4, balanceBonusValue.length()));

        holder.toggleContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!holder.expanded) {
                    expandContent(holder);
                    holder.toggleContent.setImageResource(R.drawable.ic_menu_up_dark_trim);

                    expandedPosition = holder.getAdapterPosition();
                    holder.expanded = true;
                } else {
                    collapseContent(holder);
                    holder.toggleContent.setImageResource(R.drawable.ic_menu_down_dark_trim);

                    expandedPosition = -1;
                    holder.expanded = false;
                }
            }
        });

        setAnimation(holder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return reportList.size();
    }
}