# GRASP Analysis — Discussion of Guiding Questions

**Project:** GameZone Unicesar  
**Audited Branch / Commit:** `develop` @ `cdf4438`  
**Authors:** Sergio Guerrero (Technical Lead), Isaac Mattos (Developer 1), Jhonatan Galindo (Developer 2)

---

### Question 1: Criteria for GRASP Verdicts and Minimum Evidence

**Question:**  
*What criteria did the team use to decide whether a code snippet constitutes a GRASP pattern violation, whether the pattern is correctly applied, or whether the pattern does not apply? What minimum evidence must accompany each verdict to be accepted in the consolidation?*

**Response:**  
The team established standardized evaluation criteria grounded in Craig Larman's GRASP definitions, specifically tailored to our 4-layer object-oriented architecture (`ui -> service -> persistence -> model`):

1. **Violation (`violation`):** Assigned when a class or method directly breaches the fundamental question and core intent of a pattern. For instance:
   - *Information Expert:* A class performs calculations or coordinate state manipulations using data that exclusively belongs to another domain entity (e.g., querying and iterating foreign collections rather than delegating the domain calculation to the information holder).
   - *Polymorphism / Protected Variations:* Using procedural conditional branching (`instanceof`, discriminator strings, or `switch/if-else` blocks) to alter system behavior based on polymorphic subtypes, rather than declaring polymorphic methods or dedicated strategy mappers.
   - *High Cohesion / Pure Fabrication:* A persistence repository or business service handling UI rendering (e.g., calling `System.out.println`) or business workflows (e.g., deletion operations in repository).
2. **Correct (`correct`):** Assigned when an audited class or method addresses the responsibilities targeted by the pattern adhering cleanly to object-oriented principles without inappropriate leakage or tight coupling.
3. **Not Applicable (`not applicable`):** Assigned only when the semantic scope of the pattern has no logical application point in the inspected component (e.g., domain entities and repository classes do not act as use-case Controllers because they never receive UI-driven system input events).

**Minimum Evidence Required for Consolidation:**  
To prevent subjective assertions and guarantee traceability, every verdict submitted must provide:
- **Exact Location:** Qualified class name and precise method name.
- **Concrete Code Snippet / Line Numbers:** State of code referenced directly against the base `develop` commit (`cdf4438`).
- **Technical Explanation:** Clear justification explaining *why* the implementation breaches or fulfills the pattern principles.
- **Architectural Consequence:** The practical impact or risk on system extensibility, testability, or maintenance.
- **Proposed Solution:** A concrete refactoring proposal detailing the target pattern and changes required, explicitly tagging whether cross-member code adjustments are required.

---

### Question 2: Boundary Findings and Cross-Member Responsibility Assignment

**Question:**  
*Some violations manifest at the boundary between the code of two team members, for example when a class belonging to one member improperly depends on a class belonging to another. What findings of this type did the team identify? How were they registered in the consolidation and to whom was their correction assigned? Present at least one concrete case or, if there were none, explain how you verified that they did not exist.*

**Response:**  
During the individual audits and cross-boundary review, several boundary violations were identified where an entity or service authored by one member interacted improperly with classes authored by another:

1. **Case 1: `Return` (Developer 1) accessing `Sale` internals (Technical Lead) [V-01]**
   - *Manifestation:* In `Return.calculateItemRefund` (lines 167–170), the return logic calculates the proportional discount ratio by inspecting `Sale.getDiscountAmount()` and dividing it by `Sale.calculateSubtotal()`.
   - *Consolidation & Assignment:* Registered in consolidation under unique ID `V-01` (categorized under Information Expert). The correction is split: Technical Lead introduces `Sale.getDiscountRatio()` on the `Sale` entity, and Developer 1 refactors `Return` to delegate to this method.
2. **Case 2: `BulkPurchaseDiscount` (Developer 1) aggregating `Sale` unit quantities (Technical Lead) [V-02]**
   - *Manifestation:* In `BulkPurchaseDiscount.calculateDiscount` (lines 90–93), the discount loops over `sale.getItems()` accumulating `getQuantity()` manually to check the bulk volume threshold.
   - *Consolidation & Assignment:* Registered as `V-02`. The responsibility to calculate total purchased items belongs to `Sale` (Information Expert). Technical Lead implements `Sale.getTotalQuantity()`, and Developer 1 updates `BulkPurchaseDiscount` to consume it.
3. **Case 3: `ReturnService` (Developer 2) and `SaleService` (Technical Lead) Console Output [V-04 / V-TL-02]**
   - *Manifestation:* Service-layer methods printing directly to standard console streams (`ReturnService.registerReturn` line 155; `SaleService.registerSale` line 271).
   - *Consolidation & Assignment:* Registered under High Cohesion. Each respective service owner removes console statements from the service method, while Technical Lead ensures `ConsoleMenu` displays appropriate confirmation messages upon successful service invocation.
4. **Case 4: Category Resolution via Polymorphism across Boundaries [V-03 / V-TL-01 / V-08]**
   - *Manifestation:* `SaleService`, `ConsoleMenu`, `ReturnService`, and `CategoryDiscount` each independently checked `product instanceof VideoGame / Console / Accessory`.
   - *Consolidation & Assignment:* Developer 1 adds abstract `getCategory()` in `Product` (overridden in subtypes); Technical Lead refactors `SaleService` and `ConsoleMenu`; Developer 2 refactors `ReturnService`.

---

### Question 3: Inter-Related Patterns and Primary Guide Determination

**Question:**  
*Some patterns are closely related to each other. Did the team find code fragments that breach more than one pattern at the same time? How were they registered without duplicating findings, and how was it decided which pattern guides the correction?*

**Response:**  
Yes, multiple audited code sections simultaneously breached multiple closely coupled GRASP patterns:
- **Polymorphism vs. Low Coupling vs. Protected Variations:**
  In `CategoryDiscount.belongsToTargetCategory` (Developer 1) and in `SaleService.registerSale` / `ConsoleMenu` (Technical Lead), inspecting concrete types via cascaded `instanceof` blocks violates *Polymorphism* (type-based conditional logic), increases *Coupling* (direct dependency on every concrete subclass), and violates *Protected Variations* (adding a new product type requires modifying every consuming module).
- **Pure Fabrication vs. High Cohesion:**
  Placing UI logging and console prompts directly within repository classes (`PersonRepository`, `SaleRepository`) breaches *High Cohesion* (mixing persistence and presentation) and undermines *Pure Fabrication* (repositories exist strictly to abstract persistence mechanism).

**Registration and Primary Pattern Strategy:**
1. **Single Canonical Finding:** To eliminate redundancy, each identified code fragment is recorded under a single canonical finding ID in the consolidated audit.
2. **Identification of Root Cause Pattern:** The team evaluated which pattern addressed the root structural defect rather than the symptom:
   - When behavioral variations are handled with conditionals, **Polymorphism** was selected as the guiding pattern, because polymorphic abstraction naturally resolves coupling and protects variations.
   - When a class executed responsibilities alien to its domain scope, **High Cohesion** or **Information Expert** was selected to guide reallocating the responsibility.
3. **Cross-Referencing:** Related patterns are explicitly documented in the violation entry as secondary impacted patterns.

---

### Question 4: External Behavior Preservation and Non-Regression Verification

**Question:**  
*Refactoring must strictly preserve the external behavior of the system. What strategy does the team follow to guarantee that all menu operations continue functioning identically? What evidence is shown at completion to demonstrate that nothing was broken?*

**Response:**  
To comply with the strict preservation constraint (identical user inputs, identical console messages, identical outputs, and identical CSV persistence formats), the team establishes the following non-regression methodology:

1. **Baseline Execution & Test Script Matrix:**
   Prior to applying any code refactoring on the codebase, a complete manual and automated execution matrix covering every menu option is executed against the base commit (`cdf4438`). The exact console outputs, prompts, error handling reactions, and data file states (`data/products.csv`, `data/sales.csv`, `data/persons.csv`, etc.) are recorded as the baseline reference in `docs/grasp-refactor-log.md`.
2. **Scope-Isolated Regression Verification in PRs:**
   Every Pull Request during Phase 2 requires executing and validating the specific operations affected by that refactored violation against the baseline before requesting peer review.
3. **Final Full-Regression Demonstration:**
   Upon merging all refactorings into `feature/grasp-refactor`, the entire test suite and end-to-end user scenarios are rerun:
   - Registering and listing products, consoles, and accessories.
   - Registering customers and sellers.
   - Performing multi-item sales with promotions (volume, percentage, category) and extended warranties.
   - Registering returns and checking stock restoration and monthly balances.
   - Validating differentiated exception messages for missing entities and invalid data.
4. **Final Proof of Invariance:**
   The `docs/grasp-refactor-log.md` file contains side-by-side evidence tables demonstrating identical console output transcripts and verified persistence files before and after the refactoring.

---

### Question 5: Correction Order and Team Coordination Strategy

**Question:**  
*In what order are the violations corrected and why? When a violation correction involves code belonging to more than one team member, how is work coordinated to avoid merge conflicts on the shared branch?*

**Response:**  
The refactoring order is strictly prioritized based on architectural dependency hierarchies to prevent circular blockers and code integration collisions:

1. **Foundational Model Enhancements First (Core Abstractions):**
   - *Developer 1* implements `Product.getCategory()` and abstract promotion bases (`PercentageBasedPromotion`).
   - *Technical Lead* introduces domain expert query methods in `Sale` (`getDiscountRatio()`, `getTotalQuantity()`).
   - *Reason:* Downstream services and persistence mappers directly depend on these model APIs.
2. **Domain Model Cleanups (Decoupling):**
   - *Developer 2* eliminates unused bi-directional coupling in `Customer` (purchase history list).
3. **Polymorphic Mappers and Persistence Refactoring (Pure Fabrication):**
   - *Developer 1* refactors `ProductRepository` using polymorphic CSV mappers.
   - *Developer 2* implements CSV mappers for `AccessoryRepository` and `PromotionRepository`.
   - *Technical Lead* removes console printing from `SaleRepository`.
4. **Service-Layer Refactoring & Boundary Coordination:**
   - *Developer 2* updates `ReturnService` and `WarrantyService` using the new `Product.getCategory()` and clean expert methods.
   - *Technical Lead* cleanses `SaleService` stock update branching and exception propagation.
5. **UI Layer (`ConsoleMenu`) Final Synchronization:**
   - Handled exclusively by the *Technical Lead* once all underlying service contracts are stable.

**Coordination and Branch Protection Protocol:**
- Work occurs sequentially or on isolated sub-features branching off `feature/grasp-refactor`.
- Commits are atomic and use Conventional Commits (`refactor: ... (V-XX)` and `docs: ...`).
- Whenever a shared class (`Sale`, `Product`, `ConsoleMenu`) requires edits from multiple members, the owning member introduces the interface method first, pushes the commit, and informs the team before the consumer member integrates against it.
