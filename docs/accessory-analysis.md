# Accessory Module Analysis

**1. Should accessories be integrated into the existing product hierarchy (extending Product) or form an independent hierarchy? Justify your decision considering code reuse and model coherence.**

Accessories should be integrated into the existing product hierarchy by extending the `Product` class. As specified in the requirements, an accessory shares the fundamental characteristics of any salable item in the store (identifier, title, price, and inventory quantity). By extending `Product`, we maximize code reuse by inheriting these common attributes and behaviors. It also maintains model coherence, as an accessory is ultimately a type of product, allowing the sales module to treat both video games/consoles and accessories uniformly during a transaction.

**2. What attributes are common to all three types of accessories, and which are specific to each type? How is this distinction reflected in the module's class hierarchy?**

*   **Common Attributes:** Identifier, title, price, and available stock (inherited from `Product`), plus a list of compatible consoles (defined in the `Accessory` class).
*   **Specific Attributes:**
    *   *Controller:* Connection type (wireless or wired).
    *   *Cable:* Length in meters and connector type (HDMI, USB, optical, etc.).
    *   *Memory:* Storage capacity in gigabytes and memory type (SD, microSD, internal).
*   **Hierarchy Reflection:** This is reflected by creating an abstract base class named `Accessory` that extends `Product` and encapsulates the common compatibility list. From this abstract class, three concrete subclasses (`Controller`, `Cable`, and `Memory`) inherit the common traits and define their own specific private attributes, encapsulating the distinct characteristics of each accessory type.

**3. The compatibility between an accessory and a console is a relationship between two system entities. How is this relationship represented in the design and persistence? Is compatibility an attribute of the accessory, the console, or both?**

In the design, compatibility is represented as an attribute of the *accessory*. The `Accessory` abstract class includes a private attribute that stores a list of compatible consoles (likely using their identifiers). In the persistence layer, this relationship is stored within the `data/accessories.csv` file. When an accessory is saved, its list of compatible console IDs can be serialized (e.g., as a separated string within the accessory's CSV row). Therefore, the system treats compatibility primarily as a property belonging to the accessory entity rather than the console entity.

**4. What modifications are necessary in the sales service class (`SaleService`) so that sales can include accessories without breaking the existing behavior with video games and consoles?**

The `SaleService.registerSale` method must be updated to accept a heterogeneous list of items that includes both traditional products (consoles, video games) and the new accessories. Because `Accessory` extends `Product`, the existing logic for stock validation and total price calculation can remain unified. However, the inventory update logic must be modified to check the specific type of each item being sold. It must delegate the stock deduction to the appropriate service: `ProductService` for traditional products and `AccessoryService` for accessories.

**5. In which layer of the system's architecture should the new classes of the accessories module be located? Justify your decision based on the responsibilities of each layer.**

The new classes must be distributed across the existing four-layer architecture according to their specific responsibilities:
*   **Model Layer (`model`):** The `Accessory`, `Controller`, `Cable`, and `Memory` classes belong here because they define the structure, attributes, and core domain concepts of the new business entities.
*   **Persistence Layer (`persistence`):** The `AccessoryRepository` belongs here as its sole responsibility is to handle data storage and retrieval (I/O operations) using the `accessories.csv` file.
*   **Service Layer (`service`):** The `AccessoryService` belongs here because it acts as the intermediary orchestrator, containing the business logic (registering, filtering, searching) and depending on the repository for data access.
*   **UI Layer (`ui`):** The modifications to the user interface (e.g., the `ConsoleMenu`) belong here to handle user inputs and display information to the client.