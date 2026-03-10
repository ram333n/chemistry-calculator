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

Spring Boot 4.0.3 REST API (Java 21, Maven). Single endpoint: `GET /api/v1/compounds/analysis?formula=H2O`.

**Request flow:** `CompoundAnalysisController` → `CompoundAnalysisService` → `FormulaParser` → `Element` enum

**Key design decisions:**

- `FormulaParser` is a stateful recursive descent parser (`String formula` + `int pos` cursor) with a `synchronized parse()` entry point to be safe as a singleton Spring bean. Grammar supports arbitrary parenthesis nesting: `Fe2(SO4)3`, `Ca3(PO4)2`, etc. Returns `LinkedHashMap<Element, Integer>` to preserve element order from the formula.
- `Element` enum holds all 118 elements with their `double atomicMass`. Lookup is via `Element.valueOf(symbol)` — unknown symbols throw `InvalidFormulaException`.
- Rounding to 4 decimal places happens only in `CompoundAnalysisService` (using `BigDecimal.HALF_UP`), never mid-calculation.
- `GlobalExceptionHandler` (`@RestControllerAdvice`) maps `InvalidFormulaException` and `MissingServletRequestParameterException` → 400, everything else → 500. Error body shape: `{ status, message, path }`.

**Package layout:**
```
org.prokopchuk.chemistry_calculator/
├── controller/   CompoundAnalysisController
├── service/      CompoundAnalysisService
├── parser/       FormulaParser
├── domain/       Element (enum)
├── dto/          CompoundAnalysisResponse, ElementAnalysis (records)
└── exception/    InvalidFormulaException, GlobalExceptionHandler
```
