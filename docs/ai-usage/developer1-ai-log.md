# AI Consultation Log — Developer 1 (Product Module)
**Student:** Isaac David Mattos Botello  
**Student ID:** 1042854691  
**Role:** Developer 1 — Product Module  
**Tool used:** Claude (Anthropic)  

---

## 🇬🇧 English Version

### Entry 1 — Compiler error after extending an abstract class
* **Problem raised:** After creating `VideoGame extends Product`, NetBeans reported: *"VideoGame is not abstract and does not override abstract method getFullDescription() in Product."*
* **Suggested solution:** Explained that the error is expected — a concrete subclass must implement every abstract method inherited from its parent. The fix was adding a properly annotated `@Override` implementation of `getFullDescription()` in `VideoGame` and, later, in `Console`.
* **Key lesson:** The Java compiler actively enforces the "contract" established by an abstract method — it will not compile a concrete class that leaves an inherited abstract method unimplemented. This reinforced why the design decision in Entry 1 is safe: it's impossible to accidentally forget to implement the description logic.

### Entry 2 — Layered architecture and file-based persistence
* **Problem raised:** How to persist a list of products (a polymorphic list containing both `VideoGame` and `Console`) to disk, while keeping `ProductService` completely unaware of how the data is stored.
* **Suggested solution:** Introduce `ProductRepository` as the only class allowed to read/write files, exposing just two public methods (`saveAll(List)` and `loadAll()`), which `ProductService` calls without knowing the underlying storage format.
* **Key lesson:** Separating persistence from business logic means the storage format can change without touching the service or model layers at all — a direct, practical demonstration of why the assignment requires a layered architecture with restricted dependencies (`service` → `persistence`, never `ui` → `persistence`).

### Entry 3 — Switching from serialization to CSV
* **Problem raised:** After comparing the initial implementation (Java serialization with `ObjectOutputStream`/`ObjectInputStream`) against a classmate's CSV-based approach, decided to switch `ProductRepository` to CSV for a human-readable, inspectable data file.
* **Suggested solution:** Rewrite `saveAll`/`loadAll` using `BufferedWriter`/`BufferedReader`, and add two private helper methods, `toCsvLine` and `fromCsvLine`, using a discriminator column (`VIDEOGAME`/`CONSOLE`) and Java's pattern-matching `instanceof` to identify each product's concrete type:
  ```java
  if (product instanceof VideoGame videoGame) { ... }
  ```
* **Key lesson:** Confirmed, in practice, the value of the layered design — switching the entire persistence mechanism only required changes inside `ProductRepository`. `Product`, `VideoGame`, `Console`, and `ProductService` needed no modification at all (beyond an optional cleanup: removing the now-unnecessary `Serializable` interface from `Product`).

### Entry 4 — Git: resolving a "detached HEAD" state
* **Problem raised:** After checking out a remote tracking branch in Git GUI (`origin/develop`, later `origin/feature/product-module`), got the message *"You are no longer on a local branch."* multiple times during the workflow.
* **Suggested solution:** Explained that this is the "detached HEAD" state — positioned exactly on the remote commit, but without a local branch name pointing to it. The fix: **Branch > Create Branch**, using **"Revision Expression: HEAD"** (and "Reset" when the local branch name already existed) to create a proper local branch at that exact commit.
* **Key lesson:** Understood the difference between a commit (a point in history) and a branch (a movable pointer to a commit) — checking out a specific commit by itself doesn't give you a branch to work from unless you explicitly create one.

### Entry 5 — Merging team changes without losing local work
* **Problem raised:** Needed to bring a teammate's completed work (person module classes, updated `.gitignore`) from `develop` into the `feature/product-module` branch, without discarding uncommitted local changes.
* **Suggested solution:** A two-step merge: first fast-forward the local `develop` branch to match `origin/develop` (Fetch + Merge), then merge that updated `develop` into `feature/product-module` (Merge > Local Merge). Emphasized committing and pushing all pending local work before switching branches.
* **Key lesson:** Integration in a team project flows through the shared branch (`develop`), not directly between feature branches — this matches the Git Flow model required by the assignment, where all `feature/*` branches derive from and reintegrate through `develop`.

### Entry 6 — Diagnosing a Maven/JDK version mismatch
* **Problem raised:** Clean and Build failed with error: `invalid target release: 26`, unrelated to any code written for this module.
* **Suggested solution:** Identified the cause in `pom.xml` (`maven.compiler.source/target` set to `26`, a version not matching the locally installed JDK), and proposed testing locally with a temporary downgrade to `25` — explicitly without committing that change, since `pom.xml` is a shared file owned by the Technical Lead.
* **Key lesson:** Learned to distinguish between a code error and a build/environment configuration error, and the importance of not modifying shared configuration files unilaterally in a team Git workflow — instead communicating the issue to the responsible teammate.

---

## 🇪🇸 Versión en Español

# Registro de Consultas con IA — Desarrollador 1 (Módulo de Producto)
* **Estudiante:** Isaac David Mattos Botello  
* **ID del estudiante:** 1042854691  
* **Rol:** Desarrollador 1 — Módulo de Producto  
* **Herramienta utilizada:** Claude (Anthropic)  
* **Descripción:** Este documento registra cada consulta significativa realizada durante el desarrollo del módulo de producto, siguiendo la estructura requerida por la asignación: problema planteado → solución sugerida → aprendizaje clave.

### Entrada 1 — Error del compilador al extender una clase abstracta
* **Problema planteado:** Después de crear `VideoGame extends Product`, NetBeans mostró el siguiente error:  
  > *"VideoGame is not abstract and does not override abstract method getFullDescription() in Product."*
* **Solución sugerida:** Se explicó que el error era el comportamiento esperado: una subclase concreta debe implementar todos los métodos abstractos heredados de su clase padre. La solución consistió en agregar una implementación de `getFullDescription()` con la anotación `@Override` en `VideoGame` y, posteriormente, también en `Console`.
* **Aprendizaje clave:** El compilador de Java hace cumplir el “contrato” definido por un método abstracto. Una clase concreta no puede compilar si deja sin implementar un método abstracto heredado. Esto reforzó que el diseño es seguro, ya que es imposible olvidar accidentalmente la lógica de descripción.

### Entrada 2 — Arquitectura por capas y persistencia en archivos
* **Problema planteado:** ¿Cómo guardar en disco una lista de productos (polimórfica, con objetos `VideoGame` y `Console`) manteniendo `ProductService` completamente independiente del modo en que se almacenan los datos?
* **Solución sugerida:** Se propuso crear `ProductRepository` como la única clase responsable de leer y escribir archivos, exponiendo únicamente dos métodos públicos:
  * `saveAll(List)`
  * `loadAll()`  
  `ProductService` utiliza esos métodos sin conocer el formato de almacenamiento.
* **Aprendizaje clave:** Separar la persistencia de la lógica de negocio permite cambiar el mecanismo de almacenamiento sin modificar las capas de servicio ni de modelo. Es un ejemplo práctico de la arquitectura por capas exigida en el proyecto: `servicio` → `persistencia`, nunca `interfaz` → `persistencia`.

### Entrada 3 — Cambio de serialización a CSV
* **Problema planteado:** Tras comparar la implementación inicial (serialización con `ObjectOutputStream` y `ObjectInputStream`) con el enfoque basado en CSV utilizado por un compañero, se decidió migrar `ProductRepository` a un formato CSV legible por humanos.
* **Solución sugerida:** Reescribir los métodos `saveAll()` y `loadAll()` utilizando `BufferedWriter` y `BufferedReader`, además de crear dos métodos privados auxiliares: `toCsvLine()` y `fromCsvLine()`. Se añadió una columna discriminadora (`VIDEOGAME` / `CONSOLE`) y se utilizó `instanceof` con pattern matching para identificar el tipo concreto del producto:
  ```java
  if (product instanceof VideoGame videoGame) { ... }
  ```
* **Aprendizaje clave:** La arquitectura por capas demostró nuevamente su utilidad: el cambio completo del sistema de persistencia solo requirió modificaciones en `ProductRepository`. Las clases `Product`, `VideoGame`, `Console` y `ProductService` permanecieron intactas, salvo una limpieza opcional: eliminar la interfaz `Serializable` de `Product`.

### Entrada 4 — Git: resolución del estado "detached HEAD"
* **Problema planteado:** Después de cambiar a una rama remota (`origin/develop` y posteriormente `origin/feature/product-module`) desde la interfaz gráfica de Git, apareció repetidamente el mensaje:  
  > *"You are no longer on a local branch."*
* **Solución sugerida:** Se explicó que ese mensaje corresponde al estado *detached HEAD*: el repositorio está situado sobre un commit específico, pero ninguna rama local apunta a él. La solución fue crear una nueva rama mediante **Branch → Create Branch**, utilizando **Revision Expression: HEAD** (y la opción *Reset* cuando el nombre de la rama ya existía).
* **Aprendizaje clave:** Se comprendió la diferencia entre un commit (un punto del historial) y una rama (un puntero móvil hacia un commit). Estar sobre un commit no significa estar trabajando dentro de una rama hasta que esta se crea explícitamente.

### Entrada 5 — Integración de cambios del equipo sin perder trabajo local
* **Problema planteado:** Era necesario incorporar al módulo `feature/product-module` el trabajo terminado por un compañero (clases del módulo de personas y actualización del `.gitignore`) sin perder cambios locales aún no confirmados.
* **Solución sugerida:** Se recomendó un proceso de integración en dos pasos:
  1. Actualizar la rama local `develop` con `origin/develop` mediante *Fetch + Merge*.
  2. Fusionar esa `develop` actualizada dentro de `feature/product-module` mediante *Merge → Local Merge*.  
  Además, se enfatizó la importancia de hacer commit y push de todos los cambios pendientes antes de cambiar de rama.
* **Aprendizaje clave:** En un proyecto colaborativo, la integración se realiza a través de la rama compartida `develop`, no directamente entre ramas *feature*. Esto sigue el modelo Git Flow establecido para el desarrollo del proyecto.

### Entrada 6 — Diagnóstico de incompatibilidad entre Maven y JDK
* **Problema planteado:** La opción *Clean and Build* falló con el error: `invalid target release: 26`. El problema no estaba relacionado con el código desarrollado en el módulo de producto.
* **Solución sugerida:** Se identificó que la causa estaba en el archivo `pom.xml`, donde las propiedades `maven.compiler.source` y `maven.compiler.target` estaban configuradas con la versión `26`, incompatible con el JDK instalado localmente. Se propuso realizar una prueba temporal cambiando la versión a `25`, dejando claro que ese cambio no debía confirmarse (*commit*) porque `pom.xml` es un archivo compartido administrado por el Líder Técnico.
* **Aprendizaje clave:** Se aprendió a diferenciar un error de programación de un problema de configuración del entorno de compilación, así como la importancia de no modificar unilateralmente archivos de configuración compartidos dentro de un flujo de trabajo colaborativo con Git.
