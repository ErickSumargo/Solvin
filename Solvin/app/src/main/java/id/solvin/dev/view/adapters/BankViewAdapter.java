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

import id.solvin.dev.model.basic.Bank;

/**
 * Created by Erick Sumargo on 3/6/2017.
 */

public class BankViewAdapter extends RecyclerView.Adapter<BankViewAdapter.ViewHolder> {
    private static OnSetCheckedBankPosition mSetCheckedBankPosition;

    public interface OnSetCheckedBankPosition {
        void IGetCheckedBankPosition(int position);
    }

    public void getCheckedBankPosition(OnSetCheckedBankPosition listener) {
        mSetCheckedBankPosition = listener;
    }

    public void setCheckedBankPosition(int position) {
        if (mSetCheckedBankPosition != null) {
            mSetCheckedBankPosition.IGetCheckedBankPosition(position);
        }
    }

    //    TEMP
    private RelativeLayout clickedGroup, lastClickedGroup = null;
    private RadioButton lastCheckedRadio;
    private TextView lastHighlightedAccountName, lastHighlightedAccountCode;

    //    HELPER
    private Context context;

    //    OBJECT
    private List<Bank> bankList;

    //    VARIABLE
    private int[] logoResourceList = {id.solvin.dev.R.drawable.ic_bca, id.solvin.dev.R.drawable.ic_bni};

    public BankViewAdapter(List<Bank> bankList) {
        this.bankList = bankList;
    }

    public void unselectRadio() {
        if (lastClickedGroup != null) {
            lastClickedGroup.setBackgroundResource(id.solvin.dev.R.drawable.custom_background_pressed);
            lastCheckedRadio.setChecked(false);
            lastHighlightedAccountName.setTextColor(context.getResources().getColor(id.solvin.dev.R.color.colorHeader));
            lastHighlightedAccountCode.setTextColor(context.getResources().getColor(id.solvin.dev.R.color.colorHeader));
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout group;
        private RadioButton bankRadio;
        private TextView bankAccountName, bankAccountCode;

        public ViewHolder(View view) {
            super(view);
            context = view.getContext();

            group = (RelativeLayout) view.findViewById(id.solvin.dev.R.id.bank_group);
            bankRadio = (RadioButton) view.findViewById(id.solvin.dev.R.id.bank_radio);
            bankAccountName = (TextView) view.findViewById(id.solvin.dev.R.id.bank_account_name);
            bankAccountCode = (TextView) view.findViewById(id.solvin.dev.R.id.bank_account_code);
        }
    }

    @Override
    public BankViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(id.solvin.dev.R.layout.bank_list, parent, false);

        return new BankViewAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final BankViewAdapter.ViewHolder holder, int position) {
        final Bank dataBank = bankList.get(position);

        holder.bankRadio.setCompoundDrawablesWithIntrinsicBounds(0, 0, logoResourceList[position], 0);
        holder.bankAccountName.setText(dataBank.getAccountOwner());
        holder.bankAccountCode.setText(dataBank.getAccountNumber());

        holder.group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedGroup = (RelativeLayout) v;
                holder.group.setBackgroundResource(id.solvin.dev.R.drawable.custom_radio_selected);
                holder.bankRadio.setChecked(true);
                holder.bankAccountName.setTextColor(context.getResources().getColor(id.solvin.dev.R.color.colorPrimary));
                holder.bankAccountCode.setTextColor(context.getResources().getColor(id.solvin.dev.R.color.colorPrimary));
                if (lastClickedGroup != null && lastClickedGroup != holder.group) {
                    lastClickedGroup.setBackgroundResource(id.solvin.dev.R.drawable.custom_background_pressed);
                    lastCheckedRadio.setChecked(false);
                    lastHighlightedAccountName.setTextColor(context.getResources().getColor(id.solvin.dev.R.color.colorHeader));
                    lastHighlightedAccountCode.setTextColor(context.getResources().getColor(id.solvin.dev.R.color.colorHeader));
                }
                lastClickedGroup = clickedGroup;
                lastCheckedRadio = holder.bankRadio;
                lastHighlightedAccountName = holder.bankAccountName;
                lastHighlightedAccountCode = holder.bankAccountCode;

                setCheckedBankPosition(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return bankList.size();
    }
}