package databases.users;


import java.util.Date;

public class UserDataSet {

    private int id;
    private String username;
    private String password;
    private String realname;
    private String adresses;
    private Date date; /* Пока создал как дата, дальше решим в каком виде она будет*/

    /*Конструктор пока базовый со всеми параметрами, позже обговорим нужно ли создание пустого обьекта и
     * с обьекта неполными параметрами */
    public UserDataSet(int id, String username, String password, String realname, String adresses, Date date) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.realname = realname;
        this.adresses = adresses;
        this.date = date;
    }

    /*создал только геттер для Id поля, так как Id я думаю меняться пока не будует
    * остальные переменные создал и геттеры и сеттеры*/

    public int getId() {
        return id;
    }

        public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getAdresses() {
        return adresses;
    }

    public void setAdresses(String adresses) {
        this.adresses = adresses;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
