package databases.electricity;

import java.util.Date;

public class ElectricityDataSet {

    private Integer id;
    private String user;
    private Date time;
    private Float value;
    private Float value_p;

    public ElectricityDataSet(int id, String user, Date time, float value, float value_p) {
        this.id = id;
        this.user = user;
        this.time = time;
        this.value = value;
        this.value_p = value_p;
    }

    public ElectricityDataSet(){}

    public void setId(Integer id) {
        this.id = id;
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

    public void setValue(Float value) {
        this.value = value;
    }

    public float getValue_p() {
        return value_p;
    }

    public void setValue_p(Float value_p) {
        this.value_p = value_p;
    }
}
