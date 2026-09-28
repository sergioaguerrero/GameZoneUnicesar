```mermaid

classDiagram

    class Person {
        <<abstract>>
        -String name
        -String identification
        -String phone
        +String getName()
        +void setName(String name)
        +String getIdentification()
        +void setIdentification(String identification)
        +String getPhone()
        +void setPhone(String phone)
    }

    class Customer {
        -String email
        -List~Sale~ purchaseHistory
        +String getEmail()
        +void setEmail(String email)
        +List~Sale~ getPurchaseHistory()
        +void addPurchase(Sale sale)
    }

    class Seller {
        -String employeeCode
        -String workShift
        +String getEmployeeCode()
        +void setEmployeeCode(String employeeCode)
        +String getWorkShift()
        +void setWorkShift(String workShift)
    }

    class Product {
        <<abstract>>
        -String productId
        -String title
        -double price
        -int stockQuantity
        +String getProductId()
        +void setProductId(String productId)
        +String getTitle()
        +void setTitle(String title)
        +double getPrice()
        +void setPrice(double price)
        +int getStockQuantity()
        +void setStockQuantity(int stockQuantity)
        +void decreaseStock(int quantity)
        +boolean hasEnoughStock(int quantity)
        +String getFullDescription()*
    }

    class VideoGame {
        -String platform
        -String genre
        -String ageRating
        +String getPlatform()
        +void setPlatform(String platform)
        +String getGenre()
        +void setGenre(String genre)
        +String getAgeRating()
        +void setAgeRating(String ageRating)
        +String getFullDescription()
    }

    class Console {
        -String brand
        -String model
        -int generation
        +String getBrand()
        +void setBrand(String brand)
        +String getModel()
        +void setModel(String model)
        +int getGeneration()
        +void setGeneration(int generation)
        +String getFullDescription()
    }

    class Accessory {
        <<abstract>>
        -List~Console~ compatibleConsoles
        +List~Console~ getCompatibleConsoles()
        +void setCompatibleConsoles(List~Console~ compatibleConsoles)
        +void addCompatibleConsole(Console console)
        +boolean removeCompatibleConsole(Console console)
        +boolean isCompatibleWith(Console console)
        +String getAccessoryType()*
        +String getFullDescription()*
    }

    class Controller {
        -String connectionType
        +String getConnectionType()
        +void setConnectionType(String connectionType)
        +String getAccessoryType()
        +String getFullDescription()
    }

    class Cable {
        -double length
        -String connectorType
        +double getLength()
        +void setLength(double length)
        +String getConnectorType()
        +void setConnectorType(String connectorType)
        +String getAccessoryType()
        +String getFullDescription()
    }

    class Memory {
        -int capacityGB
        -String memoryType
        +int getCapacityGB()
        +void setCapacityGB(int capacityGB)
        +String getMemoryType()
        +void setMemoryType(String memoryType)
        +String getAccessoryType()
        +String getFullDescription()
    }

    class Promotion {
        <<abstract>>
        -String id
        -String name
        -LocalDate startDate
        -LocalDate endDate
        +String getId()
        +String getName()
        +LocalDate getStartDate()
        +LocalDate getEndDate()
        +boolean isActive(LocalDate date)
        +double calculateDiscount(Sale sale)*
    }

    class PercentageDiscount {
        -double percentage
        +double getPercentage()
        +void setPercentage(double percentage)
        +double calculateDiscount(Sale sale)
    }

    class CategoryDiscount {
        +String CATEGORY_VIDEOGAME$
        +String CATEGORY_CONSOLE$
        -double percentage
        -String targetCategory
        +double getPercentage()
        +String getTargetCategory()
        +double calculateDiscount(Sale sale)
    }

    class BulkPurchaseDiscount {
        -int minimumQuantity
        -double percentage
        +int getMinimumQuantity()
        +double getPercentage()
        +double calculateDiscount(Sale sale)
    }

    class Sale {
        -LocalDate date
        -Customer customer
        -Seller seller
        -List~SaleItem~ items
        -String appliedPromotionName
        -double discountAmount
        +LocalDate getDate()
        +void setDate(LocalDate date)
        +Customer getCustomer()
        +void setCustomer(Customer customer)
        +Seller getSeller()
        +void setSeller(Seller seller)
        +List~SaleItem~ getItems()
        +void addItem(SaleItem item)
        +double calculateTotal()
        +double calculateFinalTotal()
        +String generateReceipt()
        +void register()
        +boolean canBeReturned()
    }

    class Return {
        -String returnId
        -LocalDate returnDate
        -Sale originalSale
        -List~Product~ returnedProducts
        -String reason
        -double refundAmount
        +String getReturnId()
        +LocalDate getReturnDate()
        +Sale getOriginalSale()
        +List~Product~ getReturnedProducts()
        +String getReason()
        +double getRefundAmount()
        +double calculateRefundAmount()
        +String generateReturnReceipt()
    }

    class ReturnRepository {
        +void saveAll(List~Return~ returns)
        +List~Return~ loadAll()
    }

    class ReturnService {
        +Return registerReturn(String saleId, List~String~ productIds, String reason)
        +List~Return~ viewAllReturns()
        +List~Return~ viewReturnsByCustomer(String customerId)
        +List~Return~ viewReturnsBySale(String saleId)
        +double generateMonthlyBalance(int month, int year)
    }

    class ProductService {
        +void restoreStock(String productId, int quantity)
    }

    class SaleItem {
        -Product product
        -int quantity
        +Product getProduct()
        +void setProduct(Product product)
        +int getQuantity()
        +void setQuantity(int quantity)
        +double calculateSubtotal()
    }

    Person <|-- Customer
    Person <|-- Seller

    Product <|-- VideoGame
    Product <|-- Console
    Product <|-- Accessory
    
    Accessory <|-- Controller
    Accessory <|-- Cable
    Accessory <|-- Memory

    Promotion <|-- PercentageDiscount
    Promotion <|-- CategoryDiscount
    Promotion <|-- BulkPurchaseDiscount

    Customer "1" --> "0..*" Sale : makes
    Seller "1" --> "0..*" Sale : attends

    Sale "1" *-- "1..*" SaleItem : contains
    SaleItem "0..*" --> "1" Product : includes

    Accessory "0..*" --> "0..*" Console : compatible with
    CategoryDiscount ..> VideoGame : checks category via instanceof
    CategoryDiscount ..> Console : checks category via instanceof
    Sale "1" --> "0..1" Promotion : applied promotion (by name, not object reference)

    Return "0..*" --> "1" Sale : references
    Return "0..*" --> "1..*" Product : contains
    ReturnService ..> ReturnRepository : uses
    ReturnService ..> Return : manages
    ReturnService ..> ProductService : calls restoreStock