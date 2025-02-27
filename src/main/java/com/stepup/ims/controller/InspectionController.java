package com.stepup.ims.controller;

import com.stepup.ims.model.Inspection;
import com.stepup.ims.modelmapper.InspectionModelMapper;
import com.stepup.ims.service.InspectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/inspections")
public class InspectionController {

    @Autowired
    private InspectionService inspectionService;

    @Autowired
    private InspectionModelMapper inspectionModelMapper;

    @GetMapping("/form")
    public String showInspectorForm(Model model) {
        model.addAttribute("inspection", new Inspection());
        return "inspection-management";
    }

    @PostMapping
    public Inspection createInspection(@RequestBody Inspection inspection) {
        return inspectionModelMapper.toModel(inspectionService.saveInspection(inspectionModelMapper.toEntity(inspection)));
    }

    @GetMapping
    public List<Inspection> getAllInspections() {
        return inspectionModelMapper.toModelList(inspectionService.getAllInspections());
    }
}
