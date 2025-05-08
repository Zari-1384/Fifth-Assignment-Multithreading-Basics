import java.io.IOException;
import java.io.*;
import java.util.*;

public class ReportGenerator {
    static class TaskRunnable implements Runnable {
        private final String path;
        private double totalCost;
        private int totalAmount;
        private int totalDiscountSum;
        private int totalLines;
        private Product mostExpensiveProduct;
        private double highestCostAfterDiscount;

        public TaskRunnable(String path) {
            this.path = path;
            this.totalCost = 0;
            this.totalAmount = 0;
            this.totalDiscountSum = 0;
            this.totalLines = 0;
            this.highestCostAfterDiscount = 0;
            this.mostExpensiveProduct = null;
        }

        @Override
        public void run() {
            //TODO:
            // - Read all lines from the input file (path)
            // - For each line, parse product ID, amount, and discount
            // - The format of the files are like this:
            //      [productId],[amount],[discountAmount]
            // - Find the corresponding product from catalog
            // - Calculate discounted cost and update total stats (totalAmount, totalCost, totalDiscountSum, totalLines)
            // - Track the most expensive purchase after discount
            try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    int productId = Integer.parseInt(parts[0]);
                    int amount = Integer.parseInt(parts[1]);
                    int discountPercent = Integer.parseInt(parts[2]);

                    Product product = findProductById(productId);
                    if (product == null) continue;

                    double price = product.getPrice();
                    double discountedUnitPrice = price * (1 - discountPercent / 100.0);
                    double totalPurchaseCost = discountedUnitPrice * amount;

                    totalLines++;
                    totalAmount += amount;
                    totalDiscountSum += (price * discountPercent / 100.0) * amount;
                    totalCost += totalPurchaseCost;

                    if (totalPurchaseCost > highestCostAfterDiscount) {
                        highestCostAfterDiscount = totalPurchaseCost;
                        mostExpensiveProduct = product;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private Product findProductById(int id) {
            for (Product product : productCatalog) {
                if (product != null && product.getProductID() == id) {
                    return product;
                }
            }
            return null;
        }

        public void makeReport() {
            // TODO:
            // - Print the filename
            // - Print total cost and total items bought
            // - Calculate and print average discount
            // - Display info about the most expensive purchase after discount

            System.out.println("Report for file: " + path);
            System.out.printf("Total Cost: $%.2f\n", totalCost);
            System.out.println("Total Items Bought: " + totalAmount);
            double avgDiscount = (totalAmount == 0) ? 0 : totalDiscountSum / totalAmount;
            System.out.printf("Average Discount per item: $%.2f\n", avgDiscount);
            if (mostExpensiveProduct != null) {
                System.out.printf("Most Expensive Purchase (after discount): %s - $%.2f\n",
                        mostExpensiveProduct.getProductName(), highestCostAfterDiscount);
            }
            System.out.println("******************************");
        }
    }

    static class Product {
        private int productID;
        private String productName;
        private double price;

        public Product(int productID, String productName, double price) {
            this.productID = productID;
            this.productName = productName;
            this.price = price;
        }

        public int getProductID() {
            return productID;
        }

        public String getProductName() {
            return productName;
        }

        public double getPrice() {
            return price;
        }
    }
    private static final String[] ORDER_FILES = {
            // TODO: Define the paths to the order detail text files in the resources folder

            "src/main/resources/2021_order_details.txt",
            "src/main/resources/2022_order_details.txt",
            "src/main/resources/2023_order_details.txt",
            "src/main/resources/2024_order_details.txt"
    };

    static Product[] productCatalog = new Product[20];

    public static void loadProducts() throws IOException {
        // TODO:
        // - Read lines from Products.txt
        // - For each line, parse product ID, name, and price
        // - The format of the file is like this:
        //      [productId],[name],[price]
        // - Store Product objects in the productCatalog array

        BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/Products.txt"));
        String line;
        int index = 0;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");
            int id = Integer.parseInt(parts[0]);
            String name = parts[1];
            double price = Double.parseDouble(parts[2]);
            productCatalog[index++] = new Product(id, name, price);
        }
        reader.close();
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        // TODO:
        // - Create one TaskRunnable and Thread for each order file
        // - Start all threads
        // - Wait for all threads to finish
        // - After all threads are done, call makeReport() on each TaskRunnable

        loadProducts();

        Thread[] threads = new Thread[ORDER_FILES.length];
        TaskRunnable[] tasks = new TaskRunnable[ORDER_FILES.length];

        for (int i = 0; i < ORDER_FILES.length; i++) {
            tasks[i] = new TaskRunnable(ORDER_FILES[i]);
            threads[i] = new Thread(tasks[i]);
            threads[i].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        for (TaskRunnable task : tasks) {
            task.makeReport();
        }
    }
}