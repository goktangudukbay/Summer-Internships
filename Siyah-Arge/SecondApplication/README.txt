Server and client programs that use TCP to communicate. Client can open or close echo during runtime. Client does not use multithreading.
 It uses select to get input from the user, send the message, receive echo and support gracefully shutdown.
There are two servers. The first server is from the first application that used multithreading with C++ vector library. 
The second server program uses single thread to handle all clients. 
This time I used a list because when a new client connects insertion takes less time compared to a vector.
To handle all clients using a single thread i used select to iterate until the maximum file descriptor in the list.
The servers also support graceful shutdown.
