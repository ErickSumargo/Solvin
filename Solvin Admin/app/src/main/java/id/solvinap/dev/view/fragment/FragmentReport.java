package id.solvinap.dev.view.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import id.solvinap.dev.R;
import id.solvinap.dev.realm.helper.RealmReport;
import id.solvinap.dev.server.api.Connection;
import id.solvinap.dev.server.api.Response;
import id.solvinap.dev.server.data.DataPlainReport;
import id.solvinap.dev.server.data.DataReport;
import id.solvinap.dev.server.helper.Connectivity;
import id.solvinap.dev.server.helper.Global;
import id.solvinap.dev.server.interfaces.IAPIRequest;
import id.solvinap.dev.server.interfaces.IBaseResponse;
import id.solvinap.dev.server.model.ModelReport;
import id.solvinap.dev.view.activity.ActivityPresentation;
import id.solvinap.dev.view.activity.MainActivity;
import id.solvinap.dev.view.adapter.ReportViewAdapter;
import id.solvinap.dev.view.helper.Tool;
import id.solvinap.dev.view.widget.CustomCheatSheet;

import java.io.Serializable;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Erick Sumargo on 1/31/2017.
 */

public class FragmentReport extends Fragment implements IBaseResponse {
    //    VIEW
    private View view;
    private SwipeRefreshLayout swipeRefreshLayout;
    public RecyclerView reportView;

    private ImageButton presentationIcon;

    private View nullView;
    private ImageView nullViewImage;
    private TextView nullViewTitle, nullViewDesc;

    //    HELPER
    private LinearLayoutManager layoutManager;

    private Intent intent;
    private TypedValue typedValue;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    //    CONNECTION
    private IAPIRequest iAPIRequest;
    private IBaseResponse iBaseResponse;

    //    OBJECT
    private Realm realm;
    private RealmReport realmReport;

    private ModelReport report;
    private List<DataReport> reportList;
    private List<DataPlainReport> plainReportList;
    private ReportViewAdapter reportViewAdapter;

    //    VARIABLE
    private int month, year;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_report, container, false);
        setHasOptionsMenu(true);

        init();
        setEvent();

        return view;
    }

    private void init() {
        //    VIEW
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.report_swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        if (getActivity().getResources().getBoolean(R.bool.isTablet)) {
            swipeRefreshLayout.setSize(SwipeRefreshLayout.LARGE);
        }

        nullView = view.findViewById(R.id.null_view);
        nullViewImage = (ImageView) nullView.findViewById(R.id.null_view_image);
        nullViewTitle = (TextView) nullView.findViewById(R.id.null_view_title);
        nullViewDesc = (TextView) nullView.findViewById(R.id.null_view_desc);

        nullViewImage.setImageResource(R.drawable.ic_item_null);
        nullViewTitle.setText(getResources().getString(R.string.text_no_item));
        nullViewDesc.setText(getResources().getString(R.string.text_no_report));

        reportView = (RecyclerView) view.findViewById(R.id.report_view);
        layoutManager = new LinearLayoutManager(getActivity());
        reportView.setLayoutManager(layoutManager);

        reportList = new ArrayList<>();
        plainReportList = new ArrayList<>();
        reportViewAdapter = new ReportViewAdapter(reportList, getContext());
        reportView.setAdapter(reportViewAdapter);

        //    HELPER
        sharedPreferences = getContext().getSharedPreferences(Global.PREFERENCES_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();

        //    CONNECTION
        iAPIRequest = Connection.getInstance(getContext()).getiAPIRequest();
        iBaseResponse = this;

        //    OBJECT
        realm = Realm.getDefaultInstance();
        realmReport = new RealmReport(realm);
        if (realmReport.retrieve().size() == 0) {
            reportList = new ArrayList<>();
        } else {
            reportList = realmReport.retrieve();
        }
        reportViewAdapter = new ReportViewAdapter(reportList, getContext());
        reportView.setAdapter(reportViewAdapter);
        if (reportList.size() == 0) {
            swipeRefreshLayout.setRefreshing(true);
            fetchReport();
        }
    }

    private void setEvent() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reportList.clear();
                reportViewAdapter.lastAnimatedPosition = -1;

                fetchReport();
            }
        });
    }

    private void setNullView(boolean visible) {
        if (visible) {
            nullView.setVisibility(View.VISIBLE);
        } else {
            nullView.setVisibility(View.GONE);
        }
    }

    private void saveRecord() {
        realmReport.clear();
        realmReport.save(report.getReportList());
    }

    private void fetchReport() {
        month = sharedPreferences.getInt(Global.MONTH_REPORT, 3);
        year = sharedPreferences.getInt(Global.YEAR_REPORT, 2017);

        if (Connectivity.isConnected(getContext())) {
            iAPIRequest.loadReportList(month, year).enqueue(new Callback<ModelReport>() {
                @Override
                public void onResponse(Call<ModelReport> call, retrofit2.Response<ModelReport> response) {
                    if (response.code() == 200) {
                        iBaseResponse.onSuccess(response.body());
                    } else {
                        iBaseResponse.onFailure(response.message());
                    }
                }

                @Override
                public void onFailure(Call<ModelReport> call, Throwable t) {
                    iBaseResponse.onFailure(t.getMessage());
                    if (t instanceof SocketTimeoutException) {
                        fetchReport();
                    }
                }
            });
        } else {
            ((MainActivity) getActivity()).showNoInternetNotification(new MainActivity.INoInternet() {
                @Override
                public void onRetry() {
                    fetchReport();
                }
            });
        }
    }

    @Override
    public void onSuccess(Response response) {
        swipeRefreshLayout.setRefreshing(false);

        report = (ModelReport) response;
        if (report.getStatus() == 1) {
            if (reportList.size() == 0) {
                reportList = report.getReportList();
            } else {
                reportList.addAll(report.getReportList());
            }
            reportViewAdapter.updateList(reportList);
            setNullView(false);

//            editor.putInt(Global.MONTH_REPORT, report.getCurrentMonth());
//            editor.putInt(Global.YEAR_REPORT, report.getCurrentYear());
//            editor.commit();

            saveRecord();
        } else {
            if (reportList.size() == 0) {
                realmReport.clear();
                setNullView(true);
            } else {
                final Toast toast = Toast.makeText(getActivity(), report.getMessage(), Toast.LENGTH_SHORT);
                Tool.getInstance(getContext()).resizeToast(toast);
                toast.show();
            }
        }
    }

    @Override
    public void onFailure(String message) {
        swipeRefreshLayout.setRefreshing(false);

        if (this.isVisible()) {
            final Toast toast = Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT);
            Tool.getInstance(getContext()).resizeToast(toast);
            toast.show();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, final MenuInflater inflater) {
        inflater.inflate(R.menu.report, menu);

        typedValue = new TypedValue();
        getContext().getTheme().resolveAttribute(android.R.attr.actionBarItemBackground, typedValue, true);

        presentationIcon = (ImageButton) menu.findItem(R.id.action_presentation).getActionView();
        presentationIcon.setLayoutParams(new LinearLayout.LayoutParams((int) getResources().getDimension(R.dimen.action_button_default_width), ViewGroup.LayoutParams.MATCH_PARENT));
        presentationIcon.setImageResource(R.drawable.ic_chart_light);
        presentationIcon.setBackgroundResource(typedValue.resourceId);
        presentationIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getContext(), ActivityPresentation.class);
                decryptDataReport();
                intent.putExtra(Global.PREFERENCES_INTENT_EXTRA, (Serializable) plainReportList);
                startActivity(intent);
            }
        });
        CustomCheatSheet.setup(presentationIcon, getString(R.string.action_presentation));

        super.onCreateOptionsMenu(menu, inflater);
    }

    private void decryptDataReport() {
        for (DataReport dataReport : reportList) {
            DataPlainReport dataPlainReport = new DataPlainReport();
            dataPlainReport.setMonth(dataReport.getMonth());
            dataPlainReport.setYear(dataReport.getYear());

            dataPlainReport.setStudent(dataReport.getStudent());
            dataPlainReport.setMentor(dataReport.getMentor());

            dataPlainReport.setIncome(dataReport.getIncome());
            dataPlainReport.setBalanceRedeemed(dataReport.getBalanceRedeemed());

            dataPlainReport.setMathematicsQuestionPending(dataReport.getMathematicsQuestionPending());
            dataPlainReport.setMathematicsQuestionDiscuss(dataReport.getMathematicsQuestionDiscuss());
            dataPlainReport.setMathematicsQuestionComplete(dataReport.getMathematicsQuestionComplete());

            dataPlainReport.setPhysicsQuestionPending(dataReport.getPhysicsQuestionPending());
            dataPlainReport.setPhysicsQuestionDiscuss(dataReport.getPhysicsQuestionDiscuss());
            dataPlainReport.setPhysicsQuestionComplete(dataReport.getPhysicsQuestionComplete());

            dataPlainReport.setMathematicsBestSolution(dataReport.getMathematicsBestSolution());
            dataPlainReport.setMathematicsTotalSolution(dataReport.getMathematicsTotalSolution());
            dataPlainReport.setPhysicsBestSolution(dataReport.getPhysicsBestSolution());
            dataPlainReport.setPhysicsTotalSolution(dataReport.getPhysicsTotalSolution());

            dataPlainReport.setMathematicsComment(dataReport.getMathematicsComment());
            dataPlainReport.setPhysicsComment(dataReport.getPhysicsComment());

            dataPlainReport.setTransactionPackage1Pending(dataReport.getTransactionPackage1Pending());
            dataPlainReport.setTransactionPackage1Success(dataReport.getTransactionPackage1Success());
            dataPlainReport.setTransactionPackage1Canceled(dataReport.getTransactionPackage1Canceled());

            dataPlainReport.setTransactionPackage2Pending(dataReport.getTransactionPackage2Pending());
            dataPlainReport.setTransactionPackage2Success(dataReport.getTransactionPackage2Success());
            dataPlainReport.setTransactionPackage2Canceled(dataReport.getTransactionPackage2Canceled());

            dataPlainReport.setTransactionPackage3Pending(dataReport.getTransactionPackage3Pending());
            dataPlainReport.setTransactionPackage3Success(dataReport.getTransactionPackage3Success());
            dataPlainReport.setTransactionPackage3Canceled(dataReport.getTransactionPackage3Canceled());

            dataPlainReport.setRedeemBalancePending(dataReport.getRedeemBalancePending());
            dataPlainReport.setRedeemBalanceSuccess(dataReport.getRedeemBalanceSuccess());
            dataPlainReport.setRedeemBalanceCanceled(dataReport.getRedeemBalanceCanceled());

            dataPlainReport.setFeedback(dataReport.getFeedback());
            dataPlainReport.setFreeCredit(dataReport.getFreeCredit());
            dataPlainReport.setBalanceBonus(dataReport.getBalanceBonus());

            plainReportList.add(dataPlainReport);
        }
    }
}