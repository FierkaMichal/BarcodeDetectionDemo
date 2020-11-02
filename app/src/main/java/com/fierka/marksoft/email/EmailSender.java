package com.fierka.marksoft.email;

import android.content.Context;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class EmailSender {

    private final String host;
    private final int port;
    private final String username;
    private final String password;

    private final AttachmentService attachmentService;

    public EmailSender(String host, int port, String username, String password, Context context) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.attachmentService = new AttachmentService(context);
    }

    public void sendMail(String subject, String msg, String receiver, String attachmentFileName) {
        Properties prop = new Properties();
        prop.put("mail.smtp.auth", true);
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.smtp.host", host);
        prop.put("mail.smtp.port", port);
        prop.put("mail.smtp.ssl.trust", host);

        Session session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(receiver));
            message.setSubject(subject);

            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setText(msg, StandardCharsets.UTF_8.name());

            MimeBodyPart attachment = new MimeBodyPart();
            File file = attachmentService.createAttachment(attachmentFileName);
            attachment.attachFile(file);

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(mimeBodyPart);
            multipart.addBodyPart(attachment);

            message.setContent(multipart);

            System.out.println("Sending mail...");
            Transport.send(message);
            System.out.println("Mail send");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }}
