Analysis – GameZone Unicesar
1. ¿Qué atributos son comunes a todas las personas que interactúan con la tienda, y cuáles son propios de cada tipo específico de persona? ¿Cómo se refleja esta distinción en una jerarquía de clases?

All people in the system share the attributes name, identification, and phone. A Customer additionally has an email and a purchase history, while a Seller has an employeeCode and a workShift.

This distinction can be represented through an abstract Person base class containing the common attributes. Customer and Seller inherit from Person and add their specific attributes.

2. ¿Debería existir una clase que represente a una "persona genérica" sin especificar su rol? ¿Por qué sí o por qué no? ¿Qué implicación tiene esta decisión sobre la posibilidad de instanciar dicha clase?

Yes, a generalized Person class should exist because it avoids code duplication and represents the common characteristics of all people in the system.

It should be declared as an abstract class, preventing the creation of a generic person without a specific role. Only concrete subclasses such as Customer and Seller should be instantiated.

3. ¿Qué características tienen en común todos los productos que comercializa la tienda, independientemente de su tipo? ¿Qué características son específicas de cada tipo de producto?

All products share the attributes id, title, price, and availableQuantity.

A VideoGame has the specific attributes platform, genre, and ageRating. A Console has brand, model, and generation.

These common attributes should belong to an abstract Product class, while VideoGame and Console inherit from it and define their specific characteristics.

4. Cada tipo de producto debe poder presentar una descripcion que integre sus características particulares. ¿Cómo debería declararse este comportamiento en la clase base para garantizar que todas las subclases lo implementen de manera propia? ¿Qué mecanismo de la programación orientada a objetos permite esto?

The Product class should declare an abstract method such as getDescription().

Each subclass, such as VideoGame and Console, must override this method to provide a description based on its own characteristics.

This is achieved through method overriding and polymorphism, specifically dynamic dispatch.

5. Una venta involucra a un cliente, a un vendedor y a uno o más productos. ¿Qué tipo de relaciones existen entre la clase que representa la venta y las demás clases del sistema? ¿Estas relaciones son de herencia, de asociación, de composición o de otro tipo? Justifique.

Sale has associations with Customer and Seller, because a sale records which customer made the purchase and which seller handled it.

Sale also has a relationship with one or more Product objects. Since products exist independently of a sale, this relationship is better represented as an association or aggregation, rather than composition.

These relationships are not inheritance relationships because Sale does not represent a specialized type of Customer, Seller, or Product.

6. ¿Debería la venta ser responsable de calcular su propio total, o esta responsabilidad debería recaer en otra clase? Argumente su decisión.

Sale should be responsible for calculating its own total because it contains the products involved in the transaction and the quantities purchased.

This follows the Information Expert principle: the class that has the necessary information should perform the operation.

Therefore, a method such as calculateTotal() should belong to Sale, while SaleService should coordinate the transaction instead of calculating the total itself.

7. ¿Cómo se garantiza en el diseño que una venta no pueda registrarse sin al menos un producto? ¿En qué punto del sistema debería validarse esta regla?

A Sale must contain at least one product. This rule should be validated when creating or completing the sale.

The preferred approach is to enforce the rule inside the Sale class, for example through its constructor or a method responsible for confirming the sale. The service layer can also perform an additional validation before persistence.

If the product collection is empty, the system should reject the sale by throwing an exception or returning an appropriate validation error.

8. ¿Cómo se refleja en el diseño la actualización automática del inventario cuando se registra una venta? ¿Qué clases se ven involucradas en esta operación?

When a sale is registered, SaleService should coordinate the inventory update.

It should verify that each product has enough availableQuantity and then invoke a method such as reduceStock(quantity) on the corresponding Product.

The main classes involved are Sale, SaleService, and Product. VideoGame and Console also participate through their inheritance from Product.

This design keeps transaction coordination in the service layer while the product remains responsible for maintaining its own inventory state.

9. El sistema debe organizarse en cuatro capas: modelo, persistencia, servicios e interfaz de usuario. ¿Qué tipo de clases pertenecen a cada capa? ¿Qué criterio permite decidir en qué capa debe ubicarse una clase?

The system should be organized into four layers:

Model: Person, Customer, Seller, Product, VideoGame, Console, Sale, and other domain entities.
Persistence: FileManager and classes responsible for saving and loading information from files.
Services: SaleService and other classes responsible for business rules, validations, and transaction coordination.
User Interface: classes responsible for menus, user input, and interaction with the application.

The criterion is the responsibility of each class. A class should be placed in the layer that best represents the responsibility it performs.

10. ¿Por qué la logica de guardar y recuperar datos de archivos no debe estar dentro de las clases del dominio? ¿Qué problemas se generan cuando estas responsabilidades se mezclan?

File storage and retrieval should not be implemented inside domain classes such as Product or Sale.

Mixing these responsibilities violates the Single Responsibility Principle (SRP) and creates strong coupling between the domain model and a specific storage mechanism.

For example, if Sale directly manages JSON or TXT files, changing the persistence mechanism would require modifying the domain class. Separating these responsibilities makes the system easier to maintain, test, and extend.

11. ¿Qué dependencias están permitidas entre las capas y cuáles están prohibidas? Justifique el sentido de las dependencias permitidas.

The allowed dependencies should follow this direction:

UI -> Services -> Model

Services -> Persistence

Persistence -> Model

Therefore, UI may depend on Services, Services may depend on Model and Persistence, and Persistence may depend on Model.

The Model layer must not depend on UI, Services, or Persistence.

This structure keeps the domain model independent from implementation details such as the user interface and file storage. It also provides a clear separation of responsibilities and reduces coupling between layers.
