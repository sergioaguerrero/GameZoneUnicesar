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


