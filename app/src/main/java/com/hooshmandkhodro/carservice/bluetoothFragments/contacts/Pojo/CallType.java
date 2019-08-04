package com.hooshmandkhodro.carservice.bluetoothFragments.contacts.Pojo;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class CallType {

    public static final String INPUT = "input";
    public static final String OUTPUT = "output";
    public static final String MISSED = "missed";

    @DatabaseField(id = true)
    private Integer idCallType;

    @DatabaseField(unique = true)
    private String type;

    @ForeignCollectionField
    private ForeignCollection<CallLog> callLogs;

    public CallType(){

    }

    public CallType(String type) {
        this.type = type;
    }

    public Integer getIdCallType() {
        return idCallType;
    }

    public void setIdCallType(Integer idCallType) {
        this.idCallType = idCallType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ForeignCollection<CallLog> getCallLogs() {
        return callLogs;
    }

    public void setCallLogs(ForeignCollection<CallLog> callLogs) {
        this.callLogs = callLogs;
    }

    @Override
    public String toString() {
        return "CallType{" +
                "idCallType=" + idCallType +
                ", type=" + type +
                ", callInfos=" + callLogs +
                '}';
    }

    public static CallType getInputInstance() {
        CallType input = new CallType();
        input.setIdCallType(1);
        input.setType(INPUT);
        return input;
    }
    public static CallType getOutputInstance() {
        CallType input = new CallType();
        input.setIdCallType(2);
        input.setType(OUTPUT);
        return input;
    }
    public static CallType getMissedInstance() {
        CallType input = new CallType();
        input.setIdCallType(3);
        input.setType(MISSED);
        return input;
    }

}
