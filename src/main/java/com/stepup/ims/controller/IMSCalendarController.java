package com.stepup.ims.controller;

import com.stepup.ims.service.InspectionService;
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

    @Autowired
    private InspectionService inspectionService;

    @GetMapping("/inspection-stats")
    @ResponseBody
    public ResponseEntity<List<Map<String, String>>> getInspectionStats(@RequestParam("period") String period, Model model) {
        return ResponseEntity.ok(inspectionService.fetchInspectionStats(period));
    }

    @GetMapping("/view")
    public String viewCalendar() {
        return RETURN_TO_CALENDAR;
    }

}
