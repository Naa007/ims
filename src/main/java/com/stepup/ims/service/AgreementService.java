package com.stepup.ims.service;

import org.apache.poi.xwpf.usermodel.*;
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

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");


    /**
     * Generate India-specific agreement for an inspector.
     *
     * @param inspectorName the name of the inspector
     * @param address       the address of the inspector
     * @param email         the email of the inspector
     * @param phone         the phone number of the inspector
     * @return Byte array of the generated agreement
     * @throws IOException if the template file cannot be loaded or errors occur during processing
     */
    public byte[] generateIndiaEmpaneledInspectorAgreement(String inspectorName, String address, String email, String phone) throws IOException {
        return generateAgreement(INDIA_TEMPLATE, inspectorName, address, email, phone, "India");
    }

    public byte[] generateInternationalEmpaneledInspectorAgreement(String inspectorName, String address, String email, String phone) throws IOException {
        return generateAgreement(INTERNATIONAL_TEMPLATE, inspectorName, address, email, phone, "India");
    }

    /**
     * Generate an agreement based on the provided template and details.
     *
     * @param templateName  Name of the template file
     * @param inspectorName Name of the inspector
     * @param address       Address of the inspector
     * @param email         Email address of the inspector
     * @param phone         Contact phone number of the inspector
     * @param country       Country where the inspector is located
     * @return Byte array containing the generated agreement document
     * @throws IOException If the template file is missing or cannot be read
     */
    private byte[] generateAgreement(String templateName, String inspectorName, String address, String email, String phone, String country) throws IOException {
        Resource templateResource = loadTemplate(templateName);

        try (InputStream inputStream = templateResource.getInputStream();
             XWPFDocument document = new XWPFDocument(inputStream)) {

            System.out.println("Template loaded successfully. Replacing placeholders...");

            // Prepare placeholder values
            Map<String, String> placeholders = preparePlaceholders(inspectorName, address, email, phone, country);

            // Replace placeholders throughout the document
            replacePlaceholdersInDocument(document, placeholders);

            // Convert the document to a byte array
            return convertDocumentToByteArray(document);
        }
    }

    /**
     * Load the template document as a resource.
     *
     * @param templateName Name of the template file
     * @return Resource pointing to the template
     * @throws FileNotFoundException If the file is not found
     */
    private Resource loadTemplate(String templateName) throws FileNotFoundException {
        String fullPath = TEMPLATE_DIR + templateName;
        Resource resource = new ClassPathResource(fullPath);
        if (!resource.exists()) {
            throw new FileNotFoundException("Template file not found: " + fullPath);
        }
        return resource;
    }

    /**
     * Prepare placeholders to be replaced in the document.
     *
     * @param inspectorName Name of the inspector
     * @param address       Address of the inspector
     * @param email         Email of the inspector
     * @param phone         Contact information of the inspector
     * @param country       Country where the inspector works
     * @return Map containing placeholder keys and the corresponding replacements
     */
    private Map<String, String> preparePlaceholders(String inspectorName, String address, String email, String phone, String country) {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("AGREEMENTDATE", LocalDate.now().format(DATE_FORMATTER));
        placeholders.put("INSPNAME", inspectorName);
        placeholders.put("INSPADDRESS", address);
        placeholders.put("INSPEMAIL", email);
        placeholders.put("INSPCONTACT", phone);
        placeholders.put("INSPOSITION", "Inspector/Sr.Inspector");
        placeholders.put("IISPL_NAME", "G.S Rao");
        return placeholders;
    }

    /**
     * Replace all placeholders in the given Word document.
     *
     * @param document     Document in which placeholders will be replaced
     * @param placeholders Map of placeholders and corresponding replacements
     */
    private void replacePlaceholdersInDocument(XWPFDocument document, Map<String, String> placeholders) {
        // Replace in paragraphs
        for (XWPFParagraph paragraph : document.getParagraphs()) {
            replacePlaceholdersInParagraph(paragraph, placeholders);
        }

        // Replace in tables
        for (XWPFTable table : document.getTables()) {
            for (XWPFTableRow row : table.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    for (XWPFParagraph paragraph : cell.getParagraphs()) {
                        replacePlaceholdersInParagraph(paragraph, placeholders);
                    }
                }
            }
        }

        // Replace in headers and footers
        for (XWPFHeader header : document.getHeaderList()) {
            for (XWPFParagraph paragraph : header.getParagraphs()) {
                replacePlaceholdersInParagraph(paragraph, placeholders);
            }
        }
        for (XWPFFooter footer : document.getFooterList()) {
            for (XWPFParagraph paragraph : footer.getParagraphs()) {
                replacePlaceholdersInParagraph(paragraph, placeholders);
            }
        }
    }

    /**
     * Replace all placeholders in a paragraph.
     *
     * @param paragraph    Paragraph to process
     * @param placeholders Map of placeholders and their replacements
     */
    private void replacePlaceholdersInParagraph(XWPFParagraph paragraph, Map<String, String> placeholders) {
        // Combine all runs' text into one string
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


    /**
     * Clear all runs in a paragraph for text replacement.
     *
     * @param paragraph Paragraph to clear
     */
    private void clearParagraphRuns(XWPFParagraph paragraph) {
        int runs = paragraph.getRuns().size();
        for (int i = runs - 1; i >= 0; i--) {
            paragraph.removeRun(i);
        }
    }

    /**
     * Convert the Word document to a byte array.
     *
     * @param document Document to be converted
     * @return Byte array representation of the document
     * @throws IOException If an error occurs during writing
     */
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
            placeholders.put("INSPNAME", inspectorName);
            placeholders.put("AGREEMENTDATE", LocalDate.now().format(DATE_FORMATTER));
            replacePlaceholdersInDocument(document, placeholders);
            return convertDocumentToByteArray(document);
        }
    }

}