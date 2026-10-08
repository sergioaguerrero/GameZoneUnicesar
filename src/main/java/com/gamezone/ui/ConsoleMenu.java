package com.gamezone.ui;

import com.gamezone.model.Accessory;
import com.gamezone.model.Cable;
import com.gamezone.model.Console;
import com.gamezone.model.Controller;
import com.gamezone.model.Customer;
import com.gamezone.model.Memory;
import com.gamezone.model.Product;
import com.gamezone.model.Sale;
import com.gamezone.model.SaleItem;
import com.gamezone.model.Seller;
import com.gamezone.model.VideoGame;
import com.gamezone.model.Warranty;
import com.gamezone.service.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Console-based user interface for GameZone Unicesar. All messages shown to
 * the user are written in Spanish, as required, while identifiers, comments
 * and Javadoc stay in English. This class contains no business rules of its
 * own; it only reads and validates user input, calls the appropriate
 * service, and prints the results. Every listing is numbered (1..n) to make
 * items easier to reference, and every field read from the user is
 * validated before it is passed on to a service.
 */
public class ConsoleMenu {

    private final ProductService productService;
    private final PersonService personService;
    private final SaleService saleService;
    private final AccessoryService accessoryService;
    private final Scanner scanner;
    private final PromotionService promotionService;
    private final ReturnService returnService;

    /**
     * Creates a new console menu backed by the given services.
     *
     * @param productService   the service used for product operations
     * @param personService    the service used for customer and seller operations
     * @param saleService      the service used for sale operations
     * @param accessoryService the service used for accessory operations
     * @param promotionService the service used for promotion operations
     * @param returnService    the service used for return operations
     */
    public ConsoleMenu(ProductService productService, PersonService personService,
                       SaleService saleService, AccessoryService accessoryService, 
                       PromotionService promotionService, ReturnService returnService) {
        this.productService = productService;
        this.personService = personService;
        this.saleService = saleService;
        this.accessoryService = accessoryService;
        this.scanner = new Scanner(System.in);
        this.promotionService = promotionService;
        this.returnService = returnService;
    }

    /**
     * Starts the main menu loop until the user chooses to exit.
     */
    public void start() {
        boolean running = true;
        while (running) {
            System.out.println();
            System.out.println("===== GameZone Unicesar =====");
            System.out.println("1. Gestión de productos");
            System.out.println("2. Gestión de personas");
            System.out.println("3. Gestión de ventas");
            System.out.println("4. Gestión de accesorios");
            System.out.println("5. Gestión de promociones");
            System.out.println("6. Gestión de devoluciones");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opción: ");
            String option = scanner.nextLine().trim();
            switch (option) {
                case "1":
                    showProductMenu();
                    break;
                case "2":
                    showPersonMenu();
                    break;
                case "3":
                    showSaleMenu();
                    break;
                case "4":
                    showAccessoryMenu();
                    break;
                case "5":
                    showPromotionMenu();
                    break;
                case "6":
                    showReturnMenu();
                    break;
                case "0":
                    running = false;
                    System.out.println("Gracias por usar GameZone Unicesar.");
                    break;
                default:
                    System.out.println("Opción inválida.");
            }
        }
    }

    // ---------------------------------------------------------------
    // Product management
    // ---------------------------------------------------------------

    private void showProductMenu() {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("----- Gestión de productos -----");
            System.out.println("1. Registrar videojuego");
            System.out.println("2. Registrar consola");
            System.out.println("3. Listar todos los productos");
            System.out.println("4. Buscar producto por ID");
            System.out.println("5. Actualizar producto");
            System.out.println("6. Eliminar producto");
            System.out.println("7. Listar solo videojuegos");
            System.out.println("8. Listar solo consolas");
            System.out.println("0. Volver");
            System.out.print("Seleccione una opción: ");
            String option = scanner.nextLine().trim();
            switch (option) {
                case "1":
                    registerVideoGame();
                    break;
                case "2":
                    registerConsole();
                    break;
                case "3":
                    listAllProducts();
                    break;
                case "4":
                    findProductById();
                    break;
                case "5":
                    updateProduct();
                    break;
                case "6":
                    deleteProduct();
                    break;
                case "7":
                    listVideoGamesOnly();
                    break;
                case "8":
                    listConsolesOnly();
                    break;
                case "0":
                    back = true;
                    break;
                default:
                    System.out.println("Opción inválida.");
            }
        }
    }

    private void registerVideoGame() {
        String id = readRequiredText("ID: ");
        if (productService.findById(id).isPresent()) {
            System.out.println("Ya existe un producto con el ID " + id + ".");
            return;
        }
        String title = readRequiredText("Título: ");
        double price = readPositiveDouble("Precio: ");
        int stock = readNonNegativeInt("Stock: ");
        String platform = readRequiredText("Plataforma: ");
        String genre = readRequiredText("Género: ");
        String ageRating = readRequiredText("Clasificación por edad: ");
        try {
            VideoGame videoGame = productService.registerVideoGame(id, title, price, stock,
                    platform, genre, ageRating);
            System.out.println("Videojuego registrado exitosamente: " + videoGame.getFullDescription());
        } catch (RuntimeException e) {
            System.out.println("Error al registrar el videojuego: " + e.getMessage());
        }
    }

    private void registerConsole() {
        String id = readRequiredText("ID: ");
        if (productService.findById(id).isPresent()) {
            System.out.println("Ya existe un producto con el ID " + id + ".");
            return;
        }
        String title = readRequiredText("Título: ");
        double price = readPositiveDouble("Precio: ");
        int stock = readNonNegativeInt("Stock: ");
        String brand = readRequiredText("Marca: ");
        String model = readRequiredText("Modelo: ");
        int generation = readPositiveInt("Generación: ");
        try {
            Console console = productService.registerConsole(id, title, price, stock,
                    brand, model, generation);
            System.out.println("Consola registrada exitosamente: " + console.getFullDescription());
        } catch (RuntimeException e) {
            System.out.println("Error al registrar la consola: " + e.getMessage());
        }
    }

    private void listAllProducts() {
        List<Product> products = productService.listAllProducts();
        if (products.isEmpty()) {
            System.out.println("No hay productos registrados.");
            return;
        }
        int index = 1;
        for (Product product : products) {
            System.out.println(index + ". " + product.getFullDescription());
            index++;
        }
    }

    /**
     * Lists only the products that are video games, numbered from 1 to n.
     * Filtering is done in memory over {@link ProductService#listAllProducts()}
     * since the service does not expose a type-specific query.
     */
    private void listVideoGamesOnly() {
        List<Product> videoGames = new ArrayList<>();
        for (Product product : productService.listAllProducts()) {
            if (product instanceof VideoGame) {
                videoGames.add(product);
            }
        }
        if (videoGames.isEmpty()) {
            System.out.println("No hay videojuegos registrados.");
            return;
        }
        int index = 1;
        for (Product videoGame : videoGames) {
            System.out.println(index + ". " + videoGame.getFullDescription());
            index++;
        }
    }

    /**
     * Lists only the products that are consoles, numbered from 1 to n.
     * Filtering is done in memory over {@link ProductService#listAllProducts()}
     * since the service does not expose a type-specific query.
     */
    private void listConsolesOnly() {
        List<Product> consoles = new ArrayList<>();
        for (Product product : productService.listAllProducts()) {
            if (product instanceof Console) {
                consoles.add(product);
            }
        }
        if (consoles.isEmpty()) {
            System.out.println("No hay consolas registradas.");
            return;
        }
        int index = 1;
        for (Product console : consoles) {
            System.out.println(index + ". " + console.getFullDescription());
            index++;
        }
    }

    private void findProductById() {
        String id = readRequiredText("ID del producto: ");
        Optional<Product> product = productService.findById(id);
        if (product.isEmpty()) {
            System.out.println("No se encontró ningún producto con ese ID.");
            return;
        }
        System.out.println(product.get().getFullDescription());
    }

    private void updateProduct() {
        String id = readRequiredText("ID del producto a actualizar: ");
        if (productService.findById(id).isEmpty()) {
            System.out.println("No se encontró ningún producto con ese ID.");
            return;
        }
        String title = readRequiredText("Nuevo título: ");
        double price = readPositiveDouble("Nuevo precio: ");
        int stock = readNonNegativeInt("Nuevo stock: ");
        boolean updated = productService.updateProduct(id, title, price, stock);
        System.out.println(updated ? "Producto actualizado exitosamente."
                : "No se encontró ningún producto con ese ID.");
    }

    private void deleteProduct() {
        String id = readRequiredText("ID del producto a eliminar: ");
        boolean deleted = productService.deleteProduct(id);
        System.out.println(deleted ? "Producto eliminado exitosamente."
                : "No se encontró ningún producto con ese ID.");
    }

    // ---------------------------------------------------------------
    // Person management (customers and sellers)
    // ---------------------------------------------------------------

    private void showPersonMenu() {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("----- Gestión de personas -----");
            System.out.println("1. Registrar cliente");
            System.out.println("2. Registrar vendedor");
            System.out.println("3. Listar clientes");
            System.out.println("4. Listar vendedores");
            System.out.println("5. Buscar cliente por ID");
            System.out.println("6. Buscar vendedor por ID");
            System.out.println("0. Volver");
            System.out.print("Seleccione una opción: ");
            String option = scanner.nextLine().trim();
            switch (option) {
                case "1":
                    registerCustomer();
                    break;
                case "2":
                    registerSeller();
                    break;
                case "3":
                    listAllCustomers();
                    break;
                case "4":
                    listAllSellers();
                    break;
                case "5":
                    findCustomerById();
                    break;
                case "6":
                    findSellerById();
                    break;
                case "0":
                    back = true;
                    break;
                default:
                    System.out.println("Opción inválida.");
            }
        }
    }

    private void registerCustomer() {
        String id = readRequiredText("ID: ");
        if (personService.findCustomer(id) != null) {
            System.out.println("Ya existe un cliente con el ID " + id + ".");
            return;
        }
        String name = readRequiredText("Nombre completo: ");
        String phone = readRequiredText("Teléfono: ");
        String mail = readRequiredEmail("Correo electrónico: ");
        personService.registerCustomer(name, phone, id, mail);
        System.out.println("Cliente registrado exitosamente.");
    }

    private void registerSeller() {
        String id = readRequiredText("ID: ");
        if (personService.findSeller(id) != null) {
            System.out.println("Ya existe un vendedor con el ID " + id + ".");
            return;
        }
        String name = readRequiredText("Nombre completo: ");
        String phone = readRequiredText("Teléfono: ");
        String employeeCode = readRequiredText("Código de empleado: ");
        String workShift = readRequiredText("Turno de trabajo: ");
        personService.registerSeller(name, phone, id, employeeCode, workShift);
        System.out.println("Vendedor registrado exitosamente.");
    }

    private void listAllCustomers() {
        List<Customer> customers = personService.listCustomer();
        if (customers.isEmpty()) {
            System.out.println("No hay clientes registrados.");
            return;
        }
        int index = 1;
        for (Customer customer : customers) {
            System.out.println(index + ". " + customer.getId() + " - " + customer.getName()
                    + " - " + customer.getMail());
            index++;
        }
    }

    private void listAllSellers() {
        List<Seller> sellers = personService.listSeller();
        if (sellers.isEmpty()) {
            System.out.println("No hay vendedores registrados.");
            return;
        }
        int index = 1;
        for (Seller seller : sellers) {
            System.out.println(index + ". " + seller.getId() + " - " + seller.getName()
                    + " - " + seller.getWorkShift());
            index++;
        }
    }

    private void findCustomerById() {
        String id = readRequiredText("ID del cliente: ");
        Customer customer = personService.findCustomer(id);
        if (customer == null) {
            System.out.println("No se encontró ningún cliente con ese ID.");
            return;
        }
        System.out.println(customer.getId() + " - " + customer.getName() + " - "
                + customer.getPhone() + " - " + customer.getMail());
    }

    private void findSellerById() {
        String id = readRequiredText("ID del vendedor: ");
        Seller seller = personService.findSeller(id);
        if (seller == null) {
            System.out.println("No se encontró ningún vendedor con ese ID.");
            return;
        }
        System.out.println(seller.getId() + " - " + seller.getName() + " - " + seller.getPhone()
                + " - " + seller.getEmployeeCode() + " - " + seller.getWorkShift());
    }

    // ---------------------------------------------------------------
    // Sale management
    // ---------------------------------------------------------------

    private void showSaleMenu() {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("----- Gestión de ventas -----");
            System.out.println("1. Registrar venta");
            System.out.println("2. Ver historial completo de ventas");
            System.out.println("3. Ver ventas por cliente");
            System.out.println("4. Ver ventas por vendedor");
            System.out.println("0. Volver");
            System.out.print("Seleccione una opción: ");
            String option = scanner.nextLine().trim();
            switch (option) {
                case "1":
                    registerSale();
                    break;
                case "2":
                    printSales(saleService.listAllSales(), "No hay ventas registradas.");
                    break;
                case "3":
                    viewSalesByCustomer();
                    break;
                case "4":
                    viewSalesBySeller();
                    break;
                case "0":
                    back = true;
                    break;
                default:
                    System.out.println("Opción inválida.");
            }
        }
    }

    private void registerSale() {
        String customerId = readRequiredText("ID del cliente: ");
        if (personService.findCustomer(customerId) == null) {
            System.out.println("No se encontró ningún cliente con ese ID. La venta se cancela.");
            return;
        }
        String sellerId = readRequiredText("ID del vendedor: ");
        if (personService.findSeller(sellerId) == null) {
            System.out.println("No se encontró ningún vendedor con ese ID. La venta se cancela.");
            return;
        }
        int count = readPositiveInt("Cantidad de ítems distintos a vender: ");

        List<SaleItem> items = new ArrayList<>();
        List<String> consolesWithExtendedWarranty = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            String itemId = readRequiredText("ID del producto o accesorio " + i + " (videojuego, consola o accesorio): ");
            Product item = resolveSellableItem(itemId);
            if (item == null) {
                System.out.println("No se encontró ningún producto o accesorio con el ID " + itemId
                        + ". La venta se cancela.");
                return;
            }
            int quantity = readPositiveInt("Cantidad del ítem " + i + ": ");
            items.add(new SaleItem(item, quantity));
            if (item instanceof Console && readYesNo("La consola \"" + item.getTitle()
                    + "\" incluye garantía básica gratis (6 meses). ¿Desea agregar garantía extendida (12 meses, "
                    + "cubre daños accidentales, "
                    + "costo adicional del 10% del precio, se suma al total)? (s/n): ")) {
                consolesWithExtendedWarranty.add(item.getProductId());
            }
        }

        boolean registered = saleService.registerSale(customerId, sellerId, items, consolesWithExtendedWarranty);
        if (!registered) {
            System.out.println("No se pudo registrar la venta. Verifique los datos ingresados.");
            return;
        }
        List<Sale> allSales = saleService.listAllSales();
        Sale lastSale = allSales.get(allSales.size() - 1);
        System.out.println("Venta registrada. Recibo:");
        System.out.println(formatSaleReceipt(lastSale));
        System.out.println("ID de la venta (úselo para consultar garantías): " + lastSale.getId());
    }

    /**
     * Resolves an item id entered when registering a sale, looking it up
     * first among traditional products (video games and consoles) and then,
     * if not found there, among accessories. This is what allows a sale to
     * combine any mix of products and accessories in a single transaction.
     *
     * @param itemId the id entered by the user
     * @return the matching product or accessory, or {@code null} if no item
     * with that id is registered anywhere
     */
    private Product resolveSellableItem(String itemId) {
        Optional<Product> product = productService.findById(itemId);
        if (product.isPresent()) {
            return product.get();
        }
        Optional<Accessory> accessory = accessoryService.findById(itemId);
        return accessory.orElse(null);
    }

    private void viewSalesByCustomer() {
        String customerId = readRequiredText("ID del cliente: ");
        printSales(saleService.listSalesByCustomer(customerId), "Este cliente no tiene ventas registradas.");
    }

    private void viewSalesBySeller() {
        String sellerId = readRequiredText("ID del vendedor: ");
        printSales(saleService.listSalesBySeller(sellerId), "Este vendedor no tiene ventas registradas.");
    }

    /**
     * Prints a numbered list of sales as human-readable receipts, or a
     * fallback message when the list is empty.
     *
     * @param sales        the sales to print
     * @param emptyMessage the message to show when there are no sales
     */
    private void printSales(List<Sale> sales, String emptyMessage) {
        if (sales.isEmpty()) {
            System.out.println(emptyMessage);
            return;
        }
        int index = 1;
        for (Sale sale : sales) {
            System.out.println(index + ". " + formatSaleReceipt(sale));
            index++;
        }
    }

    /**
     * Builds a readable, receipt-style representation of a sale, since
     * {@link Sale} itself only exposes its raw attributes.
     *
     * @param sale the sale to format
     * @return the formatted receipt text
     */
    private String formatSaleReceipt(Sale sale) {
        return sale.generateReceipt();
    }

    // ---------------------------------------------------------------
    // Warranty management
    // ---------------------------------------------------------------

    private void showWarrantyMenu() {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("----- Gestión de garantías -----");
            System.out.println("1. Consultar garantía de un producto en una venta");
            System.out.println("2. Listar todas las garantías registradas");
            System.out.println("3. Listar garantías vigentes");
            System.out.println("4. Listar garantías próximas a vencer");
            System.out.println("0. Volver");
            System.out.print("Seleccione una opción: ");
            String option = scanner.nextLine().trim();
            switch (option) {
                case "1":
                    findWarrantyByProduct();
                    break;
                case "2":
                    printWarranties(warrantyService.listAllWarranties(), "No hay garantías registradas.");
                    break;
                case "3":
                    printWarranties(warrantyService.listActiveWarranties(), "No hay garantías vigentes.");
                    break;
                case "4":
                    listWarrantiesExpiringSoon();
                    break;
                case "0":
                    back = true;
                    break;
                default:
                    System.out.println("Opción inválida.");
            }
        }
    }

    private void findWarrantyByProduct() {
        String saleId = readRequiredText("ID de la venta: ");
        String productId = readRequiredText("ID del producto: ");
        Warranty warranty = warrantyService.findWarrantyByProduct(productId, saleId);
        if (warranty == null) {
            System.out.println("No se encontró ninguna garantía para ese producto en esa venta.");
            return;
        }
        System.out.println(warranty.generateWarrantyCertificate());
        System.out.println(warranty.isActive(LocalDate.now())
                ? "Estado: VIGENTE"
                : "Estado: VENCIDA");
    }

    private void listWarrantiesExpiringSoon() {
        int days = readPositiveInt("Días de anticipación: ");
        printWarranties(warrantyService.listWarrantiesExpiringSoon(days),
                "No hay garantías que venzan en los próximos " + days + " días.");
    }

    /**
     * Prints a numbered list of warranties in a compact one-line format, or a
     * fallback message when the list is empty.
     *
     * @param warranties   the warranties to print
     * @param emptyMessage the message to show when there are no warranties
     */
    private void printWarranties(List<Warranty> warranties, String emptyMessage) {
        if (warranties.isEmpty()) {
            System.out.println(emptyMessage);
            return;
        }
        int index = 1;
        for (Warranty warranty : warranties) {
            System.out.println(index + ". [" + warranty.getWarrantyId() + "] "
                    + warranty.getWarrantyType() + " - " + warranty.getProduct().getTitle()
                    + " - Venta " + warranty.getSale().getId()
                    + " - Vigencia: " + warranty.getStartDate() + " a " + warranty.getEndDate());
            index++;
        }
    }

    /**
     * Asks a yes/no question until the user answers with a valid option.
     *
     * @param prompt the question shown to the user
     * @return true if the user answered yes (s/si), false if no (n/no)
     */
    private boolean readYesNo(String prompt) {
        while (true) {
            System.out.print(prompt);
            String answer = scanner.nextLine().trim().toLowerCase();
            if (answer.equals("s") || answer.equals("si") || answer.equals("sí")) {
                return true;
            }
            if (answer.equals("n") || answer.equals("no")) {
                return false;
            }
            System.out.println("Respuesta inválida. Escriba 's' para sí o 'n' para no.");
        }
    }

    // ---------------------------------------------------------------
    // Promotion management
    // ---------------------------------------------------------------

    private void showPromotionMenu() {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("----- Gestión de promociones -----");
            System.out.println("1. Registrar promoción de porcentaje");
            System.out.println("2. Registrar promoción de categoría");
            System.out.println("3. Registrar promoción de volumen");
            System.out.println("4. Listar todas las promociones");
            System.out.println("5. Listar promociones vigentes hoy");
            System.out.println("0. Volver");
            System.out.print("Seleccione una opción: ");
            String option = scanner.nextLine().trim();
            switch (option) {
                case "1":
                    registerPercentagePromotion();
                    break;
                case "2":
                    registerCategoryPromotion();
                    break;
                case "3":
                    registerBulkPromotion();
                    break;
                case "4":
                    listPromotions(promotionService.listAllPromotions());
                    break;
                case "5":
                    listPromotions(promotionService.listActivePromotions());
                    break;
                case "0":
                    back = true;
                    break;
                default:
                    System.out.println("Opción inválida.");
            }
        }
    }

    private void registerPercentagePromotion() {
        String id = readRequiredText("ID: ");
        String name = readRequiredText("Nombre de la promoción: ");
        java.time.LocalDate startDate = readRequiredDate("Fecha inicio (AAAA-MM-DD): ");
        java.time.LocalDate endDate = readRequiredDate("Fecha fin (AAAA-MM-DD): ");
        double percentage = readPositiveDouble("Porcentaje de descuento (0-100): ");
        try {
            promotionService.registerPercentage(id, name, startDate, endDate, percentage);
            System.out.println("Promoción registrada exitosamente.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void registerCategoryPromotion() {
        String id = readRequiredText("ID: ");
        String name = readRequiredText("Nombre de la promoción: ");
        java.time.LocalDate startDate = readRequiredDate("Fecha inicio (AAAA-MM-DD): ");
        java.time.LocalDate endDate = readRequiredDate("Fecha fin (AAAA-MM-DD): ");
        double percentage = readPositiveDouble("Porcentaje de descuento (0-100): ");
        String category = readRequiredText("Categoría (VIDEOGAME o CONSOLE): ").toUpperCase();
        try {
            promotionService.registerCategory(id, name, startDate, endDate, percentage, category);
            System.out.println("Promoción registrada exitosamente.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void registerBulkPromotion() {
        String id = readRequiredText("ID: ");
        String name = readRequiredText("Nombre de la promoción: ");
        java.time.LocalDate startDate = readRequiredDate("Fecha inicio (AAAA-MM-DD): ");
        java.time.LocalDate endDate = readRequiredDate("Fecha fin (AAAA-MM-DD): ");
        int minQuantity = readPositiveInt("Cantidad mínima de ítems: ");
        double percentage = readPositiveDouble("Porcentaje de descuento (0-100): ");
        try {
            promotionService.registerBulk(id, name, startDate, endDate, minQuantity, percentage);
            System.out.println("Promoción registrada exitosamente.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void listPromotions(List<com.gamezone.model.Promotion> list) {
        if (list.isEmpty()) {
            System.out.println("No se encontraron promociones.");
            return;
        }
        int index = 1;
        for (com.gamezone.model.Promotion p : list) {
            System.out.println(index + ". [" + p.getId() + "] " + p.getName()
                    + " (Vigencia: " + p.getStartDate() + " a " + p.getEndDate() + ")");
            index++;
        }
    }

    private java.time.LocalDate readRequiredDate(String prompt) {
        while (true) {
            System.out.print(prompt);
            String raw = scanner.nextLine().trim();
            try {
                return java.time.LocalDate.parse(raw);
            } catch (Exception e) {
                System.out.println("Formato de fecha inválido. Intente de nuevo usando AAAA-MM-DD.");
            }
        }
    }

    // ---------------------------------------------------------------
    // Accessory management
    // ---------------------------------------------------------------

    private void showAccessoryMenu() {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("----- Gestión de accesorios -----");
            System.out.println("1. Registrar control");
            System.out.println("2. Registrar cable");
            System.out.println("3. Registrar memoria");
            System.out.println("4. Listar todos los accesorios");
            System.out.println("5. Listar accesorios por tipo");
            System.out.println("6. Listar accesorios compatibles con una consola");
            System.out.println("0. Volver");
            System.out.print("Seleccione una opción: ");
            String option = scanner.nextLine().trim();
            switch (option) {
                case "1":
                    registerController();
                    break;
                case "2":
                    registerCable();
                    break;
                case "3":
                    registerMemory();
                    break;
                case "4":
                    listAllAccessories();
                    break;
                case "5":
                    listAccessoriesByType();
                    break;
                case "6":
                    listAccessoriesCompatibleWithConsole();
                    break;
                case "0":
                    back = true;
                    break;
                default:
                    System.out.println("Opción inválida.");
            }
        }
    }

    private void registerController() {
        String id = readRequiredText("ID: ");
        if (accessoryService.findById(id).isPresent()) {
            System.out.println("Ya existe un accesorio con el ID " + id + ".");
            return;
        }
        String title = readRequiredText("Título: ");
        double price = readPositiveDouble("Precio: ");
        int stock = readNonNegativeInt("Stock: ");
        String connectionType = readRequiredText("Tipo de conexión (inalámbrico/alámbrico): ");
        try {
            Controller controller = accessoryService.registerController(id, title, price, stock,
                    connectionType);
            System.out.println("Control registrado exitosamente: " + controller.getFullDescription());
        } catch (RuntimeException e) {
            System.out.println("Error al registrar el control: " + e.getMessage());
        }
    }

    private void registerCable() {
        String id = readRequiredText("ID: ");
        if (accessoryService.findById(id).isPresent()) {
            System.out.println("Ya existe un accesorio con el ID " + id + ".");
            return;
        }
        String title = readRequiredText("Título: ");
        double price = readPositiveDouble("Precio: ");
        int stock = readNonNegativeInt("Stock: ");
        double length = readPositiveDouble("Longitud (metros): ");
        String connectorType = readRequiredText("Tipo de conector (HDMI, USB, óptico, etc.): ");
        try {
            Cable cable = accessoryService.registerCable(id, title, price, stock, length, connectorType);
            System.out.println("Cable registrado exitosamente: " + cable.getFullDescription());
        } catch (RuntimeException e) {
            System.out.println("Error al registrar el cable: " + e.getMessage());
        }
    }

    private void registerMemory() {
        String id = readRequiredText("ID: ");
        if (accessoryService.findById(id).isPresent()) {
            System.out.println("Ya existe un accesorio con el ID " + id + ".");
            return;
        }
        String title = readRequiredText("Título: ");
        double price = readPositiveDouble("Precio: ");
        int stock = readNonNegativeInt("Stock: ");
        int capacityGB = readPositiveInt("Capacidad (GB): ");
        String memoryType = readRequiredText("Tipo de memoria (SD, microSD, almacenamiento interno): ");
        try {
            Memory memory = accessoryService.registerMemory(id, title, price, stock,
                    capacityGB, memoryType);
            System.out.println("Memoria registrada exitosamente: " + memory.getFullDescription());
        } catch (RuntimeException e) {
            System.out.println("Error al registrar la memoria: " + e.getMessage());
        }
    }

    private void listAllAccessories() {
        List<Accessory> accessories = accessoryService.listAllAccessories();
        if (accessories.isEmpty()) {
            System.out.println("No hay accesorios registrados.");
            return;
        }
        int index = 1;
        for (Accessory accessory : accessories) {
            System.out.println(index + ". " + accessory.getFullDescription());
            index++;
        }
    }

    /**
     * Lets the user pick an accessory type from a fixed list instead of
     * typing it freely, because {@code AccessoryService.listAccessoriesByType}
     * compares the type with {@code equals} (case-sensitive, exact match).
     * The menu labels shown to the user are in Spanish, but the value
     * actually passed to the service ("Controller" / "Cable" / "Memory")
     * must keep matching whatever {@code getAccessoryType()} returns in the
     * model. If that method returns different text, update the three
     * values below to match it exactly.
     */
    private void listAccessoriesByType() {
        System.out.println("1. Control");
        System.out.println("2. Cable");
        System.out.println("3. Memoria");
        String option = readRequiredText("Seleccione un tipo: ");
        String type;
        switch (option) {
            case "1":
                type = "Controller";
                break;
            case "2":
                type = "Cable";
                break;
            case "3":
                type = "Memory";
                break;
            default:
                System.out.println("Opción inválida.");
                return;
        }

        List<Accessory> accessories = accessoryService.listAccessoriesByType(type);
        if (accessories.isEmpty()) {
            System.out.println("No se encontraron accesorios de ese tipo.");
            return;
        }
        int index = 1;
        for (Accessory accessory : accessories) {
            System.out.println(index + ". " + accessory.getFullDescription());
            index++;
        }
    }

    private void listAccessoriesCompatibleWithConsole() {
        String consoleId = readRequiredText("ID de la consola: ");
        List<Accessory> accessories = accessoryService.findAccessoriesCompatibleWith(consoleId);
        if (accessories.isEmpty()) {
            System.out.println("No hay accesorios registrados como compatibles con esa consola.");
            return;
        }
        int index = 1;
        for (Accessory accessory : accessories) {
            System.out.println(index + ". " + accessory.getFullDescription());
            index++;
        }
    }

    // ---------------------------------------------------------------
    // Return management
    // ---------------------------------------------------------------

    private void showReturnMenu() {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("----- Gestión de devoluciones -----");
            System.out.println("1. Registrar una nueva devolución");
            System.out.println("2. Consultar todas las devoluciones registradas");
            System.out.println("3. Consultar devoluciones por cliente");
            System.out.println("4. Consultar devoluciones por venta");
            System.out.println("5. Consultar el balance mensual");
            System.out.println("0. Volver");
            System.out.print("Seleccione una opción: ");
            String option = scanner.nextLine().trim();
            switch (option) {
                case "1":
                    registerReturn();
                    break;
                case "2":
                    viewAllReturns();
                    break;
                case "3":
                    viewReturnsByCustomer();
                    break;
                case "4":
                    viewReturnsBySale();
                    break;
                case "5":
                    generateMonthlyBalance();
                    break;
                case "0":
                    back = true;
                    break;
                default:
                    System.out.println("Opción inválida.");
            }
        }
    }

    private void registerReturn() {
        String saleId = readRequiredText("Identificador de la venta original: ");
        int count = readPositiveInt("Cantidad de productos a devolver: ");
        List<String> productIds = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            String productId = readRequiredText("ID del producto " + i + ": ");
            productIds.add(productId);
        }
        String reason = readRequiredText("Motivo de la devolución: ");
        
        try {
            com.gamezone.model.Return ret = returnService.registerReturn(saleId, productIds, reason);
            System.out.println("Devolución registrada exitosamente.");
            System.out.println(ret.generateReturnReceipt());
        } catch (IllegalArgumentException e) {
            System.out.println("Error al procesar la devolución: " + e.getMessage());
        }
    }

    private void viewAllReturns() {
        List<com.gamezone.model.Return> returns = returnService.viewAllReturns();
        printReturns(returns, "No hay devoluciones registradas.");
    }

    private void viewReturnsByCustomer() {
        String customerId = readRequiredText("ID del cliente: ");
        List<com.gamezone.model.Return> returns = returnService.viewReturnsByCustomer(customerId);
        printReturns(returns, "Este cliente no tiene devoluciones registradas.");
    }

    private void viewReturnsBySale() {
        String saleId = readRequiredText("ID de la venta: ");
        List<com.gamezone.model.Return> returns = returnService.viewReturnsBySale(saleId);
        printReturns(returns, "Esta venta no tiene devoluciones registradas.");
    }

    private void generateMonthlyBalance() {
        int month = readPositiveInt("Mes (1-12): ");
        if (month > 12) {
            System.out.println("Mes inválido. Debe ser entre 1 y 12.");
            return;
        }
        int year = readPositiveInt("Año (ej. 2026): ");
        double balance = returnService.generateMonthlyBalance(month, year);
        System.out.println("El balance mensual neto (Ventas - Devoluciones) para " + month + "/" + year + " es: $" + String.format("%.2f", balance));
    }

    private void printReturns(List<com.gamezone.model.Return> returns, String emptyMessage) {
        if (returns.isEmpty()) {
            System.out.println(emptyMessage);
            return;
        }
        int index = 1;
        for (com.gamezone.model.Return ret : returns) {
            System.out.println(index + ". " + ret.generateReturnReceipt());
            index++;
        }
    }

    // ---------------------------------------------------------------
    // Input reading and validation helpers
    // ---------------------------------------------------------------

    /**
     * Repeatedly prompts the user until a non-blank line of text is entered.
     *
     * @param prompt the message to display before reading
     * @return the trimmed, non-empty text entered by the user
     */
    private String readRequiredText(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();
            if (!value.isEmpty()) {
                return value;
            }
            System.out.println("Este campo no puede estar vacío. Intente de nuevo.");
        }
    }

    /**
     * Repeatedly prompts the user until a syntactically valid email address
     * is entered. Validation is intentionally simple (presence of "@" and a
     * domain with a dot) and is meant to catch obvious typos, not to be a
     * full RFC-compliant check.
     *
     * @param prompt the message to display before reading
     * @return the trimmed, validated email address
     */
    private String readRequiredEmail(String prompt) {
        while (true) {
            String value = readRequiredText(prompt);
            if (value.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
                return value;
            }
            System.out.println("El correo no tiene un formato válido (ejemplo: nombre@dominio.com).");
        }
    }

    /**
     * Repeatedly prompts the user until a strictly positive decimal number
     * is entered. Intended for prices, where zero or negative values do not
     * make sense.
     *
     * @param prompt the message to display before reading
     * @return the validated, strictly positive value
     */
    private double readPositiveDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String raw = scanner.nextLine().trim();
            try {
                double value = Double.parseDouble(raw);
                if (value > 0) {
                    return value;
                }
                System.out.println("El precio debe ser mayor que cero. Intente de nuevo.");
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un número válido. Intente de nuevo.");
            }
        }
    }

    /**
     * Repeatedly prompts the user until a non-negative integer is entered.
     * Intended for stock quantities, where zero is a valid amount but
     * negative values are not.
     *
     * @param prompt the message to display before reading
     * @return the validated, non-negative integer
     */
    private int readNonNegativeInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String raw = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(raw);
                if (value >= 0) {
                    return value;
                }
                System.out.println("El valor no puede ser negativo. Intente de nuevo.");
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un número entero válido. Intente de nuevo.");
            }
        }
    }

    /**
     * Repeatedly prompts the user until a strictly positive integer is
     * entered. Intended for quantities, counts and generations, where zero
     * or negative values do not make sense.
     *
     * @param prompt the message to display before reading
     * @return the validated, strictly positive integer
     */
    private int readPositiveInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String raw = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(raw);
                if (value > 0) {
                    return value;
                }
                System.out.println("El valor debe ser mayor que cero. Intente de nuevo.");
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un número entero válido. Intente de nuevo.");
            }
        }
    }
}