package id.solvin.dev.view.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import id.solvin.dev.R;
import id.solvin.dev.helper.ClassApplicationTool;
import id.solvin.dev.model.basic.Paket;

import java.util.List;

/**
 * Created by Erick Sumargo on 3/6/2017.
 */

public class PackageViewAdapter extends RecyclerView.Adapter<PackageViewAdapter.ViewHolder> {
    //    INTERFACE
    private static OnSetCheckedPackagePosition mSetCheckedPackagePosition;

    public interface OnSetCheckedPackagePosition {
        void IGetCheckedPackagePosition(int position);
    }

    public void getCheckedPackagePosition(OnSetCheckedPackagePosition listener) {
        mSetCheckedPackagePosition = listener;
    }

    public void setCheckedPackagePosition(int position) {
        if (mSetCheckedPackagePosition != null) {
            mSetCheckedPackagePosition.IGetCheckedPackagePosition(position);
        }
    }

    //    TEMP
    private RelativeLayout clickedGroup, lastClickedGroup = null;
    private RadioButton lastCheckedRadio;
    private TextView lastShownPackageValidUntil;

    //    HELPER
    private Context context;

    private ClassApplicationTool applicationTool;

    //    OBJECT
    private List<Paket> packageList;

    public PackageViewAdapter(List<Paket> packageList) {
        this.packageList = packageList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout group;
        private RadioButton packageRadio;
        private TextView packageCost;
//        private TextView packageValidUntil;

        public ViewHolder(View view) {
            super(view);
            context = view.getContext();

            group = (RelativeLayout) view.findViewById(R.id.package_group);
            packageRadio = (RadioButton) view.findViewById(R.id.package_radio);
            packageCost = (TextView) view.findViewById(R.id.package_cost);
//            packageValidUntil = (TextView) view.findViewById(R.id.package_valid_until);

            applicationTool = new ClassApplicationTool(view.getContext());
        }
    }

    @Override
    public PackageViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.package_list, parent, false);

        return new PackageViewAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final PackageViewAdapter.ViewHolder holder, int position) {
        final Paket paket = packageList.get(position);

        holder.packageCost.setText(applicationTool.convertRpCurrency((int) paket.getNominal())
                + "/ " + paket.getCredit() + " Pertanyaan");
//        if (paket.getActive() == 7) {
//            holder.packageValidUntil.setText("berlaku selama 1 minggu (7 hari)");
//        } else if (paket.getActive() == 14) {
//            holder.packageValidUntil.setText("berlaku selama 2 minggu (14 hari)");
//        } else {
//            holder.packageValidUntil.setText("berlaku selama 1 bulan (30 hari)");
//        }
        holder.group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedGroup = (RelativeLayout) v;
                holder.group.setBackgroundResource(R.drawable.custom_radio_selected);
//                holder.packageValidUntil.setVisibility(View.VISIBLE);
                holder.packageRadio.setChecked(true);
                if (lastClickedGroup != null && lastClickedGroup != holder.group) {
                    lastClickedGroup.setBackgroundResource(R.drawable.custom_background_pressed);
                    lastCheckedRadio.setChecked(false);
//                    lastShownPackageValidUntil.setVisibility(View.GONE);
                }
                lastClickedGroup = clickedGroup;
                lastCheckedRadio = holder.packageRadio;
//                lastShownPackageValidUntil = holder.packageValidUntil;

                setCheckedPackagePosition(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return packageList.size();
    }
}