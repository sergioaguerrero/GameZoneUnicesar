# GRASP Audit — Developer 1
 
**Author:** Isaac David Mattos (Developer 1)
**Audited branch / commit:** `develop` @ `cdf4438` (*Merge pull request #46 from sergioaguerrero/feature/exception-handling*), the starting point of `feature/grasp-audit`

## 1. Scope
 
The scope follows the responsibility table of the requirement and the authorship recorded in `TEAM.md` and in the commit history (`git blame` on `develop`).
 
### 1.1 Classes and methods audited 
 
| Module (requirement) | Class | Methods / members audited |
|---|---|---|
| Products (Workshop 1) | `model.Product` | all members except `increaseStock` |
| Products (Workshop 1) | `model.VideoGame` | all members |
| Products (Workshop 1) | `model.Console` | all members |
| Products (Workshop 1) | `persistence.ProductRepository` | `saveAll`, `loadAll`, `toCsvLine`, `fromCsvLine` (except the `PersistenceException` handling) |
| Products (Workshop 1) | `service.ProductService` | all members except `restoreStock` and the calls to `ProductValidator` |
| Accessories (Req. 1) | `model.Accessory` | all members |
| Accessories (Req. 1) | `model.Controller`, `model.Cable`, `model.Memory` | all members |
| Promotions (Req. 2) | `model.Promotion` | all members |
| Promotions (Req. 2) | `model.PercentageDiscount`, `model.CategoryDiscount`, `model.BulkPurchaseDiscount` | all members |
| Returns (Req. 3) | `model.Return` | all members except `calculateRefundAmount(double)` |
| Returns (Req. 3) | `model.Sale` | `canBeReturned()` only |
| Warranties (Req. 4) | `model.Warranty`, `model.BasicWarranty`, `model.ExtendedWarranty` | all members |
| Exceptions and validation (Req. 6) | `exceptions.GameZoneException`, `ResourceNotFoundException`, `BusinessRuleException`, `InvalidDataException`, `PersistenceException` | all members |
| Exceptions and validation (Req. 6) | `validation.ProductValidator`, `PersonValidator`, `SaleValidator` | all members |
 
### 1.2 Members excluded from this audit (written by another member)
 
| Member | Author according to `git blame` | Reported to |
|---|---|---|
| `Product.increaseStock(int)` | Jhonatan Galindo (`0926aab`) | Technical Lead, for assignment in the consolidation |
| `ProductService.restoreStock(String, int)` | Jhonatan Galindo (`6a20f94`) | Technical Lead (the requirement assigns it to the Technical Lead; history shows Developer 2) |
| `Return.calculateRefundAmount(double)` and the `final` modifiers of `Return` fields | Jhonatan Galindo (`310f1eb`, A7) | Developer 2 |
| Calls to `ProductValidator` in `ProductService` and the `PersistenceException` handling in `ProductRepository` | Jhonatan Galindo (`7a1008b`, `d2d6af5`, Req. 6) | Developer 2 (use of exceptions and validators in services and repositories) |
 
---
 
## 2. Information Expert
 
| Verdict | Violations |
|---|---|
| **Violation** | V-01, V-02 |
 
**Correctly applied**
 
| Class / method | Why it is correct |
|---|---|
| `Product.hasEnoughStock`, `decreaseStock` | The product owns its stock. |
| `Promotion.isActive`, `Warranty.isActive` | Each object owns its start and end dates. |
| `ExtendedWarranty.getAdditionalCost` | The warranty owns its cost rule. |
| `Sale.canBeReturned` | The sale owns its date. |
 
**Violations**
 
| ID | Module | Evidence | Explanation | Consequence | Proposed solution | Other member's code |
|---|---|---|---|---|---|---|
| V-01 | Returns (Req. 3), A5 | `Return.calculateItemRefund`, lines 167–170: `originalSale.getDiscountAmount() / subtotal` | Subtotal and discount belong to `Sale`, but `Return` combines them. | Any new feature needing the ratio will copy the formula. | Add `Sale.getDiscountRatio()` and call it from `Return`. | Yes: Technical Lead (`Sale`) |
| V-02 | Promotions (Req. 2) | `BulkPurchaseDiscount.calculateDiscount`, lines 90–93: loop over `sale.getItems()` adding `getQuantity()` | The total units of a sale is data owned by `Sale`. | The promotion depends on the internal item list of `Sale`. | Add `Sale.getTotalQuantity()` and use it. | Yes: Technical Lead (`Sale`) |
 
---

## 3. Creator
 
| Verdict | Violations |
|---|---|
| **Correct** | — |
 
| Class / method | Why it is correct |
|---|---|
| `ProductService.registerVideoGame`, `registerConsole` | The service holds the `products` list and the initialization data. |
| `ProductRepository.fromCsvLine` | The repository holds the CSV data used to build each product. |
 
---

## 4. Controller
 
| Verdict | Violations |
|---|---|
| **Correct** | — |
 
| Class / method | Why it is correct |
|---|---|
| `ProductService` | Use-case controller: `ConsoleMenu` delegates register, list, update and delete to it. |
| Model, exceptions and validators | Not applicable: they receive no system events. |
 
---

## 5. Low Coupling
 
| Verdict | Violations |
|---|---|
| **Violation** | Recorded in V-03 |
 
| Class / method | Observation |
|---|---|
| `ProductService` | Correct: depends only on `ProductRepository` and the model. |
| Validators | Correct: depend only on `model` and `exceptions`. |
| `CategoryDiscount` | Violation: depends on `VideoGame`, `Console` and `Accessory` only to classify products (see V-03). |
 
---

## 6. High Cohesion
 
| Verdict | Violations |
|---|---|
| **Correct** | — |
 
| Class / method | Why it is correct |
|---|---|
| `Product` and subclasses | Hold product data and stock rules only. |
| `ProductRepository` | Handles only CSV persistence. |
| Promotion and warranty subclasses | Each one holds a single rule. |
| `Return.generateReturnReceipt`, `Warranty.generateWarrantyCertificate` | Assigned to these classes by Req. 3, Req. 4 and A5; they use only their own data. |
 
---

## 7. Polymorphism
 
| Verdict | Violations |
|---|---|
| **Violation** | V-03, V-04 |
 
**Correctly applied**
 
| Class / method | Why it is correct |
|---|---|
| `Product.getFullDescription` | Abstract, implemented by each product. |
| `Promotion.calculateDiscount` | Abstract, implemented by each discount. |
| `Warranty.getDurationInMonths`, `getAdditionalCost` | Abstract, implemented by each warranty. |
| `Accessory.getAccessoryType` | Abstract, implemented by each accessory. |
 
**Violations**
 
| ID | Module | Evidence | Explanation | Consequence | Proposed solution | Other member's code |
|---|---|---|---|---|---|---|
| V-03 | Promotions (Req. 2), A1 | `CategoryDiscount.belongsToTargetCategory`, lines 129–137: `product instanceof VideoGame` / `Console` / `Accessory` | The category depends on the product type but is resolved with conditionals. Also affects Low Coupling and Protected Variations. | A new product type requires editing `CategoryDiscount` (already happened in A1). | Abstract `Product.getCategory()` implemented in `VideoGame`, `Console` and `Accessory`. | No |
| V-04 | Products (Workshop 1) | `ProductRepository.toCsvLine`, lines 86–109 (`instanceof`); `fromCsvLine`, lines 121–137 (`VIDEOGAME_TYPE.equals(type)`) | The CSV format varies by type but is chosen with conditionals. | A new product type requires editing both methods; a miss fails only at runtime. | Interface `ProductCsvMapper` with one mapper per type, selected by `getCategory()`. Depends on V-03. | No |
 
---


