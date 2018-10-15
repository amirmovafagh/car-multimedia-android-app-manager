package ir.dev_roid.testusb.bluetoothFragments;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ir.dev_roid.testusb.R;
import ir.dev_roid.testusb.bluetoothFragments.contacts.Adapter.AudienceAdapter;
import ir.dev_roid.testusb.bluetoothFragments.contacts.AsyncTask.GetAudienceTask;
import ir.dev_roid.testusb.bluetoothFragments.contacts.Database;
import ir.dev_roid.testusb.bluetoothFragments.contacts.Pojo.Audience;
import ir.dev_roid.testusb.bluetoothFragments.contacts.Pojo.PhoneNumber;


public class ContactsFragment extends Fragment implements View.OnClickListener, DialogInterface.OnClickListener{
    private static final String TAG = ContactsFragment.class.getName();



    private enum DialogType {SearchDialog, AddDialog};
    private ContactsFragment.DialogType dialogType = null;
    private RecyclerView recyclerView;

    private View view;

    private View AudienceDialogView;
    private View SearchAudienceDialogView;


    public ContactsFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_contacts, container, false);

        Button btnRefresh = (Button) view.findViewById(R.id.btn_refresh);
        btnRefresh.setOnClickListener(this);

        Button btnAddAudience = (Button) view.findViewById(R.id.btn_add_audience);
        btnAddAudience.setOnClickListener(this);

        Button btnSearchAudience = (Button) view.findViewById(R.id.btn_search_audience);
        btnSearchAudience.setOnClickListener(this);


        recyclerView = (RecyclerView) view.findViewById(R.id.rv_audience);

        AudienceAdapter audienceAdapter = new AudienceAdapter(getActivity(), new ArrayList<Audience>());


        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));


        recyclerView.setAdapter(audienceAdapter);

        GetAudienceTask audienceTask = new GetAudienceTask(getActivity(),
                recyclerView);
        audienceTask.execute();

        return view;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_POSITIVE && dialogType == ContactsFragment.DialogType.AddDialog)
            this.DialogAddAudiencePositiveButtonOnClick();
        else if(which == DialogInterface.BUTTON_NEGATIVE && dialogType == ContactsFragment.DialogType.AddDialog)
            this.DialogAddAudienceNegativeButtonOnClick();
        else if (which == DialogInterface.BUTTON_POSITIVE && dialogType == ContactsFragment.DialogType.SearchDialog)
            this.DialogSearchAudiencePositiveButtonOnClick();
        else if (which == DialogInterface.BUTTON_NEGATIVE && dialogType == ContactsFragment.DialogType.SearchDialog)
            this.DialogSearchAudienceNegativeButtonOnClick();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_add_audience)
            this.BtnAddAudienceOnClick();
        else if (v.getId() == R.id.btn_search_audience)
            this.BtnSearchAudienceOnClick();
        else if (v.getId() == R.id.btn_refresh)
            this.BtnRefreshOnClick();
    }

    private void DialogAddAudienceNegativeButtonOnClick() {

        Toast.makeText(getActivity(), "Add Audience Canceled !!!", Toast.LENGTH_SHORT).show();
    }

    private void BtnRefreshOnClick() {

        Database db = new Database(getActivity());
        List<Audience> audiences = db.getAudienceRuntimeExceptionDao().queryForAll();
        db.close();

        recyclerView.setAdapter(new AudienceAdapter(getActivity(),audiences));
    }


    public void BtnAddAudienceOnClick() {
        dialogType = ContactsFragment.DialogType.AddDialog;
        this.showDialog();
    }

    private void BtnSearchAudienceOnClick() {
        dialogType = ContactsFragment.DialogType.SearchDialog;
        this.showDialog();
    }

    private void showDialog() {

        AudienceDialogView = LayoutInflater.from(getActivity()).inflate(R.layout.audience_dialog_layout, null);


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(dialogType == ContactsFragment.DialogType.AddDialog ? "Add Audience" : "Search Audience")
                .setView(AudienceDialogView)
                .setPositiveButton(dialogType == ContactsFragment.DialogType.AddDialog ? "Add" : "Search", this)
                .setNegativeButton("cancel", this);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void DialogAddAudiencePositiveButtonOnClick() {

        String newAudienceFirstname = ((EditText) AudienceDialogView.findViewById(R.id.et_firstname)).getText().toString();
        String newAudienceLastname = ((EditText) AudienceDialogView.findViewById(R.id.et_lastname)).getText().toString();
        String newAudiencePhone = ((EditText) AudienceDialogView.findViewById(R.id.et_phone)).getText().toString();

        if (!InputIsValid(newAudienceFirstname, newAudienceLastname, newAudiencePhone)) {
            Toast.makeText(getActivity(), "Insertation Failed !!!", Toast.LENGTH_SHORT).show();
        } else {

            if (AudienceExsit(newAudienceFirstname, newAudienceLastname)) {
                Toast.makeText(getActivity(), "Audience exist !!!", Toast.LENGTH_SHORT).show();
            } else if (PhoneNumberExist(newAudiencePhone)) {
                Toast.makeText(getActivity(), "PhoneNumber exist !!!", Toast.LENGTH_SHORT).show();
            } else {

                Database db = new Database(getActivity());

                Audience newAudience = new Audience(null, newAudienceFirstname, newAudienceLastname, null);
                db.getAudienceRuntimeExceptionDao().create(newAudience);

                PhoneNumber newPhonenumber = new PhoneNumber(null, newAudiencePhone);
                newPhonenumber.setAudience(newAudience);

                db.getPhoneNumberRuntimeExceptionDao().create(newPhonenumber);

                recyclerView.setAdapter(new AudienceAdapter(
                        getActivity(),
                        db.getAudienceRuntimeExceptionDao().queryForAll())
                );

                db.close();
            }

        }

    }


    private void DialogSearchAudienceNegativeButtonOnClick() {
        Toast.makeText(getActivity(), "Search Audience Canceled !!!", Toast.LENGTH_SHORT).show();
    }

    private void DialogSearchAudiencePositiveButtonOnClick() {

        List<Audience> audiencesByName = null;
        List<Audience> audiencesByPhone = null;

        String firstname = ((EditText) AudienceDialogView.findViewById(R.id.et_firstname)).getText().toString();
        String lastname = ((EditText) AudienceDialogView.findViewById(R.id.et_lastname)).getText().toString();
        String phone = ((EditText) AudienceDialogView.findViewById(R.id.et_phone)).getText().toString();


        audiencesByName = SearchAudience(firstname,lastname);
        audiencesByPhone = SearchAudience(phone);

        audiencesByName.addAll(audiencesByPhone);

        recyclerView.setAdapter(new AudienceAdapter(getActivity(),audiencesByName));

    }

    private List<Audience> SearchAudience(String phone) {

        if (phone.equals(""))
            phone = null;

        List<PhoneNumber> phoneNumbers = null;
        List<Audience> audiences = new ArrayList<>();
        Database db = new Database(getActivity());
        try{
            phoneNumbers  = db.getPhoneNumberRuntimeExceptionDao().query(
                    db.getPhoneNumberRuntimeExceptionDao().queryBuilder().where()
                            .like(PhoneNumber.PHONE_FEILD_NAME, "%"+phone+"%")
                            .prepare()
            );
        }catch (SQLException ex){
            ex.printStackTrace();
        }


        for (PhoneNumber p:phoneNumbers)
            if(p.getAudience()!= null)
                audiences.add(p.getAudience());

        return audiences;
    }

    private List<Audience> SearchAudience(String firstname, String lastname) {

        if (firstname.equals(""))
            firstname = null;
        if (lastname.equals(""))
            lastname = null;

        List<Audience> audiences = null;

        Database db = new Database(getActivity());
        try {
            audiences = db.getAudienceRuntimeExceptionDao().query(
                    db.getAudienceRuntimeExceptionDao().queryBuilder().where()
                            .like(Audience.FIRSTNAME_FEILD_NAME, "%"+firstname+"%")
                            .or()
                            .like(Audience.LASTNAME_FEILD_NAME, "%"+lastname+"%")
                            .prepare()
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return audiences;
    }

    private boolean InputIsValid(String newAudienceFirstname, String newAudienceLastname, String newAudiencePhone) {

        if (newAudienceFirstname.equals("") || newAudienceLastname.equals("") ||
                newAudiencePhone.length() < 4)
            return false;
        else
            return true;

    }

    private boolean PhoneNumberExist(String phone) {

        boolean result = false;
        Database db = new Database(getActivity());

        List<PhoneNumber> exsitedPhoneNumbers = null;
        try {
            exsitedPhoneNumbers =
                    db.getPhoneNumberRuntimeExceptionDao().query(
                            db.getPhoneNumberRuntimeExceptionDao().queryBuilder().where()
                                    .eq(PhoneNumber.PHONE_FEILD_NAME, phone)
                                    .prepare()
                    );
            if (exsitedPhoneNumbers.size() < 0)
                result = true;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        db.close();
        return result;

    }

    private boolean AudienceExsit(String firstname, String lastname) {
        boolean result = false;
        Database db = new Database(getActivity());
        List<Audience> exsitedAudience = null;
        try {
            exsitedAudience =
                    db.getAudienceRuntimeExceptionDao().query(
                            db.getAudienceRuntimeExceptionDao().queryBuilder().where()
                                    .eq(Audience.FIRSTNAME_FEILD_NAME, firstname)
                                    .and()
                                    .eq(Audience.LASTNAME_FEILD_NAME, lastname)
                                    .prepare()
                    );
            if (exsitedAudience.size() < 0)
                result = true;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        db.close();
        return result;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    public static ContactsFragment newInstance() {

        Bundle args = new Bundle();

        ContactsFragment fragment = new ContactsFragment();
        fragment.setArguments(args);
        return fragment;
    }

}
