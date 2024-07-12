import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Medicine implements Serializable {
    private String name;
    private String dosage;
    private Date expiryDate;
    private int quantity;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public Medicine(String name, String dosage, Date expiryDate, int quantity) {
        this.name = name;
        this.dosage = dosage;
        this.expiryDate = expiryDate;
        this.quantity = quantity;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getDosage() {
        return dosage;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public int getQuantity() {
        return quantity;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "Medicine{" +
                "name='" + name + '\'' +
                ", dosage='" + dosage + '\'' +
                ", expiryDate=" + dateFormat.format(expiryDate) +
                ", quantity=" + quantity +
                '}';
    }
}