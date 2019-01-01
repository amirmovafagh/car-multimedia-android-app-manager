package ir.dev_roid.testusb.bluetoothFragments.contacts;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

import ir.dev_roid.testusb.bluetoothFragments.contacts.Pojo.Audience;
import ir.dev_roid.testusb.bluetoothFragments.contacts.Pojo.CallLog;
import ir.dev_roid.testusb.bluetoothFragments.contacts.Pojo.CallType;
import ir.dev_roid.testusb.bluetoothFragments.contacts.Pojo.PhoneNumber;

public class ModelDAOs extends OrmLiteSqliteOpenHelper {

    private static final String DB_NAME = "telephone.db";
    private static final Integer DB_VERSION = 1;
    private static final String TAG = "Database";

    private RuntimeExceptionDao<Audience, Integer> audienceRuntimeExceptionDao;
    private RuntimeExceptionDao<PhoneNumber, Integer> phoneNumberRuntimeExceptionDao;
    private RuntimeExceptionDao<CallLog, Integer> callInfoRuntimeExceptionDao;
    private RuntimeExceptionDao<CallType, Integer> callTypeRuntimeExceptionDao;

    Context ctx;

    private static ModelDAOs modelDAOs = null;

    public static ModelDAOs getInstance(Context ctx) {
        if (modelDAOs == null) {
            modelDAOs = new ModelDAOs(ctx);
        }
        return modelDAOs;
    }

    private ModelDAOs(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.ctx = context;

    }


    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {

            database.endTransaction();

            database.setForeignKeyConstraintsEnabled(true);

            database.beginTransaction();
            TableUtils.createTable(connectionSource, Audience.class);
            TableUtils.createTable(connectionSource, PhoneNumber.class);
            TableUtils.createTable(connectionSource, CallLog.class);
            TableUtils.createTable(connectionSource, CallType.class);

            getCallTypeRuntimeExceptionDao().create(new CallType(CallType.INPUT));
            getCallTypeRuntimeExceptionDao().create(new CallType(CallType.OUTPUT));
            getCallTypeRuntimeExceptionDao().create(new CallType(CallType.MISSED));
        } catch (SQLException e) {
            Log.e(TAG, "onCreate: " + "Create Table Error !!! ");
            Toast.makeText(ctx, "Create Table Error !!! ", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion,
                          int newVersion) {

    }

    public RuntimeExceptionDao<Audience, Integer> getAudienceRuntimeExceptionDao() {

        if (audienceRuntimeExceptionDao == null) {
            audienceRuntimeExceptionDao = getRuntimeExceptionDao(Audience.class);
        }
        return audienceRuntimeExceptionDao;

    }

    public RuntimeExceptionDao<PhoneNumber, Integer> getPhoneNumberRuntimeExceptionDao() {

        if (phoneNumberRuntimeExceptionDao == null) {
            phoneNumberRuntimeExceptionDao = getRuntimeExceptionDao(PhoneNumber.class);
        }
        return phoneNumberRuntimeExceptionDao;
    }

    public RuntimeExceptionDao<CallLog, Integer> getCallInfoRuntimeExceptionDao() {

        if (callInfoRuntimeExceptionDao == null) {
            callInfoRuntimeExceptionDao = getRuntimeExceptionDao(CallLog.class);
        }
        return callInfoRuntimeExceptionDao;
    }

    public RuntimeExceptionDao<CallType, Integer> getCallTypeRuntimeExceptionDao() {

        if (callTypeRuntimeExceptionDao == null) {
            callTypeRuntimeExceptionDao = getRuntimeExceptionDao(CallType.class);
        }
        return callTypeRuntimeExceptionDao;

    }

    public void close() {
        audienceRuntimeExceptionDao = null;
        phoneNumberRuntimeExceptionDao = null;
        callInfoRuntimeExceptionDao = null;
        callTypeRuntimeExceptionDao = null;

        super.close();
    }

}
