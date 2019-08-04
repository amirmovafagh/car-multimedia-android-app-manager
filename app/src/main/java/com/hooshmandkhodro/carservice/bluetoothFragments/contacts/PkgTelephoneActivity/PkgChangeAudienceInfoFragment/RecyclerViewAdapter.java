package com.hooshmandkhodro.carservice.bluetoothFragments.contacts.PkgTelephoneActivity.PkgChangeAudienceInfoFragment;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;


import com.hooshmandkhodro.carservice.R;
import com.hooshmandkhodro.carservice.bluetoothFragments.contacts.Pojo.Audience;
import com.hooshmandkhodro.carservice.bluetoothFragments.contacts.Pojo.PhoneNumber;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder> {


    private Context ctx;
    private Audience audience;
    private Object[] audiencePhones;
    public RecyclerViewAdapter(Context ctx, Audience audience) {
        this.ctx = ctx;
        this.audience = audience;
        audiencePhones = audience.getPhoneNumbers().toArray();
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.change_audience_info_item_layout, parent, false);
        RecyclerViewHolder holder = new RecyclerViewHolder(v, new NotifyAdapter() {
            @Override
            public void onAudienceInfoPhoneChange(CharSequence newPhone, int itemPosition) {
                ((PhoneNumber)audiencePhones[itemPosition]).setPhone(newPhone.toString());
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        String phone = ((PhoneNumber)audiencePhones[position]).getPhone();
        holder.etChangeAudienceInfoPhone.setText(phone);
    }

    @Override
    public int getItemCount() {
        return audiencePhones.length;
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder{

        EditText etChangeAudienceInfoPhone;
        NotifyAdapter notifyAdapter;

        public RecyclerViewHolder(View itemView, final NotifyAdapter notifyAdapter) {
            super(itemView);
            this.notifyAdapter = notifyAdapter;

            etChangeAudienceInfoPhone = itemView.findViewById(R.id.et_change_audience_info_phone);
            etChangeAudienceInfoPhone.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    notifyAdapter.onAudienceInfoPhoneChange(s, getAdapterPosition());
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }
    }

    private interface NotifyAdapter{
        public void onAudienceInfoPhoneChange(CharSequence newPhone, int itemPosition);
    }
}
