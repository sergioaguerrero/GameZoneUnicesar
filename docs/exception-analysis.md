# Exception and Validation Analysis - GameZone Unicesar

This document provides technical analysis and architecture justifications for the design and implementation of the custom exception hierarchy (`com.gamezone.exceptions`) and the validation layer (`com.gamezone.validation`) in the GameZone Unicesar system.

---

## 1. Exception Hierarchy Strategy: Checked vs. Unchecked Exceptions

**Question:** The root class `GameZoneException` can be declared as checked (`extends Exception`) or unchecked (`extends RuntimeException`). Which of the two options is more suitable for this project and why? What consequence does this decision have on the method signatures of services and repositories?

### Technical Justification

`GameZoneException` is declared as an **unchecked exception** by extending `java.lang.RuntimeException`. This design decision is the most appropriate for the GameZone Unicesar architecture for several technical reasons:

1. **API Cleanliness and Decoupling:**
   Declaring `GameZoneException` as a checked exception would force every service interface, repository method, and domain operation to declare `throws GameZoneException` (or specific checked subtypes) in their method signatures. This introduces pervasive signature pollution across all architectural layers, coupling consumers to specific implementation failure modes.

2. **Layered Architecture Boundaries and Separation of Concerns:**
   In modern layered architectures, validation and business rule violations represent unrecoverable preconditions or contract failures at the local call site. The intermediate layers (services, orchestrators) cannot reasonably "fix" an invalid price, an empty customer name, or insufficient stock at the moment it is detected. These errors must propagate seamlessly up to the presentation boundary (`ConsoleMenu`), where the user interaction loop is equipped to notify the end user. Unchecked exceptions allow faults to bubble up automatically without requiring boilerplate forwarding code at every intermediate layer.

3. **Encapsulation of Infrastructure and Persistence Failures:**
   Low-level I/O operations in the persistence layer (e.g., `java.io.IOException` during CSV file reading or writing) are checked exceptions in the Java standard library. By catching checked `IOException` inside `ProductRepository`, `PersonRepository`, and `SaleRepository` and wrapping them into an unchecked `PersistenceException` (which extends `GameZoneException`), the persistence layer preserves the original root cause via `initCause(cause)` while preventing low-level file I/O exceptions from leaking into domain service contracts.

4. **Alignment with Modern Java Best Practices:**
   Contemporary enterprise Java standards (such as the Spring Framework, Jakarta EE, and modern JDK APIs) strongly favor unchecked exceptions over checked exceptions. Checked exceptions often lead to empty catch blocks or arbitrary rethrowing, whereas unchecked exceptions encourage intentional, centralized error handling at architectural boundaries.

### Impact on Method Signatures

* **Services and Repositories:** Method signatures remain clean, declarative, and focused exclusively on business parameters and return types (e.g., `public VideoGame registerVideoGame(String id, String title, double price, int stock, String platform, String genre, String ageRating)`). There is no need for cluttering `throws` clauses.
* **Documentation via JavaDoc:** Even though methods do not declare checked `throws` clauses, contract transparency is preserved through standard `@throws` JavaDoc annotations (e.g., `@throws InvalidDataException`, `@throws BusinessRuleException`), allowing developers and IDE tooling to inspect possible failure modes without compiler-enforced ceremony.

---

## 2. Advantages of a Common Custom Exception Hierarchy vs. Generic Exceptions

**Question:** Why is it preferable to have a custom exception hierarchy with a common root class (`GameZoneException`) instead of throwing generic `IllegalArgumentException` in all cases? What design principles are applied?

### Technical Justification

Relying on generic exceptions like `IllegalArgumentException` introduces severe design and operational limitations:

1. **Semantic Ambiguity:**
   `IllegalArgumentException` conveys only that an argument passed to a method was improper. It cannot express whether the failure was caused by a malformed string format, a resource not existing in persistent storage, a breach of a domain invariant (such as stock depletion or a sale with zero items), or a failure in disk I/O.

2. **Differentiated User Experience and Error Handling:**
   A user interface cannot differentiate between distinct failure categories when all errors arrive as `IllegalArgumentException`. The UI would be forced to parse error message strings—a brittle and error-prone anti-pattern—to decide whether to prompt the user for input correction or alert them to a system failure.

3. **Domain Context and Structured Metadata:**
   Generic exceptions only hold a plain text message. Specialized custom exceptions can carry strongly-typed metadata:
   * `ResourceNotFoundException` encapsulates `resourceType` and `resourceId`.
   * `InvalidDataException` encapsulates `fieldName`.
   * `GameZoneException` encapsulates an `errorCode` identifier (e.g., `RESOURCE_NOT_FOUND`, `BUSINESS_RULE`, `INVALID_DATA`, `PERSISTENCE`).
   * `PersistenceException` encapsulates technical cause chaining (`Throwable cause`).

### Design Principles Applied

* **Single Responsibility Principle (SRP):** Each exception class in the hierarchy represents one specific category of system failure. `ResourceNotFoundException` handles missing entities, `BusinessRuleException` handles business invariant breaches, `InvalidDataException` handles format and syntax validation, and `PersistenceException` handles storage I/O failures.
* **Open/Closed Principle (OCP):** New domain-specific exceptions (e.g., `WarrantyExpiredException`, `PromotionConflictException`) can be introduced into the hierarchy under `GameZoneException` without modifying existing catch blocks or altering service contracts.
* **Liskov Substitution Principle (LSP):** Any specialized exception can be caught and handled polymorphically as `GameZoneException` at global boundaries while preserving the integrity of its specialized behavior and metadata.
* **Information Hiding / Abstraction:** Implementation and infrastructure details (such as filesystem access failures) are abstracted into `PersistenceException`, hiding raw technical internals while providing clear, sanitized feedback to end users.
* **Domain-Driven Ubiquitous Language:** The naming of custom exceptions directly models the domain problem space, making code self-documenting and maintainable.

---

## 3. Architecture and Boundaries of the Validation Layer (`com.gamezone.validation`)

**Question:** Validators make up a new layer (`com.gamezone.validation`). At what point in the execution flow are they invoked and why is this location consistent with the layered architecture? Can validators access the persistence package? Justify.

### Point of Invocation in the Flow

Validators are invoked **at the very beginning of the service layer methods** (`ProductService`, `PersonService`, `SaleService`), immediately after receiving input parameters and before executing business algorithms, state changes, or persistence calls. This embodies the **Fail-Fast** software design principle: invalid requests are rejected immediately before any computational resources are consumed or any side effects occur.

### Consistency with Layered Architecture

The GameZone Unicesar architecture strictly organizes responsibilities into distinct layers:

```
[Presentation Layer: com.gamezone.ui]
           |
           v
[Application / Service Layer: com.gamezone.service]
    |                      |
    v                      v
[Validation Layer]    [Persistence Layer: com.gamezone.persistence]
(com.gamezone.validation)  |
    |                      |
    +----------> [Domain Model Layer: com.gamezone.model] <---+
```

Invoking validators from the service layer is consistent because:
1. **Centralized Enforcement of Business Invariants:** The service layer serves as the orchestrator of business use cases. By performing validation here, the system guarantees that regardless of the entry point (console UI, unit tests, automated integration scripts), validation rules are enforced uniformly.
2. **Protection of the Domain Model and Repository:** The model and persistence layers are protected from invalid state. No invalid object is ever constructed, modified, or written to CSV files.
3. **Separation of Concerns:** Business logic orchestration is maintained in the service classes, while data sanity rules and invariant specifications are isolated into dedicated validator classes (`ProductValidator`, `PersonValidator`, `SaleValidator`).

### Can Validators Access the Persistence Package?

**NO. Validators must NEVER access the persistence layer.**

#### Justification:
1. **Strict Unidirectional Architecture:** In a layered architecture, dependencies must flow strictly downward. Allowing `validation` to import or call `persistence` introduces tight coupling, architecture degradation, and risk of circular dependencies (`persistence` <-> `validation`).
2. **Statelessness and Functional Purity:** Validators must remain stateless utility classes composed purely of static methods. They validate data structures, formats, value ranges, and domain relationships passed directly into them.
3. **Testability and Performance:** Stateless validators without database or filesystem I/O are completely deterministic and can be unit tested in microseconds without setting up mock files, dummy storage, or tear-down routines.
4. **Data Provision Responsibility:** When a validation rule depends on existing system state (such as verifying that a person ID is not duplicate, or verifying that a product exists before updating stock), it is the **service layer** that queries the repository or checks in-memory collections and passes the resulting domain objects into the validator (e.g., `PersonValidator.validateUniquePerson(customers, newId)` or `ProductValidator.validateProductExists(product, productId)`). The validator evaluates the domain entities; it never initiates I/O.

---

## 4. Regression Prevention Strategy and External Behavior Preservation

**Question:** The refactoring must preserve the external behavior of the system. What strategy does the team follow to guarantee that the 10 menu operations continue functioning identically? What evidence is demonstrated upon completion to prove that nothing was broken?

### Regression Prevention Strategy

To ensure that internal exception handling enhancements did not alter functional behavior for end users, the team followed a strict **Black-Box Behavioral Preservation Strategy**:

1. **Strict Contract Preservation on Happy Paths:**
   For all valid inputs across the 10 core operations, the system preserves identical screen prompts, receipt structures, numbering formats (1..n), and persistence output.
2. **Identification and Matrix of the 10 Core Operations:**
   * **Product Management (6 operations):**
     1. Register video game (`registerVideoGame`)
     2. Register console (`registerConsole`)
     3. List all products (`listAllProducts`)
     4. Find product by ID (`findProductById`)
     5. Update product (`updateProduct`)
     6. Delete product (`deleteProduct`)
   * **Person Management (4 operations):**
     7. Register customer (`registerCustomer`)
     8. Register seller (`registerSeller`)
     9. List all customers (`listAllCustomers`)
     10. List all sellers (`listAllSellers`)
   *(Along with complementary sales, warranty, and return operations).*
3. **Controlled Fault Injection and Error Path Validation:**
   For each operation, test scenarios were executed with invalid inputs (negative prices, blank names, duplicate IDs, missing references) to verify that the system gracefully captures the failure, outputs the exact Spanish message required, and returns smoothly to the menu loop without crashing or displaying stack traces.

### Demonstrated Evidence

Upon completing the refactoring, the following concrete evidence confirms system stability and functional integrity:
* **Clean Maven Build:** Execution of `mvn compile` terminates with `BUILD SUCCESS` and zero compilation errors or warnings, confirming type safety across all 44 project classes.
* **Data Store Integrity:** CSV files in `data/` (`products.csv`, `customers.csv`, `sellers.csv`, `sales.csv`) remain completely valid, properly formatted, and consistent after running both creation and retrieval flows.
* **Sanitized Console Output:** Manual and simulated walkthroughs demonstrate that operations handle both success and error paths cleanly, displaying clear Spanish feedback without exposing raw exception traces (`e.printStackTrace()`).

---

## 5. ConsoleMenu Exception Handling Design: Try with Multiple Catch Blocks

**Question:** The `ConsoleMenu` class previously captured a single generic exception. It must now capture four distinct types. Is this done with a single `try` block with multiple `catch` blocks, cascading catch, or a general `catch (GameZoneException)`? Justify the choice considering code clarity and message differentiation for the user.

### Comparison of Approaches

1. **General Catch Block (`catch (GameZoneException e)`):**
   * *Mechanism:* Catches the common superclass in a single catch clause.
   * *Disadvantage:* Because the user requirements mandate distinct Spanish prefix messages and behaviors for each exception category (and specifically hiding technical details for `PersistenceException`), a general catch block would require an internal cascade of `if (e instanceof ResourceNotFoundException) ... else if (e instanceof BusinessRuleException) ...` statements.
   * *Flaw:* Using `instanceof` inside an exception handler is an anti-pattern that circumvents Java's built-in polymorphism and dispatching mechanisms, increasing cognitive load and cyclomatic complexity.

2. **Cascading / Multi-Catch Syntax (`catch (InvalidDataException | BusinessRuleException e)`):**
   * *Mechanism:* Groups multiple exception types into a single catch parameter.
   * *Disadvantage:* Multi-catch applies the exact same handling block to all grouped exceptions. Because each exception in GameZone Unicesar requires a completely distinct message template and security treatment, grouping them with multi-catch is technically incompatible with the requirement.

3. **Single `try` with Multiple `catch` Blocks (The Chosen Solution):**
   ```java
   try {
       // invocation of service operation
   } catch (ResourceNotFoundException e) {
       System.out.println("No se encontró el recurso solicitado: " + e.getMessage());
   } catch (BusinessRuleException e) {
       System.out.println("No se puede completar la operación: " + e.getMessage());
   } catch (InvalidDataException e) {
       System.out.println("Los datos ingresados no son válidos: " + e.getMessage());
   } catch (PersistenceException e) {
       System.out.println("Ocurrió un problema al acceder a los datos del sistema. Contacte al administrador.");
   }
   ```

### Technical Justification for Multiple Catch Blocks

* **Precise User Message Differentiation:**
  Each exception type is immediately dispatched by the Java Virtual Machine to its dedicated catch block, producing the exact required Spanish output:
  * **ResourceNotFoundException:** `"No se encontró el recurso solicitado: <detalle>"`
  * **BusinessRuleException:** `"No se puede completar la operación: <detalle>"`
  * **InvalidDataException:** `"Los datos ingresados no son válidos: <detalle>"`
  * **PersistenceException:** `"Ocurrió un problema al acceder a los datos del sistema. Contacte al administrador."`
* **Security and Technical Detail Masking:**
  `PersistenceException` wraps low-level technical information such as local file paths, corrupted line numbers, or operating system I/O errors. Having a dedicated catch block guarantees that no raw stack traces or internal storage details leak to the end user.
* **Idiomatic Java Code Clarity:**
  Multiple catch blocks leverage Java's native language semantics. The code is clean, declarative, easily readable, and completely free of manual type casting or `instanceof` inspection.
