1. A return is a new system entity that references an existing sale. What type of relationship exists between the `Return` class and the `Sale` class? Is this relationship one of inheritance, association, aggregation, or composition? Justify your answer.
- The relationship between the `Return` class and the `Sale` class is one of composition, because the `Return` class requires the existence of a sale in order to function.

2. A return may contain only some products from the original sale, not necessarily all of them. How is this situation represented in the attributes of the `Return` class? What is stored in the returned products attribute?
- Return date, salesperson ID, customer ID, product ID, quantity returned, return total.

3. The business rule states that returns can only be registered within 30 days of the sale. In which system layer is this validation located, and why? What Java mechanism is used to calculate the difference between two dates?
- In the service layer, since that is where data is handled; we take the return date at the time of the return and subtract the recorded purchase date using `.now()`.

4. Returning products increases stock levels. Which existing method from the Workshop 1 system is reused for this operation, and in which class is it invoked from the returns module? Why is it important to reuse existing methods instead of duplicating stock update logic?
- The method is `updateStock(String productId, int quantity)`, and it is invoked from `ReturnService`. Reusing methods is important because it saves time and improves software quality.

5. The monthly balance report requires consolidating information from two different modules (sales and returns). In which service class is this report located, and why is this location consistent with the layered architecture? What dependencies does this class need to generate the report? - in SaleService—since the service layer handles all business logic—the required dependencies are
   Sale, ReturnService, sales.csv, and return.csv