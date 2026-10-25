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
 
