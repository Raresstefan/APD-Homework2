# Homework2

## Order workers:

The threads dealing with processing orders read orders from the orders file
and parse each line separating the order id from the number of products that are part of that order.
For each product, a worker is created to search for a specific product from that order in
the product file (the search is done according to the index of the product we are looking for at a
certain point in time). It can be submitted in the threadPool specific to product workers as needed
a new worker is created. A semaphore is used to ensure that writing to the output file
order-specific will only be realized when all level 2 threads (those for products)
end (all products from the respective order have been found). When all the products in the order have
have been found, the ordersInteger variable is decremented to announce that we have finished processing an order,
and when the value of this variable reaches 0, we close the command-specific threadPool.



## Product workers:

The threads dealing with product processing look for a specific product in the products file.
When a specific product is found it is written in the product-specific output file and make room in
the product-specific threadPool for another worker. When the product you are looking for is found, a release is given
to the semaphore to announce level 1 threads (those for orders) that another product has been found.
When a product has been found in the products file, productsInteger is decremented. When the value of this
variables ends up being 0, we close the product-specific threadPool.
