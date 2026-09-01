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
    end

    subgraph Persistence [Layer persistence]
        direction TB
        FileManager
    end

    subgraph Model [Layer model]
        direction TB
        Person
        Customer
        Seller
        Product
        VideoGame
        Console
        Sale
        SaleItem
    end

    UI --> Service
    Service --> Persistence
    Service --> Model
    Persistence --> Model