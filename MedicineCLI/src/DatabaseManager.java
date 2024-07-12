import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DatabaseManager {
    private List<Medicine> medicines;
    private static final String FILE_PATH = "medicines.dat";
    private static final String DB_URL = "jdbc:sqlite:medicines.db";
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public DatabaseManager() {
        medicines = new ArrayList<>();
        loadMedicineData();
        createMedicineTable();
    }

    public void addMedicine(Medicine medicine) {
        medicines.add(medicine);
    }

    public List<Medicine> getAllMedicines() {
        return new ArrayList<>(medicines); // Return a copy of the list
    }

    public void updateMedicine(int index, Medicine updatedMedicine) {
        if (index >= 0 && index < medicines.size()) {
            medicines.set(index, updatedMedicine);
        }
    }

    public void deleteMedicine(int index) {
        if (index >= 0 && index < medicines.size()) {
            medicines.remove(index);
        }
    }

    public Optional<Medicine> findMedicineByName(String name) {
        return medicines.stream()
                .filter(medicine -> medicine.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    @SuppressWarnings("unchecked")
    private void loadMedicineData() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            medicines = (List<Medicine>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            // Initialize an empty list if no data is found
            medicines = new ArrayList<>();
        }
    }

    public void saveMedicineData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(medicines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveMedicinesAsCSV(String filePath) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filePath));
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT)) {
            for (Medicine medicine : medicines) {
                csvPrinter.printRecord(medicine.getName(), medicine.getDosage(), dateFormat.format(medicine.getExpiryDate()), medicine.getQuantity());
            }
        }
    }

    public void loadMedicinesFromCSV(String filePath) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath));
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT)) {
            for (CSVRecord record : csvParser) {
                Medicine medicine = new Medicine(
                        record.get(0),
                        record.get(1),
                        dateFormat.parse(record.get(2)),
                        Integer.parseInt(record.get(3)));
                medicines.add(medicine);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void saveMedicinesAsJSON(String filePath) throws IOException {
        Gson gson = new Gson();
        try (Writer writer = new FileWriter(filePath)) {
            gson.toJson(medicines, writer);
        }
    }

    public void loadMedicinesFromJSON(String filePath) throws IOException {
        Gson gson = new Gson();
        try (Reader reader = new FileReader(filePath)) {
            Type type = new TypeToken<List<Medicine>>() {}.getType();
            medicines = gson.fromJson(reader, type);
        }
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public void createMedicineTable() {
        String sql = "CREATE TABLE IF NOT EXISTS medicines ("
                + "name text PRIMARY KEY,"
                + "dosage text NOT NULL,"
                + "expiryDate text NOT NULL,"
                + "quantity integer NOT NULL)";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void insertMedicine(Medicine medicine) {
        String sql = "INSERT INTO medicines(name, dosage, expiryDate, quantity) VALUES(?,?,?,?)";
        // Fill in the SQL insert operation
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, medicine.getName());
            pstmt.setString(2, medicine.getDosage());
            pstmt.setString(3, dateFormat.format(medicine.getExpiryDate()));
            pstmt.setInt(4, medicine.getQuantity());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void updateMedicineInDB(Medicine medicine) {
        String sql = "UPDATE medicines SET dosage = ?, expiryDate = ?, quantity = ? WHERE name = ?";
        // Fill in the SQL update operation
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, medicine.getDosage());
            pstmt.setString(2, dateFormat.format(medicine.getExpiryDate()));
            pstmt.setInt(3, medicine.getQuantity());
            pstmt.setString(4, medicine.getName());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void deleteMedicineFromDB(String name) {
        String sql = "DELETE FROM medicines WHERE name = ?";
        // Fill in the SQL delete operation
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public List<Medicine> getAllMedicinesFromDB() {
        String sql = "SELECT * FROM medicines";
        List<Medicine> medicinesFromDB = new ArrayList<>();
        // Fill in the SQL query operation
        try (Connection conn = this.connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Medicine medicine = new Medicine(
                        rs.getString("name"),
                        rs.getString("dosage"),
                        dateFormat.parse(rs.getString("expiryDate")),
                        rs.getInt("quantity"));
                medicinesFromDB.add(medicine);
            }
        } catch (SQLException | ParseException e) {
            System.out.println(e.getMessage());
        }
        return medicinesFromDB;
    }

    private void initializeDatabase() {
        try (Connection conn = connect()) {
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                System.out.println("A new database has been created.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


}