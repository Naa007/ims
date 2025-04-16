package com.stepup.ims.model;

import lombok.Data;

import java.util.List;

@Data
public class ContractReview {
    private Long id;
    private List<ContractDocument> contractDocumentList;
    private String submissionList;
    private String conclusion;
    private Long inspectionId;
}
