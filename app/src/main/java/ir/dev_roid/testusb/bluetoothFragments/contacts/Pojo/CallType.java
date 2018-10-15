package ir.dev_roid.testusb.bluetoothFragments.contacts.Pojo;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class CallType {

    public enum Type{

        INPUT,OUTPUT,MISSING;
    }

    @DatabaseField(id = true)
    private Integer idCallType;

    @DatabaseField
    private Type type;

    @ForeignCollectionField
    private ForeignCollection<CallInfo> callInfos;

    public CallType(){

    }

    public CallType(Integer idCallType, Type type) {
        this.idCallType = idCallType;
        this.type = type;
    }

    public Integer getIdCallType() {
        return idCallType;
    }

    public void setIdCallType(Integer idCallType) {
        this.idCallType = idCallType;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public ForeignCollection<CallInfo> getCallInfos() {
        return callInfos;
    }

    public void setCallInfos(ForeignCollection<CallInfo> callInfos) {
        this.callInfos = callInfos;
    }

    @Override
    public String toString() {
        return "CallType{" +
                "idCallType=" + idCallType +
                ", type=" + type +
                ", callInfos=" + callInfos +
                '}';
    }
}
