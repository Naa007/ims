package com.stepup.ims.entity;

import com.stepup.ims.audit.Auditable;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "acknowledge_from_inspector")
public class AcknowledgeFromInspector extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "acknowledgement")
    @Enumerated(EnumType.STRING)
    private AcknowledgementTypes acknowledgement;

    @Column(nullable = false)
    private DocumentStatusTypes status;

    public enum AcknowledgementTypes {
        DOCS_REC, VEND_CONF, REV_REQ, CONF_REQ, SAF_INS;

        AcknowledgementTypes(){

        }
    }

    public enum DocumentStatusTypes {
        YES, NO, NA
    }
}
