package ir.dev_roid.testusb.steeringWheelController;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import ir.dev_roid.testusb.R;
import ir.dev_roid.testusb.steeringWheelController.Pojo.ControllerOption;
import ir.dev_roid.testusb.steeringWheelController.Pojo.Options;



import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private Context ctx;
    private List<ControllerOption> data;
    private NotifyActivity notifyActivity;

    private static final int IMAGE_BUTTON_SMALL = 0;
    private static final int IMAGE_BUTTON_LARGE = 1;

    public RecyclerViewAdapter(Context ctx, List<ControllerOption> data, NotifyActivity notifyActivity) {
        this.ctx = ctx;
        this.data = data;
        this.notifyActivity = notifyActivity;
    }

    @Override
    public int getItemViewType(int position) {
        //large item position is 9 (refresh button)
        if (position == 9)
            return IMAGE_BUTTON_LARGE;
        else
            return IMAGE_BUTTON_SMALL;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder vh = null;

        switch (viewType)
        {
            case IMAGE_BUTTON_SMALL:
                View v = LayoutInflater.from(ctx).inflate(R.layout.activity_swc_small_item_layouts, parent, false);
                vh = new SmallItemRecyclerViewHolder(v);
                break;
            case IMAGE_BUTTON_LARGE:
                View vv = LayoutInflater.from(ctx).inflate(R.layout.activity_swc_large_item_layout, parent, false);
                vh = new LargeItemReyclerViewHolder(vv);
                break;
        }

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {

        int itemIconResource = getItemIconResource(position);

        final ControllerOption option = data.get(position);
        
        if (holder instanceof SmallItemRecyclerViewHolder)
        {
            final SmallItemRecyclerViewHolder smallHolder = (SmallItemRecyclerViewHolder) holder;

            smallHolder.ibSmallSWCItemIcon.setImageDrawable(
                    ResourcesCompat.getDrawable(ctx.getResources(), itemIconResource, null)
            );

            if (option.getValue() == null){

                smallHolder.tvSWCItemText.setText("null");

                smallHolder.ibSmallSWCItemIcon.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_DOWN){
                            notifyActivity.TouchDownItemOfRecyclerView(option.getId());
                            smallHolder.ibSmallSWCItemIcon.setColorFilter(
                                    ResourcesCompat.getColor(ctx.getResources(),
                                            android.R.color.holo_orange_light,
                                            null)
                            );
                            smallHolder.ibSmallSWCItemIcon.setBackground(
                                    ResourcesCompat.getDrawable(ctx.getResources(),
                                            R.drawable.small__activity_swc_imgbtns_shape_onclick,
                                            null )
                            );
                        }else if (event.getAction() == MotionEvent.ACTION_UP){
                            notifyActivity.TouchUpItemOfRecyclerView(option.getId());
                            //Toast.makeText(ctx, ""+ option.getId(), Toast.LENGTH_SHORT).show();
                            smallHolder.ibSmallSWCItemIcon.clearColorFilter();
                            smallHolder.ibSmallSWCItemIcon.setBackground(
                                    ResourcesCompat.getDrawable(ctx.getResources(),
                                            R.drawable.small_activity_swc_imgbtns_shape_defaultstate,
                                            null )
                            );
                            
                        }
                        return true;
                    }
                });
                smallHolder.ibSmallSWCItemIcon.setBackground(
                        ResourcesCompat.getDrawable(ctx.getResources(),
                                R.drawable.small_activity_swc_imgbtns_shape_defaultstate,
                                null ));
            }else {

                smallHolder.tvSWCItemText.setText(String.valueOf(option.getValue()));

                smallHolder.ibSmallSWCItemIcon.setColorFilter(
                        ResourcesCompat.getColor(ctx.getResources(),
                                android.R.color.holo_orange_light,
                                null)
                );

                smallHolder.ibSmallSWCItemIcon.setBackground(
                        ResourcesCompat.getDrawable(ctx.getResources(),
                                R.drawable.small__activity_swc_imgbtns_shape_onclick,
                                null )
                );
            }


        }else{ //holder instanceof LargeItemReyclerViewHolder

            final LargeItemReyclerViewHolder largeHolder = (LargeItemReyclerViewHolder) holder;
            largeHolder.ibLargeSwcItemIcon.setColorFilter(
                    ResourcesCompat.getColor(ctx.getResources(),
                            android.R.color.holo_orange_light,
                            null)
            );
            largeHolder.ibLargeSwcItemIcon.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN){

                        largeHolder.ibLargeSwcItemIcon.clearColorFilter();
                        largeHolder.ibLargeSwcItemIcon.setBackground(
                                ResourcesCompat.getDrawable(ctx.getResources(),
                                        R.drawable.large_activity_swc_imgbtns_shape_onclick,
                                        null )
                        );

                    }else if (event.getAction() == MotionEvent.ACTION_UP){

                        notifyActivity.clickedOnRefreshItem();


                        largeHolder.ibLargeSwcItemIcon.setColorFilter(
                                ResourcesCompat.getColor(ctx.getResources(),
                                        android.R.color.holo_orange_light,
                                        null)
                        );
                        largeHolder.ibLargeSwcItemIcon.setBackground(
                                ResourcesCompat.getDrawable(ctx.getResources(),
                                        R.drawable.large_activity_swc_imgbtns_shape_defaultstate,
                                        null )
                        );

                    }
                    return true;
                }
            });
        }

    }

    private int getItemIconResource(int position) {

        switch (position)
        {
            case 0:
                return R.drawable.ic_power_settings_new_black_50dp;
            case 1:
                return R.drawable.ic_accessibility_black_50dp;
            case 2:
                return R.drawable.ic_gps_not_fixed_black_50dp;
            case 3:
                return R.drawable.ic_volume_up_black_50dp;
            case 4:
                return R.drawable.ic_volume_down_black_50dp;
            case 5:
                return R.drawable.ic_volume_mute_black_50dp;
            case 6:
                return R.drawable.ic_play_arrow_black_50dp;
            case 7:
                return R.drawable.ic_skip_previous_black_50dp;
            case 8:
                return R.drawable.ic_skip_next_black_50dp;
            case 9:
                return R.drawable.ic_refresh_black_50dp;
            case 10:
                return R.drawable.ic_arrow_back_black_50dp;
            case 11:
                return R.drawable.ic_arrow_forward_black_50dp;
            case 12:
                return R.drawable.ic_call_black_50dp;
            case 13:
                return R.drawable.ic_call_end_black_50dp;
            default:
                return -1;

        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void reloadRecylcerView(List<ControllerOption> options) {
        data.clear();
        data.addAll(options);
        notifyDataSetChanged();
    }

    public class SmallItemRecyclerViewHolder extends RecyclerView.ViewHolder{

        ImageButton ibSmallSWCItemIcon;
        TextView tvSWCItemText;

        public SmallItemRecyclerViewHolder(View itemView) {
            super(itemView);
            ibSmallSWCItemIcon = itemView.findViewById(R.id.ib_small_swc_item_icon);
            tvSWCItemText = itemView.findViewById(R.id.tv_swc_item_text);
        }
    }

    public class LargeItemReyclerViewHolder extends RecyclerView.ViewHolder{

        ImageButton ibLargeSwcItemIcon;

        public LargeItemReyclerViewHolder(View itemView) {
            super(itemView);
            ibLargeSwcItemIcon = itemView.findViewById(R.id.ib_large_swc_item_icon);
        }
    }

    public interface NotifyActivity{
        public void clickedOnRefreshItem();
        public void TouchDownItemOfRecyclerView(Options itemTouchDown);
        public void TouchUpItemOfRecyclerView(Options itemTouchUp);


    }
}
