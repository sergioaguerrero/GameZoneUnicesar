# GRASP Audit - Developer 2
***
# 1. Scope
**Classes audited:**

2. `Person`, `Customer`, `Seller`, `PersonRepository`, `PersonService`

3. `AccessoryRepository`, `AccessoryService`

4. `PromotionRepository`, `PromotionService`

5. `ReturnRepository`, `ReturnService`

6. `WarrantyRepository`, `WarrantyRepository`

7. `ProductService`
***
# 2. GRASP Audit - Person Module

**Classes audited:** `Person`, `Customer`, `Seller`, `PersonRepository`, `PersonService`
**Module:** Person Module

## Verdict by pattern

| Pattern | Verdict | Note |
|---|---|---|
| Information Expert | Correct | `PersonService` works on the lists it owns and `Customer` manages its own history. |
| Creator | Correct | `Customer` and `Seller` are created where their data is available (`PersonService.register*`, `PersonRepository.load*`). |
| Controller | Not applicable | No class in the module receives system events. |
| Low Coupling | Violation (V-02) | `Customer` and `Sale` reference each other. |
| High Cohesion | Violation (V-03, V-04) | `PersonRepository` removes elements from lists and prints to the console. |
| Polymorphism | Correct | No type-based conditionals in the module. |
| Pure Fabrication | Correct | `PersonRepository` and `PersonService` keep persistence and business rules out of the model. |
| Indirection | Violation (V-05) | `PersonService` depends on the concrete CSV repository. |
| Protected Variations | Violation (V-01) | The purchase history list is exposed. `PersonService` already returns read-only lists. |

---

## Violations

### V-01

- **ID:** V-01
- **Pattern:** Protected Variations
- **Module:** Person Module model `model`
- **Evidence:** `Customer`, constructor and getter:
  ```java
  this.purchaseHistory = purchaseHistory;
  ...
  public List<Sale> getPurchaseHistory() { return purchaseHistory; }
  ```
- **Explanation:** The internal list is stored without copying and returned directly, so callers can change it without using `addPurchaseHistory`.
- **Consequence:** Any future rule for recording sales (for example, no duplicates) can be bypassed by callers.
- **Proposed solution:** Copy the list in the constructor and return `Collections.unmodifiableList(purchaseHistory)` from the getter. Before applying it, check that no caller modifies the returned list.
- **Other developer's code:** To be confirmed (callers of `getPurchaseHistory()` outside the Person Module).

### V-02

- **ID:** V-02
- **Pattern:** Low Coupling
- **Module:** Person Module
- **Evidence:** `Customer`: `private List<Sale> purchaseHistory;`. The reverse reference exists in `Sale` (`getOriginalSale().getCustomer()` in `ReturnService.viewReturnsByCustomer`), and `SaleRepository.loadSales(customers, sellers, products)` needs the customers to rebuild sales.
- **Explanation:** `Customer` holds its sales and each `Sale` holds its customer, so the history duplicates data that the list of sales already contains.
- **Consequence:** The two classes depend on each other and the duplicated relationship can diverge (see FD-01).
- **Proposed solution:** Remove the stored history and obtain it with a query in the sales service (for example `listSalesByCustomer(customerId)`) that returns the same sales (Indirection).
- **Other developer's code:** Yes. Callers of `addPurchaseHistory` and `getPurchaseHistory` (sales flow, console UI). Owners to be confirmed.

### V-03

- **ID:** V-03
- **Pattern:** High Cohesion
- **Module:** Person Module
- **Evidence:** `PersonRepository.deleteCustomer` (`deleteSeller` follows the same structure):
  ```java
  for(Customer c:customers){
      customers.remove(c);
      saveCustomer(customers);
  }
  ```
- **Explanation:** The repository removes elements from a list owned by `PersonService`, which is a business operation inside a persistence class.
- **Consequence:** `PersonService` has no delete operation, so any deletion rule (for example, blocking the removal of a seller with registered sales) has no place to live.
- **Proposed solution:** Move the removal to `PersonService`, which owns `customers` and `sellers` and then calls `repository.saveCustomer(...)` or `saveSeller(...)`. The repository keeps only `save` and `load`. The code is moved as it is; its defects are logged in FD-02.
- **Other developer's code:** To be confirmed (callers of `deleteCustomer` and `deleteSeller`; there are none in `PersonService`).

### V-04

- **ID:** V-04
- **Pattern:** High Cohesion
- **Module:** Person Module in `model`
- **Evidence:** `PersonRepository.saveCustomer` and `loadCustomer` (the same in `saveSeller` and `loadSeller`):
  ```java
  System.out.println("Customers saved successfully in " + customerCSV);
  System.out.println("Data successfully loaded from " + customerCSV);
  ```
- **Explanation:** A persistence class writes user messages to the console, which is a user interface responsibility.
- **Consequence:** Changing how messages are shown (log, graphical UI) requires editing the repository, and the repository cannot be used without console output.
- **Proposed solution:** Remove the `System.out` calls from the repository and print the same messages from the layer that interacts with the user.
- **Other developer's code:** Yes. `ConsoleMenu` and `Main` would print the messages. Owners to be confirmed.

### V-05

- **ID:** V-05
- **Pattern:** Indirection
- **Module:** Person Module
- **Evidence:** `PersonService`:
  ```java
  private final PersonRepository repository;
  public PersonService(PersonRepository repository, ...)
  ```
- **Explanation:** The service depends directly on the concrete class that stores data in CSV files.
- **Consequence:** Changing the storage (for example, to a database) requires editing `PersonService`.
- **Proposed solution:** Create an interface with the four operations (`saveCustomer`, `loadCustomer`, `saveSeller`, `loadSeller`), implement it in `PersonRepository` and make `PersonService` depend on the interface.
- **Other developer's code:** No. Code that passes a `PersonRepository` keeps compiling.

---

## Functional defects (not corrected in this requirement)

### FD-01

- **Evidence:** `Customer(String, String, String, String)` never assigns `purchaseHistory`, and `addPurchaseHistory` runs `purchaseHistory.add(sale);`. `PersonRepository.loadCustomer` builds customers with this constructor.
- **Observed behavior:** `NullPointerException` when adding a sale; `getPurchaseHistory()` returns `null`.
- **Expected behavior:** An empty history that accepts new sales.
- **Not corrected because:** the fix changes what the user observes.

### FD-02

- **Evidence:** `PersonRepository.deleteSeller` and `deleteCustomer` never use the `id` parameter:
  ```java
  for (Seller s:sellers){ sellers.remove(s); saveSeller(sellers); break; }
  ```
  `deleteCustomer` has no `break`.
- **Observed behavior:** Both methods remove the first element of the list whatever the `id`. `deleteCustomer` can also throw `ConcurrentModificationException`, because it removes while iterating.
- **Expected behavior:** Remove only the person whose id matches.
- **Not corrected because:** the fix changes which records are deleted.

### FD-03

- **Evidence:** `PersonRepository.loadCustomer` and `loadSeller` use `line.split(",")` and `if (parts.length == 4)` (`== 5` for sellers).
- **Observed behavior:** Lines with another number of fields are silently ignored. Since `split(",")` drops trailing empty fields, a record whose last field is empty is ignored too. The next `saveCustomer` or `saveSeller` rewrites the file without them, so those records are lost.
- **Expected behavior:** Keep the records or report the invalid line.
- **Not corrected because:** the fix changes which data is loaded.

***

# 3. GRASP Audit — Accessory Service and Persistence

**Classes audited:** `AccessoryRepository`, `AccessoryService`
**Module:** `persistence`,`service`

## Verdict by pattern

| Pattern | Verdict | Note |
|---|---|---|
| Information Expert | Correct | The service works on the list it owns; the repository only converts the data it receives. |
| Creator | Correct | The service creates each accessory from the data it receives; the repository creates them from the CSV data. |
| Controller | Not applicable | Neither class receives system events. |
| Low Coupling | Correct | Both classes depend only on the model and on each other. |
| High Cohesion | Correct | Persistence stays in the repository and business rules in the service; there is no console output. |
| Polymorphism | Violation (V-06, V-07) | Type checks on accessories in the repository and in the stock operations. |
| Pure Fabrication | Correct | The repository and the service keep persistence and business rules out of the model. |
| Indirection | Violation (V-08) | `AccessoryService` depends on the concrete CSV repository. |
| Protected Variations | Correct | `listAllAccessories` returns a copy, so callers cannot modify the internal list. |

---

## Violations

### V-06

- **ID:** V-06
- **Pattern:** Polymorphism
- **Module:** `persistence`
- **Evidence:** `AccessoryRepository.toCsvLine` and `fromCsvLine`:
  ```java
  if (a instanceof Cable cable) { ... }
  if (a instanceof Controller controller) { ... }
  if (a instanceof Memory memory) { ... }
  ...
  if (CABLE_TYPE.equals(type)) { ... }
  ```
- **Explanation:** Saving and loading branch on the concrete accessory type, with `instanceof` in one direction and discriminator comparisons in the other.
- **Consequence:** A new accessory type requires editing `toCsvLine`, `fromCsvLine` and the constants. A missed branch fails at runtime with `IllegalArgumentException`.
- **Proposed solution:** Create one small mapper per accessory type (discriminator, accessory to line, line to accessory) and register them by discriminator in the repository, which then delegates to the mapper instead of testing types. The CSV lines stay identical and the model stays free of persistence code.
- **Other developer's code:** No.

### V-07

- **ID:** V-07
- **Pattern:** Polymorphism
- **Module:** `service`
- **Evidence:** `AccessoryService.updateStock` and `restoreStock` repeat the stock operations of `ProductService`, so callers must choose the service by type. In `ReturnService.restoreStockFor`:
  ```java
  if (product instanceof Accessory) {
      accessoryService.restoreStock(product.getProductId(), quantity);
  } else {
      productService.restoreStock(product.getProductId(), quantity);
  }
  ```
- **Explanation:** Stock of accessories and of other products is managed by two services without a common interface, so every client that touches inventory has to test the product type.
- **Consequence:** Each new client (or new product type) repeats the type check, and a missed check updates the wrong inventory or fails.
- **Proposed solution:** Define a common stock-management interface (`updateStock`, `restoreStock`) implemented by `ProductService` and `AccessoryService`, plus a single entry point that selects the right one, so clients call it without checking the type. Stock results stay the same.
- **Other developer's code:** Yes. `ReturnService.restoreStockFor` and the sales code that decreases stock (`SaleService`). Owners to be confirmed.

### V-08

- **ID:** V-08
- **Pattern:** Indirection
- **Module:** `service`
- **Evidence:** `AccessoryService`:
  ```java
  private final AccessoryRepository accessoryRepository;
  public AccessoryService(AccessoryRepository accessoryRepository, List<Accessory> accessories)
  ```
- **Explanation:** The service depends directly on the concrete class that stores data in a CSV file.
- **Consequence:** Changing the storage (for example, to a database) requires editing `AccessoryService`.
- **Proposed solution:** Create an interface with `saveAll` and `loadAll`, implement it in `AccessoryRepository` and make `AccessoryService` depend on the interface.
- **Other developer's code:** No. Code that passes an `AccessoryRepository` keeps compiling.

---

## Functional defects (not corrected in this requirement)

### FD-04

- **Evidence:** `AccessoryService.findAccessoriesCompatibleWith(String ConsoleId)`:
  ```java
  if (a.getAccessoryType().equals(ConsoleId)){
  ```
- **Observed behavior:** The method compares the accessory type (for example `"CABLE"`) with the console id, so it returns accessories only when the console id equals a type name. It does not check real compatibility.
- **Expected behavior:** Return the accessories compatible with the console that has the given id.
- **Not corrected because:** the fix changes the results the user sees.

# 4. GRASP Audit — Promotion Module

**Classes audited:** `PromotionRepository`, `PromotionService`
**Module:** `persistence`, `service`

## Verdict by pattern

| Pattern | Verdict | Note |
|---|---|---|
| Information Expert | Correct | `Promotion` decides whether it is active and what discount it gives; the service works on the list it owns. |
| Creator | Correct | The service creates each promotion from the data it receives; the repository creates them from the CSV data. |
| Controller | Not applicable | Neither class receives system events. |
| Low Coupling | Correct | Both classes depend only on the model and on each other. |
| High Cohesion | Correct | Persistence stays in the repository and business rules in the service; there is no console output. |
| Polymorphism | Violation (V-09) | The repository branches on the promotion type. The service already calls `isActive` and `calculateDiscount` polymorphically. |
| Pure Fabrication | Correct | The repository and the service keep persistence and business rules out of the model. |
| Indirection | Violation (V-10) | `PromotionService` depends on the concrete CSV repository. |
| Protected Variations | Correct | `listAllPromotions` returns a copy, and a new discount type needs no change in `PromotionService`. |

---

## Violations

### V-09

- **ID:** V-09
- **Pattern:** Polymorphism
- **Module:** `persistence`
- **Evidence:** `PromotionRepository.toCsvLine` and `fromCsvLine`:
  ```java
  if (promotion instanceof BulkPurchaseDiscount bulk) { ... }
  if (promotion instanceof CategoryDiscount category) { ... }
  if (promotion instanceof PercentageDiscount percentage) { ... }
  ...
  if (BULK_TYPE.equals(type)) { ... }
  ```
- **Explanation:** Saving and loading branch on the concrete promotion type, with `instanceof` in one direction and discriminator comparisons in the other.
- **Consequence:** A new promotion type requires editing `toCsvLine`, `fromCsvLine` and the constants, even though `PromotionService` works with any `Promotion`. A missed branch fails at runtime with `IllegalArgumentException`.
- **Proposed solution:** Create one small mapper per promotion type (discriminator, promotion to line, line to promotion) and register them by discriminator in the repository, which then delegates to the mapper instead of testing types. The CSV lines stay identical and the model stays free of persistence code.
- **Other developer's code:** No.

### V-10

- **ID:** V-10
- **Pattern:** Indirection
- **Module:** `service`
- **Evidence:** `PromotionService`:
  ```java
  private final PromotionRepository promotionRepository;
  public PromotionService(PromotionRepository promotionRepository, List<Promotion> promotions)
  ```
- **Explanation:** The service depends directly on the concrete class that stores data in a CSV file.
- **Consequence:** Changing the storage (for example, to a database) requires editing `PromotionService`.
- **Proposed solution:** Create an interface with `saveAll` and `loadAll`, implement it in `PromotionRepository` and make `PromotionService` depend on the interface.
- **Other developer's code:** No. Code that passes a `PromotionRepository` keeps compiling.

---

## Functional defects (not corrected in this requirement)

None found in these classes.

---

# 5. GRASP Audit — Return Service and Persistence

**Classes audited:** `ReturnRepository`, `ReturnService`
**Module:** `persistence`, `service`

## Verdict by pattern

| Pattern | Verdict | Note |
|---|---|---|
| Information Expert | Correct | The service works on the returns list it owns; the repository converts data it receives. |
| Creator | Correct | The service creates each return from parameters; the repository creates them from CSV data. |
| Controller | Not applicable | Neither class receives system events. |
| Low Coupling | Correct | Both classes depend only on the model and required services/repositories. |
| High Cohesion | Violation (V-12) | `ReturnService` prints messages directly to the console. |
| Polymorphism | Violation (V-13) | `ReturnService` checks product types with `instanceof` to restore stock. |
| Pure Fabrication | Correct | The repository and service keep persistence and business rules out of the model. |
| Indirection | Violation (V-14) | `ReturnService` depends on the concrete CSV repository. |
| Protected Variations | Violation (V-11) | `viewAllReturns` returns the internal list directly, allowing external modification. |

---

## Violations

### V-11

- **ID:** V-11
- **Pattern:** Protected Variations
- **Module:** `service`
- **Evidence:** `ReturnService`:
  ```java
  public List<Return> viewAllReturns(){
      return returns;
  }
  ```
- **Explanation:** The internal list of returns is returned directly without copying or wrapping, allowing callers to modify it without using service methods.
- **Consequence:** Business invariants or auditing rules on returns can be bypassed by external callers modifying the list.
- **Proposed solution:** Return an unmodifiable view or a copy of the list (e.g., `Collections.unmodifiableList(returns)` or `new ArrayList<>(returns)`).
- **Other developer's code:** To be confirmed (callers of `viewAllReturns()`).

### V-12

- **ID:** V-12
- **Pattern:** High Cohesion
- **Module:** `service`
- **Evidence:** `ReturnService.registerReturn`:
  ```java
  System.out.println("Devolución registrada exitosamente.");
  ```
- **Explanation:** A business service class performs console printing, mixing business logic with user interface responsibilities.
- **Consequence:** Hardcodes console output, making it difficult to adapt the application to graphical or web interfaces without modifying the service.
- **Proposed solution:** Remove the `System.out.println` from the service layer and handle user feedback in the UI layer.
- **Other developer's code:** Yes. Console UI or controllers expecting printed feedback.

### V-13

- **ID:** V-13
- **Pattern:** Polymorphism
- **Module:** `service`
- **Evidence:** `ReturnService.restoreStockFor`:
  ```java
  if (product instanceof Accessory) {
      accessoryService.restoreStock(product.getProductId(), quantity);
  } else {
      productService.restoreStock(product.getProductId(), quantity);
  }
  ```
- **Explanation:** Stock restoration branches on concrete product types using `instanceof` instead of delegating through polymorphic methods on the `Product` hierarchy.
- **Consequence:** Adding a new product type requires modifying `ReturnService` to add another conditional branch.
- **Proposed solution:** Implement a uniform stock restoration method or interface across product types so the service calls it polymorphically without type checks.
- **Other developer's code:** No.

### V-14

- **ID:** V-14
- **Pattern:** Indirection
- **Module:** `service`
- **Evidence:** `ReturnService`:
  ```java
  private final ReturnRepository returnRepository;
  public ReturnService(ReturnRepository returnRepository, ...)
  ```
- **Explanation:** The service depends directly on the concrete CSV repository implementation.
- **Consequence:** Changing the storage mechanism (e.g., to a relational database) requires modifying `ReturnService`.
- **Proposed solution:** Introduce a repository interface implemented by `ReturnRepository`, and make `ReturnService` depend on the interface.
- **Other developer's code:** No.

---

## Functional defects (not corrected in this requirement)

### FD-05

- **Evidence:** `ReturnRepository.saveAll`:
  ```java
  throw new RuntimeException("Failed to save promotions to " + FILE_PATH, e);
  ```
- **Observed behavior:** The exception message incorrectly mentions "promotions" instead of returns.
- **Expected behavior:** The error message should accurately reflect that it failed to save returns.
- **Not corrected because:** the fix changes error messages / string output.

### FD-06

- **Evidence:** `ReturnRepository.fromCsvLine`:
  ```java
  List<Sale> sales = saleService.listAllSales();
  ```
- **Observed behavior:** During `loadAll()`, for every single CSV line read, `saleService.listAllSales()` is invoked, leading to an $O(N^2)$ performance bottleneck if there are many returns and sales. Furthermore, if a sale ID cannot be found, `fromCsvLine` returns `null` and `loadAll` silently ignores it without warning.
- **Expected behavior:** Load sales once before processing lines or handle missing sales gracefully with proper logging.
- **Not corrected because:** the fix alters performance characteristics or data loading flow.

---

# 6. GRASP Audit - Warranty Module

**Classes audited:** `WarrantyRepository`, `WarrantyService`
**Module:** `persistence`, `service`

## Verdict by pattern

| Pattern | Verdict | Note |
|---|---|---|
| Information Expert | Violation (V-15) | `WarrantyRepository` resolves domain objects (`Product`, `Sale`) while loading CSV data. |
| Creator | Correct | `WarrantyService` handles the creation of `BasicWarranty` and `ExtendedWarranty` instances. |
| Controller | Not applicable | No class in the module receives system events. |
| Low Coupling | Violation (V-16, V-17) | `WarrantyService` directly instantiates external repositories and receives low-level persistence objects in its constructor. |
| High Cohesion | Correct | `WarrantyService` manages warranty business operations and `WarrantyRepository` handles CSV persistence. |
| Polymorphism | Correct | No type-based conditionals in the module for business rules. |
| Pure Fabrication | Correct | `WarrantyRepository` and `WarrantyService` keep persistence and business rules separate from the domain models. |
| Indirection | Violation (V-18) | `WarrantyService` depends directly on the concrete `WarrantyRepository` implementation. |
| Protected Variations | Violation (V-19) | Internal state `warranties` is exposed via a mutable getter and setter. |

---

## Violations

### V-15

- **ID:** V-15
- **Pattern:** Information Expert
- **Module:** Warranty Module (`persistence`)
- **Evidence:** `WarrantyRepository.loadAll` and `fromCsvLine`:
  ```java
  public List<Warranty> loadAll(List<Sale>sales,List<Product>products) {
      ...
      result.add(fromCsvLine(line,sales,products));
      ...
  }

  private Warranty fromCsvLine(String line, List<Sale>sales,List<Product>products) {
      ...
      for (Product p : products){
          if (p.getProductId().equals(productId)){
              product=p;
          }
      }
      for (Sale s : sales){
          if (s.getId().equals(saleId)){
              sale=s;
          }
      }
      ...
  }
  ```
- **Explanation:** The persistence layer is performing object reference resolution by iterating over domain lists (`Sale`, `Product`) to assemble rich domain entities. The repository should only focus on reading and writing primitive/DTO data or raw identifiers, leaving reference resolution to the service layer.
- **Consequence:** `WarrantyRepository` is overly coupled to domain model structures (`Product`, `Sale`) and requires callers to pass full domain collections just to read raw persistence records.
- **Proposed solution:** Modify `WarrantyRepository` to return raw warranty records/DTOs containing string IDs, and perform the lookup and linking of `Product` and `Sale` within `WarrantyService`.
- **Other developer's code:** Yes. Callers of `WarrantyRepository.loadAll` in `WarrantyService`.

### V-16

- **ID:** V-16
- **Pattern:** Low Coupling
- **Module:** Warranty Module (`service`)
- **Evidence:** `WarrantyService` constructor:
  ```java
  public WarrantyService(WarrantyRepository warrantyRepository,
                         SaleRepository saleRepository,
                         ProductService productService,
                         List<Customer> customers,
                         List<Seller> sellers) {
      this.warrantyRepository = warrantyRepository;
      List<Product> products = productService.listAllProducts();
      List<Sale> sales = saleRepository.loadSales(customers, sellers, products);
      this.warranties = warrantyRepository.loadAll(sales, products);
  }
  ```
- **Explanation:** `WarrantyService` directly invokes `SaleRepository` and manages low-level orchestration of `customers` and `sellers` just to rebuild sales and load warranties.
- **Consequence:** `WarrantyService` is tightly coupled to `SaleRepository`, `Customer`, `Seller`, and `ProductService` during initialization, increasing ripple effects when any of those components change.
- **Proposed solution:** Pass an already initialized `SaleService` or high-level sales provider to `WarrantyService` instead of rebuilding sales using low-level repositories and raw lists inside `WarrantyService`'s constructor.
- **Other developer's code:** Yes. Initialization code in `Main` or `ConsoleMenu` that instantiates `WarrantyService`.

### V-17

- **ID:** V-17
- **Pattern:** Low Coupling
- **Module:** Warranty Module (`persistence`)
- **Evidence:** `WarrantyRepository.java`:
  ```java
  import com.gamezone.model.*;
  ```
  `WarrantyRepository` imports and uses `Sale` and `Product` directly.
- **Explanation:** A repository class in the persistence module depends on multiple model entities outside its immediate domain responsibility.
- **Consequence:** Any structural change to `Sale` or `Product` can break `WarrantyRepository` unnecessarily.
- **Proposed solution:** Remove `Sale` and `Product` dependencies from `WarrantyRepository` by persisting and reading basic field strings (e.g., `productId`, `saleId`).
- **Other developer's code:** No.

### V-18

- **ID:** V-18
- **Pattern:** Indirection
- **Module:** Warranty Module (`service`)
- **Evidence:** `WarrantyService`:
  ```java
  private final WarrantyRepository warrantyRepository;
  public WarrantyService(WarrantyRepository warrantyRepository, ...)
  ```
- **Explanation:** `WarrantyService` depends directly on the concrete class `WarrantyRepository` which manages CSV files.
- **Consequence:** Replacing CSV persistence with a database or mock implementation requires modifying `WarrantyService`.
- **Proposed solution:** Introduce an interface `IWarrantyRepository` (or `WarrantyRepository` interface with `WarrantyRepositoryCSV` implementation) and inject the interface into `WarrantyService`.
- **Other developer's code:** No.

### V-19

- **ID:** V-19
- **Pattern:** Protected Variations
- **Module:** Warranty Module (`service`)
- **Evidence:** `WarrantyService.getWarranties` and `setWarranties`:
  ```java
  public List<Warranty> getWarranties() {
      return warranties;
  }

  public void setWarranties(List<Warranty> warranties) {
      this.warranties = warranties;
  }
  ```
- **Explanation:** The internal mutable list `warranties` is directly exposed via a getter, and can be replaced entirely via `setWarranties` without validation.
- **Consequence:** External classes can mutate the internal state of `WarrantyService` without triggering persistence or obeying business rules.
- **Proposed solution:** Return an unmodifiable list or defensive copy from `getWarranties()`, and remove `setWarranties()`.
- **Other developer's code:** To be confirmed (external callers modifying the list directly).

---

## Functional defects (not corrected in this requirement)

### FD-07

- **Evidence:** `WarrantyRepository.fromCsvLine`:
  ```java
  for (Product p : products){
      if (p.getProductId().equals(productId)){
          product=p;
      }
  }
  for (Sale s : sales){
      if (s.getId().equals(saleId)){
          sale=s;
      }
  }
  ```
- **Observed behavior:** If a `productId` or `saleId` stored in the CSV does not exist in `products` or `sales`, `product` or `sale` remains `null` when creating `ExtendedWarranty` or `BasicWarranty`.
- **Expected behavior:** Report an error, log a warning, or throw an exception for missing references during CSV parsing.
- **Not corrected because:** Fixing this behavior alters how invalid or orphaned records are handled during startup.

### FD-08

- **Evidence:** `WarrantyService.cancelWarranties`:
  ```java
  if (warranty instanceof ExtendedWarranty) {
      refundableCost += warranty.getAdditionalCost();
  }
  ```
- **Observed behavior:** Basic warranties are added to `toCancel` list and deleted, but `getAdditionalCost()` is only added for extended warranties. However, if multiple matching warranties exist, all matching ones are removed without checking if they are currently active or already expired.
- **Expected behavior:** Only active warranties should be canceled and refunded.
- **Not corrected because:** Changing cancellation logic modifies observable business output for warranty management.

***
# 7. GRASP Audit - Product Module

**Classes audited:** `Product`, `Console`, `VideoGame`, `Accessory`, `ProductRepository`, `ProductService`, `ProductValidator`  
**Module:** Product Module

## Verdict by pattern

| Pattern | Verdict | Note |
|---|---|---|
| Information Expert | Correct | `ProductService` delegates field validation to `ProductValidator` and model state checks to `Product`. |
| Creator | Correct | `ProductService` creates product sub-entities (`Console`, `VideoGame`, `Accessory`) where required data is available. |
| Controller | Not applicable | No class in the module receives system events. |
| Low Coupling | Violation (V-20) | `ProductService` delegates validation logic to `ProductValidator` using direct static calls rather than encapsulating or abstracting checks. |
| High Cohesion | Correct | `ProductService` manages business operations while `ProductRepository` manages file persistence. |
| Polymorphism | Correct | Product subclasses manage their specific properties without type checking in service methods. |
| Pure Fabrication | Correct | `ProductValidator` and `ProductRepository` separate validation and persistence concerns from domain models. |
| Indirection | Correct | `ProductService` acts as an intermediate coordinator between the UI layer and repository/domain models. |
| Protected Variations | Violation (V-21) | `ProductService.updateStock` relies on `orElse(null)` leading to potential unhandled null propagation before external validation. |

---

## Violations

### V-20

- **ID:** V-20
- **Pattern:** Low Coupling
- **Module:** Product Module (`service`)
- **Evidence:** `ProductService.registerConsole`, `updateStock`, `restoreStock`, and `updateProduct`:
  ```java
  ProductValidator.validateProductData(productId, title, price, stockQuantity);
  ProductValidator.validateProductExists(product, productId);
  ProductValidator.validateStockAvailability(product, quantity);
  ```
- **Explanation:** `ProductService` is tightly coupled to static methods in `ProductValidator`. Calling static validation utilities directly makes it impossible to substitute different validation policies or mock validation logic during isolated unit testing.
- **Consequence:** Modifying or extending validation rules requires changing static dependencies, creating tight coupling between business orchestrations and specific static helper implementations.
- **Proposed solution:** Inject a validation strategy interface into `ProductService` or instance-level `ProductValidator` to decouple concrete validation rules from service execution.
- **Other developer's code:** No.

### V-21

- **ID:** V-21
- **Pattern:** Protected Variations
- **Module:** Product Module (`service`)
- **Evidence:** `ProductService.updateStock` and `restoreStock`:
  ```java
  public void updateStock(String productId, int quantity) {
      Product product = findById(productId).orElse(null);
      ProductValidator.validateProductExists(product, productId);
      ...
  }
  ```
- **Explanation:** `ProductService` unpacks an `Optional<Product>` into a potentially `null` reference (`orElse(null)`) and relies on an external utility (`ProductValidator.validateProductExists`) to throw an exception if `null`.
- **Consequence:** Bypassing `Optional` handling patterns exposes the internal flow to potential `NullPointerException`s if validation checks are refactored, bypassed, or reordered.
- **Proposed solution:** Handle missing entity instances natively using `findById(productId).orElseThrow(...)` before invoking subsequent logic.
- **Other developer's code:** No.

---

## Functional defects (not corrected in this requirement)

### FD-09

- **Evidence:** `ProductService.updateProduct`:
  ```java
  public boolean updateProduct(String productId, String title, double price, int stockQuantity) {
      ProductValidator.validateProductData(productId, title, price, stockQuantity);
      Optional<Product> found = findById(productId);
      if (found.isEmpty()) {
          return false;
      }
      Product product = found.get();
      product.setTitle(title);
      product.setPrice(price);
      product.setStockQuantity(stockQuantity);
      persist();
      return true;
  }
  ```
- **Observed behavior:** When `updateProduct` is called for a non-existent `productId`, `ProductValidator.validateProductData` validates all supplied parameters *before* verifying if the product exists. If invalid parameters are passed for a non-existent product, an `IllegalArgumentException` is thrown instead of returning `false`.
- **Expected behavior:** The method should consistently check product existence or return `false` without throwing validation errors for entity updates that do not target a valid record.
- **Not corrected because:** Modifying exception throwing behavior for non-existent product update requests changes observable API contract responses.

***

