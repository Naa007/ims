package com.stepup.ims.service;

import org.apache.poi.xwpf.usermodel.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class AgreementService {

    private static final String TEMPLATE_DIR = "inspector-agreement/";
    private static final String INDIA_TEMPLATE = "INDIA-Empaneled_Inspector_Agreement.docx";
    private static final String INTERNATIONAL_TEMPLATE = "INTERNATIONAL-Inspector_Agreement.docx";

    public byte[] generateIndiaEmpaneledInspectorAgreement(
            String inspectorName,
            String address,
            String email,
            String phone) throws IOException {

        System.out.println("Generating India agreement for: " + inspectorName);
        return generateAgreementDocument(INDIA_TEMPLATE, inspectorName, address, email, phone, "India");
    }

    private byte[] generateAgreementDocument(
            String templateName,
            String inspectorName,
            String address,
            String email,
            String phone,
            String country) throws IOException {

        String fullPath = TEMPLATE_DIR + templateName;
        Resource resource = new ClassPathResource(fullPath);

        System.out.println("Looking for template at: " + fullPath);
        if (!resource.exists()) {
            throw new FileNotFoundException("Template file not found: " + fullPath);
        }

        try (InputStream is = resource.getInputStream();
             XWPFDocument doc = new XWPFDocument(is)) {

            System.out.println("Template loaded successfully.");

            // Replace placeholders with values
            replacePlaceholder(doc, "{{DATE}}", LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            replacePlaceholder(doc, "{{NAME}}", inspectorName);
            replacePlaceholder(doc, "{{ADDRESS}}", address);
            replacePlaceholder(doc, "{{EMAIL}}", email);
            replacePlaceholder(doc, "{{CONTACT}}", phone);
            replacePlaceholder(doc, "{{POSITION}}", "India".equalsIgnoreCase(country) ? "Empaneled Inspector" : "International Inspector");
            replacePlaceholder(doc, "{{SIGN_NAME}}", inspectorName);

            // Write to byte array
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            doc.write(out);
            System.out.println("Document generation complete.");
            return out.toByteArray();
        }
    }

    private void replacePlaceholder(XWPFDocument doc, String placeholder, String replacement) {
        System.out.println("Replacing placeholder: " + placeholder + " -> " + replacement);

        // Paragraphs
        for (XWPFParagraph paragraph : doc.getParagraphs()) {
            replaceInParagraph(paragraph, placeholder, replacement);
        }

        // Tables
        for (XWPFTable table : doc.getTables()) {
            for (XWPFTableRow row : table.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    for (XWPFParagraph paragraph : cell.getParagraphs()) {
                        replaceInParagraph(paragraph, placeholder, replacement);
                    }
                }
            }
        }
    }

    private void replaceInParagraph(XWPFParagraph paragraph, String placeholder, String replacement) {
        String fullText = paragraph.getText();
        if (fullText.contains(placeholder)) {
            System.out.println("Found placeholder in paragraph: " + fullText);
            String updatedText = fullText.replace(placeholder, replacement);
            int runs = paragraph.getRuns().size();
            for (int i = runs - 1; i >= 0; i--) {
                paragraph.removeRun(i);
            }
            XWPFRun run = paragraph.createRun();
            run.setText(updatedText);
        }
    }
}
