package sg.com.singhealth.wayfinder;

/**
 * Created by Bowen on 2017/9/27.
 */

public class User {
    String name;
    String email;
    String age;
    String password;
    String confirm;





    public User( String name, String email, String age, String password, String confirm) {
        this.name = name;
        this.email = email;
        this.age = age;
        this.password = password;
        this.confirm = confirm;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirm() {
        return confirm;
    }

    public void setConfirm(String confirm) {
        this.confirm = confirm;
    }



}
