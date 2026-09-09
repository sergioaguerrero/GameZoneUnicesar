# AI-USAGE: Technical Consultation Log - GEMINI 3.1 PRO

## 1. Project Architecture and Git Flow Strategy
* **Problem Posed:** As the Technical Lead, guidance was needed to structure the project from scratch, delegate classes for the team backlog, and establish a version control strategy before coding began[cite: 1].
* **Suggested Technical Solution:** Defined a strict 4-layer architecture (Model, Persistence, Service, UI) and outlined the Git Flow workflow (creating feature branches from `develop`, using Pull Requests, and merging)[cite: 1]. Created the initial `TEAM.md` file to distribute modules[cite: 1].
* **Key Lesson / Application:** Establishing proper software architecture layers and version control rules early is crucial for parallel team development, avoiding bottlenecks, and minimizing integration conflicts[cite: 1].

## 2. Complex Object Persistence (CSV Serialization)
* **Problem Posed:** Needed to save a `Sale` object—which contains nested objects (`Customer`, `Seller`) and a list of `SaleItem`s—into a flat text/CSV file without losing the data relationships[cite: 1].
* **Suggested Technical Solution:** Implemented a relational serialization strategy in `SaleRepository`[cite: 1]. Instead of saving entire objects, the solution saved unique IDs and quantities (e.g., `YYYY-MM-DD,CustID,SellID,ProdID:Qty`)[cite: 1]. During deserialization (`loadSales`), master lists of objects were passed to cross-reference and reconstruct the objects[cite: 1].
* **Key Lesson / Application:** Relational data mapping in flat files. It clarifies how to serialize complex object graphs into strings and reconstruct them efficiently using unique identifiers[cite: 1].

## 3. Service Layer and Business Logic Integration
* **Problem Posed:** Needed to orchestrate the sale process, apply business rules (like checking inventory), and integrate the Sale module with the teammates' Person and Product modules[cite: 1].
* **Suggested Technical Solution:** Developed the `SaleService` class using Dependency Injection[cite: 1]. Injected `PersonService` and `ProductService` to validate entity existence and available stock before utilizing the `SaleRepository` to persist the transaction[cite: 1].
* **Key Lesson / Application:** The Service layer must centralize business logic. By delegating specific tasks to specialized services, the system maintains a clean separation of concerns and robust validation[cite: 1].

## 4. Architectural Boundaries and Application Entry Point
* **Problem Posed:** Confusion arose regarding why some repositories used hardcoded file paths (like `SaleRepository`) while others required the path in the constructor, and who was responsible for configuring them[cite: 1].
* **Suggested Technical Solution:** Clarified that the `Main` class acts as the application's entry point and dependency assembler[cite: 1]. Both hardcoded and injected paths are valid persistence strategies, provided the UI layer never interacts directly with the repositories[cite: 1].
* **Key Lesson / Application:** Inversion of Control (IoC) and strict layered architecture principles (`UI -> Service -> Persistence`). It highlights how to properly wire an application's dependencies together at the root level[cite: 1].

## 5. Business Rules and Data Immutability
* **Problem Posed:** The team considered adding functionality to edit or delete existing sales and needed to know how to implement it safely[cite: 1].
* **Suggested Technical Solution:** Strictly advised against editing or deleting sales to maintain financial and historical immutability, treating sales as read-only records once persisted[cite: 1].
* **Key Lesson / Application:** Domain-driven design principles regarding the immutability of financial transactions. Real-world business rules often dictate that historical records must not be tampered with or deleted[cite: 1].

## 6. Code Standardization (JavaDoc Implementation)
* **Problem Posed:** Needed to add documentation to the technical lead's classes (`Sale`, `SaleItem`, `SaleService`, `SaleRepository`) using the proper JavaDoc format in English[cite: 1, 2].
* **Suggested Technical Solution:** Generated standard JavaDoc comments (`/** ... */`) using specific metadata tags like `@param`, `@return`, and `@throws` for core methods[cite: 2]. Removed unnecessary internal comments and focused on documenting the public APIs[cite: 2].
* **Key Lesson / Application:** Standardizing code documentation abstracts internal logic, making it easily readable by other developers and IDEs. It reinforces clean code practices and API clarity[cite: 2].

# AI-USAGE: Technical Consultation Log - CLAUDE SONNET 5 MEDIUM

**Project:** GameZone Unicesar (Java / Maven console application)
**AI tool used:** Claude (Anthropic)
**Scope of this log:** UI layer development (`Main.java`, `ConsoleMenu.java`) and one targeted extension to `SaleService.java`. The rest of the architecture (models, repositories, `SaleService.registerSale`, persistence strategy) was already implemented by the team before this consultation began.

---

## 1. Wiring the UI Layer to an Already-Built Service API

* **Problem Posed:** `Main.java` and `ConsoleMenu.java` existed only as empty class stubs. A reference implementation was provided as a starting point, but it was explicitly flagged as incomplete — the real `ProductService`, `PersonService`, and `SaleService` classes, already built by the team, needed to be inspected first.
* **Suggested Technical Solution:** Before writing any UI code, the actual signatures of every service and model class were read directly from the uploaded project (`registerVideoGame`, `registerConsole`, `listCustomer`, `listSeller`, `findCustomer`, `findSeller`, `registerSale`, etc.). The reference implementation assumed several methods that did not exist (`listAllCustomers()`, `Sale.generateReceipt()`, `registerSale()` returning a `Sale` instead of `boolean`), so `Main.java` and `ConsoleMenu.java` were built directly against the real API instead of the reference's assumptions. A JDK was installed in the sandbox to compile and run the project end-to-end (register product → customer → seller → sale) before delivering the files.
* **Key Lesson / Application:** Never assume a reference snippet matches an existing codebase — verify actual method signatures first. This avoids delivering code that looks correct but fails to compile against the real project, and reinforces the discipline of reading the Service layer's public contract before consuming it from the UI.

## 2. Closing a Gap in the Service Layer (Read-Side of Sales)

* **Problem Posed:** `SaleService` only exposed `registerSale(...)`. There was no way for the console menu to list existing sales, by customer, by seller, or in full — a capability the sales menu needed.
* **Suggested Technical Solution:** Rather than reaching into `SaleRepository` directly from the UI layer (which would break the `UI -> Service -> Persistence` boundary), three read-only methods were added to `SaleService`: `listAllSales()`, `listSalesByCustomer(String)`, and `listSalesBySeller(String)`. They reuse the same `personService` / `productService` / `saleRepository` collaborators already injected into `SaleService`, so no new dependencies or constructor changes were introduced.
* **Key Lesson / Application:** When the UI needs a capability the Service layer doesn't expose yet, the fix belongs in the Service layer, not as a workaround in the UI. Extending an existing service with a narrowly scoped, read-only method preserves the layered architecture instead of violating it.

## 3. Usability: Numbered Listings and Type-Filtered Views

* **Problem Posed:** All listings (products, customers, sellers, sales) were printed as flat, unnumbered text, making individual records hard to reference. Separately, there was no way to list only video games or only consoles — only the combined product list.
* **Suggested Technical Solution:** Added a manual index counter to every listing method so each record prints as `1. ...`, `2. ...`, etc. For type filtering, `listVideoGamesOnly()` and `listConsolesOnly()` were added at the UI layer, filtering the existing `productService.listAllProducts()` result with `instanceof VideoGame` / `instanceof Console` — no changes to `ProductService` were required since `Product` is already a polymorphic list.
* **Key Lesson / Application:** Simple UI-side filtering over an existing polymorphic collection can satisfy a new requirement without touching the Service or Persistence layers, as long as the UI is allowed to reason about concrete subtypes it already receives.

## 4. Defensive Input Handling at the Console Boundary

* **Problem Posed:** Every field was read with a single `scanner.nextLine()` call and no retry logic. Blank IDs, negative prices, non-numeric text, and malformed emails either propagated into the Service layer or crashed the current operation with an uncaught `NumberFormatException`.
* **Suggested Technical Solution:** Introduced reusable, loop-based input helpers scoped to `ConsoleMenu` — `readRequiredText`, `readRequiredEmail`, `readPositiveDouble`, `readNonNegativeInt`, and `readPositiveInt` — each re-prompting until the input is valid, instead of failing the whole registration. Also added pre-checks (duplicate ID, missing customer/seller) before calling the Service layer, so invalid operations are caught with a clear message rather than a stack trace. Validated with deliberately bad input (blank ID, negative price, non-numeric text, malformed email) to confirm the program recovers gracefully.
* **Key Lesson / Application:** Input validation belongs at the boundary where untrusted input enters the system (the console), keeping the Service layer's own validation (`IllegalArgumentException`s) as a second line of defense rather than the only one. This two-tier validation is a common and defensible pattern in layered architectures.

## 5. Language Consistency Across the Codebase

* **Problem Posed:** `ConsoleMenu.java` had been written in Spanish, based on an earlier illustrative reference, while every other class in the project (`Product`, `Sale`, `PersonRepository`, `ProductRepository`, `SaleRepository`, `SaleService`) already used English for both code and console output (`System.out`/`System.err` messages).
* **Suggested Technical Solution:** Rewrote every user-facing string in `ConsoleMenu.java` — menu headers, prompts, validation messages, success and error messages — in English, without altering any logic, validation, or numbering behavior already in place. Also corrected the one remaining Spanish string in `Main.java` (`"Error fatal: "`). Recompiled and smoke-tested the full flow to confirm consistent English output end to end.
* **Key Lesson / Application:** Language and naming consistency across a codebase is a maintainability concern, not just a cosmetic one — mixed-language output makes a project harder to hand off, review, or extend by other team members or graders.

---

## Out of Scope for This Log

The following areas — visible in some AI-usage log templates for this type of project — were **not** part of this consultation and are intentionally omitted rather than fabricated:

* Initial project architecture design (Model / Persistence / Service / UI layering) and Git Flow strategy definition — these were already established in the project before this session.
* Git branch integration and merge-conflict resolution (e.g., `develop` → `feature/sale-module`) — no Git commands or conflicts were reviewed in this session.
* Design of the CSV persistence strategy in `SaleRepository`/`ProductRepository`/`PersonRepository` — these repositories already existed and were only *read*, not designed, during this consultation.
* Business rules such as sale immutability — not discussed in this session.

If any of these were AI-assisted in a separate session, they should be logged as their own entries with their own real problem/solution/lesson details, rather than merged into this one.

---

*This document was drafted with AI assistance (Claude, Anthropic) and reviewed by the development team before submission.*