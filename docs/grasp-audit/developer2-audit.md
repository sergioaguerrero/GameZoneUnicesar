# GRASP Audit - Developer 2
***
# 1. Scope
**Classes audited:**`Person`, `Customer`, `Seller`, `PersonRepository`, `PersonService`
***
# 2. Person Module

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
- **Module:** Person Module — requirement: *[to be completed]*
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
- **Module:** Person Module — requirement: *[to be completed]*
- **Evidence:** `Customer`: `private List<Sale> purchaseHistory;`. The reverse reference exists in `Sale` (`getOriginalSale().getCustomer()` in `ReturnService.viewReturnsByCustomer`), and `SaleRepository.loadSales(customers, sellers, products)` needs the customers to rebuild sales.
- **Explanation:** `Customer` holds its sales and each `Sale` holds its customer, so the history duplicates data that the list of sales already contains.
- **Consequence:** The two classes depend on each other and the duplicated relationship can diverge (see FD-01).
- **Proposed solution:** Remove the stored history and obtain it with a query in the sales service (for example `listSalesByCustomer(customerId)`) that returns the same sales (Indirection).
- **Other developer's code:** Yes. Callers of `addPurchaseHistory` and `getPurchaseHistory` (sales flow, console UI). Owners to be confirmed.

### V-03

- **ID:** V-03
- **Pattern:** High Cohesion
- **Module:** Person Module — requirement: *[to be completed]*
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
- **Module:** Person Module — requirement: *[to be completed]*
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
- **Module:** Person Module — requirement: *[to be completed]*
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