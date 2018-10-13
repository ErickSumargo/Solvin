package id.solvinap.dev.view.fragment;

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

import id.solvinap.dev.R;
import id.solvinap.dev.server.data.DataMaterial;
import id.solvinap.dev.server.data.DataQuestion;
import id.solvinap.dev.server.data.DataSubject;
import id.solvinap.dev.server.helper.Session;
import id.solvinap.dev.view.activity.ActivityEditQuestion;
import id.solvinap.dev.view.helper.CategoryBus;
import com.rey.material.widget.Spinner;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Erick Sumargo on 2/2/2017.
 */

public class FragmentCategory extends Fragment {
    //    VIEW
    private View view;
    private TabLayout tabLayout;
    private Spinner subjectSpinner, materialSpinner;
    private AutoCompleteTextView materialFill;
    private Button questionSheet;

    //    OBJECT
    private DataQuestion dataQuestion;

    private List<DataSubject> subjectList;
    private List<DataMaterial> materialList;

    private ArrayAdapter<DataSubject> subjectListAdapter;
    private ArrayAdapter<DataMaterial> materialListAdapter;

    //    VARIABLE
    private int i = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_category, container, false);

        init();
        setEvent();

        return view;
    }

    public void init() {
        //    VIEW
        if (Build.VERSION.SDK_INT >= 21) {
            view.findViewById(R.id.shadow_view).setVisibility(View.GONE);
        } else {
            view.findViewById(R.id.shadow_view).setVisibility(View.VISIBLE);
        }

        subjectSpinner = (Spinner) view.findViewById(R.id.category_subject_spinner);
        materialSpinner = (Spinner) view.findViewById(R.id.category_material_spinner);
        materialFill = (AutoCompleteTextView) view.findViewById(R.id.category_material_fill);
        questionSheet = (Button) view.findViewById(R.id.category_question_sheet);

        setButtonEnabled();

        //    OBJECT
        dataQuestion = ((ActivityEditQuestion) getActivity()).dataQuestion;

        subjectList = new ArrayList<>();
        subjectList.add(new DataSubject(0, "PILIH MATA PELAJARAN"));
        subjectList.addAll(Session.getInstance(getContext()).getCategoryList());

        materialList = new ArrayList<>();
        materialList.add(new DataMaterial(0, "PILIH MATERI"));

        setSubjectAdapter(subjectList);
        subjectSpinner.setSelection(dataQuestion.getDataMaterial().getDataSubject().getId());

        resetMaterialList(subjectSpinner.getSelectedItemPosition());
        setMaterialAdapter(materialList);

        for (DataMaterial dataMaterial : subjectList.get(subjectSpinner.getSelectedItemPosition()).getMaterialList()) {
            if (dataMaterial.getId() == dataQuestion.getDataMaterial().getId()) {
                materialSpinner.setSelection(i);
                break;
            }
            i++;
        }

        if (dataQuestion.getOther().length() > 0) {
            materialFill.setText(dataQuestion.getOther());
            materialFill.setSelection(materialFill.length());
            materialFill.setVisibility(View.VISIBLE);
        }
    }

    private void setEvent() {
        subjectSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(Spinner parent, View view, int position, long id) {
                if (subjectSpinner.getSelectedItemPosition() == 0) {
                    setButtonDisabled();

                    resetMaterialList(subjectSpinner.getSelectedItemPosition());
                    setMaterialAdapter(materialList);

                    setMaterialSpinnerDisabled();
                } else {
                    resetMaterialList(subjectSpinner.getSelectedItemPosition());
                    setMaterialAdapter(materialList);

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
                EventBus.getDefault().post(new CategoryBus(
                        subjectList.get(subjectSpinner.getSelectedItemPosition()).getId(),
                        materialList.get(materialSpinner.getSelectedItemPosition()).getId(),
                        materialFill.getText().toString()
                ));
                tabLayout = (TabLayout) getActivity().findViewById(R.id.tabs);
                tabLayout.getTabAt(1).select();
            }
        });
    }

    private void resetMaterialList(int selectedItemPosition) {
        materialList = new ArrayList<>();
        materialList.add(new DataMaterial(0, "PILIH MATERI"));
        materialList.addAll(subjectList.get(selectedItemPosition).getMaterialList());
    }

    private void setSubjectAdapter(List<DataSubject> selectedSubjectList) {
        subjectListAdapter = new ArrayAdapter<DataSubject>(getActivity(), R.layout.custom_spinner_item, selectedSubjectList) {
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                convertView = View.inflate(getContext(), R.layout.custom_spinner_dropdown, null);
                View item = super.getView(position, convertView, parent);
                if (position == 0) {
                    ((TextView) item).setTextColor(getResources().getColor(R.color.colorPrimary));
                }
                return item;
            }
        };
        subjectSpinner.setAdapter(subjectListAdapter);
        subjectSpinner.setSelection(0);
    }

    private void setMaterialAdapter(List<DataMaterial> selectedMaterialList) {
        materialListAdapter = new ArrayAdapter<DataMaterial>(getActivity(), R.layout.custom_spinner_item, selectedMaterialList) {
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                convertView = View.inflate(getContext(), R.layout.custom_spinner_dropdown, null);
                View item = super.getView(position, convertView, parent);
                if (position == 0) {
                    ((TextView) item).setTextColor(getResources().getColor(R.color.colorPrimary));
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
        questionSheet.setBackgroundResource(R.drawable.primary_button);
        questionSheet.setPadding((int) getResources().getDimension(R.dimen.button_padding),
                (int) getResources().getDimension(R.dimen.button_padding),
                (int) getResources().getDimension(R.dimen.button_padding),
                (int) getResources().getDimension(R.dimen.button_padding));
    }

    private void setButtonDisabled() {
        questionSheet.setEnabled(false);
        questionSheet.setBackgroundResource(R.drawable.primary_button_disabled);
        questionSheet.setPadding((int) getResources().getDimension(R.dimen.button_padding),
                (int) getResources().getDimension(R.dimen.button_padding),
                (int) getResources().getDimension(R.dimen.button_padding),
                (int) getResources().getDimension(R.dimen.button_padding));
    }
}