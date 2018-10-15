package ir.dev_roid.testusb.bluetoothFragments.contacts.AsyncTask;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;



import java.util.ArrayList;
import java.util.List;

import ir.dev_roid.testusb.bluetoothFragments.contacts.Adapter.CallInfoAdapter;
import ir.dev_roid.testusb.bluetoothFragments.contacts.Database;
import ir.dev_roid.testusb.bluetoothFragments.contacts.Pojo.CallInfo;

public class GetCallInfoTask extends AsyncTask<Void, Void, List<CallInfo>> {

    private Context ctx;
    private RecyclerView recyclerView;
    private List<CallInfo> data;

    public GetCallInfoTask(Context ctx, RecyclerView recyclerVie){
        this.ctx = ctx;
        this.recyclerView = recyclerVie;
    }

    @Override
    protected List<CallInfo> doInBackground(Void... voids) {

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("CallInfoTask : " + ctx);

        Database db = new Database(ctx);
        List<CallInfo> data = db.getCallInfoRuntimeExceptionDao().queryForAll();
        db.close();
        return data;
    }

    @Override
    protected void onPostExecute(List<CallInfo> callInfos) {
        super.onPostExecute(callInfos);
        recyclerView.setAdapter(new CallInfoAdapter(ctx,callInfos,recyclerView));
    }

}
