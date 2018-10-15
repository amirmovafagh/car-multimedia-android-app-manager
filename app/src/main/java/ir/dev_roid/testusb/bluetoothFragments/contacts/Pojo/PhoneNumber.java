package ir.dev_roid.testusb.bluetoothFragments.contacts.Pojo;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

@DatabaseTable
public class PhoneNumber {

    public static final String PHONE_FEILD_NAME = "phone";

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
    private ForeignCollection<CallInfo> callInfos;


    public PhoneNumber(){

    }

    public PhoneNumber(Integer idPhoneNumber, String phone) {
        this.idPhoneNumber = idPhoneNumber;
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

    public ForeignCollection<CallInfo> getCallInfos() {
        return callInfos;
    }

    public void setCallInfos(ForeignCollection<CallInfo> callInfos) {
        this.callInfos = callInfos;
    }



    @Override
    public String toString() {
        return "PhoneNumber{" +
                "idPhoneNumber=" + idPhoneNumber +
                ", phone='" + phone + '\'' +
                ", audience=" + audience +
                ", callInfos=" + callInfos +
                '}';
    }
}
