# Warranty Module - Class Diagram

Updated class diagram showing the new warranty hierarchy (`model`), the persistence and service classes of the module, and how the module is integrated with the existing system (`Sale`, `Product`, `Console`, `SaleService`, `ConsoleMenu`).

```mermaid
classDiagram
direction TB

    class Warranty {
        <<abstract>>
        -String warrantyId
        -Product product
        -Sale sale
        -LocalDate startDate
        -LocalDate endDate
        +Warranty(String warrantyId, Product product, Sale sale, LocalDate startDate)
        +String getWarrantyId()
        +Product getProduct()
        +Sale getSale()
        +LocalDate getStartDate()
        +LocalDate getEndDate()
        +int getDurationInMonths()*
        +String getWarrantyType()*
        +double getAdditionalCost()*
        +boolean isActive(LocalDate date)
        +String generateWarrantyCertificate()
    }

    class BasicWarranty {
        -int DURATION_IN_MONTHS$ = 6
        +BasicWarranty(String warrantyId, Product product, Sale sale, LocalDate startDate)
        +int getDurationInMonths()
        +String getWarrantyType()
        +double getAdditionalCost()
    }

    class ExtendedWarranty {
        -int DURATION_IN_MONTHS$ = 12
        -double COST_PERCENTAGE$ = 0.10
        +ExtendedWarranty(String warrantyId, Product product, Sale sale, LocalDate startDate)
        +int getDurationInMonths()
        +String getWarrantyType()
        +double getAdditionalCost()
    }

    class WarrantyRepository {
        -String FILE_PATH$ = "data/warranties.csv"
        +void saveAll(List~Warranty~ warranties)
        +List~Warranty~ loadAll(List~Sale~ sales, List~Product~ products)
        -Warranty fromCsvLine(String line, List~Sale~ sales, List~Product~ products)
        -String toCsvLine(Warranty warranty)
    }

    class WarrantyService {
        -WarrantyRepository warrantyRepository
        -List~Warranty~ warranties
        +WarrantyService(WarrantyRepository warrantyRepository, SaleRepository saleRepository, ProductService productService, List~Customer~ customers, List~Seller~ sellers)
        +BasicWarranty assignBasicWarranty(Product product, Sale sale, LocalDate startDate)
        +ExtendedWarranty assignExtendedWarranty(Product product, Sale sale, LocalDate startDate)
        +Warranty findWarrantyByProduct(String productId, String saleId)
        +List~Warranty~ listAllWarranties()
        +List~Warranty~ listActiveWarranties()
        +List~Warranty~ listWarrantiesExpiringSoon(int daysAhead)
        -void persist()
    }

    class SaleService {
        -WarrantyService warrantyService
        +void setWarrantyService(WarrantyService warrantyService)
        +boolean registerSale(String customerId, String sellerId, List~SaleItem~ items)
        +boolean registerSale(String customerId, String sellerId, List~SaleItem~ items, List~String~ productIdsWithExtendedWarranty)
        -void applyWarranties(Sale sale, List~String~ extendedIds)
        -boolean isConsoleInItems(String productId, List~SaleItem~ items)
        -String nextSaleId(List~Sale~ existingSales)
        +List~Sale~ listAllSales()
    }

    class SaleRepository {
        +String saleLine(Sale sale)
        +void saveSales(List~Sale~ sales)
        +List~Sale~ loadSales(List~Customer~ customers, List~Seller~ sellers, List~Product~ products)
    }

    class Sale {
        -String id
        -LocalDate date
        -List~SaleItem~ items
        -double discountAmount
        -double extendedWarrantyCost
        +String getId()
        +LocalDate getDate()
        +List~SaleItem~ getItems()
        +double getExtendedWarrantyCost()
        +void setExtendedWarrantyCost(double extendedWarrantyCost)
        +double calculateTotal()
        +double calculateFinalTotal()
        +String generateReceipt()
    }

    class SaleItem {
        -Product product
        -int quantity
    }

    class Product {
        <<abstract>>
        -String productId
        -String title
        -double price
        +double getPrice()
    }

    class Console
    class VideoGame
    class Accessory {
        <<abstract>>
    }

    class ProductService {
        +List~Product~ listAllProducts()
    }

    class ConsoleMenu {
        -WarrantyService warrantyService
        -SaleService saleService
        -void registerSale()
        -void showWarrantyMenu()
        -void findWarrantyByProduct()
        -void listWarrantiesExpiringSoon()
        -void printWarranties(List~Warranty~ warranties, String emptyMessage)
    }

    Warranty <|-- BasicWarranty
    Warranty <|-- ExtendedWarranty

    Product <|-- Console
    Product <|-- VideoGame
    Product <|-- Accessory

    Warranty "0..*" --> "1" Product : covers
    Warranty "0..*" --> "1" Sale : granted in
    Sale "1" *-- "1..*" SaleItem : contains
    SaleItem "0..*" --> "1" Product : includes

    ConsoleMenu ..> WarrantyService : queries warranties
    SaleService "1" --> "1" WarrantyService : assigns warranties
    SaleService ..> SaleRepository : persists sales
    SaleService ..> Console : instanceof check (only consoles get warranties)
    SaleService ..> ExtendedWarranty : reads additional cost
    WarrantyService "1" --> "1" WarrantyRepository : persists and loads
    WarrantyService ..> SaleRepository : loads sales to resolve references
    WarrantyService ..> ProductService : lists products to resolve references
    WarrantyService ..> BasicWarranty : creates
    WarrantyService ..> ExtendedWarranty : creates
    WarrantyRepository ..> Warranty : saves and rebuilds
    WarrantyRepository ..> Sale : resolves from the list received on load
    WarrantyRepository ..> Product : resolves from the list received on load
```

## Notes

- **Inheritance and polymorphism:** `Warranty` holds everything common (id, product, sale, dates, `isActive`, certificate). The three abstract methods are the only things each subclass has to provide, so no duration or cost logic is duplicated.
- **Integration with the sales flow:** `SaleService.registerSale` creates the sale, applies the promotion, and then calls `applyWarranties`, which uses `instanceof Console` to decide who gets a free `BasicWarranty` and sums the cost of every requested `ExtendedWarranty` into `Sale.extendedWarrantyCost`.
- **Dependencies of the persistence layer:** `WarrantyRepository` no longer depends on any service. It stores only the product and sale identifiers, and when loading it resolves them against the lists of sales and products that its caller passes to `loadAll`.
- **Where references are resolved:** `WarrantyService` receives `WarrantyRepository`, `SaleRepository` and `ProductService` through its constructor, plus the customers and sellers that `SaleRepository.loadSales` needs. It gathers the products (`ProductService.listAllProducts`) and the sales (`SaleRepository.loadSales`) and hands both lists to `WarrantyRepository.loadAll`.
- **Dependency between `SaleService` and `WarrantyService`:** `WarrantyService` no longer depends on `SaleService`, so there is no circular dependency between them. `WarrantyService` can therefore be created before `SaleService` in `Main`; `SaleService` keeps receiving it through `setWarrantyService`.
- **Persistence:** the end date is not stored in `warranties.csv`; it is recalculated by the `Warranty` constructor when the file is loaded. The discriminator column is `BASIC` or `EXTENDED`.