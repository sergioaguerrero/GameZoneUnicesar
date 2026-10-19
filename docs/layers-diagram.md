```mermaid

flowchart TD

    subgraph UI_Layer [Layer ui]
        direction TB
        UI_Main[Main]
        UI_ConsoleMenu[ConsoleMenu]
    end

    subgraph Service_Layer [Layer service]
        direction TB
        S_Sale[SaleService]
        S_Product[ProductService]
        S_Person[PersonService]
        S_Accessory[AccessoryService]
        S_Promotion[PromotionService]
        S_Warranty[WarrantyService]
        S_Return[ReturnService]
    end

    subgraph Persistence_Layer [Layer persistence]
        direction TB
        P_Person[PersonRepository]
        P_Sale[SaleRepository]
        P_Product[ProductRepository]
        P_Accessory[AccessoryRepository]
        P_Promotion[PromotionRepository]
        P_Warranty[WarrantyRepository]
        P_Return[ReturnRepository]
    end

    subgraph Model_Layer [Layer model]
        direction TB
        M_Person[Person]
        M_Customer[Customer]
        M_Seller[Seller]
        M_Product[Product]
        M_VideoGame[VideoGame]
        M_Console[Console]
        M_Accessory[Accessory]
        M_Controller[Controller]
        M_Cable[Cable]
        M_Memory[Memory]
        M_Promotion[Promotion]
        M_Percentage[PercentageDiscount]
        M_Category[CategoryDiscount]
        M_Bulk[BulkPurchaseDiscount]
        M_Sale[Sale]
        M_SaleItem[SaleItem]
        M_Warranty[Warranty]
        M_Basic[BasicWarranty]
        M_Extended[ExtendedWarranty]
        M_Return[Return]
        M_ReturnItem[ReturnItem]
    end

    UI_Layer --> Service_Layer
    Service_Layer --> Persistence_Layer
    Service_Layer --> Model_Layer
    Persistence_Layer --> Model_Layer