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

    @Autowired
    private InspectionService inspectionService;

    public void sendNotificationEmail(Inspection originalInspection, Inspection updatedInspection) {
        try {
            if (originalInspection != null && originalInspection.getId() == null && Inspection.InspectionStatusType.NEW.equals(originalInspection.getInspectionStatus())) {
                sendNewNotificationEmail(originalInspection);
            } else if (originalInspection != null && !originalInspection.getInspectionStatus().equals(updatedInspection.getInspectionStatus())) {
                sendStatusChangeNotificationEmail(originalInspection, updatedInspection);
            }
        } catch (MessagingException e) {
            // TODO: Add appropriate logging for MessagingException
        } catch (Exception e) {
            // TODO: Add appropriate logging for generic Exception
            System.err.println("error" + e.getMessage());
        }

    }

    public void sendNewNotificationEmail(Inspection inspection) throws MessagingException {
        
        String[] to = new String[]{SecurityContextHolder.getContext().getAuthentication().getName()};
        String[] bcc = getInspectorsEmailListByCountry(inspection.getInspectionCountry());

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
                "<p><strong>Note:</strong> Don't remove any CC list; just reply all. (BCC kept intentionally)</p>",
                "</body></html>"
        );

        sendHtmlEmailWithCcBcc(to, null, bcc, subject, body);
    }

    private void sendStatusChangeNotificationEmail(Inspection originalInspection, Inspection updatedInspection) throws MessagingException {
        String[] to = new String[]{SecurityContextHolder.getContext().getAuthentication().getName()};
        String[] cc = inspectionService.getTechnicalCoordinatorsByInspectionId(originalInspection.getId());
        
        String subject = String.format("IISPL Inspection #%d Status Update", originalInspection.getInspectionNo());

        String body = String.join("\n",
                "<html><body>",
                "<p><strong>Important Update:</strong> You are receiving this mail as you were involved in the inspection. Kindly review the details below at your earliest convenience,</p>",
                "<p>The status of the following inspection has changed:</p>",
                "<ul>",
                String.format("<li><strong>Notification Number:</strong> %s</li>", originalInspection.getNotificationNo()),
                String.format("<li><strong>Previous Status:</strong> %s</li>", originalInspection.getInspectionStatus().getDescription()),
                String.format("<li><strong>New Status:</strong> %s</li>", updatedInspection.getInspectionStatus().getDescription()),
                "</ul>",
                "<p>Please log into the system for further details.</p>",
                "</body></html>"
        );

        sendHtmlEmailWithCcBcc(to, cc, null, subject, body);
    }

    public void sendHtmlEmailWithCcBcc(String[] to, String[] cc, String[] bcc, String subject, String body) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        if (to != null) {
            helper.setTo(to);
        }
        if (cc != null) {
            helper.setCc(cc);
        }
        if (bcc != null) {
            helper.setBcc(bcc);
        }
        helper.setSubject(subject);
        helper.setText(body, true);

        mailSender.send(message);
    }

    public void sendEmail(String to, String subject, String body) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body);

        mailSender.send(message);
    }

    public String[] getInspectorsEmailListByCountry(String country) {
        return inspectorService.getInspectorsListByCountry(country)
                .stream()
                .map(Inspector::getEmail)
                .toArray(String[]::new);
    }

}
