package yourpackage;

public class Transaction {
    private String productName;
    private int quantity;
    private double unitPrice;
    private double totalPrice;

    // Constructor
    public Transaction(String productName, int quantity, double unitPrice) {
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = quantity * unitPrice;
    }

    // Getters
    public String getProductName() { return productName; }
    public int getQuantity() { return quantity; }
    public double getUnitPrice() { return unitPrice; }
    public double getTotalPrice() { return totalPrice; }
}
