import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

class Tema2 {
    public static void main(String[] args) throws IOException {
        String inputDir = args[0];
        int numThreads = Integer.parseInt(args[1]);
        ExecutorService threadPoolOrders = Executors.newFixedThreadPool(numThreads);
        ExecutorService threadPoolProducts = Executors.newFixedThreadPool(numThreads);
        BufferedReader ordersInputFile = new BufferedReader(new FileReader(inputDir + "/orders.txt"));
        String productsInputFileName = inputDir + "/order_products.txt";
        BufferedWriter ordersOutputFile = new BufferedWriter(new FileWriter("orders_out.txt"));
        BufferedWriter productsOutputFile = new BufferedWriter(new FileWriter("order_products_out.txt"));
        AtomicInteger ordersInteger = new AtomicInteger(0);
        AtomicInteger productsInteger = new AtomicInteger(0);
        for (int i = 0; i < numThreads; i++) {
            CommandTask command = new CommandTask(ordersInputFile, productsInputFileName, ordersOutputFile, productsOutputFile, threadPoolOrders, threadPoolProducts,
                                                    ordersInteger, productsInteger);
            ordersInteger.incrementAndGet();
            threadPoolOrders.submit(command);
        }

    }
}
