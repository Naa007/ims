package com.stepup.ims.controller;

import com.stepup.ims.service.InspectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

import static com.stepup.ims.constants.UIRoutingConstants.RETURN_TO_CALENDAR;

@Controller
@RequestMapping("/calendar")
public class IMSCalendarController {

    private static final Logger logger = LoggerFactory.getLogger(IMSCalendarController.class);
    @Autowired
    private InspectionService inspectionService;

    @GetMapping("/inspection-stats")
    @ResponseBody
    public ResponseEntity<List<Map<String, String>>> getInspectionStats(@RequestParam("period") String period, Model model) {
        logger.info("Fetching inspection statistics for calendar view.");
        logger.debug("Inspection stats requested for period: {}", period);
        return ResponseEntity.ok(inspectionService.fetchInspectionStats(period));
    }

    @GetMapping("/view")
    public String viewCalendar() {
        logger.info("Accessing calendar view page.");
        return RETURN_TO_CALENDAR;
    }

}
