package tinosnegocios.new_user_created.consume;

import java.util.Date;

public class UserCreated {
    private int Id;
    private String EmailAddress;
    private Date OcurredOn;
    private String Password;

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getEmailAddress() {
        return EmailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        EmailAddress = emailAddress;
    }

    public Date getOcurredOn() {
        return OcurredOn;
    }

    public void setOcurredOn(Date ocurredOn) {
        OcurredOn = ocurredOn;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }
}
