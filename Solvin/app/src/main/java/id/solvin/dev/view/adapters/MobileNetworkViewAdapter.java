package id.solvin.dev.view.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import id.solvin.dev.R;
import id.solvin.dev.model.basic.MobileNetwork;

/**
 * Created by Erick Sumargo on 5/9/2017.
 */

public class MobileNetworkViewAdapter extends RecyclerView.Adapter<MobileNetworkViewAdapter.ViewHolder> {
    private static OnSetCheckedMobileNetworkPosition mSetCheckedMobileNetworkPosition;

    public interface OnSetCheckedMobileNetworkPosition {
        void IGetCheckedMobileNetworkPosition(int position);
    }

    public void getCheckedMobileNetworkPosition(OnSetCheckedMobileNetworkPosition listener) {
        mSetCheckedMobileNetworkPosition = listener;
    }

    public void setCheckedMobileNetworkPosition(int position) {
        if (mSetCheckedMobileNetworkPosition != null) {
            mSetCheckedMobileNetworkPosition.IGetCheckedMobileNetworkPosition(position);
        }
    }

    //    TEMP
    private RelativeLayout clickedGroup, lastClickedGroup = null;
    private RadioButton lastCheckedRadio;
    private TextView lastHighlightedMobileNetworkNumber;

    //    HELPER
    private Context context;

    //    OBJECT
    private List<MobileNetwork> mobileNetworkList;

    //    VARIABLE
    private int[] logoResourceList = {R.drawable.ic_telkomsel, R.drawable.ic_xl};

    public MobileNetworkViewAdapter(List<MobileNetwork> mobileNetworkList) {
        this.mobileNetworkList = mobileNetworkList;
    }

    public void unselectRadio() {
        if (lastClickedGroup != null) {
            lastClickedGroup.setBackgroundResource(R.drawable.custom_background_pressed);
            lastCheckedRadio.setChecked(false);
            lastHighlightedMobileNetworkNumber.setTextColor(context.getResources().getColor(R.color.colorHeader));
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout group;
        private RadioButton mobileNetworkRadio;
        private TextView mobileNetworkNumber;

        public ViewHolder(View view) {
            super(view);
            context = view.getContext();

            group = (RelativeLayout) view.findViewById(R.id.mobile_network_group);
            mobileNetworkRadio = (RadioButton) view.findViewById(R.id.mobile_network_radio);
            mobileNetworkNumber = (TextView) view.findViewById(R.id.mobile_network_number);
        }
    }

    @Override
    public MobileNetworkViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mobile_network_list, parent, false);

        return new MobileNetworkViewAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MobileNetworkViewAdapter.ViewHolder holder, int position) {
        final MobileNetwork dataMobileNetwork = mobileNetworkList.get(position);

        holder.mobileNetworkRadio.setCompoundDrawablesWithIntrinsicBounds(0, 0, logoResourceList[position], 0);
        holder.mobileNetworkNumber.setText(dataMobileNetwork.getNumber());

        holder.group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedGroup = (RelativeLayout) v;
                holder.group.setBackgroundResource(R.drawable.custom_radio_selected);
                holder.mobileNetworkRadio.setChecked(true);
                holder.mobileNetworkNumber.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                if (lastClickedGroup != null && lastClickedGroup != holder.group) {
                    lastClickedGroup.setBackgroundResource(R.drawable.custom_background_pressed);
                    lastCheckedRadio.setChecked(false);
                    lastHighlightedMobileNetworkNumber.setTextColor(context.getResources().getColor(R.color.colorHeader));
                }
                lastClickedGroup = clickedGroup;
                lastCheckedRadio = holder.mobileNetworkRadio;
                lastHighlightedMobileNetworkNumber = holder.mobileNetworkNumber;

                setCheckedMobileNetworkPosition(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mobileNetworkList.size();
    }
}