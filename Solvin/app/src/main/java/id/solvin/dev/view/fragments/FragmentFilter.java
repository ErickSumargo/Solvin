package id.solvin.dev.view.fragments;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.rey.material.widget.Spinner;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import id.solvin.dev.R;
import id.solvin.dev.helper.Session;
import id.solvin.dev.model.basic.Material;
import id.solvin.dev.model.basic.Subject;
import id.solvin.dev.model.basic.TransferCategory;

/**
 * Created by Erick Sumargo on 8/31/2016.
 */
public class FragmentFilter extends Fragment {
    private View view;
    private TabLayout tabLayout;
    private Spinner subjectSpinner, materialSpinner, statusSpinner;
    private Button filterQuery;

    private ArrayAdapter<Subject> subjectListAdapter;
    private ArrayAdapter<Material> materialListAdapter;
    private ArrayAdapter<String> statusListAdapter;

    private List<Subject> subjectLists;
    private List<Material> materialLists;

    private String[] statusList;
    private String[] statusListValue = {"-2", "1", "0", "-1"};
    public boolean isFilter = false;

    public FragmentFilter() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_filter, container, false);
        init();
        setEvent();

        return view;
    }

    private void init() {
        if (Build.VERSION.SDK_INT >= 21) {
            view.findViewById(R.id.shadow_view).setVisibility(View.GONE);
        } else {
            view.findViewById(R.id.shadow_view).setVisibility(View.VISIBLE);
        }

        subjectSpinner = (Spinner) view.findViewById(R.id.filter_subject_spinner);
        materialSpinner = (Spinner) view.findViewById(R.id.filter_material_spinner);
        statusSpinner = (Spinner) view.findViewById(R.id.filter_status_spinner);

        filterQuery = (Button) view.findViewById(R.id.filter_query);

//         TODO #List Categories
        subjectLists = new ArrayList<>();
        subjectLists.add(new Subject(0, "PILIH MATA PELAJARAN"));
        subjectLists.addAll(Session.with(getContext()).getMaterials());

        materialLists = new ArrayList<>();
        materialLists.add(new Material(0, "PILIH MATERI"));
        // TODO #end List
        statusList = getResources().getStringArray(R.array.statusList);

        setMaterialSpinnerDisabled();
        setButtonDisabled();

        setSubjectAdapter(subjectLists);
        setMaterialAdapter(materialLists);
        setStatusAdapter(statusList);
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
            }
        });

        materialSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(Spinner parent, View view, int position, long id) {
                if (materialSpinner.getSelectedItemPosition() == 0) {
                    setButtonDisabled();
                } else if (subjectSpinner.getSelectedItemPosition() != 0) {
                    setButtonEnabled();
                } else {
                    setButtonDisabled();
                }
            }
        });

        filterQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(
                        new TransferCategory(
                                subjectLists.get(subjectSpinner.getSelectedItemPosition()).getId(),
                                materialLists.get(materialSpinner.getSelectedItemPosition()).getId(),
                                null
                        ).setStatus(statusListValue[statusSpinner.getSelectedItemPosition()]));
                isFilter = true;
                tabLayout = (TabLayout) getActivity().findViewById(R.id.tabs);
                tabLayout.getTabAt(0).select();
            }
        });
    }

    private void resetMaterialList(int selectedItemPosition) {

        materialLists = new ArrayList<>();
        materialLists.add(new Material(0, "PILIH MATERI"));
        materialLists.addAll(subjectLists.get(selectedItemPosition).getMaterial());

    }

    private void setSubjectAdapter(List<Subject> selectedSubjectList) {
        subjectListAdapter = new ArrayAdapter<Subject>(getActivity(), R.layout.custom_spinner_item, selectedSubjectList) {
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

    private void setMaterialAdapter(List<Material> selectedMaterialList) {
        materialListAdapter = new ArrayAdapter<Material>(getActivity(), R.layout.custom_spinner_item, selectedMaterialList) {
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

    private void setStatusAdapter(String[] selectedStatusList) {
        statusListAdapter = new ArrayAdapter<String>(getActivity(), R.layout.custom_spinner_item, selectedStatusList);
        statusListAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown);
        statusSpinner.setAdapter(statusListAdapter);
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
        filterQuery.setEnabled(true);
        filterQuery.setBackgroundResource(R.drawable.primary_button);
        filterQuery.setPadding((int) getResources().getDimension(R.dimen.default_padding_0m),
                (int) getResources().getDimension(R.dimen.default_padding_0m),
                (int) getResources().getDimension(R.dimen.default_padding_0m),
                (int) getResources().getDimension(R.dimen.default_padding_0m));
    }

    private void setButtonDisabled() {
        filterQuery.setEnabled(false);
        filterQuery.setBackgroundResource(R.drawable.primary_button_disabled);
        filterQuery.setPadding((int) getResources().getDimension(R.dimen.default_padding_0m),
                (int) getResources().getDimension(R.dimen.default_padding_0m),
                (int) getResources().getDimension(R.dimen.default_padding_0m),
                (int) getResources().getDimension(R.dimen.default_padding_0m));
    }
}