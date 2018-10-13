package id.solvin.dev.view.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import id.solvin.dev.R;
import id.solvin.dev.helper.ClassApplicationTool;
import id.solvin.dev.model.basic.DraftSolution;
import id.solvin.dev.realm.helper.RealmDraftSolution;
import id.solvin.dev.view.activities.ActivityAnswerSheet;
import id.solvin.dev.view.widget.CustomAlertDialog;
import id.solvin.dev.view.widget.CustomOptionDialog;

import static android.widget.Toast.makeText;

/**
 * Created by Erick Sumargo on 4/6/2017.
 */

public class DraftSolutionViewAdapter extends RecyclerView.Adapter<DraftSolutionViewAdapter.ViewHolder> {
    private static OnGetCheckedDraftPosition mGetCheckedDraftPosition;
    private static OnCloseOpenFileDialog mCloseOpenFileDialog;

    public interface OnGetCheckedDraftPosition {
        void IGetCheckedDraftPosition(int id);
    }

    public interface OnCloseOpenFileDialog {
        void ICloseOpenFileDialog();
    }

    public void setOnGetCheckedDraftPosition(OnGetCheckedDraftPosition listener) {
        mGetCheckedDraftPosition = listener;
    }

    public void setOnCloseOpenFileDialog(OnCloseOpenFileDialog listener) {
        mCloseOpenFileDialog = listener;
    }

    public void setCheckedDraftPosition(int id) {
        if (mGetCheckedDraftPosition != null) {
            mGetCheckedDraftPosition.IGetCheckedDraftPosition(id);
        }
    }

    public void closeOpenFileDialog() {
        if (mCloseOpenFileDialog != null) {
            mCloseOpenFileDialog.ICloseOpenFileDialog();
        }
    }

    //    VIEW
    private CustomOptionDialog customOptionDialog;
    private AlertDialog.Builder renameFileBuilder;
    private AlertDialog renameFileDialog;

    private TextView titleFile;

    //    TEMP
    private RelativeLayout clickedGroup, lastClickedGroup = null;
    private RadioButton lastCheckedRadio;

    //    HELPER
    private Context context;

    //    OBJECT
    private RealmDraftSolution realmDraftSolution;
    private List<DraftSolution> draftSolutionList;

    //    VARIABLE
    private int id;
    private boolean opened;
    private String[] dateParts;

    public DraftSolutionViewAdapter(RealmDraftSolution realmDraftSolution, int id, boolean opened) {
        this.realmDraftSolution = realmDraftSolution;
        this.id = id;
        this.opened = opened;

        draftSolutionList = realmDraftSolution.retrieve();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout group;
        private RadioButton radio;
        private TextView title, updated_at;
        private ImageView attachment;

        public ViewHolder(View view) {
            super(view);
            context = view.getContext();

            group = (RelativeLayout) view.findViewById(R.id.draft_solution_group);
            radio = (RadioButton) view.findViewById(R.id.draft_solution_radio);
            title = (TextView) view.findViewById(R.id.draft_solution_title);
            attachment = (ImageView) view.findViewById(R.id.draft_solution_attachment);
            updated_at = (TextView) view.findViewById(R.id.draft_solution_time);
        }
    }

    @Override
    public DraftSolutionViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.draft_solution_list, parent, false);

        return new DraftSolutionViewAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final DraftSolutionViewAdapter.ViewHolder holder, final int position) {
        final DraftSolution draftSolution = draftSolutionList.get(position);

        holder.title.setText(draftSolution.getTitle());
        holder.attachment.setVisibility(draftSolution.getImages().size() > 0 ? View.VISIBLE : View.GONE);

        dateParts = draftSolution.getUpdated_at().split("-");
        holder.updated_at.setText(dateParts[0] + " pada " + dateParts[1] + " WIB");

        if (id == draftSolution.getId()) {
            setItemChecked(holder, draftSolution.getId());
        }

        holder.group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemChecked(holder, draftSolution.getId());
            }
        });

        holder.group.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                customOptionDialog = new CustomOptionDialog(context);
                customOptionDialog.setTitle("Opsi Aksi");
                customOptionDialog.setOnRenameListener(new CustomOptionDialog.OnRenameClickListener() {
                    @Override
                    public void onClick() {
                        customOptionDialog.dismiss();
                        showRenameDraftDialog(holder, draftSolution.getId(), draftSolution.getTitle());
                    }
                });

                customOptionDialog.setOnDeleteListener(new CustomOptionDialog.OnDeleteClickListener() {
                    @Override
                    public void onClick() {
                        if (id == draftSolution.getId() && opened) {
                            final Toast toast = Toast.makeText(context, "Aksi Hapus Gagal. Draft Sedang Dibuka", Toast.LENGTH_SHORT);
                            ClassApplicationTool.with(context).resizeToast(toast);
                            toast.show();

                            customOptionDialog.dismiss();
                        } else {
                            draftSolutionList.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, draftSolutionList.size());

                            realmDraftSolution.remove(draftSolution.getId());

                            customOptionDialog.dismiss();
                            if (draftSolutionList.size() == 0) {
                                closeOpenFileDialog();
                            }

                            final Toast toast = Toast.makeText(context, "Draft Dihapus", Toast.LENGTH_SHORT);
                            ClassApplicationTool.with(context).resizeToast(toast);
                            toast.show();
                        }
                    }
                });
                return false;
            }
        });
    }

    private void setItemChecked(ViewHolder holder, int id) {
        clickedGroup = holder.group;
        holder.group.setBackgroundResource(R.drawable.custom_radio_selected);
        holder.radio.setChecked(true);
        if (lastClickedGroup != null && lastClickedGroup != holder.group) {
            lastClickedGroup.setBackgroundResource(R.drawable.custom_background_pressed);
            lastCheckedRadio.setChecked(false);
        }
        lastClickedGroup = clickedGroup;
        lastCheckedRadio = holder.radio;

        setCheckedDraftPosition(id);
    }

    private void showRenameDraftDialog(final ViewHolder holder, final int id, String title) {
        renameFileBuilder = new AlertDialog.Builder(context);
        renameFileBuilder.setPositiveButton("Ganti", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        renameFileBuilder.setNegativeButton("Tutup", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        renameFileDialog = renameFileBuilder.create();
        renameFileDialog.setView(renameFileDialog.getLayoutInflater().inflate(R.layout.activity_save_file, null));
        renameFileDialog.setCanceledOnTouchOutside(false);
        renameFileDialog.show();
        ClassApplicationTool.with(context).resizeAlertDialog(renameFileDialog);

        renameFileDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

        titleFile = (AutoCompleteTextView) renameFileDialog.findViewById(R.id.save_file_title);
        titleFile.setText(title);

        titleFile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    renameFileDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                } else {
                    renameFileDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        renameFileDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                renameFileDialog.dismiss();
            }
        });

        renameFileDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                renameFileDialog.dismiss();
                realmDraftSolution.rename(id, titleFile.getText().toString().trim());

                draftSolutionList.get(id - 1).setTitle(titleFile.getText().toString().trim());
                holder.title.setText(titleFile.getText().toString().trim());
                notifyItemRangeChanged(0, draftSolutionList.size());

                final Toast toast = Toast.makeText(context, "Draft Berhasil Diganti", Toast.LENGTH_SHORT);
                ClassApplicationTool.with(context).resizeToast(toast);
                toast.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return draftSolutionList.size();
    }
}