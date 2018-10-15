package ir.dev_roid.testusb.bluetoothFragments.contacts.Pojo;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Collection;

@DatabaseTable
public class Audience {

    public static final String ID_FEILD_NAME = "idAudience";
    public static final String FIRSTNAME_FEILD_NAME = "firstname";
    public static final String LASTNAME_FEILD_NAME = "lastname";
    public static final String EMAIL_FEILD_NAME = "email";

    @DatabaseField(generatedId = true)
    private Integer idAudience;

    @DatabaseField(index = true, uniqueCombo = true, canBeNull = false)
    private String firstname;

    @DatabaseField(index = true, uniqueCombo = true)
    private String lastname;

    @DatabaseField(unique = true)
    private String email;

    @ForeignCollectionField(eager = true, maxEagerLevel = 1)
    private Collection<PhoneNumber> phoneNumbers;

    public Audience(){

    }


    public Audience(Integer idAudience, String firstname, String lastname, String email) {
        this.idAudience = idAudience;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
    }

    public Integer getIdAudience() {
        return idAudience;
    }

    public void setIdAudience(Integer idAudience) {
        this.idAudience = idAudience;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Collection<PhoneNumber> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(Collection<PhoneNumber> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    @Override
    public String toString() {
        return "Audience{" +
                "idAudience=" + idAudience +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumbers=" + phoneNumbers +
                '}';
    }
}
