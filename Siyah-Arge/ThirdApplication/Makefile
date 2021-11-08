

all: server  client

server: server.cpp
	g++ server.cpp -pthread -g -o server

client: client.cpp
	g++ client.cpp -pthread -g -o client

clean:
	rm -fr client server *dSYM
