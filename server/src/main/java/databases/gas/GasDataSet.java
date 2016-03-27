package databases.gas;

import java.util.Date;

public class GasDataSet {

    private Integer id;
    private String user;
    private Date time;
    private Float value;

    public GasDataSet(int id, String user, Date time, float value) {
        this.id = id;
        this.user = user;
        this.time = time;
        this.value = value;
    }

    public GasDataSet(){}

    public int getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
}
