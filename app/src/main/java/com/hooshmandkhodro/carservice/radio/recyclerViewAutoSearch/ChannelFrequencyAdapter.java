package com.hooshmandkhodro.carservice.radio.recyclerViewAutoSearch;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hooshmandkhodro.carservice.R;

import java.util.List;



public class ChannelFrequencyAdapter extends RecyclerView.Adapter<ChannelFrequencyAdapter.MyViewHolder> {
    private List<ChannelFrequency> channelFrequenciesList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView frequency;

        public MyViewHolder(View view) {
            super(view);
            frequency = view.findViewById(R.id.channel_frq_text_view);

        }
    }

    public ChannelFrequencyAdapter(List<ChannelFrequency> channelFrequenciesList) {
        this.channelFrequenciesList = channelFrequenciesList;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.radio_channel_list_row, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ChannelFrequencyAdapter.MyViewHolder myViewHolder, int i) {
        ChannelFrequency channelFrequency = channelFrequenciesList.get(i);
        myViewHolder.frequency.setText(channelFrequency.getFrequency());

    }

    @Override
    public int getItemCount() {
        return channelFrequenciesList.size();
    }
}
