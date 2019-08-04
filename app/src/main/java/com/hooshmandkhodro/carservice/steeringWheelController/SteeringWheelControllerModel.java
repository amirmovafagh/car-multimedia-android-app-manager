package com.hooshmandkhodro.carservice.steeringWheelController;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.hooshmandkhodro.carservice.steeringWheelController.Pojo.ControllerOption;
import com.hooshmandkhodro.carservice.steeringWheelController.Pojo.Options;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.List;

public class SteeringWheelControllerModel extends OrmLiteSqliteOpenHelper implements ProvidedModelOps{

    private static final String DB_NAME = "swc.db";
    private static final Integer DB_VERSION = 1;
    private static final String TAG = "Database";

    private RuntimeExceptionDao<ControllerOption, Integer> controllerOptionRuntimeExceptionDao;
    private final RequiredPresenterOps presenterOps;

    Context ctx;

    private static SteeringWheelControllerModel steeringWheelControllerModel = null;

    public static SteeringWheelControllerModel getInstance(Context ctx, RequiredPresenterOps presenterOps) {
        if (steeringWheelControllerModel == null)
            steeringWheelControllerModel = new SteeringWheelControllerModel(ctx, presenterOps);
        return steeringWheelControllerModel;
    }
    public static SteeringWheelControllerModel getInstance(Context ctx) {
        if (steeringWheelControllerModel == null)
            steeringWheelControllerModel = new SteeringWheelControllerModel(ctx);
        return steeringWheelControllerModel;
    }

    private SteeringWheelControllerModel(Context context, RequiredPresenterOps presenterOps) {
        super(context, DB_NAME, null, DB_VERSION);
        this.ctx = context;
        this.presenterOps = presenterOps;
    }

    private SteeringWheelControllerModel(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.ctx = context;

        presenterOps = null;
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, ControllerOption.class);

            getControllerOptionRuntimeExceptionDao().create(new ControllerOption(Options.OPT0, null));
            getControllerOptionRuntimeExceptionDao().create(new ControllerOption(Options.OPT1, null));
            getControllerOptionRuntimeExceptionDao().create(new ControllerOption(Options.OPT2, null));
            getControllerOptionRuntimeExceptionDao().create(new ControllerOption(Options.OPT3, null));
            getControllerOptionRuntimeExceptionDao().create(new ControllerOption(Options.OPT4, null));
            getControllerOptionRuntimeExceptionDao().create(new ControllerOption(Options.OPT5, null));
            getControllerOptionRuntimeExceptionDao().create(new ControllerOption(Options.OPT6, null));
            getControllerOptionRuntimeExceptionDao().create(new ControllerOption(Options.OPT7, null));
            getControllerOptionRuntimeExceptionDao().create(new ControllerOption(Options.OPT8, null));
            getControllerOptionRuntimeExceptionDao().create(new ControllerOption(Options.OPT9, null));
            getControllerOptionRuntimeExceptionDao().create(new ControllerOption(Options.OPT10, null));
            getControllerOptionRuntimeExceptionDao().create(new ControllerOption(Options.OPT11, null));
            getControllerOptionRuntimeExceptionDao().create(new ControllerOption(Options.OPT12, null));
            getControllerOptionRuntimeExceptionDao().create(new ControllerOption(Options.OPT13, null));

        }catch (SQLException ex){
            Log.e(TAG, "onCreate: " + "Create Table Error !!! " );
            Toast.makeText(ctx, "Create Table Error !!! ", Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {

    }

    public RuntimeExceptionDao<ControllerOption, Integer> getControllerOptionRuntimeExceptionDao() {
        if (controllerOptionRuntimeExceptionDao == null)
            controllerOptionRuntimeExceptionDao = getRuntimeExceptionDao(ControllerOption.class);
        return controllerOptionRuntimeExceptionDao;
    }

    @Override
    public void createAllDaosIfNotExsit() {
        getControllerOptionRuntimeExceptionDao().queryForAll();
    }

    @Override
    public List<ControllerOption> getAllControllerOptions() {
        return getControllerOptionRuntimeExceptionDao().queryForAll();
    }

    @Override
    public List<ControllerOption> checkIfEQ(String fieldName, Object value) {
        return getControllerOptionRuntimeExceptionDao().queryForEq(fieldName , value);
    }

    @Override
    public List<ControllerOption> checkValueBetween(String min, String max) {
        List<ControllerOption> list = null;
        try {

            QueryBuilder<ControllerOption, Integer> queryBuilder = getControllerOptionRuntimeExceptionDao().queryBuilder();
            Where where = queryBuilder.where();
            where.between("value", min,max);

            PreparedQuery<ControllerOption> preparedQuery = queryBuilder.prepare();
            list = getControllerOptionRuntimeExceptionDao().query(preparedQuery);



        }catch (Exception e){}

        return list;
    }

    @Override
    public void updateControllerOptions(List<ControllerOption> options) {
        for (ControllerOption co : options)
            getControllerOptionRuntimeExceptionDao().update(co);
    }
}

