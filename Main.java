import java.util.*;
import java.util.logging.*;

// Správa zaměstnanců
class Employee {
    private String firstName;
    private String lastName;
    private int id;
    private String position;
    private double salary;

    public Employee(String firstName, String lastName, int id, String position, double salary) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.id = id;
        this.position = position;
        this.salary = salary;
    }

    public int getId() {
        return id;
    }

    public double getSalary() {
        return salary;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public String toString() {
        return id + ": " + firstName + " " + lastName + " - " + position + " (" + salary + " Kč)";
    }
}

// Evidence zakázek
class Order {
    private int id;
    private String name;
    private String description;
    private String status;
    private Date receivedDate;
    private Date deadline;

    public Order(int id, String name, String description, String status, Date receivedDate, Date deadline) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.receivedDate = receivedDate;
        this.deadline = deadline;
    }

    public int getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public void updateStatus(String status) {
        this.status = status;
    }

    public String toString() {
        return id + ": " + name + " [" + status + "] - deadline: " + deadline;
    }
}

// Sledování zásob
class InventoryItem {
    private String name;
    private int id;
    private int quantity;
    private int minimumStock;

    public InventoryItem(String name, int id, int quantity, int minimumStock) {
        this.name = name;
        this.id = id;
        this.quantity = quantity;
        this.minimumStock = minimumStock;
    }

    public int getId() {
        return id;
    }

    public void updateQuantity(int newQuantity) {
        this.quantity = newQuantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getMinimumStock() {
        return minimumStock;
    }

    public String toString() {
        return id + ": " + name + " (" + quantity + " ks)";
    }
}

// Zpracování objednávek
class OrderProcessing {
    private CompanySystem system;
    private static Logger logger = Logger.getLogger(OrderProcessing.class.getName());

    public OrderProcessing(CompanySystem system) {
        this.system = system;
    }

    public void processOrder(Map<Integer, Integer> orderItems) {
        for (Map.Entry<Integer, Integer> entry : orderItems.entrySet()) {
            int itemId = entry.getKey();
            int quantity = entry.getValue();
            InventoryItem item = system.getInventoryItem(itemId);
            if (item == null || item.getQuantity() < quantity) {
                logger.warning("Nedostatek zásob pro položku ID " + itemId);
                return;
            }
        }

        for (Map.Entry<Integer, Integer> entry : orderItems.entrySet()) {
            InventoryItem item = system.getInventoryItem(entry.getKey());
            item.updateQuantity(item.getQuantity() - entry.getValue());
        }

        System.out.println("Objednávka byla úspěšně vyřízena.");
    }
}

// Ostatní funkce
class CompanySystem {
    private static CompanySystem instance;
    private static Logger logger = Logger.getLogger(CompanySystem.class.getName());

    private Map<Integer, Employee> employees = new HashMap<>();
    private Map<Integer, Order> orders = new HashMap<>();
    private Map<Integer, InventoryItem> inventory = new HashMap<>();

    private CompanySystem() {}

    public static CompanySystem getInstance() {
        if (instance == null) {
            instance = new CompanySystem();
        }
        return instance;
    }

    // Zaměstnanci
    public void addEmployee(Employee e) {
        employees.put(e.getId(), e);
    }

    public void editEmployee(int id, String newPosition, double newSalary) {
        Employee e = employees.get(id);
        if (e != null) {
            e.setPosition(newPosition);
            e.setSalary(newSalary);
        }
    }

    public void removeEmployee(int id) {
        employees.remove(id);
    }

    public Employee getEmployee(int id) {
        return employees.get(id);
    }

    public double totalSalaries() {
        return employees.values().stream().mapToDouble(Employee::getSalary).sum();
    }

    public void printAllEmployees() {
        for (Employee e : employees.values()) {
            System.out.println(e);
        }
    }

    // Zakázky
    public void addOrder(Order o) {
        orders.put(o.getId(), o);
    }

    public void updateOrderStatus(int id, String newStatus) {
        Order o = orders.get(id);
        if (o != null) {
            o.updateStatus(newStatus);
        }
    }

    public Order getOrder(int id) {
        return orders.get(id);
    }

    public void listActiveOrders() {
        for (Order o : orders.values()) {
            if (!o.getStatus().equalsIgnoreCase("Dokončena")) {
                System.out.println(o);
            }
        }
    }

    // Zásoby
    public void addInventoryItem(InventoryItem i) {
        inventory.put(i.getId(), i);
    }

    public InventoryItem getInventoryItem(int id) {
        return inventory.get(id);
    }

    public void updateInventory(int id, int newQuantity) {
        InventoryItem i = inventory.get(id);
        if (i != null) {
            i.updateQuantity(newQuantity);
        }
    }

    public void checkLowStock() {
        for (InventoryItem i : inventory.values()) {
            if (i.getQuantity() < i.getMinimumStock()) {
                logger.warning("Nízký stav zásob: " + i);
            }
        }
    }

    public void printAllInventoryItems() {
        for (InventoryItem item : inventory.values()) {
            System.out.println(item);
        }
    }
}

// Hlavní část
public class Main {
    public static void main(String[] args) {
        CompanySystem system = CompanySystem.getInstance();
        OrderProcessing processor = new OrderProcessing(system);

        System.out.println("============== PŘIDÁVÁNÍ ZAMĚSTNANCŮ ==============");
        system.addEmployee(new Employee("Jan", "Novák", 1, "Manažer", 50000));
        system.addEmployee(new Employee("Eva", "Svobodová", 2, "Technik", 40000));
        system.printAllEmployees();

        System.out.println("\n============== MZDOVÉ NÁKLADY ==============");
        System.out.println("Celkové mzdové náklady: " + system.totalSalaries() + " Kč");

        System.out.println("\n============== ZAKÁZKY ==============");
        system.addOrder(new Order(101, "Webová aplikace", "Vývoj systému", "Přijata", new Date(), new Date()));
        system.addOrder(new Order(102, "Mobilní appka", "iOS + Android", "Probíhá", new Date(), new Date()));
        system.updateOrderStatus(101, "Probíhá");

        System.out.println("\n-- Aktivní zakázky --");
        system.listActiveOrders();

        System.out.println("\n============== SKLAD ==============");
        system.addInventoryItem(new InventoryItem("Monitor", 201, 5, 3));
        system.addInventoryItem(new InventoryItem("Notebook", 202, 2, 5));

        System.out.println("\n-- Kontrola nízkých zásob --");
        system.checkLowStock();

        System.out.println("\n============== OBJEDNÁVKA ==============");
        Map<Integer, Integer> objednavka = new HashMap<>();
        objednavka.put(201, 1);
        objednavka.put(202, 1);
        processor.processOrder(objednavka);

        System.out.println("\n-- Kontrola zásob po objednávce --");
        system.checkLowStock();

        System.out.println("\n============== HOTOVO ==============");
    }
}
