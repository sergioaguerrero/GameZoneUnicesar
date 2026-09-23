# Return Analysis

**1. A return is a new system entity that references an existing sale. What type of relationship exists between the `Return` class and the `Sale` class? Is this relationship one of inheritance, association, aggregation, or composition? Justify your answer.**

The relationship between the `Return` class and the `Sale` class is one of **composition** (or strong directed association). The `Return` class requires a reference to an original, existing sale to be instantiated. Because a return cannot logically exist without a prior sale, its lifecycle is strictly dependent on the existence of that `Sale` object.

**2. A return may contain only some products from the original sale, not necessarily all of them. How is this situation represented in the attributes of the `Return` class? What is stored in the returned products attribute?**

This situation is represented by a specific attribute in the `Return` class that holds only the returned items, rather than the entire original order. Specifically, the class stores a `List<Product>`, which contains only the subset of products the customer actually chose to return.

**3. The business rule states that returns can only be registered within 30 days of the sale. In which system layer is this validation located, and why? What Java mechanism is used to calculate the difference between two dates?**

The validation is orchestrated in the **Service layer** (`ReturnService`), but the core business logic is placed in the **Model layer** by adding a `canBeReturned(): boolean` method within the `Sale` class. This design keeps the domain rules encapsulated. To calculate the date difference in Java, the `java.time.LocalDate` API is used, taking the current date (e.g., using `LocalDate.now()`) and verifying it falls within 30 days of the recorded sale date.

**4. Returning products increases stock levels. Which existing method from the Workshop 1 system is reused for this operation, and in which class is it invoked from the returns module? Why is it important to reuse existing methods instead of duplicating stock update logic?**

The method used is **`restoreStock(String productId, int quantity)`**, which must be added to the `ProductService` class. This method is invoked from `ReturnService` when processing a successful return. Reusing this method ensures that all inventory manipulation logic remains centralized within `ProductService`, avoiding code duplication and preventing data inconsistencies in the storage files.

**5. The monthly balance report requires consolidating information from two different modules (sales and returns). In which service class is this report located, and why is this location consistent with the layered architecture? What dependencies does this class need to generate the report?**

The report generation is located in the **`ReturnService`** class via the `generateMonthlyBalance(int month, int year)` method. This placement aligns with the layered architecture because the service layer is responsible for orchestrating business logic across multiple domains. To generate the report, `ReturnService` requires its injected dependencies: `SaleService` (to calculate the total sales for the month) and its own `ReturnRepository` (to calculate the total returns for the month).