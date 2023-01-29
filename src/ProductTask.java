import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;


public class ProductTask implements Runnable {
    private String productsInputFileName;
    private BufferedWriter productsOutputFile;
    private ExecutorService threadPoolProducts;
    private AtomicInteger productsInteger;
    private Semaphore semaphore;
    private String orderId;
    private int productIndex;

    public ProductTask(String productsInputFileName, BufferedWriter productsOutputFile, ExecutorService threadPoolProducts,
                        AtomicInteger productsInteger, Semaphore semaphore, String orderId, int productIndex) {
        this.productsInputFileName = productsInputFileName;
        this.productsOutputFile = productsOutputFile;
        this.threadPoolProducts = threadPoolProducts;
        this.productsInteger = productsInteger;
        this.semaphore = semaphore;
        this.orderId = orderId;
        this.productIndex = productIndex;
    }

    @Override
    public void run() {
        try {
            String line;
            int processedProducts = 0;
            BufferedReader productsInputFile = new BufferedReader(new FileReader(productsInputFileName));
            line = productsInputFile.readLine();
            
            while (line != null) {
                String[] parts = line.split(",");
                String extractedId = parts[0];
                String productId = parts[1];

                if (extractedId.equals(orderId)) {
                    if (processedProducts == productIndex) {
                        productsOutputFile.write(orderId + "," + productId + ",shipped\n");
                        break;
                    }
                    processedProducts++;
                }
                line = productsInputFile.readLine();
                
            }
            int left = productsInteger.decrementAndGet();
            productsInputFile.close();
            semaphore.release();
            
            if (left == 0) {
                productsOutputFile.close();
                threadPoolProducts.shutdown();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
