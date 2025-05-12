package com.stepup.ims.service;

import com.stepup.ims.model.Inspection;
import com.stepup.ims.model.Inspector;
import com.stepup.ims.repository.InspectionRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.stepup.ims.constants.ApplicationConstants.STATUS_CHANGE_EMAIL_SUBJECT;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private InspectorService inspectorService;

    @Autowired
    private InspectionService inspectionService;
    
    @Autowired
    private InspectionRepository inspectionRepository;

    @Autowired
    private EmployeeService employeeService;

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
        
        String subject = String.format(STATUS_CHANGE_EMAIL_SUBJECT, originalInspection.getInspectionNo());

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

    public void sendInspectionAdviseNotification(com.stepup.ims.entity.Inspection inspection) {
        if (inspection == null || inspection.getInspectionAdvise() == null || inspection.getInspectionAdvise().getId() == null) {
            return;
        }

        String[] to = {inspection.getCreatedBy()};
        String[] cc = inspectionService.getTechnicalCoordinatorsByInspectionId(inspection.getId());

        String subject = String.format(STATUS_CHANGE_EMAIL_SUBJECT, inspection.getInspectionNo());
        String body = String.format(
                "<html><body>" +
                        "<p><strong>Important Update:</strong> You are receiving this mail as you were involved in the inspection. Kindly review the details below at your earliest convenience,</p>" +
                        "<p>The Inspection Advise of the following inspection has been added or updated:</p>" +
                        "<ul>" +
                        "<li><strong>Notification Number:</strong> %s</li>" +
                        "<li><strong>Inspection Advise:</strong> %s</li>" +
                        "<li><strong>Reviewed By:</strong> %s</li>" +
                        "<li><strong>Inspection Status:</strong> %s</li>" +
                        "</ul>" +
                        "<p>Please log into the system for further details.</p>" +
                        "</body></html>",
                inspection.getNotificationNo(),
                inspection.getInspectionAdvise().getInstructionsFromTechnicalTeam(),
                employeeService.getEmployeeNameByEmail(SecurityContextHolder.getContext().getAuthentication().getName()),
                Inspection.InspectionStatusType.valueOf(String.valueOf(inspection.getInspectionStatus())).getDescription()
        );

        try {
            sendHtmlEmailWithCcBcc(to, cc, null, subject, body);
        } catch (MessagingException e) {
            // TODO: Add appropriate logging for MessagingException
        } catch (Exception e) {
            // TODO: Add appropriate logging for generic Exception
            System.err.println("Error occurred while sending inspection advise notification: " + e.getMessage());
        }
    }

    public void sendContractReviewNotification(com.stepup.ims.entity.Inspection inspection) {
        if (inspection == null || inspection.getContractReview() == null || inspection.getContractReview().getId() == null) {
            return;
        }

        String[] to = {inspection.getCreatedBy()};
        String[] cc = inspectionService.getTechnicalCoordinatorsByInspectionId(inspection.getId());

        String subject = String.format(STATUS_CHANGE_EMAIL_SUBJECT, inspection.getInspectionNo());
        String body = String.format(
                "<html><body>" +
                        "<p><strong>Important Update:</strong> You are receiving this mail as you were involved in the inspection. Kindly review the details below at your earliest convenience,</p>" +
                        "<p>The Contract Review of the following inspection has been added or updated:</p>" +
                        "<ul>" +
                        "<li><strong>Notification Number:</strong> %s</li>" +
                        "<li><strong>Contract Review Conclusion:</strong> %s</li>" +
                        "<li><strong>Reviewed By:</strong> %s</li>" +
                        "<li><strong>Inspection Status:</strong> %s</li>" +
                        "</ul>" +
                        "<p>Please log into the system for further details.</p>" +
                        "</body></html>",
                inspection.getNotificationNo(),
                inspection.getContractReview().getConclusion(),
                employeeService.getEmployeeNameByEmail(SecurityContextHolder.getContext().getAuthentication().getName()),
                Inspection.InspectionStatusType.valueOf(String.valueOf(inspection.getInspectionStatus())).getDescription()
        );

        try {
            sendHtmlEmailWithCcBcc(to, cc, null, subject, body);
        } catch (MessagingException e) {
            // TODO: Add appropriate logging for MessagingException
        } catch (Exception e) {
            // TODO: Add appropriate logging for generic Exception
            System.err.println("Error occurred while sending contract review notification: " + e.getMessage());
        }
    }

    public void sendPQRNotification(Inspector inspector, String action, Long inspectionId) {
        if (inspector == null || action == null || inspectionId == null) {
            return;
        }

        Optional<com.stepup.ims.entity.Inspection> inspection = inspectionRepository.findById(inspectionId);
        if (inspection.isEmpty()) {
            return;
        }
        String[] to = {inspection.get().getCreatedBy()};
        String[] cc = inspectionService.getTechnicalCoordinatorsByInspectionId(inspection.get().getId());

        String subject = String.format(STATUS_CHANGE_EMAIL_SUBJECT, inspection.get().getInspectionNo());
        String body = String.format(
                "<html><body>" +
                        "<p><strong>Important Update:</strong> You are receiving this mail as you were involved in the inspection. Kindly review the details below at your earliest convenience,</p>" +
                        "<p>The PQR of the Inspector assigned to the following inspection has been added or updated:</p>" +
                        "<ul>" +
                        "<li><strong>Notification Number:</strong> %s</li>" +
                        "<li><strong>Inspector Name:</strong> %s</li>" +
                        "<li><strong>Inspector Status:</strong> %s</li>" +
                        "<li><strong>Reviewed By:</strong> %s" +
                        "<li><strong>Inspection Status:</strong> %s</li>" +
                        "</ul>" +
                        "<p>Please log into the system for further details.</p>" +
                        "</body></html>",
                inspection.get().getNotificationNo(),
                inspector.getInspectorName(),
                action.equals("approve") ? "Approved" : "Rejected",
                employeeService.getEmployeeNameByEmail(SecurityContextHolder.getContext().getAuthentication().getName()),
                Inspection.InspectionStatusType.valueOf(String.valueOf(inspection.get().getInspectionStatus())).getDescription()
        );

        try {
            sendHtmlEmailWithCcBcc(to, cc, null, subject, body);
        } catch (MessagingException e) {
            // TODO: Add appropriate logging for MessagingException
        } catch (Exception e) {
            // TODO: Add appropriate logging for generic Exception
            System.err.println("Error occurred while sending Inspector PQR notification: " + e.getMessage());
        }
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
