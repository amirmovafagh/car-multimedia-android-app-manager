package com.hooshmandkhodro.carservice.bluetoothFragments.contacts.PkgTelephoneActivity.PkgAudienceInfoFragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


import com.hooshmandkhodro.carservice.R;
import com.hooshmandkhodro.carservice.bluetoothFragments.contacts.PkgTelephoneActivity.InfluenceOfAudienceInfoToActivity;
import com.hooshmandkhodro.carservice.bluetoothFragments.contacts.PkgTelephoneActivity.PkgPhoneDialerFragment.PhoneDialerFragment;
import com.hooshmandkhodro.carservice.TelephoneActivity;
import com.hooshmandkhodro.carservice.bluetoothFragments.contacts.Pojo.Audience;

public class AudienceInfoFragment extends Fragment implements
        InfluenceOfActivityToAudienceInfoOps, RequiredViewOps {

    private Context ctx;
    private ProvidedPresenterOps presenterOps;

    RecyclerViewAdapter recyclerViewAdapter;

    InfluenceOfAudienceInfoToActivity toActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ctx = getContext();
        presenterOps = new AudienceInfoPresenter(ctx, this);
        toActivity = (TelephoneActivity) getActivity();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_audience, container, false);

        RecyclerView rvAudience = v.findViewById(R.id.rv_audience);
        List<Audience> data = new ArrayList<>();
        recyclerViewAdapter = new RecyclerViewAdapter(getContext(), data,
                new RecyclerViewAdapter.NotifyFragment() {
                    @Override
                    public void clickOnAudienceItem(Audience audience) {
                        PhoneDialerFragment frag = new PhoneDialerFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("num", presenterOps.searchOnIdPhoneNumber(audience.getIdAudience()));  // Key, value
                        frag.setArguments(bundle);

                        FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction()
                                .replace(R.id.fl_fragment_container
                                        , frag)
                                .addToBackStack(null)
                                .commit();
                    }

                    @Override
                    public boolean longClickOnAudienceItem(final Audience audience, View anchor) {
                        PopupMenu popupMenu = new PopupMenu(ctx, anchor);
                        popupMenu.inflate(R.menu.audience_info_item_options);
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.mi_change_audience_info:
                                        toActivity.showChangeAudienceInfoFragment(audience);
                                        return true;
                                    case R.id.mi_remove_audience:
                                        showDialogBasedOnPermissionOfRemoveAudience(audience);
                                        return true;
                                    default:
                                        return false;
                                }
                            }
                        });
                        popupMenu.show();
                        return true;
                    }
                }
        );
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rvAudience.setLayoutManager(layoutManager);
        rvAudience.setAdapter(recyclerViewAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(ctx,
                layoutManager.getOrientation());
        rvAudience.addItemDecoration(dividerItemDecoration);

        return v;
    }

    private void showDialogBasedOnPermissionOfRemoveAudience(final Audience audience) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle("Remove Audience")
                .setMessage(String.format("Are you sure to remove {%s, %s}", audience.getFirstname(), audience.getLastname()))
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        presenterOps.clickedOnYesInRemoveAudiencePermissionDialog(audience);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(ctx, "remove audience canceled", Toast.LENGTH_SHORT).show();
                    }
                })
                .setCancelable(false)
                .show();
    }


    public static AudienceInfoFragment newInstance() {

        Bundle args = new Bundle();

        AudienceInfoFragment fragment = new AudienceInfoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void fillRecyclerView() {
        presenterOps.reloadRecyclerView();
    }

    @Override
    public void searchActived(String searchText) {
        presenterOps.searchOnAudienceInfos(searchText);
    }

    @Override
    public void showPreparedAudienceInfos(List<Audience> audiences) {
        recyclerViewAdapter.updateRecyclerView(audiences);
    }

    @Override
    public void showAudienceRemoveSuccessfullyMessage(int removedRow) {
        presenterOps.reloadRecyclerView();
        Toast.makeText(ctx, "Audience Removed at row " + removedRow, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showAudienceNotRemoveMessage() {
        Toast.makeText(ctx, "exception occured in remove audience", Toast.LENGTH_SHORT).show();
    }
}
