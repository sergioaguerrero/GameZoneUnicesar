```mermaid

flowchart TD

    subgraph UI [Layer ui]
        direction TB
        Main
        UserInterface
    end

    subgraph Service [Layer service]
        direction TB
        SaleService
        ProductService
        PersonService
        AccessoryService
    end

    subgraph Persistence [Layer persistence]
        direction TB
        PersonRepository
        SaleRepository
        ProductRepository
        AccessoryRepository
    end

    subgraph Model [Layer model]
        direction TB
        Person
        Customer
        Seller
        Product
        VideoGame
        Console
        Accessory
        Controller
        Cable
        Memory
        Sale
        SaleItem
    end

    UI --> Service
    Service --> Persistence
    Service --> Model
    Persistence --> Model