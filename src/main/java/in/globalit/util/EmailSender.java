package in.globalit.util;

import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
public class EmailSender {

	private Logger logger = LoggerFactory.getLogger(EmailSender.class);

	private final JavaMailSender mailSender;

	private MimeMessageHelper helper;

	public EmailSender(JavaMailSender mailSender) throws MessagingException {
		this.mailSender = mailSender;
		helper = new MimeMessageHelper(mailSender.createMimeMessage(), true);
	}

	public void sendEmailWithAttachment(String to, String subject, String body, String attachmentUrl) {
		try {
			helper.setTo(to);
			helper.setSubject(subject);
			helper.setText(body, true);

			helper.setText(body + "<br><br>Click the link below to view the PDF:<br>" + attachmentUrl, true);
			mailSender.send(helper.getMimeMessage());
		} catch (MessagingException e) {
			logger.error("An error occurred while sending the email: {}", e.getMessage());
		}
	}

}
