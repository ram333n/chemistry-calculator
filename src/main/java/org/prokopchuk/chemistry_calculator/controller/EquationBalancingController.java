package org.prokopchuk.chemistry_calculator.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.prokopchuk.chemistry_calculator.dto.BalancedEquationResponse;
import org.prokopchuk.chemistry_calculator.service.EquationBalancingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Equation Balancing", description = "Balance chemical equations")
@RestController
@RequestMapping("/equations")
public class EquationBalancingController {

    private final EquationBalancingService equationBalancingService;

    public EquationBalancingController(EquationBalancingService equationBalancingService) {
        this.equationBalancingService = equationBalancingService;
    }

    @Operation(summary = "Balance a chemical equation",
               description = "Returns the balanced equation with integer stoichiometric coefficients for reactants and products.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Balanced equation result",
                    content = @Content(schema = @Schema(implementation = BalancedEquationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid or missing equation", content = @Content),
            @ApiResponse(responseCode = "500", description = "Unexpected server error", content = @Content)
    })
    @GetMapping("/balance")
    public ResponseEntity<BalancedEquationResponse> balance(
            @Parameter(description = "Chemical equation, e.g. H2+O2->H2O", example = "H2+O2->H2O", required = true)
            @RequestParam String equation) {
        return ResponseEntity.ok(equationBalancingService.balance(equation));
    }
}
