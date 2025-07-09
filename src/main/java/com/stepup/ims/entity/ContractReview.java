package com.stepup.ims.entity;

import com.stepup.ims.audit.Auditable;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "contract_review")
public class ContractReview extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "scope_sector", nullable = true)
    private Integer scopeSector;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "contract_review_id", referencedColumnName = "id")
    private List<ContractDocument> contractDocumentList;

    @Column(name = "special_remarks")
    private String submissionList;

    @Lob
    @Column(name = "conclusion", columnDefinition = "TEXT")
    private String conclusion;

    @Column(name = "inspection_id", nullable = true)
    private Long inspectionId;

}
