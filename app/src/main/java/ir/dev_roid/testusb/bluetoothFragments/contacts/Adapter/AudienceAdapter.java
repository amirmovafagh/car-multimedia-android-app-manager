package ir.dev_roid.testusb.bluetoothFragments.contacts.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import java.util.List;

import ir.dev_roid.testusb.R;
import ir.dev_roid.testusb.bluetoothFragments.contacts.Pojo.Audience;
import ir.dev_roid.testusb.bluetoothFragments.contacts.Pojo.PhoneNumber;

public class AudienceAdapter extends RecyclerView.Adapter<AudienceViewHolder> {

    private Context ctx;
    private List<Audience> audiences;

    public AudienceAdapter(Context ctx, List<Audience> audiences){

        this.ctx = ctx;
        this.audiences = audiences;
    }

    @NonNull
    @Override
    public AudienceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(ctx).inflate(R.layout.audience_layout,parent,false);
        return new AudienceViewHolder(view, ctx);
    }

    @Override
    public void onBindViewHolder(@NonNull AudienceViewHolder holder, int position) {


        if (audiences.get(position) == null)
            return;
        String firstname = audiences.get(position).getFirstname();
        String lastname = audiences.get(position).getLastname();
        String audienceName = firstname + " " + lastname;
        holder.txtAudienceName.setText(audienceName);

        holder.btnAudienceImage.setBackground(
                ResourcesCompat.getDrawable(
                        ctx.getResources(),R.drawable.ic_person_black_32dp_known_user, null
                )
        );

        holder.audience = audiences.get(position);

    }

    @Override
    public int getItemCount() {
        return audiences.size();
    }
}

class AudienceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    Button btnAudienceImage;
    TextView txtAudienceName;
    Audience audience;

    private Context ctx;

    public AudienceViewHolder(View itemView, Context ctx) {
        super(itemView);

        btnAudienceImage = (Button) itemView.findViewById(R.id.btn_audience_image);
        txtAudienceName = (TextView) itemView.findViewById(R.id.tv_audience_name);
        this.ctx = ctx;

        btnAudienceImage.setOnClickListener(this);
        itemView.setOnClickListener(this);
        txtAudienceName.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.btn_audience_image)
            this.BtnAudienceImageOnClick();
        else if(v.getId() == R.id.rl_audience)
            this.ViewAudienceLayoutOnClick();
        else if(v.getId() == R.id.tv_audience_name)
            this.TxtAudienceNameOnClick();
    }

    private void TxtAudienceNameOnClick() {

        StringBuilder stringBuilder = new StringBuilder();
        for (PhoneNumber p : audience.getPhoneNumbers())
            stringBuilder.append(p.getPhone()).append("\n");
        Toast.makeText(ctx, stringBuilder.toString(), Toast.LENGTH_SHORT).show();

    }

    private void ViewAudienceLayoutOnClick() {
        this.TxtAudienceNameOnClick();
    }

    private void BtnAudienceImageOnClick() {
        Toast.makeText(ctx,"button audience image clicked ... ", Toast.LENGTH_SHORT).show();
    }
}
