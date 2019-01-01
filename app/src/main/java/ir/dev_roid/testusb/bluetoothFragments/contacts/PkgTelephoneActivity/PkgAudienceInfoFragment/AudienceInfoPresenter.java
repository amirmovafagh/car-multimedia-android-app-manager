package ir.dev_roid.testusb.bluetoothFragments.contacts.PkgTelephoneActivity.PkgAudienceInfoFragment;

import android.content.Context;

import java.util.List;

import ir.dev_roid.testusb.bluetoothFragments.contacts.Pojo.Audience;

public class AudienceInfoPresenter implements ProvidedPresenterOps, RequiredPresenterOps {

    private static AudienceInfoPresenter audienceInfoPresenter;

    private Context ctx;
    private final RequiredViewOps viewOps;
    private final ProvidedModelOps modelOps;

    public static AudienceInfoPresenter getInstance(Context ctx, RequiredViewOps viewOps) {
        if (audienceInfoPresenter == null)
            audienceInfoPresenter = new AudienceInfoPresenter(ctx, viewOps);
        return audienceInfoPresenter;
    }

    public AudienceInfoPresenter(Context ctx, RequiredViewOps viewOps) {
        this.ctx = ctx;
        this.viewOps = viewOps;
        modelOps = AudienceInfoModel.getInstance(ctx, this);
    }

    @Override
    public void reloadRecyclerView() {
        List<Audience> audiences = modelOps.retriveAllAudiences();
        viewOps.showPreparedAudienceInfos(audiences);
    }

    @Override
    public void searchOnAudienceInfos(String searchText) {
        List<Audience> audiences = modelOps.searchOnAudiencesByName(searchText);
        viewOps.showPreparedAudienceInfos(audiences);
    }

    @Override
    public void clickedOnYesInRemoveAudiencePermissionDialog(Audience audience) {
        int res = modelOps.removeAnAudience(audience);
        if (res >= 0)
            viewOps.showAudienceRemoveSuccessfullyMessage(res);
        else
            viewOps.showAudienceNotRemoveMessage();
    }

    @Override
    public String searchOnIdPhoneNumber(int id) {
        return modelOps.searchOnIdPhoneNumber(id);
    }

}
