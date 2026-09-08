# Bitácora de Consultas Técnicas — Java / Manejo de Archivos

---

## 1. Manejo de archivos en Java (lectura/escritura general)

**Pregunta o problema planteado:**
Cómo manejar archivos en Java desde cero (lectura y escritura).

**Solución técnica sugerida:**
- Lectura moderna con `Files.readAllLines()` (NIO.2).
- Lectura línea por línea con `BufferedReader` dentro de `try-with-resources`.
- Escritura con `BufferedWriter` / `FileWriter`, incluyendo modo *append* (`new FileWriter(ruta, true)`).
- Alternativa con `Scanner` para lectura, común en cursos introductorios.

**Lección clave o aplicación:**
`try-with-resources` cierra automáticamente los recursos (archivos) incluso si ocurre una excepción, evitando fugas de recursos. Se integra como patrón base para todos los métodos de persistencia (`loadCustomer`, `saveCustomer`, etc.) del proyecto.

---

## 2. Clase abstracta en Java

**Pregunta o problema planteado:**
Qué es una clase abstracta y cómo se diferencia de una interfaz.

**Solución técnica sugerida:**
Definición de clase con `abstract`, combinando métodos abstractos (sin cuerpo, obligatorios de implementar) y métodos concretos (con cuerpo, heredables). Ejemplo con jerarquía `Animal` → `Perro`.

**Lección clave o aplicación:**
Una clase abstracta permite definir comportamiento común y forzar que las subclases implementen los detalles específicos. Es la base conceptual para modelar jerarquías como `Customer`/`Seller` si comparten comportamiento pero difieren en detalles.

---

## 3. Actualizar repositorio Git local con GitHub

**Pregunta o problema planteado:**
Cómo traer los cambios más recientes del repositorio remoto (GitHub) al repositorio local.

**Solución técnica sugerida:**
Uso de `git pull origin main` (fetch + merge combinados), o alternativa en dos pasos con `git fetch` + `git merge` para mayor control, junto con manejo de conflictos (`git add` + `git commit` tras resolverlos).

**Lección clave o aplicación:**
Antes de sincronizar, es importante gestionar cambios locales pendientes (`git status`, `commit` o `checkout --`) para evitar pérdida de trabajo o conflictos innecesarios.

---

## 4. `Unhandled exception: java.io.IOException`

**Pregunta o problema planteado:**
Error de compilación por excepción no manejada al trabajar con archivos.

**Solución técnica sugerida:**
Dos enfoques: envolver el código en `try-catch (IOException e)`, o declarar `throws IOException` en la firma del método. Se recomendó `try-with-resources` como mejor práctica.

**Lección clave o aplicación:**
`IOException` es una excepción **comprobada** (checked exception): Java obliga a manejarla explícitamente. Este concepto es transversal a toda operación de E/S del proyecto (lectura/escritura de CSV).

---

## 5. CSV vs. TXT: ¿cuál es más fácil de manejar?

**Pregunta o problema planteado:**
Comparar la dificultad de manejo entre archivos `.csv` y `.txt`.

**Solución técnica sugerida:**
TXT es más simple para texto plano sin estructura (`readLine()` directo). CSV requiere `split(",")` pero ofrece estructura tabular, compatible con Excel/Sheets.

**Lección clave o aplicación:**
La elección del formato depende del tipo de dato: texto simple → TXT; datos con múltiples campos por registro (como `Customer`, `Seller`) → CSV. Justifica el uso de CSV en el proyecto del repositorio virtual.

---

## 6. Depuración de `saveCustomer()` (escritura CSV)

**Pregunta o problema planteado:**
Por qué un método de guardado de clientes en CSV daba error.

**Solución técnica sugerida:**
Revisión sistemática de causas posibles: tipo incorrecto de `customerCSV`, método `aCsv()` inexistente o mal implementado, ruta de archivo inválida, permisos de escritura.

**Lección clave o aplicación:**
Ante un error con `try-catch` que solo imprime el mensaje, es clave revisar el `System.err.println` exacto antes de descartar hipótesis — el manejo de excepciones debe ser informativo, no solo silencioso.

---

## 7. `loadCustomer()` no imprime datos, pero `loadSeller()` sí

**Pregunta o problema planteado:**
Un bucle `for` con la misma lógica no mostraba los clientes, aunque el CSV tenía datos.

**Solución técnica sugerida:**
Verificación de: tamaño real de la lista (`.size()`), excepciones "tragadas" silenciosamente en el `catch`, discrepancias en la ruta del archivo, y errores de parseo (`ArrayIndexOutOfBoundsException`) por número de columnas distinto entre `Customer` y `Seller`.

**Lección clave o aplicación:**
Cuando dos métodos con lógica idéntica se comportan distinto, el problema casi siempre está en los datos de entrada o en el manejo de excepciones que oculta el fallo real, no en el bucle que los consume.

---

## 8. Uso de `final` en un atributo

**Pregunta o problema planteado:**
Qué significa la palabra clave `final` antes de un atributo.

**Solución técnica sugerida:**
`final` impide reasignar el valor una vez inicializado (en la declaración o en el constructor). Para objetos, `final` protege la referencia, no el contenido interno modificable del objeto.

**Lección clave o aplicación:**
Aplicable a campos como `id` en `Customer`/`Seller`, que no deberían cambiar tras la creación del objeto — refuerza la integridad de los datos del modelo.

---

## 9. `Collections.unmodifiableList()` en getters de listas

**Pregunta o problema planteado:**
Qué hace el patrón `Collections.unmodifiableList(customers)` en métodos como `listAllCustomers()`.

**Solución técnica sugerida:**
Devuelve una vista de solo lectura de la lista interna, lanzando `UnsupportedOperationException` si se intenta modificar desde fuera de la clase.

**Lección clave o aplicación:**
Es una técnica de **encapsulamiento**: protege el estado interno (`customers`, `sellers`) para que solo pueda modificarse a través de los métodos propios de la clase (como `saveCustomer`), evitando alteraciones no controladas desde el exterior.

---

## 10. Alternativas a `unmodifiableList()`

**Pregunta o problema planteado:**
Si existen otras formas de exponer una lista de solo lectura además de `unmodifiableList()`.

**Solución técnica sugerida:**
`List.copyOf()` (Java 10+), `stream().toList()` (Java 16+), `Collectors.toUnmodifiableList()`, variantes para `Set`/`Map`, o exponer solo un `Iterator`.

**Lección clave o aplicación:**
Diferencia clave entre **vista** (`unmodifiableList`, refleja cambios futuros de la lista original) y **copia inmutable** (`copyOf`, `toList`, congelada en el momento). La elección depende de si se necesita sincronización con el estado interno o un snapshot fijo.

---

## 11. Eliminar un registro concreto de un CSV

**Pregunta o problema planteado:**
Cómo eliminar un registro específico dentro de un archivo CSV.

**Solución técnica sugerida:**
Patrón estándar: cargar todos los registros a una lista (`loadCustomer()`), filtrar con `removeIf()` según un criterio (ej. ID), y reescribir el archivo completo (`saveCustomer()`). Alternativa para archivos grandes: leer/escribir línea por línea usando un archivo temporal y luego reemplazar el original.

**Lección clave o aplicación:**
Los archivos de texto no permiten borrar contenido "desde el medio"; el patrón *leer → filtrar en memoria → sobrescribir* es la solución estándar para operaciones CRUD sobre archivos planos, y se integra directamente con los métodos `loadCustomer`/`saveCustomer` ya existentes en el proyecto.

---

*Bitácora generada a partir de la sesión de consultas técnicas sobre manejo de archivos, POO y Git en el contexto del proyecto de repositorio virtual.*