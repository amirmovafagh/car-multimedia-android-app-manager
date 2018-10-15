package ir.dev_roid.testusb.bluetoothFragments.contacts.AsyncTask;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;



import java.util.List;

import ir.dev_roid.testusb.bluetoothFragments.contacts.Adapter.AudienceAdapter;
import ir.dev_roid.testusb.bluetoothFragments.contacts.Database;
import ir.dev_roid.testusb.bluetoothFragments.contacts.Pojo.Audience;

public class GetAudienceTask extends AsyncTask<Void, Void, List<Audience>> {

    private Context ctx;
    private RecyclerView recyclerView;

    public GetAudienceTask(Context ctx, RecyclerView recyclerView){

        this.ctx = ctx;
        this.recyclerView = recyclerView;
    }

    @Override
    protected List<Audience> doInBackground(Void... voids) {

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Database db = new Database(ctx);
        List<Audience> audiences = db.getAudienceRuntimeExceptionDao().queryForAll();
        db.close();
        return audiences;
    }

    @Override
    protected void onPostExecute(List<Audience> audiences) {
        super.onPostExecute(audiences);
        recyclerView.setAdapter(new AudienceAdapter(ctx, audiences));

    }
}
