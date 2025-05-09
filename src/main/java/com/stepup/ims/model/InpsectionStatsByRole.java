package com.stepup.ims.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InpsectionStatsByRole {
    private final Long totalInspections;
    private final Long newInspections;
    private final Long completedInspections;
    private final Long ongoingInspections;
    private final Long rejectedInspections;
    private final PeriodType period;

    public enum PeriodType {
        WEEK,
        MONTH,
        YEAR
    }
}
