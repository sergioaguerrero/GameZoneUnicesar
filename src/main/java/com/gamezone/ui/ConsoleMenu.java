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
 * This class contains no business rules of its own; it only reads user
 * input, calls the appropriate service, and prints the results.
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
        try {
            System.out.print("ID: ");
            String id = scanner.nextLine().trim();
            System.out.print("Titulo: ");
            String title = scanner.nextLine().trim();
            System.out.print("Precio: ");
            double price = Double.parseDouble(scanner.nextLine().trim());
            System.out.print("Stock: ");
            int stock = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("Plataforma: ");
            String platform = scanner.nextLine().trim();
            System.out.print("Genero: ");
            String genre = scanner.nextLine().trim();
            System.out.print("Clasificacion por edad: ");
            String ageRating = scanner.nextLine().trim();
            VideoGame videoGame = productService.registerVideoGame(id, title, price, stock,
                    platform, genre, ageRating);
            System.out.println("Videojuego registrado exitosamente: " + videoGame.getFullDescription());
        } catch (NumberFormatException e) {
            System.out.println("Precio o stock invalidos.");
        } catch (RuntimeException e) {
            System.out.println("Error al registrar el videojuego: " + e.getMessage());
        }
    }

    private void registerConsole() {
        try {
            System.out.print("ID: ");
            String id = scanner.nextLine().trim();
            System.out.print("Titulo: ");
            String title = scanner.nextLine().trim();
            System.out.print("Precio: ");
            double price = Double.parseDouble(scanner.nextLine().trim());
            System.out.print("Stock: ");
            int stock = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("Marca: ");
            String brand = scanner.nextLine().trim();
            System.out.print("Modelo: ");
            String model = scanner.nextLine().trim();
            System.out.print("Generacion: ");
            int generation = Integer.parseInt(scanner.nextLine().trim());
            Console console = productService.registerConsole(id, title, price, stock,
                    brand, model, generation);
            System.out.println("Consola registrada exitosamente: " + console.getFullDescription());
        } catch (NumberFormatException e) {
            System.out.println("Precio, stock o generacion invalidos.");
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
        for (Product product : products) {
            System.out.println(product.getFullDescription());
        }
    }

    private void findProductById() {
        System.out.print("ID del producto: ");
        String id = scanner.nextLine().trim();
        Optional<Product> product = productService.findById(id);
        if (product.isEmpty()) {
            System.out.println("No se encontro ningun producto con ese ID.");
            return;
        }
        System.out.println(product.get().getFullDescription());
    }

    private void updateProduct() {
        try {
            System.out.print("ID del producto a actualizar: ");
            String id = scanner.nextLine().trim();
            System.out.print("Nuevo titulo: ");
            String title = scanner.nextLine().trim();
            System.out.print("Nuevo precio: ");
            double price = Double.parseDouble(scanner.nextLine().trim());
            System.out.print("Nuevo stock: ");
            int stock = Integer.parseInt(scanner.nextLine().trim());
            boolean updated = productService.updateProduct(id, title, price, stock);
            System.out.println(updated ? "Producto actualizado exitosamente."
                    : "No se encontro ningun producto con ese ID.");
        } catch (NumberFormatException e) {
            System.out.println("Precio o stock invalidos.");
        }
    }

    private void deleteProduct() {
        System.out.print("ID del producto a eliminar: ");
        String id = scanner.nextLine().trim();
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
        System.out.print("ID: ");
        String id = scanner.nextLine().trim();
        System.out.print("Nombre completo: ");
        String name = scanner.nextLine().trim();
        System.out.print("Telefono: ");
        String phone = scanner.nextLine().trim();
        System.out.print("Correo electronico: ");
        String mail = scanner.nextLine().trim();
        personService.registerCustomer(name, phone, id, mail);
        System.out.println("Cliente registrado exitosamente.");
    }

    private void registerSeller() {
        System.out.print("ID: ");
        String id = scanner.nextLine().trim();
        System.out.print("Nombre completo: ");
        String name = scanner.nextLine().trim();
        System.out.print("Telefono: ");
        String phone = scanner.nextLine().trim();
        System.out.print("Codigo de empleado: ");
        String employeeCode = scanner.nextLine().trim();
        System.out.print("Turno de trabajo: ");
        String workShift = scanner.nextLine().trim();
        personService.registerSeller(name, phone, id, employeeCode, workShift);
        System.out.println("Vendedor registrado exitosamente.");
    }

    private void listAllCustomers() {
        List<Customer> customers = personService.listCustomer();
        if (customers.isEmpty()) {
            System.out.println("No hay clientes registrados.");
            return;
        }
        for (Customer customer : customers) {
            System.out.println(customer.getId() + " - " + customer.getName() + " - " + customer.getMail());
        }
    }

    private void listAllSellers() {
        List<Seller> sellers = personService.listSeller();
        if (sellers.isEmpty()) {
            System.out.println("No hay vendedores registrados.");
            return;
        }
        for (Seller seller : sellers) {
            System.out.println(seller.getId() + " - " + seller.getName() + " - " + seller.getWorkShift());
        }
    }

    private void findCustomerById() {
        System.out.print("ID del cliente: ");
        String id = scanner.nextLine().trim();
        Customer customer = personService.findCustomer(id);
        if (customer == null) {
            System.out.println("No se encontro ningun cliente con ese ID.");
            return;
        }
        System.out.println(customer.getId() + " - " + customer.getName() + " - "
                + customer.getPhone() + " - " + customer.getMail());
    }

    private void findSellerById() {
        System.out.print("ID del vendedor: ");
        String id = scanner.nextLine().trim();
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
        try {
            System.out.print("ID del cliente: ");
            String customerId = scanner.nextLine().trim();
            System.out.print("ID del vendedor: ");
            String sellerId = scanner.nextLine().trim();
            System.out.print("Cantidad de productos distintos a vender: ");
            int count = Integer.parseInt(scanner.nextLine().trim());

            List<SaleItem> items = new ArrayList<>();
            for (int i = 1; i <= count; i++) {
                System.out.print("ID del producto " + i + ": ");
                String productId = scanner.nextLine().trim();
                Optional<Product> product = productService.findById(productId);
                if (product.isEmpty()) {
                    System.out.println("No se encontro ningun producto con ID " + productId
                            + ". Se cancela el registro de la venta.");
                    return;
                }
                System.out.print("Cantidad del producto " + i + ": ");
                int quantity = Integer.parseInt(scanner.nextLine().trim());
                items.add(new SaleItem(product.get(), quantity));
            }

            boolean registered = saleService.registerSale(customerId, sellerId, items);
            if (!registered) {
                System.out.println("No fue posible registrar la venta. Verifique los datos ingresados.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Cantidad invalida.");
        }
    }

    private void viewSalesByCustomer() {
        System.out.print("ID del cliente: ");
        String customerId = scanner.nextLine().trim();
        printSales(saleService.listSalesByCustomer(customerId), "Este cliente no tiene ventas registradas.");
    }

    private void viewSalesBySeller() {
        System.out.print("ID del vendedor: ");
        String sellerId = scanner.nextLine().trim();
        printSales(saleService.listSalesBySeller(sellerId), "Este vendedor no tiene ventas registradas.");
    }

    /**
     * Prints a list of sales as human-readable receipts, or a fallback
     * message when the list is empty.
     *
     * @param sales        the sales to print
     * @param emptyMessage the message to show when there are no sales
     */
    private void printSales(List<Sale> sales, String emptyMessage) {
        if (sales.isEmpty()) {
            System.out.println(emptyMessage);
            return;
        }
        for (Sale sale : sales) {
            System.out.println(formatSaleReceipt(sale));
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
            sb.append("  - ").append(item.getProduct().getTitle())
                    .append(" x").append(item.getQuantity())
                    .append(" = $").append(String.format("%.2f", item.calculateSubtotal()))
                    .append("\n");
        }
        sb.append("  Total: $").append(String.format("%.2f", sale.calculateTotal()));
        return sb.toString();
    }
}