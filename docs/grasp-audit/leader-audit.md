# GRASP Audit — Technical Lead

**Author:** Sergio Guerrero (Technical Lead)  
**Audited branch / commit:** `develop` @ `cdf4438` (*Merge pull request #46 from sergioaguerrero/feature/exception-handling*), the baseline of `feature/grasp-audit`

---

## 1. Scope

The scope follows the responsibility matrix in Section 4 of the Requirement 7 specification, `TEAM.md`, and the Git commit history (`git blame` on `develop`).

### 1.1 Classes and methods audited

| Module (Requirement) | Class | Methods / Members Audited |
|---|---|---|
| Sales, Interface & Startup (Workshop 1) | `model.Sale` | All members (attributes, constructors, getters/setters, `addItem`, `calculateTotal`, `calculateSubtotal`, `getSubtotal`, `calculateFinalTotal`, `getFinalTotal`, `register`, `generateReceipt`) except `canBeReturned` (Developer 1) |
| Sales, Interface & Startup (Workshop 1) | `persistence.SaleRepository` | All members (`saleLine`, `saveSales`, `loadSales`, `findCustomerById`, `findSellerById`, `findProductById`) |
| Sales, Interface & Startup (Workshop 1) | `service.SaleService` | All members (`registerSale` overloads, getter/setter methods, `applyWarranties`, `isConsoleInItems`, `nextSaleId`, `allSellableItems`, `listAllSales`, `listSalesByCustomer`, `listSalesBySeller`, `findSaleById`, `getSaleOrThrow`) |
| Sales, Interface & Startup (Workshop 1) | `ui.ConsoleMenu` | All members (submenus, validation helpers, formatted receipts, and differentiated exception handling) |
| Sales, Interface & Startup (Workshop 1) | `Main` | Application wiring, instantiation, dependency injection, and entry point |
| Returns (Req. 3) | `service.ProductService` | Method `restoreStock(String, int)` (assigned to Technical Lead in requirement specification) |
| Warranties (Req. 4) | `service.WarrantyService` | Methods `getWarranties()` and `setWarranties(List<Warranty>)` (written by Technical Lead in A3) |

### 1.2 Members excluded from this audit

| Member | Assigned Owner | Audited In |
|---|---|---|
| `Sale.canBeReturned()` | Isaac David Mattos (Developer 1) | `developer1-audit.md` |
| `ProductService` (all other methods) | Isaac David Mattos (Developer 1) | `developer1-audit.md` |
| `WarrantyService` (all core warranty rules and methods) | Jhonatan Galindo (Developer 2) | `developer2-audit.md` |

---

## 2. Information Expert

| Verdict | Violations |
|---|---|
| **Violation** | V-01 |

### Correctly Applied

| Class / Method | Why It Is Correct |
|---|---|
| `Sale.calculateTotal`, `calculateSubtotal`, `calculateFinalTotal` | The sale encapsulates its items, applied discount, and warranty costs, computing its financial totals autonomously. |
| `Sale.generateReceipt` | The sale formats its own transaction statement using its internal state. |
| `SaleService.nextSaleId` | Generates consecutive sequence keys based on inspecting all registered sales. |

### Violations

| ID | Module | Evidence | Explanation | Consequence | Proposed Solution | Other Member's Code |
|---|---|---|---|---|---|---|
| V-01 | Sales / Accessories (Req. 1) | `SaleService.registerSale`, lines 213–219: `Optional<Accessory> accessoryOpt = accessoryService.findById(productId); if (item.getProduct() instanceof Accessory \|\| accessoryOpt.isPresent()) ... else productService.hasEnoughStock...` | Stock query checks concrete type using `instanceof` and duplicate service calls rather than querying a unified inventory or delegating polymorphic behavior to the item. | Tight coupling between `SaleService` and individual catalog services; adding new item categories requires touching `SaleService`. | Unify inventory verification or use `Product.getCategory()` / inventory abstraction. | No |

---

## 3. Creator

| Verdict | Violations |
|---|---|
| **Correct** | — |

| Class / Method | Why It Is Correct |
|---|---|
| `SaleRepository.loadSales` | Possesses the CSV text records and reconstructs `Sale` and `SaleItem` instances using provided master collections. |
| `SaleService.registerSale` | Aggregates customers, sellers, and items to create a valid `Sale` domain entity. |
| `Main.main` | System bootstrap responsible for instantiating concrete repositories and services. |

---

## 4. Controller

| Verdict | Violations |
|---|---|
| **Correct** | — |

| Class / Method | Why It Is Correct |
|---|---|
| `SaleService` | Serves as the use-case controller coordinating sale registration, discount calculation, warranty attachment, and inventory decrement. |
| `ConsoleMenu` | Boundary presentation layer capturing UI events and delegating directly to use-case service controllers. |
| `Main` / `SaleRepository` | Not applicable: they do not receive system UI input events. |

---

## 5. Low Coupling

| Verdict | Violations |
|---|---|
| **Violation** | V-02 |

### Correctly Applied

| Class / Method | Why It Is Correct |
|---|---|
| `SaleRepository` | Interacts strictly with domain models and throws domain-specific `PersistenceException`. |
| `ConsoleMenu` | Relies entirely on service interfaces without direct coupling to repositories or database files. |

### Violations

| ID | Module | Evidence | Explanation | Consequence | Proposed Solution | Other Member's Code |
|---|---|---|---|---|---|---|
| V-02 | Warranties (Req. 4) | `WarrantyService.getWarranties` (lines 212–215) and `setWarranties` (lines 220–223) | Unused getter/setter authored during integration exposing the internal warranty collection directly. | Breaks encapsulation and allows external classes to mutate internal service state without domain checks. | Eliminate `getWarranties` and `setWarranties` as no callers require them. | No |

---

## 6. High Cohesion

| Verdict | Violations |
|---|---|
| **Violation** | V-03, V-04 |

### Correctly Applied

| Class / Method | Why It Is Correct |
|---|---|
| `Sale` | Holds only core transaction data and direct calculation rules. |
| `ConsoleMenu` | Focuses strictly on menu loops, user prompt formatting, and presentation. |

### Violations

| ID | Module | Evidence | Explanation | Consequence | Proposed Solution | Other Member's Code |
|---|---|---|---|---|---|---|
| V-03 | Sales (Workshop 1) | `SaleRepository.saveSales` line 56 (`System.out.println("Sales saved successfully in " + saleCSV);`) and line 140 (`System.out.println("Sales successfully loaded from " + saleCSV);`) | Persistence repository directly emits status messages to standard console output. | Intertwines persistence mechanisms with user-interface console printing. | Remove `System.out.println` from repository methods; handle user-facing feedback at UI level. | No |
| V-04 | Sales (Workshop 1) | `SaleService.registerSale` line 271 (`System.out.println("Venta registrada exitosamente");`) and validation errors printed via `System.err.println` (lines 186, 192, 202, 222, 236) | Business service class directly writes to console output streams and suppresses domain exceptions instead of propagating them to the UI layer. | Prevents `ConsoleMenu` from applying its differentiated exception handlers (lines 596–604) and couples service to console. | Remove console prints from `SaleService` and let exceptions propagate naturally to `ConsoleMenu`. | Yes (`ConsoleMenu` handles exceptions) |

---

## 7. Polymorphism

| Verdict | Violations |
|---|---|
| **Violation** | V-05 |

### Correctly Applied

| Class / Method | Why It Is Correct |
|---|---|
| `SaleService.registerSale` | Evaluates discounts polymorphically via `promotionService.findBestPromotionFor(sale)` and `bestPromotion.calculateDiscount(sale)`. |

### Violations

| ID | Module | Evidence | Explanation | Consequence | Proposed Solution | Other Member's Code |
|---|---|---|---|---|---|---|
| V-05 | Sales / Accessories / Warranties | `SaleService.applyWarranties` lines 312–318 (`if (product instanceof Console)`); `SaleService.isConsoleInItems` lines 334–336 (`if (item.getProduct() instanceof Console)`); `SaleService.registerSale` lines 260–264 (`instanceof Accessory`); `ConsoleMenu` lines 577–580 (`item instanceof Console`) | Business rules for warranties and inventory updates inspect concrete product subtypes with procedural `instanceof` checks. | Adding a new warrantable item or sellable subtype forces changes across multiple service and UI methods. | Utilize polymorphic discriminator or method `Product.getCategory()` introduced in Developer 1's V-03. | No |

---

## 8. Pure Fabrication

| Verdict | Violations |
|---|---|
| **Correct** | — |

| Class / Method | Why It Is Correct |
|---|---|
| `SaleRepository` | Artificial fabrication created specifically to isolate file persistence from the `Sale` domain entity. |
| `ConsoleMenu` | Artificial fabrication separating user console presentation from domain processing. |

---

## 9. Indirection

| Verdict | Violations |
|---|---|
| **Correct** | — |

| Class / Method | Why It Is Correct |
|---|---|
| `SaleService` | Acts as an intermediary between the UI (`ConsoleMenu`) and persistence (`SaleRepository`), decoupling user actions from storage. |
| `ProductService.restoreStock` | Provides an indirect facade between return processing and internal product stock updates. |

---

## 10. Protected Variations

| Verdict | Violations |
|---|---|
| **Violation** | V-06 |

### Correctly Applied

| Class / Method | Why It Is Correct |
|---|---|
| `Sale.items` | Encapsulated collection manipulated exclusively through `addItem` and accessed via getter. |

### Violations

| ID | Module | Evidence | Explanation | Consequence | Proposed Solution | Other Member's Code |
|---|---|---|---|---|---|---|
| V-06 | Sales (Workshop 1) | `SaleRepository.saleCSV` line 17 (`private static final String saleCSV = "data/sales.csv";`) | Hardcoded file storage path embedded directly as a constant inside repository. | Prevents configurable storage locations or test-specific directory redirections. | Introduce configurable constructor parameter for storage path while providing a default constructor for production compatibility. | No |

---

## 11. Boundary and Out-of-Scope Findings

### Boundary Issues Addressed
1. **Methods needed on `Sale` by Teammates:**
   - `Return.calculateItemRefund` requires discount ratio -> Added `Sale.getDiscountRatio()`.
   - `BulkPurchaseDiscount.calculateDiscount` requires total units count -> Added `Sale.getTotalQuantity()`.
2. **Exception Propagation Boundary:**
   - Removing internal `catch` blocks in `SaleService.registerSale` so that `ConsoleMenu` can properly catch `ResourceNotFoundException`, `BusinessRuleException`, and `InvalidDataException`.

### Out-of-Scope Functional Defects Identified
- None directly in the Technical Lead's assigned classes beyond those already captured in Developer 1 and Developer 2 audits.

---

## 12. Summary of Verdicts

| Pattern | Verdict | Violations |
|---|---|---|
| Information Expert | Violation | V-01 |
| Creator | Correct | — |
| Controller | Correct | — |
| Low Coupling | Violation | V-02 |
| High Cohesion | Violation | V-03, V-04 |
| Polymorphism | Violation | V-05 |
| Pure Fabrication | Correct | — |
| Indirection | Correct | — |
| Protected Variations | Violation | V-06 |

| Summary Metric | Count |
|---|---|
| Total Patterns Audited | 9 |
| Patterns with Violations | 5 (Information Expert, Low Coupling, High Cohesion, Polymorphism, Protected Variations) |
| Patterns Correct | 4 (Creator, Controller, Pure Fabrication, Indirection) |
| Total Identified Violations | 6 (V-01 through V-06) |

