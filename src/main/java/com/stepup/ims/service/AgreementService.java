package com.stepup.ims.service;

import org.apache.poi.xwpf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static com.stepup.ims.constants.FilePathConstants.*;

@Service
public class AgreementService {

    private static final Logger logger = LoggerFactory.getLogger(AgreementService.class);

    // Formatters
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Placeholder Keys
    private static final String PLACEHOLDER_DATE = "AGREEMENTDATE";
    private static final String PLACEHOLDER_NAME = "INSPNAME";
    private static final String PLACEHOLDER_ADDRESS = "INSPADDRESS";
    private static final String PLACEHOLDER_EMAIL = "INSPEMAIL";
    private static final String PLACEHOLDER_PHONE = "INSPCONTACT";
    private static final String PLACEHOLDER_POSITION = "INSPOSITION";
    private static final String PLACEHOLDER_APPROVER = "IISPL_NAME";

    // Default Values
    private static final String DEFAULT_POSITION = "Inspector/Sr.Inspector";
    private static final String DEFAULT_APPROVER = "G.S Rao";
    private static final String COUNTRY_INDIA = "India";
    private static final String COUNTRY_INTERNATIONAL = "International";

    // Content Types
    private static final String DOCX_MIME = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
    private static final String PDF_MIME = "application/pdf";
    private static final String EXCEL_MIME = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    public byte[] generateIndiaEmpaneledInspectorAgreement(String inspectorName, String address, String email, String phone) throws IOException {
        return generateAgreement(INDIA_TEMPLATE, inspectorName, address, email, phone, COUNTRY_INDIA);
    }

    public byte[] generateInternationalEmpaneledInspectorAgreement(String inspectorName, String address, String email, String phone) throws IOException {
        return generateAgreement(INTERNATIONAL_TEMPLATE, inspectorName, address, email, phone, COUNTRY_INTERNATIONAL);
    }

    private byte[] generateAgreement(String templateName, String inspectorName, String address, String email, String phone, String country) throws IOException {
        Resource templateResource = loadTemplate(templateName);

        try (InputStream inputStream = templateResource.getInputStream();
             XWPFDocument document = new XWPFDocument(inputStream)) {

            logger.info("Loaded template '{}' for inspector '{}'", templateName, inspectorName);

            Map<String, String> placeholders = preparePlaceholders(inspectorName, address, email, phone, country);
            logger.debug("Placeholders prepared: {}", placeholders);

            replacePlaceholdersInDocument(document, placeholders);
            return convertDocumentToByteArray(document);
        }
    }

    private Resource loadTemplate(String templateName) throws FileNotFoundException {
        String fullPath = TEMPLATE_DIR + templateName;
        Resource resource = new ClassPathResource(fullPath);
        if (!resource.exists()) {
            logger.error("Template not found: {}", fullPath);
            throw new FileNotFoundException("Template file not found: " + fullPath);
        }
        return resource;
    }

    private Map<String, String> preparePlaceholders(String inspectorName, String address, String email, String phone, String country) {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put(PLACEHOLDER_DATE, LocalDate.now().format(DATE_FORMATTER));
        placeholders.put(PLACEHOLDER_NAME, inspectorName);
        placeholders.put(PLACEHOLDER_ADDRESS, address);
        placeholders.put(PLACEHOLDER_EMAIL, email);
        placeholders.put(PLACEHOLDER_PHONE, phone);
        placeholders.put(PLACEHOLDER_POSITION, DEFAULT_POSITION);
        placeholders.put(PLACEHOLDER_APPROVER, DEFAULT_APPROVER);
        return placeholders;
    }

    private void replacePlaceholdersInDocument(XWPFDocument document, Map<String, String> placeholders) {
        document.getParagraphs().forEach(paragraph -> replacePlaceholdersInParagraph(paragraph, placeholders));

        document.getTables().forEach(table ->
                table.getRows().forEach(row ->
                        row.getTableCells().forEach(cell ->
                                cell.getParagraphs().forEach(paragraph ->
                                        replacePlaceholdersInParagraph(paragraph, placeholders)))));

        document.getHeaderList().forEach(header ->
                header.getParagraphs().forEach(paragraph -> replacePlaceholdersInParagraph(paragraph, placeholders)));

        document.getFooterList().forEach(footer ->
                footer.getParagraphs().forEach(paragraph -> replacePlaceholdersInParagraph(paragraph, placeholders)));
    }

    private void replacePlaceholdersInParagraph(XWPFParagraph paragraph, Map<String, String> placeholders) {
        StringBuilder fullTextBuilder = new StringBuilder();
        for (XWPFRun run : paragraph.getRuns()) {
            String text = run.getText(0);
            if (text != null) {
                fullTextBuilder.append(text);
            }
        }

        String fullText = fullTextBuilder.toString();
        if (fullText.isEmpty()) return;

        String updatedText = fullText;
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            updatedText = updatedText.replace(entry.getKey(), entry.getValue());
        }

        if (!fullText.equals(updatedText)) {
            clearParagraphRuns(paragraph);
            XWPFRun run = paragraph.createRun();
            run.setText(updatedText, 0);
        }
    }

    private void clearParagraphRuns(XWPFParagraph paragraph) {
        int runs = paragraph.getRuns().size();
        for (int i = runs - 1; i >= 0; i--) {
            paragraph.removeRun(i);
        }
    }

    private byte[] convertDocumentToByteArray(XWPFDocument document) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            document.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    public byte[] generateImpartialityDocument(String inspectorName) throws IOException {
        Resource templateResource = loadTemplate(IMPARTIALITY_TEMPLATE);

        try (InputStream inputStream = templateResource.getInputStream();
             XWPFDocument document = new XWPFDocument(inputStream)) {

            Map<String, String> placeholders = new HashMap<>();
            placeholders.put(PLACEHOLDER_NAME, inspectorName);
            placeholders.put(PLACEHOLDER_DATE, LocalDate.now().format(DATE_FORMATTER));
            placeholders.put(PLACEHOLDER_POSITION, DEFAULT_POSITION);

            logger.info("Generating impartiality agreement for '{}'", inspectorName);

            replacePlaceholdersInDocument(document, placeholders);
            return convertDocumentToByteArray(document);
        }
    }
}
