package ro.uaic.info.MailApplication;

import java.time.LocalDate;

public class Mail {

//    private MailAddress from;
//    private MailAddress to;
    private String from;
    private String to;
    private String subject;
    private String content;
    private String attachment;
    private LocalDate date;

    private boolean isRead;
    private MailType type;

    public Mail(String from,
                String to,
                String subject,
                String content) {
        this.from = from;
        this.to = to;
        this.subject = subject;
        this.content = content;
        this.date = LocalDate.now();
        this.type = MailType.INBOX;
        isRead = false;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDate getDate(){
        return this.date;
    }

    public boolean isRead() { return isRead; }

    public String getAttachment() {
        return attachment;
    }
    public String getAttachmentName(){
        String[] strings= this.attachment.split("\\\\");
        return strings[strings.length-1];
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }
}