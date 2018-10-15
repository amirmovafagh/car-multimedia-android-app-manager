package ir.dev_roid.testusb.bluetoothFragments.contacts;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

import ir.dev_roid.testusb.bluetoothFragments.contacts.Pojo.Audience;
import ir.dev_roid.testusb.bluetoothFragments.contacts.Pojo.CallInfo;
import ir.dev_roid.testusb.bluetoothFragments.contacts.Pojo.CallType;
import ir.dev_roid.testusb.bluetoothFragments.contacts.Pojo.PhoneNumber;

public class Database extends OrmLiteSqliteOpenHelper {

    private static final String DB_NAME = "telephone.db";
    private static final Integer DB_VERSION = 1;
    private static final String TAG = "Database";

    private RuntimeExceptionDao<Audience, Integer> audienceRuntimeExceptionDao;
    private RuntimeExceptionDao<PhoneNumber, Integer> phoneNumberRuntimeExceptionDao;
    private RuntimeExceptionDao<CallInfo, Integer> callInfoRuntimeExceptionDao;
    private RuntimeExceptionDao<CallType, Integer> callTypeRuntimeExceptionDao;

    public Database(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {


            database.endTransaction();

            database.setForeignKeyConstraintsEnabled(true);

            database.beginTransaction();
            TableUtils.createTable(connectionSource, Audience.class);
            TableUtils.createTable(connectionSource, PhoneNumber.class);
            TableUtils.createTable(connectionSource, CallInfo.class);
            TableUtils.createTable(connectionSource, CallType.class);



            getCallTypeRuntimeExceptionDao().create(new CallType(1, CallType.Type.INPUT));
            getCallTypeRuntimeExceptionDao().create(new CallType(2, CallType.Type.OUTPUT));
            getCallTypeRuntimeExceptionDao().create(new CallType(3, CallType.Type.MISSING));
        } catch (SQLException e) {
            Log.e(TAG, "onCreate: " + "Create Table Error !!! " );
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {

    }

    public RuntimeExceptionDao<Audience, Integer> getAudienceRuntimeExceptionDao() {

        if (audienceRuntimeExceptionDao == null)
            audienceRuntimeExceptionDao = getRuntimeExceptionDao(Audience.class);
        return audienceRuntimeExceptionDao;

    }

    public RuntimeExceptionDao<PhoneNumber, Integer> getPhoneNumberRuntimeExceptionDao() {

        if (phoneNumberRuntimeExceptionDao == null)
            phoneNumberRuntimeExceptionDao = getRuntimeExceptionDao(PhoneNumber.class);
        return phoneNumberRuntimeExceptionDao;
    }

    public RuntimeExceptionDao<CallInfo, Integer> getCallInfoRuntimeExceptionDao() {

        if (callInfoRuntimeExceptionDao == null)
            callInfoRuntimeExceptionDao = getRuntimeExceptionDao(CallInfo.class);
        return callInfoRuntimeExceptionDao;
    }

    public RuntimeExceptionDao<CallType, Integer> getCallTypeRuntimeExceptionDao() {

        if (callTypeRuntimeExceptionDao == null)
            callTypeRuntimeExceptionDao = getRuntimeExceptionDao(CallType.class);
        return callTypeRuntimeExceptionDao;

    }

    public void close()
    {
        super.close();
        audienceRuntimeExceptionDao = null;
        phoneNumberRuntimeExceptionDao = null;
        callInfoRuntimeExceptionDao = null;
    }
}
