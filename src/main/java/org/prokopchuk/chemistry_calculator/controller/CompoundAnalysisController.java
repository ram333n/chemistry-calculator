package org.prokopchuk.chemistry_calculator.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.prokopchuk.chemistry_calculator.dto.CompoundAnalysisResponse;
import org.prokopchuk.chemistry_calculator.service.CompoundAnalysisService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Compound Analysis", description = "Analyse chemical compound formulas")
@RestController
@RequestMapping("/compounds")
public class CompoundAnalysisController {

    private final CompoundAnalysisService compoundAnalysisService;

    public CompoundAnalysisController(CompoundAnalysisService compoundAnalysisService) {
        this.compoundAnalysisService = compoundAnalysisService;
    }

    @Operation(summary = "Analyse a chemical formula",
               description = "Returns molar mass and per-element breakdown (count, atomic mass, total mass, mass fraction) for the given formula.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Analysis result",
                    content = @Content(schema = @Schema(implementation = CompoundAnalysisResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid or missing formula", content = @Content),
            @ApiResponse(responseCode = "500", description = "Unexpected server error", content = @Content)
    })
    @GetMapping("/analysis")
    public ResponseEntity<CompoundAnalysisResponse> analyze(
            @Parameter(description = "Chemical formula, e.g. H2O or Fe2(SO4)3", example = "H2O", required = true)
            @RequestParam String formula) {
        return ResponseEntity.ok(compoundAnalysisService.analyze(formula));
    }
}
