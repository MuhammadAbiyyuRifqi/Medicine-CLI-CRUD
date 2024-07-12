import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
public class MedicineCLI {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private DatabaseManager dbManager;

    public MedicineCLI(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        while (!exit) {
            showMenu();
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Clear the buffer

            switch (choice) {
                case 1:
                    addMedicine(scanner);
                    break;
                case 2:
                    listMedicines();
                    break;
                case 3:
                    updateMedicine(scanner);
                    break;
                case 4:
                    deleteMedicine(scanner);
                    break;
                case 5:
                    dbManager.saveMedicineData();
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void showMenu() {
        System.out.println("\nMedicine Management System");
        System.out.println("1. Add Medicine");
        System.out.println("2. List Medicines");
        System.out.println("3. Update Medicine");
        System.out.println("4. Delete Medicine");
        System.out.println("5. Exit");
    }

    private void addMedicine(Scanner scanner) {
        try {
            System.out.print("Enter the medicine name: ");
            String name = scanner.nextLine();
            System.out.print("Enter the dosage: ");
            String dosage = scanner.nextLine();
            System.out.print("Enter the expiry date (yyyy-MM-dd): ");
            Date expiryDate = dateFormat.parse(scanner.nextLine());
            System.out.print("Enter the quantity: ");
            int quantity = scanner.nextInt();

            Medicine medicine = new Medicine(name, dosage, expiryDate, quantity);
            dbManager.addMedicine(medicine);
            System.out.println("Medicine added: " + medicine);
        } catch (Exception e) {
            System.out.println("Invalid input. Please try again.");
        }
    }

    private void listMedicines() {
        List<Medicine> medicines = dbManager.getAllMedicines();
        if (medicines.isEmpty()) {
            System.out.println("No medicines available.");
            return;
        }
        System.out.println("List of Medicines:");
        for (Medicine medicine : medicines) {
            System.out.println(medicine);
        }
    }

    private void updateMedicine(Scanner scanner) {
        listMedicines();
        System.out.print("Enter the medicine number to update: ");
        int index = scanner.nextInt() - 1;
        scanner.nextLine(); // Clear the buffer

        if (index >= 0 && index < dbManager.getAllMedicines().size()) {
            Medicine medicine = dbManager.getAllMedicines().get(index);

            try {
                System.out.print("Enter the new medicine name: ");
                medicine.setName(scanner.nextLine());
                System.out.print("Enter the new dosage: ");
                medicine.setDosage(scanner.nextLine());
                System.out.print("Enter the new expiry date (yyyy-MM-dd): ");
                medicine.setExpiryDate(dateFormat.parse(scanner.nextLine()));
                System.out.print("Enter the new quantity: ");
                medicine.setQuantity(scanner.nextInt());

                System.out.println("Medicine updated: " + medicine);
            } catch (Exception e) {
                System.out.println("Invalid input. Please try again.");
            }
        } else {
            System.out.println("Invalid medicine number.");
        }
    }

    private void deleteMedicine(Scanner scanner) {
        listMedicines();
        System.out.print("Enter the medicine number to delete: ");
        int index = scanner.nextInt() - 1;

        if (index >= 0 && index < dbManager.getAllMedicines().size()) {
            Medicine medicine = dbManager.getAllMedicines().remove(index);
            System.out.println("Medicine deleted: " + medicine);
        } else {
            System.out.println("Invalid medicine number.");
        }
    }
}
