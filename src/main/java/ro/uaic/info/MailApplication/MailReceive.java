package ro.uaic.info.MailApplication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import com.sun.mail.pop3.POP3Store;

public class MailReceive{

    public static ArrayList<Mail> receiveEmail(String pop3Host, String port , String storeType,
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
                    "javax.net.ssl.SSLSocketFactory" );
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

                messages_mail.add(new Mail(message.getFrom().toString(), message.getReplyTo().toString(),message.getSubject(),message.getContent().toString()));
            }



            /*
            for (int i = 0; i < messages.length; i++) {
                Message message = messages[i];
                System.out.println("---------------------------------");
                System.out.println("Email Number " + (i + 1));
                System.out.println("Subject: " + message.getSubject());
                System.out.println("From: " + message.getFrom()[0]);
                System.out.println("Text: " + message.getContent().toString());
            }*/

            //5) close the store and folder objects


            emailFolder.close(false);
            emailStore.close();

        } catch (NoSuchProviderException e) {e.printStackTrace();}
        catch (MessagingException e) {e.printStackTrace();} catch (IOException e) {
            e.printStackTrace();
        }
        return messages_mail;
    }

}