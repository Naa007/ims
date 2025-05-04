package com.stepup.ims.service;

import com.stepup.ims.model.Inspection;
import com.stepup.ims.model.Inspector;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private InspectorService inspectorService;

    public void sendEmail(String to, String subject, String body) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body);

        mailSender.send(message);
    }

    public void sendNotificationEmail(Inspection inspection) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        String recipientEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        String subject = String.format("IISPL Inspection Notification #%d Published", inspection.getInspectionNo());
        String body = String.join("\n",
                "<html><body>",
                "<p>Dear Inspector,</p>",
                "<p>Please find details of the new inspection below:</p>",
                "<ul>",
                String.format("<li><strong>Notification Number:</strong> %s</li>", inspection.getNotificationNo()),
                String.format("<li><strong>Inspection Country:</strong> %s</li>", inspection.getInspectionCountry()),
                String.format("<li><strong>Inspection Date(s):</strong> %s</li>", inspection.getInspectionDateAsPerNotification()),
                String.format("<li><strong>Inspection Item:</strong> %s</li>", inspection.getInspectionItem()),
                String.format("<li><strong>Inspection Type:</strong> %s</li>", inspection.getInspectionType()),
                String.format("<li><strong>Inspection Stages:</strong> %s</li>", inspection.getInspectionActivityWithStages()),
                String.format("<li><strong>Inspection Location Details:</strong> %s</li>", inspection.getInspectionLocationDetails()),
                "</ul>",
                "<p>If you feel you are eligible for this inspection, please share your details to this email. </p>",
                "<p><strong>Note:</strong> Don't remove any CC list; just reply all.</p>",
                "</body></html>"
        );

        String[] bccList = getInspectorsEmailListByCountry(inspection.getInspectionCountry());
        helper.setBcc(bccList);
        helper.setCc(recipientEmail);
        helper.setSubject(subject);
        helper.setText(body, true);

        mailSender.send(message);
    }

    public String[] getInspectorsEmailListByCountry(String country) {
        return inspectorService.getInspectorsListByCountry(country)
                .stream()
                .map(Inspector::getEmail)
                .toArray(String[]::new);
    }
    
    
}
