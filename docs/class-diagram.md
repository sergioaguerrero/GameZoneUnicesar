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

    class Sale {
        -LocalDate date
        -Customer customer
        -Seller seller
        -List~SaleItem~ items
        +LocalDate getDate()
        +void setDate(LocalDate date)
        +Customer getCustomer()
        +void setCustomer(Customer customer)
        +Seller getSeller()
        +void setSeller(Seller seller)
        +List~SaleItem~ getItems()
        +void addItem(SaleItem item)
        +double calculateTotal()
        +void register()
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

    Customer "1" --> "0..*" Sale : makes
    Seller "1" --> "0..*" Sale : attends

    Sale "1" *-- "1.." SaleItem : contains
    SaleItem "0..*" --> "1" Product : includes