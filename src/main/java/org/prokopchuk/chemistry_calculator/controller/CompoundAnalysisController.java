package org.prokopchuk.chemistry_calculator.controller;

import org.prokopchuk.chemistry_calculator.dto.CompoundAnalysisResponse;
import org.prokopchuk.chemistry_calculator.service.CompoundAnalysisService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/compounds")
public class CompoundAnalysisController {

    private final CompoundAnalysisService compoundAnalysisService;

    public CompoundAnalysisController(CompoundAnalysisService compoundAnalysisService) {
        this.compoundAnalysisService = compoundAnalysisService;
    }

    @GetMapping("/analysis")
    public ResponseEntity<CompoundAnalysisResponse> analyze(@RequestParam String formula) {
        return ResponseEntity.ok(compoundAnalysisService.analyze(formula));
    }
}
