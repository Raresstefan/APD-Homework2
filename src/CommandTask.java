import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class CommandTask implements Runnable {
    private BufferedReader ordersInputFile;
    private String productsInputFileName;
    private BufferedWriter ordersOutputFile;
    private BufferedWriter productsOutputFile;
    private ExecutorService threadPoolOrders;
    private ExecutorService threadPoolProducts;
    private AtomicInteger ordersInteger;
    private AtomicInteger productsInteger;

    public CommandTask(BufferedReader ordersInputFile, String productsInputFileName, BufferedWriter ordersOutputFile,
                        BufferedWriter productsOutputFile, ExecutorService threadPoolOrders,
                        ExecutorService threadPoolProducts, AtomicInteger ordersInteger, AtomicInteger productsInteger) {
        this.ordersInputFile = ordersInputFile;
        this.productsInputFileName = productsInputFileName;
        this.ordersOutputFile = ordersOutputFile;
        this.productsOutputFile = productsOutputFile;
        this.threadPoolOrders = threadPoolOrders;
        this.threadPoolProducts = threadPoolProducts;
        this.ordersInteger = ordersInteger;
        this.productsInteger = productsInteger;
    }

    @Override
    public void run() {
        try {
            // Read the orders in the part
            String line;
            line = ordersInputFile.readLine();
            if (line != null) {
                System.out.println(line);
                // Split the line into order id and product id
                String[] parts = line.split(",");
                String orderId = parts[0];
                int numProducts = Integer.parseInt(parts[1]);
                if (numProducts != 0) {
                    Semaphore semaphore = new Semaphore((-1) * (numProducts - 1));
                    for (int i = 0; i < numProducts; i++) {
                        ProductTask productTask = new ProductTask(productsInputFileName, productsOutputFile, threadPoolProducts, productsInteger, semaphore,
                                                                orderId, i);
                        productsInteger.incrementAndGet();
                        threadPoolProducts.submit(productTask);
                    }
                
                    // Wait for the semaphore to be released by the level 2 thread
                    semaphore.acquire();
                    ordersOutputFile.write(orderId + "," + numProducts + ",shipped\n");
                    ordersInteger.incrementAndGet();
                    threadPoolOrders.submit(new CommandTask(ordersInputFile, productsInputFileName, ordersOutputFile, 
                    productsOutputFile, threadPoolOrders, threadPoolProducts, ordersInteger, productsInteger));
                }
            }
            int left = ordersInteger.decrementAndGet();
            if (left == 0) {
                ordersOutputFile.close();
                ordersInputFile.close();
                threadPoolOrders.shutdown();
            }
            
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        } 
    }
}
