package com.stepup.ims.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class PerformanceTrendResponse {
    private List<String> labels;
    private List<Dataset> datasets;
    @Data
    @AllArgsConstructor
    public static class Dataset {
        private String label;
        private List<Integer> data;
    }
}

