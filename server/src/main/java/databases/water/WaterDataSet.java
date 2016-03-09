package databases.water;

import java.util.Date;

public class WaterDataSet {

    private int id;
    private String user;
    private Date time;
    private float value;

    public WaterDataSet(int id, String user, Date time, float value) {
        this.id = id;
        this.user = user;
        this.time = time;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }
}
