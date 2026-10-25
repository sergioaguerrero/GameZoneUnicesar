# GRASP Audit — Developer 2

**Author:** Jhonatan Galindo (Developer 2)
**Audited branch / commit:** `develop` @ `cdf4438` (*Merge pull request #46 from sergioaguerrero/feature/exception-handling*), the starting point of `feature/grasp-audit`

---

## 1. Scope

The scope follows the responsibility table of the requirement and the authorship recorded in `TEAM.md` and in the commit history (`git blame` on `develop`).

### 1.1 Classes and methods audited

| Module (requirement) | Class | Methods / members audited |
|---|---|---|
| Persons (Workshop 1) | `model.Person`, `model.Customer`, `model.Seller` | all members |
| Persons (Workshop 1) | `persistence.PersonRepository`, `service.PersonService` | all members |
| Accessories (Req. 1) | `persistence.AccessoryRepository`, `service.AccessoryService` | all members |
| Promotions (Req. 2) | `persistence.PromotionRepository`, `service.PromotionService` | all members |
| Returns (Req. 3) | `persistence.ReturnRepository`, `service.ReturnService` | all members, including the monthly balance (A6) |
| Returns (Req. 3) | `model.Return` | `calculateRefundAmount(double)` only (A7) |
| Returns (Req. 3) | `model.Product` | `increaseStock(int)` only |
| Warranties (Req. 4) | `persistence.WarrantyRepository`, `service.WarrantyService` | all members except `getWarranties` and `setWarranties` |
| Exceptions and validation (Req. 6) | `service.ProductService`, `service.PersonService`, `service.SaleService` | calls to the validators and the exception handling |
| Exceptions and validation (Req. 6) | `persistence.ProductRepository`, `persistence.PersonRepository`, `persistence.SaleRepository` | the `IOException` → `PersistenceException` handling |

### 1.2 Members excluded from this audit

| Member | Reason | Reported to |
|---|---|---|
| `WarrantyService.getWarranties`, `setWarranties` | Written by the Technical Lead (`55dd5fd`, A3) | Technical Lead |
| `ProductService.restoreStock` | Written by Developer 2 (`6a20f94`), but the requirement assigns its audit to the Technical Lead | Technical Lead |
| `exceptions` and `validation` packages | Classes of Developer 1; only their use in services and repositories is audited here | — |

---

## 2. Information Expert

| Verdict | Violations |
|---|---|
| **Violation** | V-10 |

**Correctly applied**

| Class / method | Why it is correct |
|---|---|
| `ReturnService.calculateMonthlySales` | Uses `Sale.calculateFinalTotal()`: the sale knows its own total. |
| `Return.calculateRefundAmount(double)` | The return owns its refund amount and adds the warranty refund to it. |
| `Product.increaseStock` | The product owns its stock. |
| `PromotionService.findBestPromotionFor` | Asks each `Promotion` for `isActive` and `calculateDiscount`. |

**Violations**

| ID | Module | Evidence | Explanation | Consequence | Proposed solution | Other member's code |
|---|---|---|---|---|---|---|
| V-10 | Warranties (Req. 4), A2 | `WarrantyRepository.loadAll(List<Sale>, List<Product>)`, line 34, and `fromCsvLine`, lines 90–114: loops over `products` and `sales` to find each reference | The repository resolves `Sale` and `Product` objects; A2 assigned that responsibility to `WarrantyService`. The two loops are also duplicated for each warranty type. Also affects Low Coupling. | The repository depends on `Sale` and `Product` and needs full domain lists only to read a file. | The repository returns the raw records (ids); `WarrantyService` resolves `Sale` and `Product`. Same warranties are loaded. | No |

---

## 3. Creator

| Verdict | Violations |
|---|---|
| **Correct** | — |

| Class / method | Why it is correct |
|---|---|
| `PersonService.registerCustomer`, `registerSeller` | The service holds the `customers` and `sellers` lists and the initialization data. |
| `WarrantyService.assignBasicWarranty`, `assignExtendedWarranty` | The service holds the warranty list and receives the product, sale and date. |
| `ReturnService.registerReturn` | The service holds the returns list and has the sale and the returned products. |
| `*Repository.fromCsvLine` | Each repository holds the CSV data used to rebuild its objects. |

---

## 4. Controller

| Verdict | Violations |
|---|---|
| **Correct** | — |

| Class / method | Why it is correct |
|---|---|
| `PersonService`, `AccessoryService`, `PromotionService`, `ReturnService`, `WarrantyService` | Use-case controllers: `ConsoleMenu` delegates each system operation of its module to them. |
| Repositories | Not applicable: they receive no system events. |

---

## 5. Low Coupling

| Verdict | Violations |
|---|---|
| **Violation** | V-01 (also V-10) |

**Correctly applied**

| Class / method | Why it is correct |
|---|---|
| `WarrantyService` constructor | Receives `WarrantyRepository`, `SaleRepository` and `ProductService`, exactly as adjustment A2 requires to break the circular dependency. |
| `PersonRepository`, `ProductRepository`, `SaleRepository` | Depend only on the model and on `PersistenceException`. |

**Violations**

| ID | Module | Evidence | Explanation | Consequence | Proposed solution | Other member's code |
|---|---|---|---|---|---|---|
| V-01 | Persons (Workshop 1) | `Customer`: field `purchaseHistory` (line 11), constructor with history (lines 21–25), `getPurchaseHistory` (59–61), `addPurchaseHistory` (67–69) | `Customer` keeps a list of `Sale` while each `Sale` already references its `Customer`. No class calls these members, and the constructor used by the system never initializes the list. Also affects High Cohesion. | Two-way dependency between `Customer` and `Sale` for an unused responsibility; any future use would fail with `NullPointerException`. | Remove the unused history members; the history of a customer can be obtained from the sales. Behavior does not change because nothing uses them. | No |

---

## 6. High Cohesion

| Verdict | Violations |
|---|---|
| **Violation** | V-02, V-03, V-04, V-05 |

**Correctly applied**

| Class / method | Why it is correct |
|---|---|
| `AccessoryService`, `PromotionService`, `WarrantyService` | Only business rules of their module; no console output. |
| `AccessoryRepository`, `PromotionRepository` | Only CSV persistence. |

**Violations**

| ID | Module | Evidence | Explanation | Consequence | Proposed solution | Other member's code |
|---|---|---|---|---|---|---|
| V-02 | Persons (Workshop 1) | `PersonRepository.deleteSeller` (lines 160–166) and `deleteCustomer` (175–180): remove elements from the list received | A persistence class performs a business operation (deleting persons). No class calls these methods. | Any deletion rule would have to live in the repository instead of the service. | Remove both unused methods; the repository keeps only save and load. Behavior does not change. | No |
| V-03 | Persons (Workshop 1) | `PersonRepository`: `System.out.println("Customers saved successfully in " ...)` and the same messages at lines 54, 88, 109, 144 | A persistence class writes messages to the console, a user-interface responsibility. | The repository cannot be used without console output. | Remove the messages from the repository. **Note:** the messages are shown today, so the consolidation must decide whether to print them from `Main` (Technical Lead) or move this finding to out of scope. | Possibly Technical Lead (`Main`) |
| V-04 | Returns (Req. 3) | `ReturnService.registerReturn`, line 155: `System.out.println("Devolución registrada exitosamente.");` | A business service prints to the console. | The service is tied to the console UI. | Remove the line and print the same message in `ConsoleMenu` right after `registerReturn` succeeds. Output stays identical. | Yes: Technical Lead (`ConsoleMenu`) |
| V-05 | Exceptions (Req. 6) | `SaleService.registerSale`, lines 183–188: `catch (InvalidDataException \| BusinessRuleException e) { System.err.println("Sale validation failed: " + e.getMessage()); return false; }` | The service catches the typed exceptions, prints them and returns `false`. Also affects Indirection: the exceptions never reach the UI. | The differentiated `catch` blocks of `ConsoleMenu.registerSale` (lines 596–603) never run for these errors. | Let the exceptions propagate to `ConsoleMenu`. **Note:** this changes the message the user sees; if the team keeps the rule of identical messages, move it to out of scope. | Yes: Technical Lead (`ConsoleMenu`) |

---

## 7. Polymorphism

| Verdict | Violations |
|---|---|
| **Violation** | V-06, V-07, V-08, V-09 |

**Correctly applied**

| Class / method | Why it is correct |
|---|---|
| `PromotionService.findBestPromotionFor` | Calls `calculateDiscount` on any `Promotion` without checking its type. |
| `WarrantyService.listActiveWarranties` | Calls `isActive` on any `Warranty`. |

**Violations**

| ID | Module | Evidence | Explanation | Consequence | Proposed solution | Other member's code |
|---|---|---|---|---|---|---|
| V-06 | Accessories (Req. 1) | `AccessoryRepository.toCsvLine`, lines 80–114 (`instanceof Cable` / `Controller` / `Memory`); `fromCsvLine`, lines 126–146 (`CABLE_TYPE.equals(type)` …) | The CSV format varies by accessory type but is chosen with conditionals. | A new accessory type requires editing both methods; a miss fails only at runtime. | One mapper per accessory type, registered by discriminator. CSV lines stay identical. | No |
| V-07 | Promotions (Req. 2) | `PromotionRepository.toCsvLine`, lines 77–110 (`instanceof BulkPurchaseDiscount` …); `fromCsvLine`, lines 122–142 | Same problem for promotion types. | A new promotion type requires editing the repository, although `PromotionService` already works with any `Promotion`. | One mapper per promotion type, registered by discriminator. | No |
| V-08 | Returns (Req. 3), A4 | `ReturnService.restoreStockFor`, lines 168–174, and `ReturnRepository.resolveProduct`, lines 113–120: `if (product instanceof Accessory)` | The service and repository choose the inventory by checking the product type. | A new product type with its own inventory requires editing both classes. | `resolveProduct`: look up the id in `AccessoryService` without checking the type. `restoreStockFor`: map of restore operations by `Product.getCategory()`, the method added by Developer 1 in his V-03. | No (uses `getCategory()` once Developer 1 adds it; no change to his code) |
| V-09 | Returns (Req. 3), A7 | `WarrantyService.cancelWarranties`, line 157: `if (warranty instanceof ExtendedWarranty) { refundableCost += warranty.getAdditionalCost(); }` | `BasicWarranty.getAdditionalCost()` already returns 0, so the type check is unnecessary. | Each new warranty type would need another check. | `refundableCost += warranty.getAdditionalCost();` for every cancelled warranty. Same result. | No |

---

## 8. Pure Fabrication

| Verdict | Violations |
|---|---|
| **Correct** | — |

| Class / method | Why it is correct |
|---|---|
| `PersonRepository`, `AccessoryRepository`, `PromotionRepository`, `ReturnRepository`, `WarrantyRepository` | Invented classes for file persistence; keep the model free of I/O. |
| Services of the module | Keep business rules out of the model classes. |

---

## 9. Indirection

| Verdict | Violations |
|---|---|
| **Correct** | — |

| Class / method | Why it is correct |
|---|---|
| `PersonService`, `AccessoryService`, `PromotionService`, `ReturnService`, `WarrantyService` | Sit between `ConsoleMenu` and the repositories, so the UI never depends on persistence. |
| Repositories with `PersistenceException` | Translate low-level `IOException` into the exception the UI handles. |

The services depend on concrete repositories. This was not recorded as a violation: the requirements foresee no change of storage, and the same design is used by every service of the system.

---

## 10. Protected Variations

| Verdict | Violations |
|---|---|
| **Violation** | V-11 (V-06, V-07, V-08 also contribute) |

**Correctly applied**

| Class / method | Why it is correct |
|---|---|
| `AccessoryService.listAllAccessories`, `PromotionService.listAllPromotions`, `WarrantyService.listAllWarranties` | Return copies, so callers cannot change the internal lists. |
| `PromotionService` | A new discount type needs no change in the service. |

**Violations**

| ID | Module | Evidence | Explanation | Consequence | Proposed solution | Other member's code |
|---|---|---|---|---|---|---|
| V-11 | Returns (Req. 3) | `ReturnService.viewAllReturns`, lines 181–182: `return returns;` | The internal list is returned directly, unlike the other services. | Callers can add or remove returns without the service and without persisting. | Return `new ArrayList<>(returns)`. `ConsoleMenu.viewAllReturns` only reads the list, so the output is the same. | No |

---

## 11. Functional defects (not corrected in this requirement)

Fixing these defects would change what the user observes, so they are recorded for a later requirement.

| ID | Location | Evidence | Observed behavior |
|---|---|---|---|
| FD-01 | `ReturnRepository.saveAll`, line 50 | `throw new RuntimeException("Failed to save promotions to " + FILE_PATH, e);` | A failure saving returns reports that promotions could not be saved. |
| FD-02 | `ReturnRepository.fromCsvLine` (lines 98–102) and `loadAll` (lines 142–144) | `fromCsvLine` returns `null` when the sale is not found, and `loadAll` skips it. | Returns whose sale is missing disappear silently, and the next `saveAll` rewrites the file without them. |
| FD-03 | `WarrantyRepository.fromCsvLine`, lines 90–114 | When a product or sale id is not found, `product` or `sale` stays `null` and is passed to the warranty constructor. | The `Warranty` constructor throws `IllegalArgumentException("Product must not be null")`; `Main` prints "Fatal error: Product must not be null" and exits, so the system cannot start. |
| FD-04 | `PersonRepository.loadCustomer` (lines 77–79) and `loadSeller` (132–134) | `line.split(",")` and `if (parts.length == 4)` (`== 5` for sellers). | Records with a comma in a field, or with an empty last field, are ignored silently and lost at the next save. |
| FD-05 | `PersonService.registerCustomer` (line 40) and `registerSeller` (line 56) | Uniqueness is checked only inside `customers` or inside `sellers`. | A customer and a seller can be registered with the same id, although Req. 6 asks that no other person share the identifier. |
| FD-06 | `AccessoryService.findAccessoriesCompatibleWith`, line 133 | `if (a.getAccessoryType().equals(ConsoleId))` | Compares the accessory type with a console id, so it never returns the compatible accessories. |

---

## 12. Problems reported to the Technical Lead

| # | Location | Observation |
|---|---|---|
| R-1 | `WarrantyService.getWarranties`, `setWarranties` (lines 212–223) | Written in A3 by the Technical Lead; they expose the internal list and have no callers. |
| R-2 | `ProductService.restoreStock` | Written by Developer 2 but assigned to the Technical Lead by the requirement; to be confirmed in the consolidation. |

---

## 13. Summary of verdicts

| Pattern | Verdict | Violations |
|---|---|---|
| Information Expert | Violation | V-10 |
| Creator | Correct | — |
| Controller | Correct | — |
| Low Coupling | Violation | V-01 |
| High Cohesion | Violation | V-02, V-03, V-04, V-05 |
| Polymorphism | Violation | V-06, V-07, V-08, V-09 |
| Pure Fabrication | Correct | — |
| Indirection | Correct | — |
| Protected Variations | Violation | V-11 |

| Item | Detail |
|---|---|
| Boundary violations | V-04 and V-05 (`ConsoleMenu`), possibly V-03 (`Main`): Technical Lead. |
| May move to out of scope | V-03 and V-05, if their correction changes the console output. |
| Dependency on Developer 1 | V-08 uses `Product.getCategory()` from Developer 1's V-03; no change to his code. |
| Suggested order | V-01, V-02, V-09, V-11, V-10, V-06, V-07, then V-08 (after Developer 1's V-03), then V-03, V-04, V-05 with the Technical Lead. |

---

## 14. Revision notes

This version replaces the first draft of the report. Changes:

| Change | Reason |
|---|---|
| Removed the five Indirection findings about services depending on concrete repositories | No storage change is foreseeable in the requirements, and the same design is used by every service, so it is not a violation. |
| Removed the findings about static validators (`ProductService`, `PersonService`) | Req. 6 requires the validators to have static methods; the "solution" would contradict the requirement and change Developer 1's code. |
| Removed the findings about `orElse(null)` before `validate…Exists` | This is the pattern Req. 6 asks for; there is no observable risk. |
| Removed the finding about the `WarrantyService` constructor | Its dependencies are exactly the ones required by adjustment A2. |
| Moved `getWarranties`/`setWarranties` to R-1 | Written by the Technical Lead. |
| Removed the product module section | `Product`, `Console`, `VideoGame`, `Accessory`, `ProductRepository` and `ProductValidator` belong to Developer 1; only the use of validators and exceptions is audited here. |
| Merged duplicated findings (customer history; stock restoration; warranty reference resolution) | Same code fragment, recorded once. |
| Corrected the functional defects | Removed the ones that cannot happen (unused methods, warranties always active inside the 30-day return window, seller email handled by `PersonValidator`) and corrected the description of the warranty loading defect. |