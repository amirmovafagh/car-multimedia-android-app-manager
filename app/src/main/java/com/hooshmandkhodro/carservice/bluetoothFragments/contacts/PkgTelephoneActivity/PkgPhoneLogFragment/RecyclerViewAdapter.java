package com.hooshmandkhodro.carservice.bluetoothFragments.contacts.PkgTelephoneActivity.PkgPhoneLogFragment;

import android.content.Context;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hooshmandkhodro.carservice.R;
import com.hooshmandkhodro.carservice.bluetoothFragments.contacts.Pojo.Audience;
import com.hooshmandkhodro.carservice.bluetoothFragments.contacts.Pojo.CallLog;
import com.hooshmandkhodro.carservice.bluetoothFragments.contacts.Pojo.CallType;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;



public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder> {

    private Context ctx;
    private List<CallLog> data;
    private NotifyFragment notifyFragment;

    public RecyclerViewAdapter(Context ctx, List<CallLog> data, NotifyFragment notifyFragment){

        this.ctx = ctx;
        this.data = data;
        this.notifyFragment = notifyFragment;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.phone_log_item_layout, parent, false);
        return new RecyclerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        final CallLog log = data.get(position);

        //holder.ibImageUser
        if (log.getPhoneNumber().getAudience() != null){// audience is known
            holder.ivImageUser.setImageDrawable(
                    ResourcesCompat.getDrawable(
                            ctx.getResources(), R.drawable.ic_known_person_black_32dp,null
                    )
            );

            //holder.tvFirstname
            holder.tvFirstname.setText(log.getPhoneNumber().getAudience().getFirstname());

            //holder.tvLastname
            holder.tvLastname.setText(log.getPhoneNumber().getAudience().getLastname());

        }else {// audience is unknown
            holder.ivImageUser.setImageDrawable(
                    ResourcesCompat.getDrawable(
                            ctx.getResources(),R.drawable.ic_unknown_person_black_32dp,null
                    )
            );
            holder.ivImageUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    notifyFragment.clickOnUnknownImageUser(log);
                }
            });

            //holder.tvFirstname
            holder.tvFirstname.setText(Audience.UNKNOWN_AUDIENCE_NAME);

            //holder.tvLastname
            holder.tvLastname.setText("");
        }

        //holder.ivCallType
        switch (log.getCallType().getType()){
            case CallType.INPUT:
                holder.ivCallType.setImageDrawable(
                        ResourcesCompat.getDrawable(
                                ctx.getResources(), R.drawable.ic_call_received_black_16dp, null
                        )
                );
                break;
            case CallType.OUTPUT:
                holder.ivCallType.setImageDrawable(
                        ResourcesCompat.getDrawable(
                                ctx.getResources(), R.drawable.ic_call_made_black_16dp, null
                        )
                );
                break;

            case CallType.MISSED:
                holder.ivCallType.setImageDrawable(
                        ResourcesCompat.getDrawable(
                                ctx.getResources(), R.drawable.ic_call_missed_black_16dp, null
                        )
                );
                break;
        }

        holder.tvPhonenumber.setText(log.getPhoneNumber().getPhone());

        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        holder.tvDate.setText(dateFormat.format(log.getDate()));

        DateFormat timeFormat = new SimpleDateFormat("hh:mm");
        holder.tvTime.setText(timeFormat.format(log.getDate()));

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void updateRecyclerView(List<CallLog> allLogs) {
        this.data.clear();
        this.data.addAll(allLogs);
        notifyDataSetChanged();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder{

        ImageView ivImageUser;
        TextView tvFirstname;
        TextView tvLastname;
        TextView tvPhonenumber;
        ImageView ivCallType;
        TextView tvDate;
        TextView tvTime;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            ivImageUser = itemView.findViewById(R.id.iv_img_user);
            tvFirstname = itemView.findViewById(R.id.tv_firstname);
            tvLastname = itemView.findViewById(R.id.tv_lastname);
            tvPhonenumber = itemView.findViewById(R.id.tv_phonenumber);
            ivCallType = itemView.findViewById(R.id.img_calltype_icon);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvTime = itemView.findViewById(R.id.tv_time);
        }

    }

    public interface NotifyFragment{
        void clickOnUnknownImageUser(CallLog callLog);
    }
}
