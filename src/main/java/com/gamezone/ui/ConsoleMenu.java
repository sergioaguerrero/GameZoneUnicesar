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
 * English and delegating all business operations to the injected services.
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
            System.out.println("1. Product management");
            System.out.println("2. Person management");
            System.out.println("3. Sale management");
            System.out.println("0. Exit");
            System.out.print("Select an option: ");
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
                    System.out.println("Thank you for using GameZone Unicesar.");
                    break;
                default:
                    System.out.println("Invalid option.");
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
            System.out.println("----- Product management -----");
            System.out.println("1. Register video game");
            System.out.println("2. Register console");
            System.out.println("3. List all products");
            System.out.println("4. Find product by ID");
            System.out.println("5. Update product");
            System.out.println("6. Delete product");
            System.out.println("7. List video games only");
            System.out.println("8. List consoles only");
            System.out.println("0. Back");
            System.out.print("Select an option: ");
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
                    System.out.println("Invalid option.");
            }
        }
    }

    private void registerVideoGame() {
        String id = readRequiredText("ID: ");
        if (productService.findById(id).isPresent()) {
            System.out.println("A product with ID " + id + " already exists.");
            return;
        }
        String title = readRequiredText("Title: ");
        double price = readPositiveDouble("Price: ");
        int stock = readNonNegativeInt("Stock: ");
        String platform = readRequiredText("Platform: ");
        String genre = readRequiredText("Genre: ");
        String ageRating = readRequiredText("Age rating: ");
        try {
            VideoGame videoGame = productService.registerVideoGame(id, title, price, stock,
                    platform, genre, ageRating);
            System.out.println("Video game registered successfully: " + videoGame.getFullDescription());
        } catch (RuntimeException e) {
            System.out.println("Error registering the video game: " + e.getMessage());
        }
    }

    private void registerConsole() {
        String id = readRequiredText("ID: ");
        if (productService.findById(id).isPresent()) {
            System.out.println("A product with ID " + id + " already exists.");
            return;
        }
        String title = readRequiredText("Title: ");
        double price = readPositiveDouble("Price: ");
        int stock = readNonNegativeInt("Stock: ");
        String brand = readRequiredText("Brand: ");
        String model = readRequiredText("Model: ");
        int generation = readPositiveInt("Generation: ");
        try {
            Console console = productService.registerConsole(id, title, price, stock,
                    brand, model, generation);
            System.out.println("Console registered successfully: " + console.getFullDescription());
        } catch (RuntimeException e) {
            System.out.println("Error registering the console: " + e.getMessage());
        }
    }

    private void listAllProducts() {
        List<Product> products = productService.listAllProducts();
        if (products.isEmpty()) {
            System.out.println("No products registered.");
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
            System.out.println("No video games registered.");
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
            System.out.println("No consoles registered.");
            return;
        }
        int index = 1;
        for (Product console : consoles) {
            System.out.println(index + ". " + console.getFullDescription());
            index++;
        }
    }

    private void findProductById() {
        String id = readRequiredText("Product ID: ");
        Optional<Product> product = productService.findById(id);
        if (product.isEmpty()) {
            System.out.println("No product was found with that ID.");
            return;
        }
        System.out.println(product.get().getFullDescription());
    }

    private void updateProduct() {
        String id = readRequiredText("ID of the product to update: ");
        if (productService.findById(id).isEmpty()) {
            System.out.println("No product was found with that ID.");
            return;
        }
        String title = readRequiredText("New title: ");
        double price = readPositiveDouble("New price: ");
        int stock = readNonNegativeInt("New stock: ");
        boolean updated = productService.updateProduct(id, title, price, stock);
        System.out.println(updated ? "Product updated successfully."
                : "No product was found with that ID.");
    }

    private void deleteProduct() {
        String id = readRequiredText("ID of the product to delete: ");
        boolean deleted = productService.deleteProduct(id);
        System.out.println(deleted ? "Product deleted successfully."
                : "No product was found with that ID.");
    }

    // ---------------------------------------------------------------
    // Person management (customers and sellers)
    // ---------------------------------------------------------------

    private void showPersonMenu() {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("----- Person management -----");
            System.out.println("1. Register customer");
            System.out.println("2. Register seller");
            System.out.println("3. List customers");
            System.out.println("4. List sellers");
            System.out.println("5. Find customer by ID");
            System.out.println("6. Find seller by ID");
            System.out.println("0. Back");
            System.out.print("Select an option: ");
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
                    System.out.println("Invalid option.");
            }
        }
    }

    private void registerCustomer() {
        String id = readRequiredText("ID: ");
        if (personService.findCustomer(id) != null) {
            System.out.println("A customer with ID " + id + " already exists.");
            return;
        }
        String name = readRequiredText("Full name: ");
        String phone = readRequiredText("Phone: ");
        String mail = readRequiredEmail("Email: ");
        personService.registerCustomer(name, phone, id, mail);
        System.out.println("Customer registered successfully.");
    }

    private void registerSeller() {
        String id = readRequiredText("ID: ");
        if (personService.findSeller(id) != null) {
            System.out.println("A seller with ID " + id + " already exists.");
            return;
        }
        String name = readRequiredText("Full name: ");
        String phone = readRequiredText("Phone: ");
        String employeeCode = readRequiredText("Employee code: ");
        String workShift = readRequiredText("Work shift: ");
        personService.registerSeller(name, phone, id, employeeCode, workShift);
        System.out.println("Seller registered successfully.");
    }

    private void listAllCustomers() {
        List<Customer> customers = personService.listCustomer();
        if (customers.isEmpty()) {
            System.out.println("No customers registered.");
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
            System.out.println("No sellers registered.");
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
        String id = readRequiredText("Customer ID: ");
        Customer customer = personService.findCustomer(id);
        if (customer == null) {
            System.out.println("No customer was found with that ID.");
            return;
        }
        System.out.println(customer.getId() + " - " + customer.getName() + " - "
                + customer.getPhone() + " - " + customer.getMail());
    }

    private void findSellerById() {
        String id = readRequiredText("Seller ID: ");
        Seller seller = personService.findSeller(id);
        if (seller == null) {
            System.out.println("No seller was found with that ID.");
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
            System.out.println("----- Sale management -----");
            System.out.println("1. Register sale");
            System.out.println("2. View full sales history");
            System.out.println("3. View sales by customer");
            System.out.println("4. View sales by seller");
            System.out.println("0. Back");
            System.out.print("Select an option: ");
            String option = scanner.nextLine().trim();
            switch (option) {
                case "1":
                    registerSale();
                    break;
                case "2":
                    printSales(saleService.listAllSales(), "No sales registered.");
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
                    System.out.println("Invalid option.");
            }
        }
    }

    private void registerSale() {
        String customerId = readRequiredText("Customer ID: ");
        if (personService.findCustomer(customerId) == null) {
            System.out.println("No customer was found with that ID. The sale is cancelled.");
            return;
        }
        String sellerId = readRequiredText("Seller ID: ");
        if (personService.findSeller(sellerId) == null) {
            System.out.println("No seller was found with that ID. The sale is cancelled.");
            return;
        }
        int count = readPositiveInt("Number of distinct products to sell: ");

        List<SaleItem> items = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            String productId = readRequiredText("ID of product " + i + ": ");
            Optional<Product> product = productService.findById(productId);
            if (product.isEmpty()) {
                System.out.println("No product was found with ID " + productId
                        + ". The sale is cancelled.");
                return;
            }
            int quantity = readPositiveInt("Quantity of product " + i + ": ");
            items.add(new SaleItem(product.get(), quantity));
        }

        boolean registered = saleService.registerSale(customerId, sellerId, items);
        if (!registered) {
            System.out.println("The sale could not be registered. Please check the entered data.");
        }
    }

    private void viewSalesByCustomer() {
        String customerId = readRequiredText("Customer ID: ");
        printSales(saleService.listSalesByCustomer(customerId), "This customer has no sales registered.");
    }

    private void viewSalesBySeller() {
        String sellerId = readRequiredText("Seller ID: ");
        printSales(saleService.listSalesBySeller(sellerId), "This seller has no sales registered.");
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
        sb.append("Sale [").append(sale.getDate()).append("] ")
                .append("Customer: ").append(sale.getCustomer().getName())
                .append(" | Seller: ").append(sale.getSeller().getName())
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
            System.out.println("This field cannot be blank. Please try again.");
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
            System.out.println("The email does not have a valid format (example: name@domain.com).");
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
                System.out.println("The price must be greater than zero. Please try again.");
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number. Try again.");
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
                System.out.println("The value cannot be negative. Please try again.");
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid whole number. Try again.");
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
                System.out.println("The value must be greater than zero. Please try again.");
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid whole number. Try again.");
            }
        }
    }
}