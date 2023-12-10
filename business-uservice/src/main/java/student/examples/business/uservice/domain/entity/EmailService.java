package student.examples.business.uservice.domain.entity;

import java.util.Properties;

import org.springframework.stereotype.Component;

import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.*;

@Component
public class EmailService {
	public void sendEmail(User userByEmail) {
		Properties prop = new Properties();
		prop.put("mail.smtp.auth", true);
		prop.put("mail.smtp.starttls.enable", "true");
		prop.put("mail.smtp.host", "smtp.gmail.com");
		prop.put("mail.smtp.port", "587");
		prop.put("mail.smtp.tls.trust", "smtp.gmail.com");

		String username = "valentin.ignatov.97@gmail.com";
		String password = "mlbr mnfa ybei zqew";

		Session session = Session.getInstance(prop, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		Message message = new MimeMessage(session);
		try {
			message.setFrom(new InternetAddress("valentin.ignatov.97@gmail.com"));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(userByEmail.getEmail()));

			message.setSubject("Mail Subject");

			String activationMessage = "<a href=\"http://localhost:8089/user/activation?token=" + userByEmail.getToken() + "\">Click here to activate</a>";

			MimeBodyPart mimeBodyPart = new MimeBodyPart();
//			mimeBodyPart.setContent(activationMessage, "text/html; charset=utf-8");
			mimeBodyPart.setContent(activationMessage, "text/html;");

			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(mimeBodyPart);

			message.setContent(multipart);

			Transport.send(message);

			System.out.println("Email sent");

		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public void sendDeleteEmail(User userByEmail) {
		Properties prop = new Properties();
		prop.put("mail.smtp.auth", true);
		prop.put("mail.smtp.starttls.enable", "true");
		prop.put("mail.smtp.host", "smtp.gmail.com");
		prop.put("mail.smtp.port", "587");
		prop.put("mail.smtp.tls.trust", "smtp.gmail.com");

		String username = "valentin.ignatov.97@gmail.com";
		String password = "mlbr mnfa ybei zqew";

		Session session = Session.getInstance(prop, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		Message message = new MimeMessage(session);
		try {
			message.setFrom(new InternetAddress("valentin.ignatov.97@gmail.com"));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(userByEmail.getEmail()));

			message.setSubject("Mail Subject");

			String activationMessage = "<a href=\"http://localhost:8089/user/delete?token=" + userByEmail.getToken() + "\">Click here to completly delete user</a>";

			MimeBodyPart mimeBodyPart = new MimeBodyPart();
//			mimeBodyPart.setContent(activationMessage, "text/html; charset=utf-8");
			mimeBodyPart.setContent(activationMessage, "text/html;");

			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(mimeBodyPart);

			message.setContent(multipart);

			Transport.send(message);

			System.out.println("Email sent");

		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
