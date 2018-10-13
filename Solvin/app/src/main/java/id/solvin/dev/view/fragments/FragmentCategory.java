package id.solvin.dev.view.fragments;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import id.solvin.dev.helper.Session;
import id.solvin.dev.model.basic.Material;
import id.solvin.dev.model.basic.Question;
import id.solvin.dev.model.basic.Subject;
import id.solvin.dev.model.basic.TransferCategory;
import id.solvin.dev.view.activities.ActivityCreateQuestion;
import com.rey.material.widget.Spinner;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Erick Sumargo on 9/1/2016.
 */
public class FragmentCategory extends Fragment {
    private View view;
    private TabLayout tabLayout;
    private TextView selectLabel;
    private Spinner subjectSpinner, materialSpinner;
    private AutoCompleteTextView materialFill;
    private Button questionSheet;

    private ArrayAdapter<Subject> subjectListAdapter;
    private ArrayAdapter<Material> materialListAdapter;

    private int QUESTION_MODE;

    private List<Subject> subjectLists;
    private List<Material> materialLists;

    private Question question;

    public FragmentCategory() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(id.solvin.dev.R.layout.activity_category, container, false);
        init();
        setEvent();

        return view;
    }

    public void init() {
        if (Build.VERSION.SDK_INT >= 21) {
            view.findViewById(id.solvin.dev.R.id.shadow_view).setVisibility(View.GONE);
        } else {
            view.findViewById(id.solvin.dev.R.id.shadow_view).setVisibility(View.VISIBLE);
        }

        selectLabel = (TextView) view.findViewById(id.solvin.dev.R.id.category_select_label);
        subjectSpinner = (Spinner) view.findViewById(id.solvin.dev.R.id.category_subject_spinner);
        materialSpinner = (Spinner) view.findViewById(id.solvin.dev.R.id.category_material_spinner);
        materialFill = (AutoCompleteTextView) view.findViewById(id.solvin.dev.R.id.category_material_fill);
        questionSheet = (Button) view.findViewById(id.solvin.dev.R.id.category_question_sheet);

        // TODO #List Categories
        subjectLists = new ArrayList<>();
        subjectLists.add(new Subject(0, "PILIH MATA PELAJARAN"));
        subjectLists.addAll(Session.with(getContext()).getMaterials());

        materialLists = new ArrayList<>();
        materialLists.add(new Material(0, "PILIH MATERI"));

        QUESTION_MODE = ((ActivityCreateQuestion) getActivity()).QUESTION_MODE;
        if (QUESTION_MODE == 0) {
            selectLabel.setText("Pilih Kategori Pertanyaan");
            setMaterialSpinnerDisabled();

            setSubjectAdapter(subjectLists);
            setMaterialAdapter(materialLists);

            setButtonDisabled();
        } else {
            selectLabel.setText("Edit Kategori Pertanyaan");
            question = ((ActivityCreateQuestion) getActivity()).question;

            setSubjectAdapter(subjectLists);
            subjectSpinner.setSelection(question.getMaterial().getSubject().getId());

            resetMaterialList(subjectSpinner.getSelectedItemPosition());
            setMaterialAdapter(materialLists);
            int i = 1;
            for (Material material : subjectLists.get(subjectSpinner.getSelectedItemPosition()).getMaterial()) {
                if (material.getId() == question.getMaterial().getId()) {
                    materialSpinner.setSelection(i);
                    break;
                }
                i++;
            }

            if (question.getOther().length() > 0) {
                materialFill.setText(question.getOther());
                materialFill.setSelection(materialFill.length());
                materialFill.setVisibility(View.VISIBLE);
            }
            setButtonEnabled();
        }
    }

    private void setEvent() {
        subjectSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(Spinner parent, View view, int position, long id) {
                if (subjectSpinner.getSelectedItemPosition() == 0) {
                    setButtonDisabled();

                    // reset Material List
                    resetMaterialList(subjectSpinner.getSelectedItemPosition());
                    setMaterialAdapter(materialLists);

                    setMaterialSpinnerDisabled();
                } else {
                    // reset Material List
                    resetMaterialList(subjectSpinner.getSelectedItemPosition());
                    setMaterialAdapter(materialLists);

                    setMaterialSpinnerEnabled();
                }
                materialFill.setVisibility(View.GONE);
                materialFill.getText().clear();
            }
        });

        materialSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(Spinner parent, View view, int position, long id) {
                if (materialSpinner.getSelectedItemPosition() == 0) {
                    setButtonDisabled();
                    materialFill.setVisibility(View.GONE);
                    materialFill.getText().clear();
                } else if (subjectSpinner.getSelectedItemPosition() != 0
                        && materialSpinner.getSelectedItemPosition() < materialSpinner.getAdapter().getCount() - 1) {
                    materialFill.setVisibility(View.GONE);
                    materialFill.getText().clear();
                    setButtonEnabled();
                } else if (materialSpinner.getSelectedItemPosition() == materialSpinner.getAdapter().getCount() - 1) {
                    setButtonDisabled();
                    materialFill.setVisibility(View.VISIBLE);
                } else {
                    materialFill.setVisibility(View.GONE);
                    materialFill.getText().clear();
                }
            }
        });

        materialFill.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    setButtonEnabled();
                } else {
                    setButtonDisabled();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        questionSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(
                        new TransferCategory(
                                subjectLists.get(subjectSpinner.getSelectedItemPosition()).getId(),
                                materialLists.get(materialSpinner.getSelectedItemPosition()).getId(),
                                materialFill.getText().toString()
                        ));
                tabLayout = (TabLayout) getActivity().findViewById(id.solvin.dev.R.id.tabs);
                tabLayout.getTabAt(1).select();
            }
        });
    }

    private void resetMaterialList(int selectedItemPosition) {
        materialLists = new ArrayList<>();
        materialLists.add(new Material(0, "PILIH MATERI"));
        materialLists.addAll(subjectLists.get(selectedItemPosition).getMaterial());
    }

    private void setSubjectAdapter(List<Subject> selectedSubjectList) {
        subjectListAdapter = new ArrayAdapter<Subject>(getActivity(), id.solvin.dev.R.layout.custom_spinner_item, selectedSubjectList) {
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                convertView = View.inflate(getContext(), id.solvin.dev.R.layout.custom_spinner_dropdown, null);
                View item = super.getView(position, convertView, parent);
                if (position == 0) {
                    ((TextView) item).setTextColor(getResources().getColor(id.solvin.dev.R.color.colorPrimary));
                }
                return item;
            }
        };
        subjectSpinner.setAdapter(subjectListAdapter);
        subjectSpinner.setSelection(0);
    }

    private void setMaterialAdapter(List<Material> selectedMaterialList) {
        materialListAdapter = new ArrayAdapter<Material>(getActivity(), id.solvin.dev.R.layout.custom_spinner_item, selectedMaterialList) {
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                convertView = View.inflate(getContext(), id.solvin.dev.R.layout.custom_spinner_dropdown, null);
                View item = super.getView(position, convertView, parent);
                if (position == 0) {
                    ((TextView) item).setTextColor(getResources().getColor(id.solvin.dev.R.color.colorPrimary));
                }
                return item;
            }
        };
        materialSpinner.setAdapter(materialListAdapter);
        materialSpinner.setSelection(0);
    }

    private void setMaterialSpinnerEnabled() {
        materialSpinner.setEnabled(true);
        materialSpinner.setAlpha(1f);
    }

    private void setMaterialSpinnerDisabled() {
        materialSpinner.setEnabled(false);
        materialSpinner.setAlpha(0.5f);
    }

    private void setButtonEnabled() {
        questionSheet.setEnabled(true);
        questionSheet.setBackgroundResource(id.solvin.dev.R.drawable.primary_button);
        questionSheet.setPadding((int) getResources().getDimension(id.solvin.dev.R.dimen.default_padding_0m),
                (int) getResources().getDimension(id.solvin.dev.R.dimen.default_padding_0m),
                (int) getResources().getDimension(id.solvin.dev.R.dimen.default_padding_0m),
                (int) getResources().getDimension(id.solvin.dev.R.dimen.default_padding_0m));
    }

    private void setButtonDisabled() {
        questionSheet.setEnabled(false);
        questionSheet.setBackgroundResource(id.solvin.dev.R.drawable.primary_button_disabled);
        questionSheet.setPadding((int) getResources().getDimension(id.solvin.dev.R.dimen.default_padding_0m),
                (int) getResources().getDimension(id.solvin.dev.R.dimen.default_padding_0m),
                (int) getResources().getDimension(id.solvin.dev.R.dimen.default_padding_0m),
                (int) getResources().getDimension(id.solvin.dev.R.dimen.default_padding_0m));
    }
}