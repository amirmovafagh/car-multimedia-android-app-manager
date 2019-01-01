package ir.dev_roid.testusb.bluetoothFragments.contacts.Pojo;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

@DatabaseTable
public class CallLog {

    public static final String PHONE_FEILD_NAME = "phoneNumber_id";

    @DatabaseField(generatedId = true)
    private Integer idCallInfo;

    @DatabaseField( dataType = DataType.DATE_LONG)
    private Date date;

    @DatabaseField(foreign = true,
            foreignAutoRefresh = true, foreignAutoCreate = true,
            canBeNull = false,
            columnDefinition = "integer references phonenumber(idPhoneNumber)"
    )
    private PhoneNumber phoneNumber;

    @DatabaseField(foreign = true,
            foreignAutoCreate = true, foreignAutoRefresh = true,
            columnDefinition = "integer references calltype(idCallType)"
    )
    private CallType callType;

    public CallLog(){

    }

    public CallLog(Integer idCallInfo, Date date) {
        this.idCallInfo = idCallInfo;
        this.date = date;
    }

    public Integer getIdCallInfo() {
        return idCallInfo;
    }

    public void setIdCallInfo(Integer idCallInfo) {
        this.idCallInfo = idCallInfo;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public PhoneNumber getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(PhoneNumber phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public CallType getCallType() {
        return callType;
    }

    public void setCallType(CallType callType) {
        this.callType = callType;
    }


    @Override
    public String toString() {
        return "CallInfo{" +
                "idCallInfo=" + idCallInfo +
                ", date=" + date +
                ", phoneNumber=" + phoneNumber +
                ", callType=" + callType +
                '}';
    }
}
