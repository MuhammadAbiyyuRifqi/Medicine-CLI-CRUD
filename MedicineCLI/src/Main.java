public class Main {
    public static void main(String[] args) {
        DatabaseManager dbManager = new DatabaseManager();
        MedicineCLI cli = new MedicineCLI(dbManager);
        cli.start();
    }
}