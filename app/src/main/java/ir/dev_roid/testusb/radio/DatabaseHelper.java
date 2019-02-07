package ir.dev_roid.testusb.radio;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.List;

/**
 * Database helper class used to manage the creation and upgrading of your database. This class also usually provides
 * the DAOs used by the other classes.
 */

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
    // name of the database file for your application -- change to something appropriate for your app
    private static final String DATABASE_NAME = "radioDb.db";
    // any time you make changes to your database objects, you may have to increase the database version
    private static final int DATABASE_VERSION = 1;

    private static final String TAG = DatabaseHelper.class.getSimpleName();

    // the DAO object we use to access the SimpleData table
    private static RuntimeExceptionDao<RadioChannelFM, Integer> radioChannelFM_RuntimeExceptionDao = null;
    private static RuntimeExceptionDao<RadioChannelAM, Integer> radioChannelAM_RuntimeExceptionDao = null;


    private Context ctx;

    private static DatabaseHelper databaseHelper = null;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.ctx = context;
    }

    public static DatabaseHelper getInstance(Context ctx) {
        if (databaseHelper == null)
            databaseHelper = new DatabaseHelper(ctx);
        return databaseHelper;
    }


    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, RadioChannelFM.class);
            TableUtils.createTable(connectionSource, RadioChannelAM.class);


        } catch (SQLException e) {
            Log.e(TAG, "onCreate: " + "Create Table Error !!! ");
            Toast.makeText(ctx, "Create Table Error !!! ", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {

    }

    public RuntimeExceptionDao<RadioChannelFM, Integer> getRadioChannelFM_RuntimeExceptionDao() {
        if (radioChannelFM_RuntimeExceptionDao == null)
            radioChannelFM_RuntimeExceptionDao = getRuntimeExceptionDao(RadioChannelFM.class);
        return radioChannelFM_RuntimeExceptionDao;
    }

    public List<RadioChannelFM> getAllRadioChannelsFM() {
        return getRadioChannelFM_RuntimeExceptionDao().queryForAll();
    }

    public void insertRadioChannelsFM(String channelValue) {
        try {
            getRadioChannelFM_RuntimeExceptionDao().create(new RadioChannelFM(channelValue));
        } catch (Exception e) {
        }
    }
    public void removeAllRadioChannelsFM() {
        DeleteBuilder<RadioChannelFM, Integer> db = radioChannelFM_RuntimeExceptionDao.deleteBuilder();
        try {
            db.delete();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public RuntimeExceptionDao<RadioChannelAM, Integer> getRadioChannelAM_RuntimeExceptionDao() {
        if (radioChannelAM_RuntimeExceptionDao == null)
            radioChannelAM_RuntimeExceptionDao = getRuntimeExceptionDao(RadioChannelAM.class);
        return radioChannelAM_RuntimeExceptionDao;
    }

    public List<RadioChannelAM> getAllRadioChannelsAM() {
        return getRadioChannelAM_RuntimeExceptionDao().queryForAll();
    }

    public void insertRadioChannelsAM(String channelValue) {
        try {getRadioChannelAM_RuntimeExceptionDao().create(new RadioChannelAM(channelValue));
            if(Integer.parseInt(channelValue)!=0){

            }
        } catch (Exception e) {
        }
    }
    public void removeAllRadioChannelsAM() {
        DeleteBuilder<RadioChannelAM, Integer> db = radioChannelAM_RuntimeExceptionDao.deleteBuilder();
        try {
            db.delete();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
