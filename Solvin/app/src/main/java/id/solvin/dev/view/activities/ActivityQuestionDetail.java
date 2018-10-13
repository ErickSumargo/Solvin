package id.solvin.dev.view.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import id.solvin.dev.R;
import id.solvin.dev.helper.ClassApplicationTool;
import id.solvin.dev.helper.ConfigApp;
import id.solvin.dev.helper.Connectivity;
import id.solvin.dev.helper.Global;
import id.solvin.dev.helper.Session;
import id.solvin.dev.model.basic.Auth;
import id.solvin.dev.model.basic.Comment;
import id.solvin.dev.model.basic.Question;
import id.solvin.dev.model.basic.Response;
import id.solvin.dev.model.basic.Solution;
import id.solvin.dev.model.interfaces.IBaseResponse;
import id.solvin.dev.model.response.ResponseQuestion;
import id.solvin.dev.presenter.QuestionPresenter;
import id.solvin.dev.view.adapters.CommentViewAdapter;
import id.solvin.dev.view.adapters.SolutionViewAdapter;
import id.solvin.dev.view.adapters.VoteViewAdapter;
import id.solvin.dev.view.fragments.FragmentQuestionSheet;
import id.solvin.dev.view.widget.ClassRichEditText;
import id.solvin.dev.view.widget.CustomAlertDialog;
import id.solvin.dev.view.widget.CustomProgressDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.onegravity.rteditor.RTManager;
import com.onegravity.rteditor.api.RTApi;
import com.onegravity.rteditor.api.RTMediaFactoryImpl;
import com.onegravity.rteditor.api.RTProxyImpl;
import com.rey.material.widget.CheckBox;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import pl.bclogic.pulsator4droid.library.PulsatorLayout;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Erick Sumargo on 8/31/2016.
 */
public class ActivityQuestionDetail extends AppCompatActivity implements IBaseResponse {
    //    INTERFACE
    public interface INoInternet {
        void onRetry();
    }

    private Toolbar toolbar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private NestedScrollView contentLayout;
    private CircularProgressBar progressView;
    private FragmentQuestionSheet questionSheet;
    private ActivityAnswerSheet answerSheet;
    private ActivityCommentSheet commentSheet;
    private ClassApplicationTool applicationTool;
    private CustomAlertDialog customAlertDialog;
    private CustomProgressDialog customProgressDialog;

    private ClassRichEditText tempRT;

    private RecyclerView solutionView, voteView, commentView;
    private RecyclerView.LayoutManager solutionLayoutManager, voteLayoutManager, commentLayoutManager;

    private List<Solution> solutionList;
    private List<Comment> commentList;

    private SolutionViewAdapter solutionViewAdapter;
    private VoteViewAdapter voteViewAdapter;
    private CommentViewAdapter commentViewAdapter;

    private CoordinatorLayout questionDetailLayout;
    private RelativeLayout avatarLayout;
    private LinearLayout listAnsweringContainer, solutionContainer, commentContainer;
    private RelativeLayout questionImageContainer;
    private CircleImageView userPhoto, mentorAnsweringPhoto;
    private ImageView questionImage, questionAttachment, questionStatus;
    private RelativeLayout imageForeground;
    private TextView avatarInitial, userName, questionTime, questionContent, questionSubject, questionMaterial,
            solutionHeader, commentHeader;
    private Button answer, comment, editQuestion, editSolution;
    private CheckBox voteAgreement;
    private Snackbar snackbar;
    private Toast toast;

    private PulsatorLayout pulsatorLayout;
    private FloatingActionButton vote;

    private Intent intent;
    private Bundle extras;
    private AlertDialog.Builder voteBuilder;
    private AlertDialog voteDialog;
    private GradientDrawable gradientDrawable;
    private LinearLayout.LayoutParams layoutParams;

    private RTApi rtApi;
    private RTManager rtManager;

    private int votedPosition, avatarColor;

    private Question question;
    private QuestionPresenter questionPresenter;
    private ProgressDialog progressDialog;
    private MaterialProgressBar progressBar;

    private int questionStatusDrawable[] = {R.drawable.ic_pending, R.drawable.ic_discussion, R.drawable.ic_complete};
    private int[] flagResourceList = {R.drawable.hashtag_mathematics, R.drawable.hashtag_physics, R.drawable.hashtag_chemistry};
    private int SOLUTION_POSITION = -1;
    private int question_id;

    /**
     * Firebase Realtime Database
     */
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReferencePertanyaan;
    private List<Map<String, String>> onlineMentor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_detail);

        question_id = getIntent().getIntExtra("question_id", -1);
        questionPresenter = new QuestionPresenter(this);

        rtApi = new RTApi(this, new RTProxyImpl(this), new RTMediaFactoryImpl(this, true));
        rtManager = new RTManager(rtApi, savedInstanceState);

        init();
        setEvent();
        if (question_id == -1) {
            attachData((Question) getIntent().getSerializableExtra(ConfigApp.get().KEY_DATA_INTENT));

            setUserMode();
            setData();

            progressView.setVisibility(View.GONE);
            contentLayout.setVisibility(View.VISIBLE);
        } else {
            contentLayout.setVisibility(View.GONE);
            tryRefreshQuestion();
        }
    }

    private void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        questionDetailLayout = (CoordinatorLayout) findViewById(R.id.question_detail_layout);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.question_detail_swipe_refresh);
        contentLayout = (NestedScrollView) findViewById(R.id.question_detail_content_layout);
        progressView = (CircularProgressBar) findViewById(R.id.question_detail_progress_view);
        avatarLayout = (RelativeLayout) findViewById(R.id.detail_avatar_layout);
        questionImageContainer = (RelativeLayout) findViewById(R.id.detail_question_image_container);
        listAnsweringContainer = (LinearLayout) findViewById(R.id.detail_list_answering_container);
        solutionContainer = (LinearLayout) findViewById(R.id.detail_solution_container);
        solutionView = (RecyclerView) findViewById(R.id.detail_solution_view);
        commentContainer = (LinearLayout) findViewById(R.id.detail_comment_container);
        commentView = (RecyclerView) findViewById(R.id.detail_comment_view);

        pulsatorLayout = (PulsatorLayout) findViewById(R.id.detail_pulsator_layout);
        pulsatorLayout.start();
        vote = (FloatingActionButton) findViewById(R.id.detail_vote);

        userPhoto = (CircleImageView) findViewById(R.id.detail_user_photo);
        userName = (TextView) findViewById(R.id.detail_user_name);
        avatarInitial = (TextView) findViewById(R.id.detail_avatar_initial);
        questionTime = (TextView) findViewById(R.id.detail_question_time);
        questionAttachment = (ImageView) findViewById(R.id.detail_question_attachment);
        questionStatus = (ImageView) findViewById(R.id.detail_question_status);
        questionContent = (TextView) findViewById(R.id.detail_question_content);
        questionImage = (ImageView) findViewById(R.id.detail_question_image);
        imageForeground = (RelativeLayout) findViewById(R.id.detail_question_image_foreground);
        questionSubject = (TextView) findViewById(R.id.detail_question_subject);
        questionMaterial = (TextView) findViewById(R.id.detail_question_material);
        answer = (Button) findViewById(R.id.detail_answer);
        progressBar = (MaterialProgressBar) findViewById(R.id.question_detail_horizontal_progressbar);

        solutionHeader = (TextView) findViewById(R.id.detail_solution_header);

        editQuestion = (Button) findViewById(R.id.detail_edit_question);
        editSolution = (Button) findViewById(R.id.detail_edit_solution);

        commentHeader = (TextView) findViewById(R.id.detail_comment_header);
        comment = (Button) findViewById(R.id.detail_comment);

        tempRT = (ClassRichEditText) findViewById(R.id.temp_rt);
        rtManager.registerEditor(tempRT, true);

        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        if (getResources().getBoolean(R.bool.isTablet)) {
            swipeRefreshLayout.setSize(SwipeRefreshLayout.LARGE);
        }

        progressBar.setVisibility(View.INVISIBLE);

        questionSheet = new FragmentQuestionSheet();
        answerSheet = new ActivityAnswerSheet();
        commentSheet = new ActivityCommentSheet();
        applicationTool = new ClassApplicationTool(getApplicationContext());
    }

    private void setData() {
        solutionLayoutManager = new LinearLayoutManager(this);
        solutionLayoutManager.setAutoMeasureEnabled(true);

        solutionViewAdapter = new SolutionViewAdapter(solutionList, tempRT);
        solutionView.setLayoutManager(solutionLayoutManager);
        solutionView.setAdapter(solutionViewAdapter);
        solutionView.setNestedScrollingEnabled(false);

        commentLayoutManager = new LinearLayoutManager(this);
        commentLayoutManager.setAutoMeasureEnabled(true);

        commentViewAdapter = new CommentViewAdapter(question, tempRT, this);
        commentView.setLayoutManager(commentLayoutManager);
        commentView.setAdapter(commentViewAdapter);
        commentView.setNestedScrollingEnabled(false);

        if (question.getStudent().getPhoto().isEmpty()) {
            avatarInitial.setText(Global.get().getInitialName(question.getStudent().getName()));
            avatarColor = applicationTool.getAvatarColor(question.getStudent().getId());
            gradientDrawable = (GradientDrawable) avatarLayout.getBackground();
            gradientDrawable.setColor(avatarColor);

            avatarLayout.setVisibility(View.VISIBLE);
            userPhoto.setVisibility(View.GONE);
        } else {
            Picasso.with(getApplicationContext())
                    .load(Global.get().getAssetURL(question.getStudent().getPhoto(), Auth.AUTH_TYPE_STUDENT))
                    .fit()
                    .centerCrop()
                    .placeholder(R.drawable.operator)
                    .into(userPhoto);

            avatarLayout.setVisibility(View.GONE);
            userPhoto.setVisibility(View.VISIBLE);
        }

        userName.setText(question.getStudent().getName());
        questionTime.setText(Global.get().convertDateToFormat(question.getCreated_at(), "EEE, dd MMM ''yy-HH:mm") + " WIB");

        questionAttachment.setVisibility(TextUtils.isEmpty(question.getImage()) ? View.GONE : View.VISIBLE);
        questionImageContainer.setVisibility(TextUtils.isEmpty(question.getImage()) ? View.GONE : View.VISIBLE);

        questionStatus.setImageResource(questionStatusDrawable[question.getStatus()]);
        if (!question.getContent().isEmpty()) {
            tempRT.setRichTextEditing(true, question.getContent());
            tempRT.setText(ClassApplicationTool.with(getApplicationContext()).loadTextStyle(tempRT.getText()));

            questionContent.setText(tempRT.getText());
            questionContent.setVisibility(View.VISIBLE);
        } else {
            questionContent.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(question.getImage())) {
            Picasso.with(getApplicationContext()).load(Global.get().getAssetURL(question.getImage(), "question"))
                    .placeholder(R.drawable.image_placeholder)
                    .fit()
                    .centerCrop()
                    .into(questionImage);
        }
        questionSubject.setText(question.getMaterial().getSubject().getName());
        if (question.getOther().isEmpty()) {
            questionMaterial.setText(question.getMaterial().getName());
        } else {
            questionMaterial.setText(question.getOther());
        }
        final int subjectType = question.getMaterial().getSubject().getId();
        questionSubject.setBackgroundResource(flagResourceList[subjectType - 1]);
        questionMaterial.setBackgroundResource(flagResourceList[subjectType - 1]);


        if (question.getSolutions().size() > 0) {
            solutionContainer.setVisibility(View.VISIBLE);
            solutionHeader.setText(question.getSolutions().size() + " Solusi");
        } else {
            solutionContainer.setVisibility(View.GONE);
        }
        if (question.getComments().size() > 0) {
            commentContainer.setVisibility(View.VISIBLE);
            commentHeader.setText(question.getComments().size() + " Komentar");
        } else {
            commentContainer.setVisibility(View.GONE);
        }
    }

    private void tryRefreshQuestion() {
        if (Connectivity.isConnected(getApplicationContext())) {
            questionPresenter.doRefreshQuestion(question_id, null, getApplicationContext());
        } else {
            showNotificationNetwork(new INoInternet() {
                @Override
                public void onRetry() {
                    tryRefreshQuestion();
                }
            });
        }
    }

    private void showDialogVote() {
        voteBuilder = new AlertDialog.Builder(ActivityQuestionDetail.this);
        voteBuilder.setPositiveButton("Kirim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        voteBuilder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        voteDialog = voteBuilder.create();
        voteDialog.setView(voteDialog.getLayoutInflater().inflate(R.layout.activity_vote_solution, null));
        voteDialog.setCanceledOnTouchOutside(false);
        voteDialog.show();
        applicationTool.resizeAlertDialog(voteDialog);

        voteDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
        voteDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customAlertDialog = new CustomAlertDialog(ActivityQuestionDetail.this);
                customAlertDialog.setTitle("Konfirmasi");
                customAlertDialog.setMessage("Anda akan mem-vote nama mentor yang terpilih sebagai solusi yang terbaik?");
                customAlertDialog.setNegativeButton("Tidak", new CustomAlertDialog.OnNegativeClickListener() {
                    @Override
                    public void onClick() {
                        customAlertDialog.dismiss();
                    }
                });
                customAlertDialog.setPositiveButton("Ya", new CustomAlertDialog.OnPositiveClickListener() {
                    @Override
                    public void onClick() {
                        voteDialog.dismiss();
                        customAlertDialog.dismiss();

                        voteBestSolution();
                    }
                });
            }
        });

        voteDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                voteDialog.dismiss();
            }
        });

        voteView = (RecyclerView) voteDialog.findViewById(R.id.vote_view);
        voteAgreement = (CheckBox) voteDialog.findViewById(R.id.vote_agreement);
        setVoteAgreementDisabled();

        voteLayoutManager = new LinearLayoutManager(this);
        voteLayoutManager.setAutoMeasureEnabled(true);

        voteViewAdapter = new VoteViewAdapter(solutionList);
        voteView.setLayoutManager(voteLayoutManager);
        voteView.setAdapter(voteViewAdapter);
        voteView.setNestedScrollingEnabled(false);

        voteViewAdapter.getVotedPosition(new VoteViewAdapter.OnSetVotedPosition() {
            @Override
            public void IGetVotedPosition(int position) {
                votedPosition = position;
                setVoteAgreementEnabled();
            }
        });

        voteAgreement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (voteAgreement.isChecked()) {
                    voteDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                } else {
                    voteDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                }
            }
        });
    }

    private void voteBestSolution() {
        customProgressDialog = new CustomProgressDialog(ActivityQuestionDetail.this);
        customProgressDialog.setMessage("Vote solusi terbaik...");

        if (Connectivity.isConnected(getApplicationContext())) {
            questionPresenter.doSelectBest(question.getSolutions().get(votedPosition), getApplicationContext());
        } else {
            customProgressDialog.dismiss();

            showNotificationNetwork(new INoInternet() {
                @Override
                public void onRetry() {
                    voteBestSolution();
                }
            });
        }
    }

    private void showNotificationNetwork(final INoInternet iNoInternet) {
        snackbar = Snackbar.make(questionDetailLayout, getResources().getString(R.string.text_no_internet), Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("Coba Lagi", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iNoInternet.onRetry();
            }
        });
        applicationTool.resizeSnackBarWithCallBack(snackbar);
        snackbar.show();
    }

    private void setUserMode() {
        if (Session.with(getApplicationContext()).getLoginType() == ConfigApp.get().STUDENT) {
            if (Session.with(getApplicationContext()).getAuth().getId() == question.getStudent().getId()) {
                // Check bila sudah ada jawaban
                pulsatorLayout.setVisibility(question.getStatus() == 1 ? View.VISIBLE : View.GONE);
                if (question.getStatus() == 0) { // Pending
                    editQuestion.setVisibility(View.VISIBLE);
                } else {
                    editQuestion.setVisibility(View.GONE);
                }
                if (question.getStatus() < 2) {
                    firebaseAuth = FirebaseAuth.getInstance();
                    firebaseDatabase = FirebaseDatabase.getInstance();
                    databaseReferencePertanyaan = firebaseDatabase.getReference("question").child(String.valueOf(question.getId()));
                    databaseReferencePertanyaan.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            listAnsweringContainer.removeAllViewsInLayout();

                            if (dataSnapshot.hasChildren()) {
                                onlineMentor = new ArrayList<Map<String, String>>();
                                if (dataSnapshot.child("mentor").getValue() instanceof HashMap) {
                                    for (Map.Entry<String, HashMap> entry : ((HashMap<String, HashMap>) dataSnapshot.child("mentor").getValue()).entrySet()) {
//                                        String key = entry.getKey();
//                                        HashMap value = entry.getValue();
                                        onlineMentor.add(entry.getValue());
                                        // do what you have to do here
                                        // In your case, an other loop.
                                    }
                                } else {
                                    onlineMentor = (ArrayList<Map<String, String>>) dataSnapshot.child("mentor").getValue();
                                }


                                int size = 0;
                                for (Map<String, String> subdata : onlineMentor) {
                                    if (subdata != null) {
                                        addMentorAnswering(subdata.get("image"));
                                        size++;
                                    }
                                }

                                if (size > 0) {
                                    solutionContainer.setVisibility(View.VISIBLE);
                                    if (question.getSolutions().size() > 0) {
                                        solutionHeader.setText(question.getSolutions().size() + " Solusi");
                                    } else {
                                        solutionHeader.setText("Solusi");
                                    }
                                }

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            } else {
                editQuestion.setVisibility(View.GONE);
                pulsatorLayout.setVisibility(View.GONE);
            }
            answer.setVisibility(View.GONE);
            editSolution.setVisibility(View.GONE);
        } else {
            editQuestion.setVisibility(View.GONE);
            editSolution.setVisibility(View.GONE);

            if (solutionList.size() < 3) {
                answer.setVisibility(View.VISIBLE);
            } else {
                answer.setVisibility(View.GONE);
            }
            SOLUTION_POSITION = -1;
            for (Solution solution : solutionList) {
                SOLUTION_POSITION++;
                if (solution.getMentor().getId() == Session.with(getApplicationContext()).getAuth().getId()) {
                    answer.setVisibility(View.GONE);
                    editSolution.setVisibility(View.VISIBLE);
                    break;
                }
            }
            if (question.getStatus() == 2) {
                answer.setVisibility(View.GONE);
                editSolution.setVisibility(View.GONE);
            }
            pulsatorLayout.setVisibility(View.GONE);
        }
    }

    private void attachData(Question data) {
        question = data;
        solutionList = question.getSolutions();
        commentList = question.getComments();
    }

    private void setEvent() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

            }
        });

        imageForeground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getApplication(), ActivityFullScreen.class);
                intent.putExtra("image", question.getImage());
                intent.putExtra("category", "question");
                startActivity(intent);
            }
        });

        answer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getApplication(), ActivityAnswerSheet.class);
                intent.putExtra(Global.get().key().QUESTION_MODE, Global.get().value().QUESTION_MODE_CREATE);        /*Creating Activity*/
                intent.putExtra(Global.get().key().QUESTION_DATA, question);
                startActivityForResult(intent, Global.TRIGGGET_UPDATE);
            }
        });

        editQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getApplication(), ActivityCreateQuestion.class);
                intent.putExtra(Global.get().key().QUESTION_MODE, Global.get().value().QUESTION_MODE_EDIT);        /*Editing Activity*/
                intent.putExtra("QUESTION_CONTENT", questionContent.getText());
                intent.putExtra(Global.get().key().QUESTION_DATA, question);
                startActivityForResult(intent, Global.TRIGGGET_UPDATE);
            }
        });

        editSolution.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getApplication(), ActivityAnswerSheet.class);
                intent.putExtra("SOLUTION_MODE", 1);
                intent.putExtra(Global.get().key().QUESTION_DATA_POSITION, SOLUTION_POSITION);
                intent.putExtra(Global.get().key().QUESTION_DATA, question);
                startActivityForResult(intent, Global.TRIGGGET_UPDATE);
            }
        });

        vote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listAnsweringContainer.getChildCount() == 0) {
                    showDialogVote();
                } else {
                    showVoteDisabledMessage();
                }
            }
        });

        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getApplication(), ActivityCommentSheet.class);
                intent.putExtra("COMMENT_MODE", 0);
                intent.putExtra(Global.get().key().QUESTION_DATA, question);
                startActivityForResult(intent, Global.TRIGGGET_UPDATE);
            }
        });

        questionSheet.setQuestionEdittedSent(new FragmentQuestionSheet.OnQuestionEdittedSent() {
            @Override
            public void ISentConfirmedEditted() {
                showNotificationQuestionEditted();
            }
        });

        answerSheet.setSolutionSent(new ActivityAnswerSheet.OnSolutionSent() {
            @Override
            public void ISentConfirmedCreated() {
                showNotificationSolutionSent();
            }

            @Override
            public void ISentConfirmedEditted() {
                showNotificationSolutionEditted();
            }
        });

        commentSheet.setCommentSent(new ActivityCommentSheet.OnCommentSent() {
            @Override
            public void ISentConfirmedCreated() {
                showNotificationCommentSent();
            }

            @Override
            public void ISentConfirmedEditted() {
                showNotificationCommentEditted();
            }
        });
    }

    private void showVoteDisabledMessage() {
        customAlertDialog = new CustomAlertDialog(ActivityQuestionDetail.this);
        customAlertDialog.setTitle("Pesan");
        customAlertDialog.setMessage("Maaf, vote untuk solusi terbaik belum dapat dilakukan selama masih terdapat mentor yang sedang menjawab pertanyaan anda");
        customAlertDialog.setNegativeButton("", null);
        customAlertDialog.setPositiveButton("Tutup", new CustomAlertDialog.OnPositiveClickListener() {
            @Override
            public void onClick() {
                customAlertDialog.dismiss();
            }
        });
    }

    private void showNotificationQuestionEditted() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                snackbar = Snackbar.make(questionDetailLayout, "Pertanyaan telah berhasil diedit", Snackbar.LENGTH_LONG);
                applicationTool.resizeSnackBar(snackbar, 1);
                snackbar.show();
            }
        }, 500);
    }

    private void showNotificationSolutionSent() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                snackbar = Snackbar.make(questionDetailLayout, "Solusi telah diajukan", Snackbar.LENGTH_LONG);
                applicationTool.resizeSnackBar(snackbar, 1);
                snackbar.show();
            }
        }, 500);
    }

    private void showNotificationSolutionEditted() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                snackbar = Snackbar.make(questionDetailLayout, "Solusi telah berhasil diedit", Snackbar.LENGTH_LONG);
                applicationTool.resizeSnackBar(snackbar, 1);
                snackbar.show();
            }
        }, 500);
    }

    private void showNotificationCommentSent() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                snackbar = Snackbar.make(questionDetailLayout, "Komentar telah ditambahkan", Snackbar.LENGTH_LONG);
                applicationTool.resizeSnackBar(snackbar, 1);
                snackbar.show();
            }
        }, 500);
    }

    private void showNotificationCommentEditted() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                snackbar = Snackbar.make(questionDetailLayout, "Komentar telah berhasil diedit", Snackbar.LENGTH_LONG);
                applicationTool.resizeSnackBar(snackbar, 1);
                snackbar.show();
            }
        }, 500);
    }

    private void setVoteAgreementEnabled() {
        voteAgreement.setEnabled(true);
        voteAgreement.setAlpha(1f);
    }

    private void setVoteAgreementDisabled() {
        voteAgreement.setEnabled(false);
        voteAgreement.setAlpha(0.25f);
    }

    private void addMentorAnswering(String image) {
        layoutParams = new LinearLayout.LayoutParams((int) getResources().getDimension(R.dimen.circle_image_view_small_radius),
                (int) getResources().getDimension(R.dimen.circle_image_view_small_radius));
        layoutParams.setMargins((int) getResources().getDimension(R.dimen.default_margin_4m), 0, 0, 0);
        mentorAnsweringPhoto = new CircleImageView(ActivityQuestionDetail.this);
        mentorAnsweringPhoto.setLayoutParams(layoutParams);

        Picasso.with(getApplicationContext())
                .load(Global.get().getAssetURL(image, "mentor"))
                .placeholder(R.drawable.operator)
                .fit()
                .centerCrop()
                .into(mentorAnsweringPhoto);
        listAnsweringContainer.addView(mentorAnsweringPhoto);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Global.TRIGGGET_UPDATE:
                    doRefreshData();
                    setResult(RESULT_OK);
                    break;
            }
        }
    }

    private void doRefreshData() {
        if (Connectivity.isConnected(getApplicationContext())) {
            questionPresenter.doRefreshQuestion(question.getId(), progressBar, getApplicationContext());
        } else {
            showNotificationNetwork(new INoInternet() {
                @Override
                public void onRetry() {
                    doRefreshData();
                }
            });
        }
    }

    @Override
    public void onSuccess(Response response) {
        if (response.getTag() != null) {
            if (response.getTag().equals("vote")) {
                if (customProgressDialog != null) {
                    customProgressDialog.dismiss();
                }
                progressBar.setVisibility(View.INVISIBLE);

                pulsatorLayout.setVisibility(View.GONE);

                toast = Toast.makeText(ActivityQuestionDetail.this, "Vote solusi terbaik berhasil dikirimkan", Toast.LENGTH_SHORT);
                applicationTool.resizeToast(toast);
                toast.show();

                doRefreshData();
                setResult(RESULT_OK);
            }
        } else {
            attachData(((ResponseQuestion) response).getData().getQuestion());
            setUserMode();
            setData();

            progressView.setVisibility(View.GONE);
            contentLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onFailure(String message) {
        if (progressBar != null) {
            progressBar.setVisibility(View.INVISIBLE);
        }
        if (customProgressDialog != null) {
            customProgressDialog.dismiss();
        }
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }

        toast = Toast.makeText(ActivityQuestionDetail.this, message, Toast.LENGTH_SHORT);
        applicationTool.resizeToast(toast);
        toast.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        rtManager.onDestroy(isFinishing());
    }
}