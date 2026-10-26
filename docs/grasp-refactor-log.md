# GRASP Refactor Log & Non-Regression Verification

**Project:** GameZone Unicesar  
**Audited Branch / Commit:** `develop` @ `cdf4438` (merged with Phase 1 audit as `feature/grasp-refactor`)  
**Coordinating Author:** Sergio Guerrero (Technical Lead)  
**Contributors:** Isaac Mattos (Developer 1), Jhonatan Galindo (Developer 2), Sergio Guerrero (Technical Lead)

---

## 1. Non-Regression Verification Strategy

As defined in Question 4 of `docs/grasp-analysis.md`, the objective of Phase 2 is pure refactoring: reassigning responsibilities according to GRASP patterns without altering observable system behavior. Every menu option must preserve:
- Identical console prompts, options, formatting, and Spanish messages.
- Identical outputs for valid and invalid inputs (including differentiated exception messages).
- Identical CSV persistence schema and file content structure across `data/*.csv`.

### 1.1 Scope and Test Matrix

The verification strategy mandates executing a comprehensive baseline verification across all 7 functional menus on `develop` prior to any refactoring commits. This exact test matrix is reapplied during each Pull Request for affected modules, and across the entire system before final merge.

| # | Menu Section | Tested Operation | Input Test Data | Expected Baseline Output Behavior |
|---|---|---|---|---|
| **TC-01** | Main Menu | Application Launch & Navigation | `0` (Exit) | Displays main banner, 7 menu categories, and terminates cleanly. |
| **TC-02** | Products | Register VideoGame | ID `VG-TEST`, Title `Halo CE`, Price `29.99`, Stock `10`, Platform `Xbox`, Genre `Shooter` | Success message `"Videojuego registrado exitosamente."`, saved to `products.csv`. |
| **TC-03** | Products | Register Console | ID `CN-TEST`, Title `PlayStation 5`, Price `499.99`, Stock `5`, Manufacturer `Sony`, Storage `825GB` | Success message `"Consola registrada exitosamente."`, saved to `products.csv`. |
| **TC-04** | Products | List All & Filter Products | Options `3`, `7`, `8` | Displays numbered list of all products, only videogames, and only consoles. |
| **TC-05** | Products | Search Product by ID | ID `VG-TEST` | Displays complete product description with title, price, stock, and specific details. |
| **TC-06** | Persons | Register Customer | ID `CUST-TEST`, Name `Carlos Perez`, Email `carlos@example.com`, Phone `3001234567` | Success message `"Cliente registrado exitosamente."`, saved to `customers.csv`. |
| **TC-07** | Persons | Register Seller | ID `SELL-TEST`, Name `Laura Ruiz`, Email `laura@example.com`, Department `Ventas`, Commission `0.05` | Success message `"Vendedor registrado exitosamente."`, saved to `sellers.csv`. |
| **TC-08** | Persons | List & Search Persons | Options `3`, `4`, `5`, `6` | Displays numbered lists of customers and sellers; finds registered entities by ID. |
| **TC-09** | Accessories | Register & List Accessories | Controller `ACC-TEST`, Price `59.99`, Stock `8`, Type `Inalámbrico` | Success message, listed under `Listar todos los accesorios` and `Listar por tipo`. |
| **TC-10** | Promotions | Register Percentage / Volume Promotion | Name `PROMO-TEST`, Start `2026-01-01`, End `2026-12-31`, Percentage `15.0` | Success message `"Promoción registrada exitosamente."`, saved to `promotions.csv`. |
| **TC-11** | Sales | Register Sale (Multi-item + Warranty + Promo) | Customer `CUST-TEST`, Seller `SELL-TEST`, Items `CN-TEST x1` (with extended warranty), `VG-TEST x1` | Subtotal calculated, discount applied if eligible, extended warranty cost added (+10%), receipt generated, stock decremented, saved in `sales.csv` and `warranties.csv`. |
| **TC-12** | Sales | Sales History & Filters | Options `2`, `3`, `4` | Displays all sales, sales by customer, and sales by seller with detailed receipt format. |
| **TC-13** | Warranties | Query Warranties | Query by product and sale ID; options `2`, `3`, `4` | Displays basic warranty (6 months) and extended warranty (12 months), lists active/expiring warranties. |
| **TC-14** | Returns | Register Return | Sale ID of registered sale, Product `VG-TEST`, Quantity `1`, Reason `Defectuoso` | Validates return period (30 days), restores inventory, calculates refund, generates return receipt, saved in `returns.csv`. |
| **TC-15** | Returns | Monthly Financial Balance | Option `5`, Month `10`, Year `2026` | Displays total sales, total refunds, and net financial balance. |
| **TC-16** | Exceptions | Differentiated Error Handling | Non-existent customer/product (`ResourceNotFoundException`), negative values (`InvalidDataException`), insufficient stock (`BusinessRuleException`) | Friendly Spanish error messages displayed according to typed exception; no unhandled stack traces. |

---

## 2. Baseline Verification Evidence (develop @ cdf4438 / Pre-Refactor)

The following baseline test run was executed on the `develop` baseline before applying any Phase 2 refactoring changes.

### 2.1 Execution Log & Verification Results

* **Execution Date & Commit:** 2026-10-25 @ `cdf4438` (baseline branch `feature/grasp-refactor`)
* **Environment:** OpenJDK 25 / Windows PowerShell 5.1 / UTF-8 Console

| Test Case | Status | Verified Output / Persistence Behavior |
|---|---|---|
| **TC-01** (Menu loop) | **PASSED** | Main menu displays all 7 options cleanly and exits on `0`. |
| **TC-02** (Register VideoGame) | **PASSED** | Registered and appended to `data/products.csv`. |
| **TC-03** (Register Console) | **PASSED** | Registered and appended to `data/products.csv`. |
| **TC-04** (List Products) | **PASSED** | Numbered output formatted as `1. ...`, `2. ...`. Filters isolate type correctly. |
| **TC-05** (Search Product) | **PASSED** | Returns full polymorphic description (`getFullDescription()`). |
| **TC-06** (Register Customer) | **PASSED** | Customer created and validated via `PersonValidator`; written to `customers.csv`. |
| **TC-07** (Register Seller) | **PASSED** | Seller validated and written to `sellers.csv`. |
| **TC-08** (List/Search Person) | **PASSED** | Correctly lists customers and sellers by ID. |
| **TC-09** (Accessories) | **PASSED** | Controller, Cable, Memory registered; listed by type. |
| **TC-10** (Promotions) | **PASSED** | Active promotions listed and evaluated. |
| **TC-11** (Register Sale) | **PASSED** | Receipt format matches specification: subtotal, discount, extended warranty cost, total. Stock properly updated in inventory files. |
| **TC-12** (Sale History) | **PASSED** | Full sale receipts printed correctly for all historical sales. |
| **TC-13** (Warranty Queries) | **PASSED** | Basic warranty (6 months) and extended warranty (12 months) retrieved for console sales. |
| **TC-14** (Returns) | **PASSED** | Return processed within 30-day window; stock restored via `ProductService.restoreStock`. |
| **TC-15** (Monthly Balance) | **PASSED** | Calculates sales minus refunds correctly. |
| **TC-16** (Exceptions) | **PASSED** | Differentiated catches print Spanish user-friendly messages for resource not found, invalid data, and business rules. |

**Baseline Verdict:** 16 / 16 Test Cases Passed. Baseline reference established.

---

## 3. Refactoring Change Log

Every violation identified in `docs/grasp-audit/consolidated-audit.md` (`V-01` through `V-21`) must be logged in this table upon correction.

| Class | Change Description | Violation ID | Pattern Applied | Commit / PR | Assigned Member |
|---|---|---|---|---|---|
| Sale | Implemented getDiscountRatio() providing proportional discount calculation to unblock Return | V-01 | Information Expert | 0897236 | Sergio Guerrero (Tech Lead) |
| Sale | Implemented getTotalQuantity() aggregating units purchased across items to unblock BulkPurchaseDiscount | V-02 | Information Expert | 0897236 | Sergio Guerrero (Tech Lead) |
| WarrantyService | Removed unused getWarranties() and setWarranties() to protect internal mutable state | V-17 | Low Coupling | cd3e2a3 | Sergio Guerrero (Tech Lead) |
| SaleRepository | Removed console output messages (System.out.println) from saveSales and loadSales | V-18 | High Cohesion | 9c06809 | Sergio Guerrero (Tech Lead) |
| SaleRepository | Replaced hardcoded file path constant with default and parameterized constructors | V-21 | Protected Variations | 9c06809 | Sergio Guerrero (Tech Lead) |
| Product | Added category constants and abstract getCategory() so each product type declares its own category | V-03 | Polymorphism | 6901c66 | Isaac Mattos (Dev 1) |
| VideoGame, Console, Accessory | Implemented getCategory() returning their category code (Controller, Cable and Memory inherit it from Accessory) | V-03 | Polymorphism | 6901c66 | Isaac Mattos (Dev 1) |
| CategoryDiscount | Replaced the instanceof checks in belongsToTargetCategory() with a comparison against product.getCategory() | V-03 | Polymorphism | 6901c66 | Isaac Mattos (Dev 1) |
| ProductCsvMapper | New interface: each product type converts its own type-specific CSV columns to text and back | V-04 | Polymorphism | 529399a | Isaac Mattos (Dev 1) |
| VideoGameCsvMapper, ConsoleCsvMapper | New mappers that write and read the platform/genre/age rating and brand/model/generation columns | V-04 | Pure Fabrication | 529399a | Isaac Mattos (Dev 1) |
| ProductRepository | Replaced the instanceof checks in toCsvLine() and the type comparisons in fromCsvLine() with a mapper registry selected by product category; CSV content is unchanged | V-04 | Polymorphism | 529399a | Isaac Mattos (Dev 1) |
| PercentageBasedPromotion | New abstract class that holds the percentage field, its 0-100 validation and applyPercentage() for all percentage-based promotions | V-05 | Protected Variations | 2e538c7 | Isaac Mattos (Dev 1) |
| PercentageDiscount, CategoryDiscount, BulkPurchaseDiscount | Now extend PercentageBasedPromotion; removed their duplicated percentage field, getter, setter and validation | V-05 | Protected Variations | 2e538c7 | Isaac Mattos (Dev 1) |
| BulkPurchaseDiscount | calculateDiscount() now asks the sale for its total units with Sale.getTotalQuantity() instead of iterating the sale items | V-02 | Information Expert | HASH | Isaac Mattos (Dev 1) |

---

## 4. Final Post-Refactor Verification Evidence

*(To be executed and documented upon completion of all refactoring PRs prior to final merge into develop).*
