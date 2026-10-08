```mermaid
classDiagram
    class Product {
        <<abstract>>
        -String productId
        -String title
        -double price
        -int stockQuantity
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

    class Console {
        -String brand
        -String model
        -int generation
    }

    class AccessoryRepository {
        +void saveAll(List~Accessory~ accessories)
        +List~Accessory~ loadAll()
    }

    class AccessoryService {
        -AccessoryRepository accessoryRepository
        +AccessoryService(AccessoryRepository repository)
        +void addAccessory(Accessory accessory)
        +List~Accessory~ getAllAccessories()
    }

    Product <|-- Accessory
    Accessory <|-- Controller
    Accessory <|-- Cable
    Accessory <|-- Memory
    Accessory "0..*" --> "0..*" Console : compatible with
    AccessoryService ..> AccessoryRepository : uses
    AccessoryService ..> Accessory : manages

