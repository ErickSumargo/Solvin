package id.solvinap.dev.view.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import id.solvinap.dev.R;
import id.solvinap.dev.server.api.Connection;
import id.solvinap.dev.server.api.Response;
import id.solvinap.dev.server.data.DataQuestion;
import id.solvinap.dev.server.data.DataSolution;
import id.solvinap.dev.server.helper.Connectivity;
import id.solvinap.dev.server.helper.Global;
import id.solvinap.dev.server.interfaces.IAPIRequest;
import id.solvinap.dev.server.interfaces.IBaseResponse;
import id.solvinap.dev.server.model.ModelSolution;
import id.solvinap.dev.view.adapter.VoteSolutionViewAdapter;
import id.solvinap.dev.view.helper.Tool;
import id.solvinap.dev.view.widget.CustomRichEditText;
import com.onegravity.rteditor.RTManager;
import com.onegravity.rteditor.api.RTApi;
import com.onegravity.rteditor.api.RTMediaFactoryImpl;
import com.onegravity.rteditor.api.RTProxyImpl;
import com.squareup.picasso.Picasso;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Erick Sumargo on 2/20/2017.
 */

public class ActivityVoteSolution extends AppCompatActivity implements IBaseResponse {
    //    INTERFACE
    public interface INoInternet {
        void onRetry();
    }

    public interface OnVotedListener {
        void OnVoted();
    }

    private static OnVotedListener mVotedListener;

    public void setOnVotedListener(OnVotedListener listener) {
        mVotedListener = listener;
    }

    private void voted() {
        if (mVotedListener != null) {
            mVotedListener.OnVoted();
        }
    }

    //    VIEW
    private Toolbar toolbar;
    private CoordinatorLayout mainLayout;
    private CircleImageView studentPhoto;
    private RelativeLayout studentAvatarLayout, questionImageContainer,
            questionImageForeground;
    private TextView studentAvatarInitial, studentName, questionTime, questionContent,
            questionSubject, questionMaterial;
    private ImageView questionAttachment, questionStatus, questionImage;

    private RecyclerView voteSolutionView;
    private View loadingContainer;

    private CustomRichEditText tempRT;

    //    HELPER
    private LinearLayoutManager layoutManager;
    private Intent intent;

    private RTApi rtApi;
    private RTManager rtManager;

    private Tool tool;
    private GradientDrawable gradientDrawable;

    //    CONNECTION
    private IAPIRequest iAPIRequest;
    private IBaseResponse iBaseResponse;

    //    OBJECT
    private ModelSolution solution;
    private List<DataSolution> solutionList;
    private VoteSolutionViewAdapter voteSolutionViewAdapter;

    private DataQuestion dataQuestion;

    //    VARIABLE
    private int subjectType;

    private int[] statusResourceList = {R.drawable.ic_pending, R.drawable.ic_discussion, R.drawable.ic_complete};
    private int[] flagResourceList = {R.drawable.hashtag_mathematics, R.drawable.hashtag_physics};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote_solution);

        rtApi = new RTApi(this, new RTProxyImpl(this), new RTMediaFactoryImpl(this, true));
        rtManager = new RTManager(rtApi, savedInstanceState);

        init();
        setEvent();
        fetchData();
    }

    private void init() {
        //    VIEW
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mainLayout = (CoordinatorLayout) findViewById(R.id.vote_solution_main_layout);
        studentPhoto = (CircleImageView) findViewById(R.id.vote_student_photo);
        studentAvatarLayout = (RelativeLayout) findViewById(R.id.vote_student_avatar_layout);
        studentAvatarInitial = (TextView) findViewById(R.id.vote_student_avatar_initial);
        studentName = (TextView) findViewById(R.id.vote_student_name);
        questionTime = (TextView) findViewById(R.id.vote_question_time);
        questionAttachment = (ImageView) findViewById(R.id.vote_question_attachment);
        questionStatus = (ImageView) findViewById(R.id.vote_question_status);
        questionContent = (TextView) findViewById(R.id.vote_question_content);
        questionImageContainer = (RelativeLayout) findViewById(R.id.vote_question_image_container);
        questionImage = (ImageView) findViewById(R.id.vote_question_image);
        questionImageForeground = (RelativeLayout) findViewById(R.id.vote_question_image_foreground);
        questionSubject = (TextView) findViewById(R.id.vote_question_subject);
        questionMaterial = (TextView) findViewById(R.id.vote_question_material);
        loadingContainer = findViewById(R.id.loading_view_container);

        tempRT = (CustomRichEditText) findViewById(R.id.temp_rt);
        rtManager.registerEditor(tempRT, true);

        voteSolutionView = (RecyclerView) findViewById(R.id.vote_solution_view);
        solutionList = new ArrayList<>();
        layoutManager = new LinearLayoutManager(getApplicationContext());
        voteSolutionView.setLayoutManager(layoutManager);

        voteSolutionViewAdapter = new VoteSolutionViewAdapter(solutionList, tempRT);
        voteSolutionView.setAdapter(voteSolutionViewAdapter);

        //    HELPER
        tool = new Tool(getApplicationContext());

        //    CONNECTION
        iAPIRequest = Connection.getInstance(getApplicationContext()).getiAPIRequest();
        iBaseResponse = this;

        //    OBJECT
        dataQuestion = (DataQuestion) getIntent().getSerializableExtra(Global.PREFERENCES_INTENT_EXTRA);

        if (dataQuestion.getDataStudent().getPhoto().length() > 0) {
            Picasso.with(getApplicationContext()).load(Global.ASSETS_URL + "student" + "/" + dataQuestion.getDataStudent().getPhoto())
                    .placeholder(R.drawable.operator)
                    .fit()
                    .centerCrop()
                    .into(studentPhoto);

            studentAvatarLayout.setVisibility(View.GONE);
            studentPhoto.setVisibility(View.VISIBLE);
        } else {
            gradientDrawable = (GradientDrawable) studentAvatarLayout.getBackground();
            gradientDrawable.setColor(tool.getAvatarColor(dataQuestion.getDataStudent().getId()));

            studentAvatarInitial.setText(tool.getInitialName(dataQuestion.getDataStudent().getName()));

            studentAvatarLayout.setVisibility(View.VISIBLE);
            studentPhoto.setVisibility(View.GONE);
        }

        studentName.setText(dataQuestion.getDataStudent().getName());
        questionTime.setText(tool.convertToDateTimeFormat(0, dataQuestion.getCreatedAt()));
        if (dataQuestion.getImage().length() > 0) {
            questionAttachment.setVisibility(View.VISIBLE);
            questionImageContainer.setVisibility(View.VISIBLE);

            Picasso.with(getApplicationContext()).load(Global.ASSETS_URL + "question" + "/" + dataQuestion.getImage())
                    .placeholder(R.drawable.image_placeholder)
                    .fit()
                    .centerCrop()
                    .into(questionImage);
        }
        questionStatus.setImageResource(statusResourceList[dataQuestion.getStatus()]);
        if (!dataQuestion.getContent().isEmpty()) {
            tempRT.setRichTextEditing(true, dataQuestion.getContent());
            tempRT.setText(tool.loadTextStyle(tempRT.getText()));

            questionContent.setText(tempRT.getText());
            questionContent.setVisibility(View.VISIBLE);
        } else {
            questionContent.setVisibility(View.GONE);
        }

        subjectType = dataQuestion.getDataMaterial().getDataSubject().getId();
        questionSubject.setBackgroundResource(flagResourceList[subjectType - 1]);
        questionMaterial.setBackgroundResource(flagResourceList[subjectType - 1]);

        questionSubject.setText(dataQuestion.getDataMaterial().getDataSubject().getName());
        questionMaterial.setText(dataQuestion.getDataMaterial().getName());
    }

    private void setEvent() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        questionImageForeground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getApplication(), ActivityImageFullScreen.class);
                intent.putExtra("image", dataQuestion.getImage());
                intent.putExtra("category", "question");

                startActivity(intent);
            }
        });

        voteSolutionViewAdapter.setOnVoteSolutionListener(new VoteSolutionViewAdapter.OnVoteSolutionListener() {
            @Override
            public void OnVoteSolution(String message, int questionId, int solutionId, int mentorId) {
                voteBestSolution(message, questionId, solutionId, mentorId);
            }
        });
    }

    public void fetchData() {
        if (Connectivity.isConnected(getApplicationContext())) {
            iAPIRequest.loadPriorityQuestionDetail(String.valueOf(dataQuestion.getId())).enqueue(new Callback<ModelSolution>() {
                @Override
                public void onResponse(Call<ModelSolution> call, retrofit2.Response<ModelSolution> response) {
                    if (response.code() == 200) {
                        iBaseResponse.onSuccess(response.body());
                    } else {
                        iBaseResponse.onFailure(response.message());
                    }
                }

                @Override
                public void onFailure(Call<ModelSolution> call, Throwable t) {
                    iBaseResponse.onFailure(t.getMessage());
                    if (t instanceof SocketTimeoutException) {
                        fetchData();
                    }
                }
            });
        } else {
            showNoInternetNotification(new INoInternet() {
                @Override
                public void onRetry() {
                    fetchData();
                }
            });
        }
    }

    private void voteBestSolution(final String message, final int questionId, final int solutionId, final int mentorId) {
        voteSolutionViewAdapter.showCustomProgressDialog(message);
        if (Connectivity.isConnected(getApplicationContext())) {
            iAPIRequest.voteBestSolution(String.valueOf(questionId), String.valueOf(solutionId), String.valueOf(mentorId)).enqueue(new Callback<Response>() {
                @Override
                public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                    if (response.code() == 200) {
                        finish();
                        voted();
                    } else {
                        iBaseResponse.onFailure(response.message());
                    }
                }

                @Override
                public void onFailure(Call<Response> call, Throwable t) {
                    iBaseResponse.onFailure(t.getMessage());
                }
            });
        } else {
            voteSolutionViewAdapter.customProgressDialog.dismiss();
            showNoInternetNotification(new INoInternet() {
                @Override
                public void onRetry() {
                    voteBestSolution(message, questionId, solutionId, mentorId);
                }
            });
        }
    }

    private void showNoInternetNotification(final INoInternet iNoInternet) {
        final Snackbar snackbar = Snackbar.make(mainLayout, getResources().getString(R.string.text_no_internet), Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("Coba Lagi", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iNoInternet.onRetry();
            }
        });
        Tool.getInstance(getApplicationContext()).resizeSnackBarWithCallBack(snackbar);
        snackbar.show();
    }

    @Override
    public void onSuccess(Response response) {
        if (response instanceof ModelSolution) {
            solution = (ModelSolution) response;
            final int size = solution.getSolutionList().size();
            if (size > 0) {
                solutionList = solution.getSolutionList();
                voteSolutionViewAdapter.addList(solutionList, dataQuestion.getId());

                voteSolutionView.setVisibility(View.VISIBLE);
                loadingContainer.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onFailure(String message) {
        final Toast toast = Toast.makeText(ActivityVoteSolution.this, message, Toast.LENGTH_SHORT);
        Tool.getInstance(getApplicationContext()).resizeToast(toast);
        toast.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        rtManager.onDestroy(isFinishing());
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}