package ro.uaic.info.MailApplication;

import java.util.ArrayList;
import java.util.List;

public class Mailbox {

    private String name;
    private List<Mail> emails = new ArrayList<>();

    public Mailbox(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addEmail(Mail mail) {
        emails.add(mail);
    }

    public List<Mail> giveMeMails() {
        return emails;
    }

}
