package ir.dev_roid.testusb.bluetoothFragments.contacts.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;



import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import ir.dev_roid.testusb.R;
import ir.dev_roid.testusb.bluetoothFragments.contacts.Database;
import ir.dev_roid.testusb.bluetoothFragments.contacts.Pojo.Audience;
import ir.dev_roid.testusb.bluetoothFragments.contacts.Pojo.CallInfo;
import ir.dev_roid.testusb.bluetoothFragments.contacts.Pojo.CallType;
import ir.dev_roid.testusb.bluetoothFragments.contacts.Pojo.PhoneNumber;


public class CallInfoAdapter extends RecyclerView.Adapter<CallInfoViewHolder> {

    private Context ctx;
    private List<CallInfo> callInfos;
    private RecyclerView recyclerView;


    public CallInfoAdapter(Context ctx, List<CallInfo> callInfos, RecyclerView recyclerView){

        this.ctx = ctx;
        this.callInfos = callInfos;
        this.recyclerView = recyclerView;
    }

    @NonNull
    @Override
    public CallInfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(ctx).inflate(R.layout.callinfo_layout,parent,false);
        return new CallInfoViewHolder(view, ctx, recyclerView);

    }

    @Override
    public void onBindViewHolder(@NonNull CallInfoViewHolder holder, int position) {

        CallInfo callInfo = callInfos.get(position);
        PhoneNumber callinfoPhonenumber = callInfo.getPhoneNumber();
        Audience callinfoAudience = callInfo.getPhoneNumber().getAudience();
        CallType callinfoCalltype= callInfo.getCallType();
        holder.txtUserName.setText(
                callinfoAudience==null ? "(UNKNOWN)" :
                        callinfoAudience.getFirstname() + " " + callinfoAudience.getLastname()
        );

        holder.txtPhoneNumber.setText(callinfoPhonenumber.getPhone());

        if (callinfoAudience == null){
            holder.btnImangeUser.setBackground(ResourcesCompat.getDrawable(ctx.getResources(),R.drawable.ic_person_add_black_32dp_unknown_user,null));
        }else {
            holder.btnImangeUser.setBackground(ResourcesCompat.getDrawable(ctx.getResources(),R.drawable.ic_person_black_32dp_known_user,null));
        }

        if (callinfoCalltype.getType() == CallType.Type.INPUT) {
            holder.imgCallTypeIcon.setImageDrawable(ResourcesCompat.getDrawable(ctx.getResources(), R.drawable.ic_call_received_black_16dp_call_recived, null));
        }else if (callinfoCalltype.getType() == CallType.Type.OUTPUT) {
            holder.imgCallTypeIcon.setImageDrawable(ResourcesCompat.getDrawable(ctx.getResources(), R.drawable.ic_call_made_black_16dp_made_call, null));
        }else if (callinfoCalltype.getType() == CallType.Type.MISSING) {
            holder.imgCallTypeIcon.setImageDrawable(ResourcesCompat.getDrawable(ctx.getResources(), R.drawable.ic_call_missed_black_16dp_missed_call, null));
        }

        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        holder.txtDate.setText(dateFormat.format(callInfo.getDate()));

        DateFormat timeFormat = new SimpleDateFormat("hh:mm");
        holder.txtTime.setText(timeFormat.format(callInfo.getDate()));


    }

    @Override
    public int getItemCount() {
        return callInfos.size();
    }
}

class CallInfoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, DialogInterface.OnClickListener{

    public Button btnImangeUser;
    public TextView txtUserName;
    public ImageView imgCallTypeIcon;
    public TextView txtPhoneNumber;
    public TextView txtDate;
    public TextView txtTime;
    private Context ctx;
    private RecyclerView recyclerView;
    private View AudienceDialogView;

    public CallInfoViewHolder(View itemView, Context ctx, RecyclerView recyclerView) {
        super(itemView);

        btnImangeUser = (Button) itemView.findViewById(R.id.btn_img_user);
        txtUserName = (TextView) itemView.findViewById(R.id.tv_username);
        imgCallTypeIcon = (ImageView) itemView.findViewById(R.id.img_calltype_icon);
        txtPhoneNumber = (TextView) itemView.findViewById(R.id.tv_phonenumber);
        txtDate = (TextView) itemView.findViewById(R.id.tv_date);
        txtTime = (TextView) itemView.findViewById(R.id.tv_time);
        this.ctx = ctx;
        this.recyclerView = recyclerView;


        btnImangeUser.setOnClickListener(this);
        itemView.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.btn_img_user)
            this.BtnImgUserOnClick();
        else
            this.ViewOnClick();
    }

    private void ViewOnClick() {

        Toast.makeText(ctx, "view Clicked", Toast.LENGTH_SHORT).show();

    }

    private void BtnImgUserOnClick() {

        if (txtUserName.getText().toString().equals("(UNKNOWN)"))
        {
            this.showDialog();

        }else {

            Toast.makeText(ctx, "phonenumber have a audience", Toast.LENGTH_SHORT).show();

        }

    }

    private void showDialog() {

        AudienceDialogView = LayoutInflater.from(ctx).inflate(R.layout.audience_dialog_layout, null);

        EditText txtPhone = (EditText) AudienceDialogView.findViewById(R.id.et_phone);
        txtPhone.setText(txtPhoneNumber.getText().toString());
        txtPhone.setEnabled(false);

        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle("Add Audience")
                .setView(AudienceDialogView)
                .setPositiveButton("Add", this)
                .setNegativeButton("cancel", this);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

        if (which == DialogInterface.BUTTON_POSITIVE)
            this.BtnPositiveDialogOnClick();
        else
            this.BtnNegativeDialogOnClick();

    }

    private void BtnNegativeDialogOnClick() {
        Toast.makeText(ctx, "Add audience canceled !!!", Toast.LENGTH_SHORT).show();
    }

    private void BtnPositiveDialogOnClick() {

        Database db = new Database(ctx);


        Audience newAudience = new Audience();

        String firstname = ((EditText) AudienceDialogView.findViewById(R.id.et_firstname)).getText().toString();
        String lastname = ((EditText) AudienceDialogView.findViewById(R.id.et_lastname)).getText().toString();

        newAudience.setFirstname(firstname);
        newAudience.setLastname(lastname);

        db.getAudienceRuntimeExceptionDao().create(newAudience);

        String phone = txtPhoneNumber.getText().toString();

        PhoneNumber phoneNumber = null;

        try {
            phoneNumber = db.getPhoneNumberRuntimeExceptionDao().query(
                    db.getPhoneNumberRuntimeExceptionDao().queryBuilder().where()
                            .eq(PhoneNumber.PHONE_FEILD_NAME, phone)
                            .prepare()
            ).get(0);
        }catch (SQLException ex){
            ex.printStackTrace();
        }


        phoneNumber.setAudience(newAudience);
        db.getPhoneNumberRuntimeExceptionDao().update(phoneNumber);

        List<CallInfo> callInfos = db.getCallInfoRuntimeExceptionDao().queryForAll();

        db.close();


        recyclerView.setAdapter(new CallInfoAdapter(ctx, callInfos, recyclerView));

    }
}
