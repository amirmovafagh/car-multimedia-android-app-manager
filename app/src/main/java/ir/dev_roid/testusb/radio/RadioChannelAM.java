package ir.dev_roid.testusb.radio;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;

/**
 * A simple demonstration object we are creating and persisting to the database.
 */

public class RadioChannelAM {
    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(unique = true,dataType = DataType.STRING)
    private String frq;

    public RadioChannelAM(int id, String frq) {
        this.id = id;
        this.frq = frq;
    }

    public RadioChannelAM(String frq) {
        this.frq = frq;
    }

    public RadioChannelAM() {
        // needed by ormlite
    }

    public int getId() {
        return id;
    }

    /*public void setId(int id) {
        this.id = id;
    }*/

    public String getFrq() {
        return frq;
    }

    public void setFrq(String frq) {
        this.frq = frq;
    }
}
