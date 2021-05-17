package ro.uaic.info.MailApplication;

import com.sun.mail.pop3.POP3Store;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.*;

import javax.mail.*;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

@RestController
@RequestMapping("mail")
public class MailController {

    @Autowired
    private static JavaMailSenderImpl mailSender;

    private MailReceive mailbox = new MailReceive();

    private Session emailSession;

    POP3Store emailStore;

    @BeforeAll
    public static void setup() {
        mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);
        mailSender.setUsername("your_email");
        mailSender.setPassword("your_password");

        Properties properties = new Properties();
        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.smtp.starttls.enable", "true");

        mailSender.setJavaMailProperties(properties);
    }

    @GetMapping("/test")
    @ResponseBody
    public String testResponse() {
        return "Hello";
    }

    @PostMapping("/send_text_email")
    public String sendPlainTextEmail(@RequestBody Mail mail) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mail.getFrom());
        message.setTo(mail.getTo());
        message.setSubject(mail.getSubject());
        message.setText(mail.getContent());

        mailSender.send(message);

        return "result";
    }

    @PostMapping("/send_email_attachment")
    public ResponseEntity sendHTMLEmailWithAttachment(@RequestBody Mail mailAttachment) throws MessagingException {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setSubject(mailAttachment.getSubject());
        helper.setFrom(mailAttachment.getFrom());
        helper.setTo(mailAttachment.getTo());
        helper.setText(mailAttachment.getContent());

        // helper.setText("Attach file here: ", true);

        FileSystemResource file = new FileSystemResource(new File(mailAttachment.getAttachment()));
        helper.addAttachment(mailAttachment.getAttachmentName(), file);

        mailSender.send(message);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PostMapping("/configPOP")
    public ResponseEntity postController(
            @RequestBody MailPOPcfg user) throws MessagingException {

        mailbox.setConfig(user.host, Integer.toString(user.port), user.mailStoreType, user.username, user.password);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping("/getEmails")
    public ArrayList<Mail> getEmails(){
        return mailbox.getEmails();
    }

    @PostMapping("/configSMTP")
    public ResponseEntity postController(
            @RequestBody MailCfg user) {

        mailSender = new JavaMailSenderImpl();
        mailSender.setHost(user.host);
        mailSender.setPort(user.port);
        mailSender.setUsername(user.username);
        mailSender.setPassword(user.password);

        Properties properties = new Properties();
        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.smtp.starttls.enable", "true");

        mailSender.setJavaMailProperties(properties);

        return ResponseEntity.ok(HttpStatus.OK);
    }

}
