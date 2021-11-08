Server and client programs that use TCP to communicate. Server uses a linked list implementation made by me to store thread ids for graceful shutdown.
A mutex lock is used to access the linked list.
Both server and client uses multithreading. Server creates a thread for each client. Client has two threads, 
one for listening to the server for echos and one for getting input from the user and sending messages to the server. 
Client can open or close echo during runtime. The programs support gracefully shutdown. 
So, when the client closes server can understand if the connection is closed and when the server is closed client also closes. 
Select is used for gracefully shutdown. CTRL-c is handled using signal handling. 
