package org.prokopchuk.chemistry_calculator.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class EquationBalancingControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void balanceWater() throws Exception {
        mockMvc.perform(get("/api/v1/equations/balance").param("equation", "H2+O2->H2O"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.equation").value("2H2 + O2 -> 2H2O"))
                .andExpect(jsonPath("$.reactants[0].formula").value("H2"))
                .andExpect(jsonPath("$.reactants[0].coefficient").value(2))
                .andExpect(jsonPath("$.reactants[1].formula").value("O2"))
                .andExpect(jsonPath("$.reactants[1].coefficient").value(1))
                .andExpect(jsonPath("$.products[0].formula").value("H2O"))
                .andExpect(jsonPath("$.products[0].coefficient").value(2));
    }

    @Test
    void balanceIronOxide() throws Exception {
        mockMvc.perform(get("/api/v1/equations/balance").param("equation", "Fe+O2->Fe2O3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.equation").value("4Fe + 3O2 -> 2Fe2O3"));
    }

    @Test
    void coefficientOneOmittedFromRenderedString() throws Exception {
        mockMvc.perform(get("/api/v1/equations/balance").param("equation", "H2+Cl2->HCl"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.equation").value("H2 + Cl2 -> 2HCl"));
    }

    @Test
    void missingArrowReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/v1/equations/balance").param("equation", "H2O2H2O"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void unknownElementReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/v1/equations/balance").param("equation", "Xx+O2->XxO"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void missingParamReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/v1/equations/balance"))
                .andExpect(status().isBadRequest());
    }
}
