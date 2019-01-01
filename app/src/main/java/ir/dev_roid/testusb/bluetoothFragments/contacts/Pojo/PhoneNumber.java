package ir.dev_roid.testusb.bluetoothFragments.contacts.Pojo;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

@DatabaseTable
public class PhoneNumber implements Serializable, Cloneable {

    public static final String PHONE_FEILD_NAME = "phone";
    public static final String PHONE_FEILD_AUDIENCE_ID = "audience_id";

    @DatabaseField(generatedId = true)
    private Integer idPhoneNumber;

    @DatabaseField(unique = true, canBeNull = false)
    private String phone;

    @DatabaseField(foreign = true,
            foreignAutoCreate = true, foreignAutoRefresh = true,
            columnDefinition = "integer references Audience(idAudience)"
    )
    private Audience audience;

    @ForeignCollectionField
    private ForeignCollection<CallLog> callLogs;


    public PhoneNumber(){

    }

    public PhoneNumber(String phone) {
        this.phone = phone;
    }

    public Integer getIdPhoneNumber() {
        return idPhoneNumber;
    }

    public void setIdPhoneNumber(Integer idPhoneNumber) {
        this.idPhoneNumber = idPhoneNumber;
    }


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Audience getAudience() {
        return audience;
    }

    public void setAudience(Audience audience) {
        this.audience = audience;
    }

    public ForeignCollection<CallLog> getCallLogs() {
        return callLogs;
    }

    public void setCallLogs(ForeignCollection<CallLog> callLogs) {
        this.callLogs = callLogs;
    }



    @Override
    public String toString() {
        return "PhoneNumber{" +
                "idPhoneNumber=" + idPhoneNumber +
                ", phone='" + phone + '\'' +
                ", audience=" + audience +
                ", callInfos=" + callLogs +
                '}';
    }

    public PhoneNumber clone() throws CloneNotSupportedException {
        return (PhoneNumber) super.clone();
    }
}
