package com.gamezone.ui;

import com.gamezone.model.Console;
import com.gamezone.model.Customer;
import com.gamezone.model.Product;
import com.gamezone.model.Sale;
import com.gamezone.model.SaleItem;
import com.gamezone.model.Seller;
import com.gamezone.model.VideoGame;
import com.gamezone.service.PersonService;
import com.gamezone.service.ProductService;
import com.gamezone.service.SaleService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Console-based user interface for GameZone Unicesar, displaying menus in
 * Spanish and delegating all business operations to the injected services.
 * This class contains no business rules of its own; it only reads and
 * validates user input, calls the appropriate service, and prints the
 * results. Every listing is numbered (1..n) to make items easier to
 * reference, and every field read from the user is validated before it is
 * passed on to a service.
 */
public class ConsoleMenu {

    private final ProductService productService;
    private final PersonService personService;
    private final SaleService saleService;
    private final Scanner scanner;

    /**
     * Creates a new console menu backed by the given services.
     *
     * @param productService the service used for product operations
     * @param personService  the service used for customer and seller operations
     * @param saleService    the service used for sale operations
     */
    public ConsoleMenu(ProductService productService, PersonService personService, SaleService saleService) {
        this.productService = productService;
        this.personService = personService;
        this.saleService = saleService;
        this.scanner = new Scanner(System.in);
    }

    /**
     * Starts the main menu loop until the user chooses to exit.
     */
    public void start() {
        boolean running = true;
        while (running) {
            System.out.println();
            System.out.println("===== GameZone Unicesar =====");
            System.out.println("1. Gestion de productos");
            System.out.println("2. Gestion de personas");
            System.out.println("3. Gestion de ventas");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opcion: ");
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
                case "0":
                    running = false;
                    System.out.println("Gracias por usar GameZone Unicesar.");
                    break;
                default:
                    System.out.println("Opcion invalida.");
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
            System.out.println("----- Gestion de productos -----");
            System.out.println("1. Registrar videojuego");
            System.out.println("2. Registrar consola");
            System.out.println("3. Listar todos los productos");
            System.out.println("4. Buscar producto por ID");
            System.out.println("5. Actualizar producto");
            System.out.println("6. Eliminar producto");
            System.out.println("0. Volver");
            System.out.print("Seleccione una opcion: ");
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
                case "0":
                    back = true;
                    break;
                default:
                    System.out.println("Opcion invalida.");
            }
        }
    }

    private void registerVideoGame() {
        String id = readRequiredText("ID: ");
        if (productService.findById(id).isPresent()) {
            System.out.println("Ya existe un producto con el ID " + id + ".");
            return;
        }
        String title = readRequiredText("Titulo: ");
        double price = readPositiveDouble("Precio: ");
        int stock = readNonNegativeInt("Stock: ");
        String platform = readRequiredText("Plataforma: ");
        String genre = readRequiredText("Genero: ");
        String ageRating = readRequiredText("Clasificacion por edad: ");
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
        String title = readRequiredText("Titulo: ");
        double price = readPositiveDouble("Precio: ");
        int stock = readNonNegativeInt("Stock: ");
        String brand = readRequiredText("Marca: ");
        String model = readRequiredText("Modelo: ");
        int generation = readPositiveInt("Generacion: ");
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

    private void findProductById() {
        String id = readRequiredText("ID del producto: ");
        Optional<Product> product = productService.findById(id);
        if (product.isEmpty()) {
            System.out.println("No se encontro ningun producto con ese ID.");
            return;
        }
        System.out.println(product.get().getFullDescription());
    }

    private void updateProduct() {
        String id = readRequiredText("ID del producto a actualizar: ");
        if (productService.findById(id).isEmpty()) {
            System.out.println("No se encontro ningun producto con ese ID.");
            return;
        }
        String title = readRequiredText("Nuevo titulo: ");
        double price = readPositiveDouble("Nuevo precio: ");
        int stock = readNonNegativeInt("Nuevo stock: ");
        boolean updated = productService.updateProduct(id, title, price, stock);
        System.out.println(updated ? "Producto actualizado exitosamente."
                : "No se encontro ningun producto con ese ID.");
    }

    private void deleteProduct() {
        String id = readRequiredText("ID del producto a eliminar: ");
        boolean deleted = productService.deleteProduct(id);
        System.out.println(deleted ? "Producto eliminado exitosamente."
                : "No se encontro ningun producto con ese ID.");
    }

    // ---------------------------------------------------------------
    // Person management (customers and sellers)
    // ---------------------------------------------------------------

    private void showPersonMenu() {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("----- Gestion de personas -----");
            System.out.println("1. Registrar cliente");
            System.out.println("2. Registrar vendedor");
            System.out.println("3. Listar clientes");
            System.out.println("4. Listar vendedores");
            System.out.println("5. Buscar cliente por ID");
            System.out.println("6. Buscar vendedor por ID");
            System.out.println("0. Volver");
            System.out.print("Seleccione una opcion: ");
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
                    System.out.println("Opcion invalida.");
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
        String phone = readRequiredText("Telefono: ");
        String mail = readRequiredEmail("Correo electronico: ");
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
        String phone = readRequiredText("Telefono: ");
        String employeeCode = readRequiredText("Codigo de empleado: ");
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
            System.out.println("No se encontro ningun cliente con ese ID.");
            return;
        }
        System.out.println(customer.getId() + " - " + customer.getName() + " - "
                + customer.getPhone() + " - " + customer.getMail());
    }

    private void findSellerById() {
        String id = readRequiredText("ID del vendedor: ");
        Seller seller = personService.findSeller(id);
        if (seller == null) {
            System.out.println("No se encontro ningun vendedor con ese ID.");
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
            System.out.println("----- Gestion de ventas -----");
            System.out.println("1. Registrar venta");
            System.out.println("2. Ver historial completo de ventas");
            System.out.println("3. Ver ventas por cliente");
            System.out.println("4. Ver ventas por vendedor");
            System.out.println("0. Volver");
            System.out.print("Seleccione una opcion: ");
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
                    System.out.println("Opcion invalida.");
            }
        }
    }

    private void registerSale() {
        String customerId = readRequiredText("ID del cliente: ");
        if (personService.findCustomer(customerId) == null) {
            System.out.println("No se encontro ningun cliente con ese ID. Se cancela el registro de la venta.");
            return;
        }
        String sellerId = readRequiredText("ID del vendedor: ");
        if (personService.findSeller(sellerId) == null) {
            System.out.println("No se encontro ningun vendedor con ese ID. Se cancela el registro de la venta.");
            return;
        }
        int count = readPositiveInt("Cantidad de productos distintos a vender: ");

        List<SaleItem> items = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            String productId = readRequiredText("ID del producto " + i + ": ");
            Optional<Product> product = productService.findById(productId);
            if (product.isEmpty()) {
                System.out.println("No se encontro ningun producto con ID " + productId
                        + ". Se cancela el registro de la venta.");
                return;
            }
            int quantity = readPositiveInt("Cantidad del producto " + i + ": ");
            items.add(new SaleItem(product.get(), quantity));
        }

        boolean registered = saleService.registerSale(customerId, sellerId, items);
        if (!registered) {
            System.out.println("No fue posible registrar la venta. Verifique los datos ingresados.");
        }
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
        StringBuilder sb = new StringBuilder();
        sb.append("Venta [").append(sale.getDate()).append("] ")
                .append("Cliente: ").append(sale.getCustomer().getName())
                .append(" | Vendedor: ").append(sale.getSeller().getName())
                .append("\n");
        for (SaleItem item : sale.getItems()) {
            sb.append("     - ").append(item.getProduct().getTitle())
                    .append(" x").append(item.getQuantity())
                    .append(" = $").append(String.format("%.2f", item.calculateSubtotal()))
                    .append("\n");
        }
        sb.append("     Total: $").append(String.format("%.2f", sale.calculateTotal()));
        return sb.toString();
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
            System.out.println("Este dato no puede quedar en blanco. Intente de nuevo.");
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
            System.out.println("El correo electronico no tiene un formato valido (ejemplo: nombre@dominio.com).");
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
                System.out.println("Debe ingresar un numero valido. Intente de nuevo.");
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
                System.out.println("Debe ingresar un numero entero valido. Intente de nuevo.");
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
                System.out.println("Debe ingresar un numero entero valido. Intente de nuevo.");
            }
        }
    }
}