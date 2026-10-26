# Consolidated GRASP Audit — GameZone Unicesar

**Project:** GameZone Unicesar  
**Audited branch / commit:** `develop` @ `cdf4438` (*Merge pull request #46 from sergioaguerrero/feature/exception-handling*)  
**Consolidation Author:** Sergio Guerrero (Technical Lead)  
**Contributors:** Isaac David Mattos (Developer 1), Jhonatan Galindo (Developer 2), Sergio Guerrero (Technical Lead)

---

## 1. Audit Coverage Matrix

Every class in the codebase (`com.gamezone.*`) has been comprehensively audited by its designated owner according to the responsibility distribution defined in Section 4 of the Requirement 7 specification, `TEAM.md`, and the Git commit history.

| Full Class Name | Architectural Layer | Audited By | Individual Report Reference |
|---|---|---|---|
| `com.gamezone.Main` | Application Entry / Bootstrap | Technical Lead | `leader-audit.md` |
| `com.gamezone.model.Product` | Model (Abstract) | Developer 1 (stock restore by Tech Lead) | `developer1-audit.md` |
| `com.gamezone.model.VideoGame` | Model | Developer 1 | `developer1-audit.md` |
| `com.gamezone.model.Console` | Model | Developer 1 | `developer1-audit.md` |
| `com.gamezone.model.Accessory` | Model (Abstract) | Developer 1 | `developer1-audit.md` |
| `com.gamezone.model.Controller` | Model | Developer 1 | `developer1-audit.md` |
| `com.gamezone.model.Cable` | Model | Developer 1 | `developer1-audit.md` |
| `com.gamezone.model.Memory` | Model | Developer 1 | `developer1-audit.md` |
| `com.gamezone.model.Promotion` | Model (Abstract) | Developer 1 | `developer1-audit.md` |
| `com.gamezone.model.PercentageDiscount` | Model | Developer 1 | `developer1-audit.md` |
| `com.gamezone.model.CategoryDiscount` | Model | Developer 1 | `developer1-audit.md` |
| `com.gamezone.model.BulkPurchaseDiscount` | Model | Developer 1 | `developer1-audit.md` |
| `com.gamezone.model.Warranty` | Model (Abstract) | Developer 1 | `developer1-audit.md` |
| `com.gamezone.model.BasicWarranty` | Model | Developer 1 | `developer1-audit.md` |
| `com.gamezone.model.ExtendedWarranty` | Model | Developer 1 | `developer1-audit.md` |
| `com.gamezone.model.Return` | Model | Developer 1 (refund calc by Dev 2) | `developer1-audit.md` / `developer2-audit.md` |
| `com.gamezone.model.Sale` | Model | Technical Lead (`canBeReturned` by Dev 1) | `leader-audit.md` / `developer1-audit.md` |
| `com.gamezone.model.SaleItem` | Model | Technical Lead | `leader-audit.md` |
| `com.gamezone.model.Person` | Model (Abstract) | Developer 2 | `developer2-audit.md` |
| `com.gamezone.model.Customer` | Model | Developer 2 | `developer2-audit.md` |
| `com.gamezone.model.Seller` | Model | Developer 2 | `developer2-audit.md` |
| `com.gamezone.persistence.ProductRepository` | Persistence | Developer 1 | `developer1-audit.md` |
| `com.gamezone.persistence.AccessoryRepository` | Persistence | Developer 2 | `developer2-audit.md` |
| `com.gamezone.persistence.PromotionRepository` | Persistence | Developer 2 | `developer2-audit.md` |
| `com.gamezone.persistence.PersonRepository` | Persistence | Developer 2 | `developer2-audit.md` |
| `com.gamezone.persistence.ReturnRepository` | Persistence | Developer 2 | `developer2-audit.md` |
| `com.gamezone.persistence.WarrantyRepository` | Persistence | Developer 2 | `developer2-audit.md` |
| `com.gamezone.persistence.SaleRepository` | Persistence | Technical Lead | `leader-audit.md` |
| `com.gamezone.service.ProductService` | Service | Developer 1 (`restoreStock` by Tech Lead) | `developer1-audit.md` / `leader-audit.md` |
| `com.gamezone.service.AccessoryService` | Service | Developer 2 | `developer2-audit.md` |
| `com.gamezone.service.PromotionService` | Service | Developer 2 | `developer2-audit.md` |
| `com.gamezone.service.PersonService` | Service | Developer 2 | `developer2-audit.md` |
| `com.gamezone.service.ReturnService` | Service | Developer 2 | `developer2-audit.md` |
| `com.gamezone.service.WarrantyService` | Service | Developer 2 (`get/setWarranties` by Tech Lead) | `developer2-audit.md` / `leader-audit.md` |
| `com.gamezone.service.SaleService` | Service | Technical Lead | `leader-audit.md` |
| `com.gamezone.ui.ConsoleMenu` | UI Layer | Technical Lead | `leader-audit.md` |
| `com.gamezone.exceptions.*` | Exceptions | Developer 1 | `developer1-audit.md` |
| `com.gamezone.validation.*` | Validation | Developer 1 | `developer1-audit.md` |

*Coverage Check:* 100% of codebase classes are accounted for; no duplicated scope or unassigned classes exist.

---

## 2. Team Verdicts per GRASP Pattern

| GRASP Pattern | Team Final Verdict | Summary of Findings |
|---|---|---|
| **Information Expert** | **Violation** | Business formulas and cross-entity aggregations executed by non-holders (`Return`, `BulkPurchaseDiscount`, `WarrantyRepository`). |
| **Creator** | **Correct** | Factories and constructor instantiations cleanly placed in services, repositories (`fromCsvLine`), and `Main`. |
| **Controller** | **Correct** | Service classes act as use-case controllers; `ConsoleMenu` delegates UI events without embedding logic. |
| **Low Coupling** | **Violation** | Bi-directional coupling in `Customer` (unused history); direct exposure in `WarrantyService`; product category dependencies. |
| **High Cohesion** | **Violation** | Persistence repositories printing console messages; services executing console output and swallowing domain exceptions. |
| **Polymorphism** | **Violation** | Excessive procedural `instanceof` and type discriminator branching for product categories, CSV serialization, and warranties. |
| **Pure Fabrication** | **Correct** | Repositories, validators, and service layers properly created as non-domain fabrications to preserve cohesion. |
| **Indirection** | **Correct** | Layered separation (`ui -> service -> persistence -> model`) respected throughout the application. |
| **Protected Variations** | **Violation** | Magic numbers in return windows, duplicated percentage validations across promo types, gender heuristics in exceptions, and hardcoded paths. |

---

## 3. Definitive Consolidated List of Violations

| ID | Pattern | Source Report & ID | Module | Evidence (Code / Lines) | Explanation | Consequence | Agreed Solution | Assigned Member |
|---|---|---|---|---|---|---|---|---|
| **V-01** | Information Expert | `developer1-audit` V-01 | Returns (Req. 3) | `Return.calculateItemRefund` lines 167–170: `originalSale.getDiscountAmount() / subtotal` | `Return` combines subtotal and discount owned by `Sale` to calculate the discount ratio. | Duplication of financial logic; foreign calculation. | Add `Sale.getDiscountRatio()` and delegate to it. | Isaac Mattos (Dev 1) & Sergio Guerrero (Tech Lead) |
| **V-02** | Information Expert | `developer1-audit` V-02 | Promotions (Req. 2) | `BulkPurchaseDiscount.calculateDiscount` lines 90–93: loops over `sale.getItems()` summing `item.getQuantity()` | Total unit quantity of a sale is calculated by `BulkPurchaseDiscount` instead of `Sale`. | Promotion is unnecessarily coupled to `SaleItem` structure. | Add `Sale.getTotalQuantity()` and call it from discount. | Isaac Mattos (Dev 1) & Sergio Guerrero (Tech Lead) |
| **V-03** | Polymorphism | `developer1-audit` V-03 | Promotions (Req. 2) | `CategoryDiscount.belongsToTargetCategory` lines 129–137: `product instanceof VideoGame` / `Console` / `Accessory` | Procedural type checks to determine product category instead of polymorphic query. | Adding a new product category requires editing discount classes. | Introduce abstract `Product.getCategory()` implemented by all product subclasses. | Isaac Mattos (Dev 1) |
| **V-04** | Polymorphism | `developer1-audit` V-04 | Products (Workshop 1) | `ProductRepository.toCsvLine` (86–109) & `fromCsvLine` (121–137): `instanceof` and type discriminators | Formatting CSV depends on conditional type checks. | New product types break persistence unless mapper methods are manually updated. | Introduce `ProductCsvMapper` strategy registry selected by category. | Isaac Mattos (Dev 1) |
| **V-05** | Protected Variations | `developer1-audit` V-05 | Promotions (Req. 2) | `setPercentage` validation duplicated in `PercentageDiscount` (46–51), `CategoryDiscount` (68–73), `BulkPurchaseDiscount` (73–78) | Redundant validation rule duplicated in three distinct classes. | Range rule changes require modifications across multiple files. | Extract intermediate abstract class `PercentageBasedPromotion`. | Isaac Mattos (Dev 1) |
| **V-06** | Protected Variations | `developer1-audit` V-06 | Returns (Req. 3) | `Sale.canBeReturned` lines 224–227: `daysSinceSale <= 30` with `LocalDate.now()` | Hardcoded magic number and rigid reliance on current clock date. | Testing and policy modification is inflexible. | Define constant `RETURN_PERIOD_DAYS` and overloaded `canBeReturned(LocalDate)`. | Isaac Mattos (Dev 1) |
| **V-07** | Protected Variations | `developer1-audit` V-07 | Exceptions (Req. 6) | `ResourceNotFoundException.buildMessage` lines 55–62: `type.endsWith("a")` | Spanish grammatical gender inferred by string suffix heuristic. | Misidentifies non-standard nouns (e.g. "promoción"). | Introduce `ResourceType` enum declaring gender article/participle. | Isaac Mattos (Dev 1) |
| **V-08** | Low Coupling | `developer2-audit` V-01 | Persons (Workshop 1) | `Customer`: `purchaseHistory` list (line 11), constructor (21–25), getter/adder (59–69) | Unused two-way coupling between `Customer` and `Sale`. | Unused dead state; potential `NullPointerException`. | Remove unused `purchaseHistory` field and methods. | Jhonatan Galindo (Dev 2) |
| **V-09** | High Cohesion | `developer2-audit` V-02 | Persons (Workshop 1) | `PersonRepository.deleteSeller` (160–166) & `deleteCustomer` (175–180) | Deletion business operations located within persistence class. | Bloats repository with dead business logic. | Remove unused deletion methods from repository. | Jhonatan Galindo (Dev 2) |
| **V-10** | High Cohesion | `developer2-audit` V-04 | Returns (Req. 3) | `ReturnService.registerReturn` line 155: `System.out.println("Devolución registrada exitosamente.");` | Service class directly invokes standard console output. | Service couples to console environment. | Remove print from service; delegate confirmation display to `ConsoleMenu`. | Jhonatan Galindo (Dev 2) & Sergio Guerrero (Tech Lead) |
| **V-11** | Polymorphism | `developer2-audit` V-06 | Accessories (Req. 1) | `AccessoryRepository.toCsvLine` (80–114) & `fromCsvLine` (126–146) | Conditional `instanceof` blocks for CSV serialization of accessories. | High maintenance overhead when new accessories are added. | Implement CSV mapper strategies for accessories. | Jhonatan Galindo (Dev 2) |
| **V-12** | Polymorphism | `developer2-audit` V-07 | Promotions (Req. 2) | `PromotionRepository.toCsvLine` (77–110) & `fromCsvLine` (122–142) | Conditional `instanceof` blocks for CSV serialization of promotions. | New promotions require editing persistence class. | Implement CSV mapper strategies for promotions. | Jhonatan Galindo (Dev 2) |
| **V-13** | Polymorphism | `developer2-audit` V-08 | Returns (Req. 3) | `ReturnService.restoreStockFor` (168–174) & `ReturnRepository.resolveProduct` (113–120): `if (product instanceof Accessory)` | Direct type checking to determine stock restoration target. | Fragile type inspection across modules. | Refactor using `Product.getCategory()` and unified lookup. | Jhonatan Galindo (Dev 2) |
| **V-14** | Polymorphism | `developer2-audit` V-09 | Warranties (Req. 4) | `WarrantyService.cancelWarranties` line 157: `if (warranty instanceof ExtendedWarranty)` | Redundant conditional; `BasicWarranty.getAdditionalCost()` already returns 0. | Defeats polymorphic intent of base warranty contract. | Directly accumulate `warranty.getAdditionalCost()` without type check. | Jhonatan Galindo (Dev 2) |
| **V-15** | Information Expert | `developer2-audit` V-10 | Warranties (Req. 4) | `WarrantyRepository.loadAll` line 34 & `fromCsvLine` lines 90–114 | Repository searches and resolves `Product` and `Sale` domain entities. | Repository unnecessarily coupled to domain lists. | Repository returns raw record; service resolves references. | Jhonatan Galindo (Dev 2) |
| **V-16** | Protected Variations | `developer2-audit` V-11 | Returns (Req. 3) | `ReturnService.viewAllReturns` lines 181–182: `return returns;` | Exposes internal mutable collection reference. | External callers can mutate internal state. | Return defensive copy `new ArrayList<>(returns)`. | Jhonatan Galindo (Dev 2) |
| **V-17** | Low Coupling | `leader-audit` V-02 | Warranties (Req. 4) | `WarrantyService.getWarranties` (212–215) & `setWarranties` (220–223) | Unused getters/setters exposing internal warranty collection. | Exposes private mutable state unnecessarily. | Remove `getWarranties` and `setWarranties`. | Sergio Guerrero (Tech Lead) |
| **V-18** | High Cohesion | `leader-audit` V-03 | Sales (Workshop 1) | `SaleRepository.saveSales` line 56 & line 140 | Repository prints status messages directly to console. | Violates separation of presentation and persistence. | Remove `System.out.println` from repository. | Sergio Guerrero (Tech Lead) |
| **V-19** | High Cohesion | `leader-audit` V-04 | Sales (Workshop 1) | `SaleService.registerSale` line 271 & lines 186, 192, 202, 222, 236 | Service prints messages and swallows domain validation exceptions. | Suppresses exceptions from reaching `ConsoleMenu` handlers. | Remove console prints; propagate exceptions to `ConsoleMenu`. | Sergio Guerrero (Tech Lead) |
| **V-20** | Polymorphism | `leader-audit` V-05 | Sales / UI | `SaleService.registerSale` (213–219, 260–264), `applyWarranties` (312–318), `ConsoleMenu` (577–580) | Procedural `instanceof` blocks to detect `Console` or `Accessory`. | Modifying product categories requires updating sales and UI code. | Refactor using `Product.getCategory()`. | Sergio Guerrero (Tech Lead) |
| **V-21** | Protected Variations | `leader-audit` V-06 | Sales (Workshop 1) | `SaleRepository.saleCSV` line 17 | Hardcoded storage path constant. | Prevents configurable storage or testing paths. | Support parameterized path constructor alongside default. | Sergio Guerrero (Tech Lead) |

---

## 4. Boundary Findings (Cross-Member Coordination)

The consolidation resolved several intersections where changes in one member's component require coordination with another:

1. **`Sale` Financial & Unit Metrics (`V-01`, `V-02`):**
   - *Conflict:* Developer 1 needed discount ratio and item counts; calculating them inside `Return` and `BulkPurchaseDiscount` breached Information Expert.
   - *Resolution:* Sergio Guerrero (Technical Lead) implements `Sale.getDiscountRatio()` and `Sale.getTotalQuantity()`. Isaac Mattos (Developer 1) updates `Return` and `BulkPurchaseDiscount` to consume these methods.
2. **Polymorphic Product Categorization (`V-03`, `V-13`, `V-20`):**
   - *Conflict:* `CategoryDiscount` (Dev 1), `ReturnService` (Dev 2), and `SaleService`/`ConsoleMenu` (Tech Lead) all relied on `instanceof VideoGame / Console / Accessory`.
   - *Resolution:* Isaac Mattos (Developer 1) introduces abstract `Product.getCategory()` returning a standard category identifier. Sergio Guerrero and Jhonatan Galindo consume this method to eliminate `instanceof` branches in their respective services.
3. **Console Output Separation & Exception Propagation (`V-10`, `V-19`):**
   - *Conflict:* `ReturnService` and `SaleService` printed success/error strings and suppressed exceptions.
   - *Resolution:* Respective service owners remove console prints and allow typed exceptions (`InvalidDataException`, `BusinessRuleException`) to propagate cleanly to `ConsoleMenu`, where Sergio Guerrero (Technical Lead) ensures friendly Spanish messaging.

---

## 5. Out-of-Scope Findings (Functional Defects)

The following defects were discovered during code analysis. Because fixing them would alter observable system behavior, change CSV schemas, or affect business specifications, they are strictly deferred to subsequent requirements in accordance with Section 2 of the Requirement specification:

| Defect ID | Discovered In | Code Location | Observed Defect & Impact | Reason Deferred |
|---|---|---|---|---|
| **FD-01** | `developer2-audit` | `ReturnRepository.saveAll` line 50 | Error message reads: `"Failed to save promotions to..."` instead of returns. | Changing error string alters user/log output. |
| **FD-02** | `developer2-audit` | `ReturnRepository.fromCsvLine` (98–102) & `loadAll` (142–144) | Returns referencing non-existent sales return `null` and are silently omitted. | Data integrity repair would alter loaded return count. |
| **FD-03** | `developer2-audit` | `WarrantyRepository.fromCsvLine` lines 90–114 | If sale or product is missing, passes `null` to constructor, causing fatal exception on startup. | Changing error handling changes application startup behavior. |
| **FD-04** | `developer2-audit` | `PersonRepository.loadCustomer` & `loadSeller` | Records with comma in fields or missing trailing fields are silently skipped. | Modifying parser changes CSV persistence parsing rules. |
| **FD-05** | `developer2-audit` | `PersonService.registerCustomer` & `registerSeller` | ID uniqueness is evaluated only within customer list or seller list independently, not globally. | Business rule alteration. |
| **FD-06** | `developer1-audit` / `developer2-audit` | `AccessoryService.findAccessoriesCompatibleWith` line 133 | Compares `a.getAccessoryType()` against `consoleId`, returning an empty list always. | Fixing logic alters menu query output results. |
| **FD-07** | `developer1-audit` | `AccessoryRepository` | Compatible console IDs are not written into `accessories.csv`. | Alters CSV storage schema format. |
| **FD-08** | `developer2-audit` | `PersonRepository.saveCustomers` lines 54, 88, 109, 144 | Repository prints `"Customers saved successfully..."` on console. | Kept as out of scope to avoid changing console output observed by users. |


