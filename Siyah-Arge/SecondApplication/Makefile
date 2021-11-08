

all: server server2 client 

server: server.cpp
	g++ server.cpp -pthread -g -o server

server2: server.cpp
	g++ serverSingleThread.cpp -pthread -g -o serverSingleThread

client: client.cpp
	g++ client.cpp -pthread -g -o client

clean:
	rm -fr client server serverSingleThread *dSYM
