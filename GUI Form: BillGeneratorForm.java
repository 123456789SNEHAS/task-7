package yourpackage;

import java.awt.print.PrinterException;
import java.io.FileOutputStream;
import java.sql.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

public class BillGeneratorForm extends javax.swing.JFrame {

    public BillGeneratorForm() {
        initComponents();
    }

    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {
        String buyerId = buyerIdField.getText();
        Connection conn = DatabaseConnection.getConnection();
        StringBuilder bill = new StringBuilder();
        
        try {
            // Fetch Buyer Info
            String buyerQuery = "SELECT * FROM buyers WHERE id=?";
            PreparedStatement pstBuyer = conn.prepareStatement(buyerQuery);
            pstBuyer.setString(1, buyerId);
            ResultSet rsBuyer = pstBuyer.executeQuery();
            
            if (rsBuyer.next()) {
                bill.append("********** Company Name **********\n");
                bill.append("Address: Your Company Address\n");
                bill.append("-----------------------------------\n");
                bill.append("Buyer Name: ").append(rsBuyer.getString("name")).append("\n");
                bill.append("Contact: ").append(rsBuyer.getString("phone")).append("\n");
                bill.append("Email: ").append(rsBuyer.getString("email")).append("\n");
                bill.append("-----------------------------------\n\n");
            } else {
                JOptionPane.showMessageDialog(this, "Buyer Not Found!");
                return;
            }
            
            // Fetch Purchased Products
            String transQuery = "SELECT * FROM transactions WHERE buyer_id=?";
            PreparedStatement pstTrans = conn.prepareStatement(transQuery);
            pstTrans.setString(1, buyerId);
            ResultSet rsTrans = pstTrans.executeQuery();
            
            double subtotal = 0;
            bill.append(String.format("%-20s %-10s %-10s %-10s\n", "Product Name", "Qty", "Unit Price", "Total"));
            bill.append("------------------------------------------------------\n");

            while (rsTrans.next()) {
                String pname = rsTrans.getString("product_name");
                int qty = rsTrans.getInt("quantity");
                double price = rsTrans.getDouble("unit_price");
                double total = rsTrans.getDouble("total_price");

                bill.append(String.format("%-20s %-10d %-10.2f %-10.2f\n", pname, qty, price, total));
                subtotal += total;
            }

            bill.append("------------------------------------------------------\n");
            bill.append(String.format("Subtotal: %.2f\n", subtotal));
            bill.append("Total Amount Due: ").append(subtotal).append("\n\n");

            // Transaction Details
            bill.append("-----------------------------------\n");
            bill.append("Transaction Date: CURRENT DATE\n");
            bill.append("Payment Method: Cash / Card / UPI\n");

            billArea.setText(bill.toString());
            conn.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void printButtonActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            billArea.print();
        } catch (PrinterException e) {
            JOptionPane.showMessageDialog(this, "Print Error: " + e.getMessage());
        }
    }

    private void savePdfButtonActionPerformed(java.awt.event.ActionEvent evt) {
        String path = "bill.pdf";
        try {
            Document doc = new Document();
            PdfWriter.getInstance(doc, new FileOutputStream(path));
            doc.open();
            doc.add(new Paragraph(billArea.getText()));
            doc.close();
            JOptionPane.showMessageDialog(this, "PDF Saved Successfully!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "PDF Save Error: " + e.getMessage());
        }
    }
}
