package rest;

import org.springframework.security.crypto.codec.Base64;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;

/**
 * Created by alnedorezov on 4/5/16.
 */
public class Mail {
    public void send(String recipientEmail, String messageSubject, String messageText) throws UnsupportedEncodingException {
        final String senderEmail = "innomaps@alnedorezov.com";
        byte[] data = "S290eWF0YTEyMyE=".getBytes("UTF-8");
        final String password = new String(Base64.decode(data));

        try {
            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.mail.ru"); // for gmail use smtp.gmail.com
            props.put("mail.smtp.auth", "true");
            props.put("mail.debug", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.port", "465");
            props.put("mail.smtp.socketFactory.port", "465");
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.socketFactory.fallback", "false");

            Session mailSession = Session.getInstance(props, new javax.mail.Authenticator() {

                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(senderEmail, password);
                }
            });

            mailSession.setDebug(true); // Enable the debug mode

            Message msg = new MimeMessage(mailSession);

            //--[ Set the FROM, TO, DATE and SUBJECT fields
            msg.setFrom(new InternetAddress(senderEmail));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            msg.setSentDate(new Date());
            msg.setSubject(messageSubject);

            //--[ Create the body of the mail
            msg.setText(messageText);

            //--[ Ask the Transport class to send our mail message
            Transport.send(msg);

        } catch (Exception E) {
            System.out.println("Oops something has gone pearshaped!");
            System.out.println(E);
        }
    }
}
