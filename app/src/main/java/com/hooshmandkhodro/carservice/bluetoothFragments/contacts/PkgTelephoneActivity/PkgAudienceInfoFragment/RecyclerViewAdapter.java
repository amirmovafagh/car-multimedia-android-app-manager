package com.hooshmandkhodro.carservice.bluetoothFragments.contacts.PkgTelephoneActivity.PkgAudienceInfoFragment;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.util.List;


import com.hooshmandkhodro.carservice.R;
import com.hooshmandkhodro.carservice.bluetoothFragments.contacts.Pojo.Audience;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder> {

    private Context ctx;
    private List<Audience> data;
    private NotifyFragment notifyFragment;

    public RecyclerViewAdapter(Context ctx, List<Audience> data, NotifyFragment notifyFragment){
        this.ctx = ctx;
        this.data = data;
        this.notifyFragment = notifyFragment;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.audience_item_layout, parent, false);
        return new RecyclerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, int position) {
        final Audience audience = data.get(position);
        holder.tvAudienceFirstname.setText(audience.getFirstname());
        holder.tvAudienceLastname.setText(audience.getLastname());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifyFragment.clickOnAudienceItem(audience);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                notifyFragment.longClickOnAudienceItem(audience, holder.itemView);
                return true;
            }
        });
    }

    public void updateRecyclerView(List<Audience> audiences){
        data.clear();
        data.addAll(audiences);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder{

        //ImageButton ibAudienceImage;// always is fix
        TextView tvAudienceFirstname;
        TextView tvAudienceLastname;
        View itemView;
        public RecyclerViewHolder(View itemView) {
            super(itemView);
            tvAudienceFirstname = itemView.findViewById(R.id.tv_audience_firstname);
            tvAudienceLastname = itemView.findViewById(R.id.tv_audience_lastname);
            this.itemView = itemView;
        }
    }

    public interface NotifyFragment{
        void clickOnAudienceItem(Audience audience);
        boolean longClickOnAudienceItem(Audience audience, View anchor);
    }
}
