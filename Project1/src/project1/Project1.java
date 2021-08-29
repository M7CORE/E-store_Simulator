/*  Name: Maria Corella
    Course: CNT 4714 – Spring 2021 
    Assignment title: Project 1 – Event-driven Enterprise Simulation
    Date: Sunday January 31, 2021
*/
package project1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.DateFormat;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class Project1 implements ActionListener{
    
    JButton processItem;
    JButton confirmItem;
    JButton viewOrder;
    JButton finishOrder;
    JButton newOrder;
    JButton exit;
    
    JFrame frame;
    JPanel cPanel;
    JPanel ePanel;
    JPanel sPanel;
    
    JTextField numberField;
    JTextField itemField;
    JTextField quantityField;
    JTextField itemIField;
    JTextField subtotalField;
    
    JLabel numberLabel;
    JLabel itemLabel;
    JLabel quantityLabel;
    JLabel itemILabel;
    JLabel subtotalLabel;
    
    int itemsInOrder = 0, current = 0;
    double subtotal = 0, tempTotal = 0;
    String infoString = "";
    FileWriter writer;
    File outputFile;
    String orderString = "";
    int number, quantity, discount2;
    double price, discount1, total, taxRate = .06;
    String itemID, description;
    boolean fileCreated = false;
    
    //Array for confirmed items in order
    String[] confirmedOrderID = new String[50];
    String[] confirmedItemID = new String [50];
    String[] confirmedInfo = new String [50];
    Double[] confirmedPrice = new Double[50];
    Integer[] confirmedQuantity = new Integer[50];
    Integer[] confirmedDiscount = new Integer[50];
    Double[] confirmedTotal = new Double[50];
    String[] confirmedDateTime = new String[50];

    public static void main(String[] args) {
        Project1 project = new Project1();
        project.setUp();
    }
    
    public void setUp (){
       
        //Button Configuration
        processItem = new JButton();
        confirmItem = new JButton();
        viewOrder = new JButton();
        finishOrder = new JButton();
        newOrder = new JButton();
        exit = new JButton();
        
        cPanel = new JPanel();
        cPanel.setMinimumSize(new Dimension(500,200));
        cPanel.setPreferredSize(new Dimension(500,200));
        cPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        cPanel.setLayout(new GridLayout(5, 1, 5, 5));
          
        ePanel = new JPanel();
        ePanel.setMinimumSize(new Dimension(500,200));
        ePanel.setPreferredSize(new Dimension(500,200));
        ePanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 50));
        ePanel.setLayout(new GridLayout(5,1,5, 20));
        
        sPanel = new JPanel();
        sPanel.setMinimumSize(new Dimension(800,50));
        sPanel.setPreferredSize(new Dimension(800,50));
        sPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        sPanel.setBackground(Color.lightGray);
        sPanel.setLayout(new GridLayout(1,6, 5, 5));
        
        //Frame instance
        frame = new JFrame("Project 1");
        frame.setSize(850,300);
        BorderLayout layout = new BorderLayout();
        frame.setLayout(layout);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        frame.add(cPanel, layout.CENTER);
        frame.add(ePanel, layout.EAST);
        frame.add(sPanel, layout.SOUTH);
        
        //Field Configuration
        numberLabel = new JLabel("Number of items in order", JLabel.RIGHT);
        cPanel.add(numberLabel);
        numberField = new JTextField(30);
        ePanel.add(numberField);
        
        itemLabel = new JLabel("Item ID for item #1", JLabel.RIGHT);
        cPanel.add(itemLabel);
        itemField = new JTextField(30);
        ePanel.add(itemField);
        
        quantityLabel = new JLabel("Quantity for Item #1", JLabel.RIGHT);
        cPanel.add(quantityLabel);
        quantityField = new JTextField(30);
        ePanel.add(quantityField);
        
        itemILabel = new JLabel("Item #1 info", JLabel.RIGHT);
        cPanel.add(itemILabel);
        itemIField = new JTextField(30);
        itemIField.setEditable(false);
        ePanel.add(itemIField);
        
        subtotalLabel = new JLabel("Order subtotal for 0 item(s)", JLabel.RIGHT);
        cPanel.add(subtotalLabel);
        subtotalField = new JTextField(30);
        subtotalField.setEditable(false);
        ePanel.add(subtotalField);
                
        processItem.addActionListener(this);
        processItem.setText("Process Item #1");
        sPanel.add(processItem);
        
        confirmItem.addActionListener(this);
        confirmItem.setText("Confirm Item #1");
        sPanel.add(confirmItem);
        confirmItem.setEnabled(false);
        
        viewOrder.addActionListener(this);
        viewOrder.setText("View Order");
        sPanel.add(viewOrder);
        viewOrder.setEnabled(false);
        
        finishOrder.addActionListener(this);
        finishOrder.setText("Finish Order");
        sPanel.add(finishOrder);
        finishOrder.setEnabled(false);
        
        newOrder.addActionListener(this);
        newOrder.setText("New Order");
        sPanel.add(newOrder);
        
        exit.addActionListener(this);
        exit.setText("Exit");
        sPanel.add(exit);
        
        frame.setVisible(true);
    }   
    
    public void processItemFunc() throws Exception{
        
        discount2 = 0;
        price = 0;
        discount1 = 1;
        total = 0;
        String temp;
        boolean found = false;
        boolean inStock = false;
        DecimalFormat df = new DecimalFormat("#.##"); 
        description = "n/a";
        
        //Get number of items in order
        temp = numberField.getText();
        number = Integer.parseInt(temp);
        itemsInOrder = number;
        
        if (number != 0){
            numberField.setEditable(false);
        }
          
        //Get item quantity
        quantity = Integer.parseInt(quantityField.getText());
        if (quantity >= 15){
            discount1 = .8;
            discount2 = 20;
        }
        else if (quantity >= 10){
            discount1 = .85;
            discount2 = 15;
        }
        else if (quantity >= 5){
            discount1 = .9;
            discount2 = 10;
        }

        //Get item ID
        itemID = itemField.getText();

        //Search for itemID in inventory
        try {
            File invFile = new File("inventory.txt");
            Scanner scanner = new Scanner(invFile).useDelimiter(",|\\n");
            

            while ((scanner.hasNext()) && (found == false)){
                temp = scanner.next();
                //If item is found
                if (itemID.equals(temp)){
                    found = true;
                    description = scanner.next(); //get description
                    temp = scanner.next().trim(); //get if in stock
                    inStock = Boolean.valueOf(temp);
                    if (inStock == false){
                        JOptionPane.showMessageDialog(null, "Item is not in stock");
                        itemField.setText("");
                        quantityField.setText("");
                        return;
                    }
                    price = Double.valueOf(scanner.next()).doubleValue();
                    total = (price * discount1) * quantity;
                }
            }

            //Item not in inventory
            if ((scanner.hasNext() == false) && (found == false)){
                JOptionPane.showMessageDialog(null, "ItemID not found");
                return;
            }
            
            itemILabel.setText("Item #" + (current+1) + " info");
            itemIField.setText(itemID + " " + description + " $" + 
                    df.format(price) + " " + quantity + " " + discount2 + 
                    "% $" + df.format(total));
            
            infoString = itemIField.getText();
            
            processItem.setEnabled(false);
            confirmItem.setEnabled(true);
            tempTotal = total;
            

        } catch (FileNotFoundException e){
            e.printStackTrace();
        }
        return;
    }
    
    public void confirmItemFunc(){
        
        DecimalFormat df = new DecimalFormat("#.##");
        JOptionPane.showMessageDialog(null, "Item #" + (current+1) + " accepted");
        subtotal = subtotal + tempTotal;
        
        if ((current+1) < itemsInOrder){
            confirmItem.setEnabled(false); 
            confirmItem.setText("Confirm Item #" + (current+2));
            processItem.setEnabled(true);
            processItem.setText("Process Item #" + (current+2));
            itemField.setText("");
            quantityField.setText("");
            subtotalField.setText("$" + df.format(subtotal));
            subtotalLabel.setText("Order subtotal for " + (current+1) + " item(s)");
            itemLabel.setText("Item ID for item #" + (current+2));
            quantityLabel.setText("Quantity for Item #" + (current+2));
            viewOrder.setEnabled(true);
            finishOrder.setEnabled(true);
        }
        else {
            confirmItem.setEnabled(false);
            processItem.setEnabled(false);
            itemField.setEditable(false);
            itemField.setText("");
            itemLabel.setText("");
            quantityField.setEditable(false);
            quantityField.setText("");
            quantityLabel.setText("");
            subtotalField.setText("$" + df.format(subtotal));
            subtotalLabel.setText("Order subtotal for " + (current+1) + " item(s)");
            viewOrder.setEnabled(true);
            finishOrder.setEnabled(true);
        }
        
        //Add order to array
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();

        confirmedItemID[current] = itemID;
        confirmedInfo[current] = description;
        confirmedPrice[current] = price;
        confirmedQuantity[current] = quantity;
        confirmedDiscount[current] = discount2;
        confirmedTotal[current] = total;
        orderString = orderString + (current+1) + ". " + infoString + "\n";
         
        current++;
    }
    
    public void finishOrderFunc(){
        
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/YY HH:MM:SS a z");
        DateFormat code = new SimpleDateFormat ("ddMMyyyyHHmm");
        DecimalFormat df = new DecimalFormat("#.##");
        
        for (int i = 0; i < itemsInOrder; i++){
            confirmedOrderID[i] = code.format(date);
            confirmedDateTime[i] = dateFormat.format(date);
        }
        JOptionPane.showMessageDialog(null, "Date: " + confirmedDateTime[0] +
                "\n\nNumber of line items: " + itemsInOrder + 
                "\n\nItem# / ID / Title / Price / Qty / Disc % / Subtotal:\n" + 
                orderString + "\n\nOrder subtotal: $" + df.format(subtotal) + 
                "\nTax rate: 6%\nTax amount: $" + df.format((subtotal*taxRate)) + 
                "\nOrderTotal: $" + df.format(((subtotal*taxRate) + subtotal)) +
                "\nThanks for shopping!");

        try {
            writer = new FileWriter("transaction.txt", true);
            fileCreated = true;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        for (int j = 0; j < itemsInOrder; j++){
            try {
                writer.append(confirmedOrderID[j] + ", " + confirmedItemID[j] + ", " +
                        confirmedInfo[j] + ", " + confirmedPrice[j] + ", " +
                        confirmedQuantity[j] + ", " + confirmedDiscount[j] + "%, " +
                        df.format(confirmedTotal[j]) + ", " + confirmedDateTime[j] + "\n");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        
        try {
            writer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        finishOrder.setEnabled(false);
        viewOrder.setEnabled(false);
        newOrderFunc();
    }
    
    public void viewOrderFunc(){
        JOptionPane.showMessageDialog(null, orderString);
    }
    
    public void newOrderFunc(){
        
        itemsInOrder = 0;
        current = 0;
        subtotal = 0;
        numberField.setEditable(true);
        numberField.setText("");
        itemLabel.setText("Item ID for item #1");
        itemField.setEditable(true);
        quantityLabel.setText("Quantity for Item #1");
        quantityField.setEditable(true);
        itemILabel.setText("Item #1 info");
        itemIField.setText("");
        subtotalField.setText("");
        subtotalLabel.setText("Order subtotal for 0 item(s)");
        processItem.setEnabled(true);
        processItem.setText("Process Item #1");
        confirmItem.setText("Confirm Item #1");
        
        Arrays.fill(confirmedDateTime, null);
        Arrays.fill(confirmedDiscount, null);
        Arrays.fill(confirmedInfo, null);
        Arrays.fill(confirmedItemID, null);
        Arrays.fill(confirmedOrderID, null);
        Arrays.fill(confirmedPrice, null);
        Arrays.fill(confirmedQuantity, null);
        Arrays.fill(confirmedTotal, null);
        
        orderString = "";
        
    }
    
    public void actionPerformed (ActionEvent e){
        
        if (e.getSource() == processItem){
            try {
                processItemFunc();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        
        if (e.getSource() == confirmItem){

            confirmItemFunc();
        }
         
        if (e.getSource() == viewOrder){
            
            viewOrderFunc();
            
        }
          
        if (e.getSource() == finishOrder){
            finishOrderFunc();
        }
        
        if (e.getSource() == newOrder){
            newOrderFunc();
        }
        if (e.getSource() == exit){

            System.exit(0);
        }
    }
}


