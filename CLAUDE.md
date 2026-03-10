# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# Build
./mvnw package

# Run all tests
./mvnw test

# Run a single test class
./mvnw test -Dtest=ChemistryCalculatorApplicationTests

# Start the application
./mvnw spring-boot:run
```

## Architecture

Spring Boot 4.0.3 REST API (Java 21, Maven). Two endpoint groups:
- `GET /api/v1/compounds/analysis?formula=H2O`
- `GET /api/v1/equations/balance?equation=H2+O2->H2O`

**Request flows:**
- `CompoundAnalysisController` → `CompoundAnalysisService` → `FormulaParser` → `Element` enum
- `EquationBalancingController` → `EquationBalancingService` → `EquationParser` + `EquationBalancer` → `FormulaParser` → `Element` enum

**Key design decisions:**

- `FormulaParser` is a stateless Spring bean. Each `parse()` call creates a private inner `Parse` instance that holds the `String formula` + `int pos` cursor, so no synchronization is needed. Grammar supports arbitrary parenthesis nesting: `Fe2(SO4)3`, `Ca3(PO4)2`, etc. Returns `LinkedHashMap<Element, Integer>` to preserve element order from the formula.
- `Element` enum holds all 118 elements with their `double atomicMass`. Lookup is via `Element.valueOf(symbol)` — unknown symbols throw `InvalidFormulaException`.
- Rounding to 4 decimal places happens only in `CompoundAnalysisService` (using `BigDecimal.HALF_UP`), never mid-calculation.
- `EquationParser` splits on `"->"` (exactly one required) then on `"+"`, delegates each token to `FormulaParser`. Returns a `ParsedEquation` record holding both formula strings and element maps for reactants and products.
- `EquationBalancer` builds a stoichiometry matrix (elements × compounds) with reactant columns as `+count` and product columns as `-count`, runs Gaussian elimination to RREF using exact `Fraction` arithmetic, identifies the single free variable, back-substitutes, scales to the smallest positive integers via LCM of denominators.
- `Fraction` is a package-private class in the `balancer` package. Fields are canonical: reduced, denominator always > 0.
- `GlobalExceptionHandler` (`@RestControllerAdvice`) maps `InvalidFormulaException`, `InvalidEquationException`, and `MissingServletRequestParameterException` → 400, everything else → 500. Error body shape: `{ status, message, path }`.
- Integration tests use `@SpringBootTest` + `MockMvcBuilders.webAppContextSetup(context)` (no `@AutoConfigureMockMvc` — not available in `spring-boot-starter-webmvc-test`).

**Package layout:**
```
org.prokopchuk.chemistry_calculator/
├── controller/   CompoundAnalysisController, EquationBalancingController
├── service/      CompoundAnalysisService, EquationBalancingService
├── parser/       FormulaParser, EquationParser
├── balancer/     EquationBalancer, Fraction (package-private)
├── domain/       Element (enum)
├── dto/          CompoundAnalysisResponse, ElementAnalysis,
│                 BalancedEquationResponse, CompoundCoefficient (records)
└── exception/    InvalidFormulaException, InvalidEquationException,
                  GlobalExceptionHandler
```
