package ir.dev_roid.testusb.bluetoothFragments.contacts.PkgTelephoneActivity.PkgAddUserFragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import ir.dev_roid.testusb.R;
import ir.dev_roid.testusb.bluetoothFragments.contacts.PkgTelephoneActivity.InfluenceOfAddUserToActivityOps;
import ir.dev_roid.testusb.TelephoneActivity;
import ir.dev_roid.testusb.bluetoothFragments.contacts.Pojo.Audience;
import ir.dev_roid.testusb.bluetoothFragments.contacts.Pojo.PhoneNumber;


public class AddUserFragment extends Fragment implements RequiredViewOps {

    ProvidedPresenterOps presenterOps;
    Context ctx;

    EditText etAddUserFirstname;
    EditText etAddUserLastname;
    EditText etAddUserPhonenumber;

    private InfluenceOfAddUserToActivityOps toActivityOps;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.ctx = context;
        presenterOps = new AddUserPresenter(ctx, this);
        toActivityOps = (TelephoneActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.add_user_layout, container, false);

        etAddUserFirstname = v.findViewById(R.id.et_add_user_firstname);
        etAddUserLastname = v.findViewById(R.id.et_add_user_lastname);
        etAddUserPhonenumber = v.findViewById(R.id.et_add_user_phonenumber);

        if (getArguments().getSerializable("phoneNumber") != null) {
            PhoneNumber phoneNumber = (PhoneNumber) getArguments().getSerializable("phoneNumber");

            etAddUserPhonenumber.setText(phoneNumber.getPhone());
            etAddUserPhonenumber.setEnabled(false);
        }


        Button btnAddUserSave = v.findViewById(R.id.btn_add_user_save);
        Button btnAddUserCancel = v.findViewById(R.id.btn_add_user_cancel);

        btnAddUserSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newUserFirstName = etAddUserFirstname.getText().toString();
                String newUserLastName = etAddUserLastname.getText().toString();
                String phone = etAddUserPhonenumber.getText().toString();

                presenterOps.clickOnBtnSave(phone, newUserFirstName, newUserLastName);
            }
        });

        btnAddUserCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ctx, "add user canceled", Toast.LENGTH_SHORT).show();
                toActivityOps.closeAddUserFragment();
            }
        });

        return v;
    }

    @Override
    public void showAudienceAddSuccessfullyMessage(Audience newAudience) {

        Toast.makeText(ctx, "new audience added : \n" + newAudience.toString(), Toast.LENGTH_SHORT).show();
        toActivityOps.closeAddUserFragmentAndUpdatePhoneLogRecyclerView();
    }

    @Override
    public void showAudienceAddOccuredErrorMessage() {
        Toast.makeText(ctx, "new audience not added", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showDialogBasedOnAudienceInfoExsit(final String phone, final Audience exsistedAudience) {

        String dialogMessage = String.format("Do you want add %s to exsited audience {%s, %s} ?",
                phone, exsistedAudience.getFirstname(), exsistedAudience.getLastname()
        );

        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle("Audience exsit")
                .setMessage(dialogMessage)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        presenterOps.clickOnYesInAddExsitedAudienceInfoDialog(phone, exsistedAudience);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //close dialog
                    }
                })
                .setCancelable(false)
                .show();
    }

    @Override
    public void showDialogBasedOnEnteredPhoneNumberHasAnAudience(PhoneNumber dbPhoneNumberObj) {
        String dialogMessage = String.format("The phonenumber(%s) has an audience with username {%s,%s}",
                dbPhoneNumberObj.getPhone(),
                dbPhoneNumberObj.getAudience().getFirstname(), dbPhoneNumberObj.getAudience().getLastname()
        );

        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle("Phonenumber Exsit")
                .setMessage(dialogMessage)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setCancelable(false)
                .show();

        etAddUserPhonenumber.requestFocus();
    }

}
