package ro.uaic.info.MailApplication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.MimeMultipart;
import org.apache.commons.lang.StringEscapeUtils;

import com.sun.mail.pop3.POP3Store;

public class MailReceive {

    POP3Store emailStore;
    Properties properties = new Properties();

    public  ArrayList<Mail> receiveEmail(String pop3Host, String port, String storeType,
                                               String user, String password) {
        ArrayList<Message> messages = new ArrayList<>();
        ArrayList<Mail> messages_mail = new ArrayList<>();
        try {
            //1) get the session object
            Properties properties = new Properties();
            properties.put("mail.pop3.host", pop3Host);
            properties.put("mail.pop3.port", port);

            properties.setProperty("mail.store.protocol", "pop3s");
            properties.setProperty("mail.pop3s.host", pop3Host);
            properties.setProperty("mail.pop3s.port", port);
            properties.setProperty("mail.pop3s.auth", "true");
            properties.setProperty("mail.pop3s.socketFactory.class",
                    "javax.net.ssl.SSLSocketFactory");
            properties.setProperty("mail.pop3s.ssl.trust", "*");

            Session emailSession = Session.getDefaultInstance(properties);
            //store = session.getStore( "pop3s" );

            //2) create the POP3 store object and connect with the pop server
//            POP3Store emailStore = (POP3Store) emailSession.getStore(storeType);
            POP3Store emailStore = (POP3Store) emailSession.getStore("pop3s");

            emailStore.connect(user, password);

            //3) create the folder object and open it
            Folder emailFolder = emailStore.getFolder("INBOX");
            emailFolder.open(Folder.READ_ONLY);

            //4) retrieve the messages from the folder in an array and print it
            messages = new ArrayList<>(Arrays.asList(emailFolder.getMessages()));


            for (int i = 0; i < 10; i++) {
                Message message = messages.get(i);
                //messages_mail.get(i).setFrom(message.getFrom().toString());
                //messages_mail.get(i).setSubject(message.getSubject());
                //messages_mail.get(i).setContent(message.getContent().toString());

               // messages_mail.add(new Mail(message.getFrom()[0].toString(), message.getAllRecipients()[0].toString(), message.getSubject(), getTextFromMessage(message)));
            }

            //5) close the store and folder objects


            emailFolder.close(false);
            emailStore.close();

        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return messages_mail;
    }

    public void setConfig(String pop3Host, String port, String storeType,
                          String user, String password) throws NoSuchProviderException {
        //mailbox.receiveEmail(user.host, Integer.toString(user.port), user.mailStoreType, user.username, user.password).get(7);


        properties.put("mail.pop3.host", pop3Host);
        properties.put("mail.pop3.port", port);

        properties.setProperty("mail.store.protocol", "pop3s");
        properties.setProperty("mail.pop3s.host", pop3Host);
        properties.setProperty("mail.pop3s.port", port);
        properties.setProperty("mail.pop3s.auth", "true");
        properties.setProperty("mail.pop3s.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        properties.setProperty("mail.pop3s.ssl.trust", "*");

        properties.setProperty("username", "recent:" + user);
        properties.setProperty("password", password);

        Session emailSession = Session.getInstance(properties);

        this.emailStore = (POP3Store) emailSession.getStore("pop3s");
    }

    public ArrayList<Mail> getEmails() {
        ArrayList<Message> messages;
        ArrayList<Mail> messages_mail = new ArrayList<>();
        try {

            emailStore.connect(properties.getProperty("username"), properties.getProperty("password"));
            //3) create the folder object and open it
            Folder emailFolder = null;

            emailFolder = emailStore.getDefaultFolder().getFolder("INBOX");
            emailFolder.open(Folder.READ_ONLY);

            //4) retrieve the messages from the folder in an array and print it
            messages = new ArrayList<>(Arrays.asList(emailFolder.getMessages()));


            for (int i = messages.size()-1; i > 20 /*messages.size()*/; i--) {
                Message message = messages.get(i);
                //messages_mail.get(i).setFrom(message.getFrom().toString());
                //messages_mail.get(i).setSubject(message.getSubject());
                //messages_mail.get(i).setContent(message.getContent().toString());

                String mailContent = getText(message);

                //System.out.println(mailContent);
                messages_mail.add(new Mail(message.getFrom()[0].toString(), message.getAllRecipients()[0].toString(), message.getSubject(), mailContent)/*message.getContent().toString())getTextFromMessage(message))*/);
            }


            emailFolder.close(false);
            emailStore.close();
        } catch (MessagingException | IOException e) {
            e.printStackTrace();
        }

        return messages_mail;
    }


    private boolean textIsHtml = false;

    /**
     * Return the primary text content of the message.
     */
    private String getText(Part p) throws
            MessagingException, IOException {
        if (p.isMimeType("text/*")) {
            String s = (String)p.getContent();
            textIsHtml = p.isMimeType("text/html");
            return s;
        }

        if (p.isMimeType("multipart/alternative")) {
            // prefer html text over plain text
            Multipart mp = (Multipart)p.getContent();
            String text = null;
            for (int i = 0; i < mp.getCount(); i++) {
                Part bp = mp.getBodyPart(i);
                if (bp.isMimeType("text/plain")) {
                    if (text == null)
                        text = getText(bp);
                    continue;
                } else if (bp.isMimeType("text/html")) {
                    String s = getText(bp);
                    if (s != null)
                        return s;
                } else {
                    return getText(bp);
                }
            }
            return text;
        } else if (p.isMimeType("multipart/*")) {
            Multipart mp = (Multipart)p.getContent();
            for (int i = 0; i < mp.getCount(); i++) {
                String s = getText(mp.getBodyPart(i));
                if (s != null)
                    return s;
            }
        }

        return null;
    }

}