# Chemistry Calculator

A Spring Boot REST API for chemical compound analysis and chemical equation balancing.

## Tech Stack

| Component | Version |
|-----------|---------|
| Java | 21 |
| Spring Boot | 4.0.3 |
| OpenAPI / Swagger UI | 3.0.2 |
| Build Tool | Maven (wrapper) |

## Getting Started

### Prerequisites

- Java 21+

### Build

```bash
./mvnw package
```

### Run

```bash
./mvnw spring-boot:run
```

The application starts on `http://localhost:8080`.

### Test

```bash
# Run all tests
./mvnw test

# Run a single test class
./mvnw test -Dtest=ChemistryCalculatorApplicationTests
```

### Interactive API Docs

Swagger UI is available at `http://localhost:8080/swagger-ui.html` once the application is running.

---

## API Reference

Base URL: `http://localhost:8080/api/v1`

### Compound Analysis

Parses a chemical formula, calculates molar mass, and returns per-element mass fractions.

```
GET /api/v1/compounds/analysis?formula={formula}
```

**Supported formula syntax**

| Feature | Example |
|---------|---------|
| Simple formula | `H2O` |
| Multi-letter elements | `Fe`, `Na`, `Ca` |
| Parentheses | `Ca3(PO4)2` |
| Nested parentheses | `Fe2(SO4)3` |
| Multi-digit counts | `(NH4)2SO4` |

#### Example 1 — Water

```
GET /api/v1/compounds/analysis?formula=H2O
```

```json
{
  "formula": "H2O",
  "molarMass": 18.015,
  "elements": [
    {
      "symbol": "H",
      "count": 2,
      "atomicMass": 1.008,
      "totalMass": 2.016,
      "massFraction": 11.1907
    },
    {
      "symbol": "O",
      "count": 1,
      "atomicMass": 15.999,
      "totalMass": 15.999,
      "massFraction": 88.8093
    }
  ]
}
```

#### Example 2 — Iron(III) Sulfate

```
GET /api/v1/compounds/analysis?formula=Fe2(SO4)3
```

```json
{
  "formula": "Fe2(SO4)3",
  "molarMass": 399.873,
  "elements": [
    {
      "symbol": "Fe",
      "count": 2,
      "atomicMass": 55.845,
      "totalMass": 111.69,
      "massFraction": 27.9314
    },
    {
      "symbol": "S",
      "count": 3,
      "atomicMass": 32.065,
      "totalMass": 96.195,
      "massFraction": 24.0564
    },
    {
      "symbol": "O",
      "count": 12,
      "atomicMass": 15.999,
      "totalMass": 191.988,
      "massFraction": 48.0122
    }
  ]
}
```

#### Example 3 — Ammonium Sulfate (nested parentheses)

```
GET /api/v1/compounds/analysis?formula=(NH4)2SO4
```

```json
{
  "formula": "(NH4)2SO4",
  "molarMass": 132.139,
  "elements": [
    {
      "symbol": "N",
      "count": 2,
      "atomicMass": 14.007,
      "totalMass": 28.014,
      "massFraction": 21.2004
    },
    {
      "symbol": "H",
      "count": 8,
      "atomicMass": 1.008,
      "totalMass": 8.064,
      "massFraction": 6.1027
    },
    {
      "symbol": "S",
      "count": 1,
      "atomicMass": 32.065,
      "totalMass": 32.065,
      "massFraction": 24.2661
    },
    {
      "symbol": "O",
      "count": 4,
      "atomicMass": 15.999,
      "totalMass": 63.996,
      "massFraction": 48.4308
    }
  ]
}
```

---

### Equation Balancing

Balances a chemical equation using Gaussian elimination with exact fraction arithmetic and returns the minimal integer coefficients.

```
GET /api/v1/equations/balance?equation={equation}
```

**Equation format:** `reactant1+reactant2->product1+product2`

- Exactly one `->` separator is required.
- Compounds on each side are separated by `+`.

#### Example 1 — Water Formation

```
GET /api/v1/equations/balance?equation=H2+O2->H2O
```

```json
{
  "equation": "2H2 + O2 -> 2H2O",
  "reactants": [
    { "formula": "H2", "coefficient": 2 },
    { "formula": "O2", "coefficient": 1 }
  ],
  "products": [
    { "formula": "H2O", "coefficient": 2 }
  ]
}
```

#### Example 2 — Iron Oxide Formation

```
GET /api/v1/equations/balance?equation=Fe+O2->Fe2O3
```

```json
{
  "equation": "4Fe + 3O2 -> 2Fe2O3",
  "reactants": [
    { "formula": "Fe", "coefficient": 4 },
    { "formula": "O2", "coefficient": 3 }
  ],
  "products": [
    { "formula": "Fe2O3", "coefficient": 2 }
  ]
}
```

#### Example 3 — Hydrogen Chloride Synthesis

```
GET /api/v1/equations/balance?equation=H2+Cl2->HCl
```

```json
{
  "equation": "H2 + Cl2 -> 2HCl",
  "reactants": [
    { "formula": "H2", "coefficient": 1 },
    { "formula": "Cl2", "coefficient": 1 }
  ],
  "products": [
    { "formula": "HCl", "coefficient": 2 }
  ]
}
```

#### Example 4 — Combustion of Glucose

```
GET /api/v1/equations/balance?equation=C6H12O6+O2->CO2+H2O
```

```json
{
  "equation": "C6H12O6 + 6O2 -> 6CO2 + 6H2O",
  "reactants": [
    { "formula": "C6H12O6", "coefficient": 1 },
    { "formula": "O2", "coefficient": 6 }
  ],
  "products": [
    { "formula": "CO2", "coefficient": 6 },
    { "formula": "H2O", "coefficient": 6 }
  ]
}
```

---

## Error Handling

All errors return a JSON body with `status`, `message`, and `path` fields.

| Scenario | Status |
|----------|--------|
| Invalid or malformed formula | 400 |
| Unknown chemical element | 400 |
| Malformed equation (missing `->`) | 400 |
| Missing required query parameter | 400 |
| Unexpected server error | 500 |

### Error Examples

**Unknown element:**
```
GET /api/v1/compounds/analysis?formula=Xx2O
```
```json
{
  "status": 400,
  "message": "Unknown element: Xx",
  "path": "/api/v1/compounds/analysis"
}
```

**Missing `->` separator:**
```
GET /api/v1/equations/balance?equation=H2OH2O
```
```json
{
  "status": 400,
  "message": "Equation must contain exactly one '->' separator",
  "path": "/api/v1/equations/balance"
}
```

**Missing query parameter:**
```
GET /api/v1/compounds/analysis
```
```json
{
  "status": 400,
  "message": "Required parameter 'formula' is not present.",
  "path": "/api/v1/compounds/analysis"
}
```
