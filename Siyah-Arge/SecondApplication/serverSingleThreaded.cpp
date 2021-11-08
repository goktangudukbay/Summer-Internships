//*************************************************************
// 					MUSTAFA GÖKTAN GÜDÜKBAY *
// 						Siyah AR-GE. *
//*************************************************************

//C++ Implementation of the TCP single-threaded server that 
//uses standard library list.

#include <unistd.h>
#include <stdio.h>
#include <sys/socket.h>
#include <stdlib.h>
#include <netinet/in.h>
#include <string.h>
#include <iostream>
#include <csignal>
#include <list>


using namespace std;

//global variables
int terminating = 0;

struct ClientInformation{
  int socket_fd;
};

void closeServer (int signum){
  terminating = 1;
}

int main(int argc, char const *argv[]){

  //variables
  int server_fd;
  int port;
  list <struct ClientInformation> clients;
  fd_set set;
  struct timeval timeout;
  int select_return_value;
  int valread;
  int echo = 0;
  char buffer[1024];
  struct sockaddr_in address;
  int opt = 1;
  int addrlen = sizeof(address);
  int new_socket = 0;



  //code
  signal(SIGINT, closeServer);

  cout << "Server is opening. Enter the port number: ";
  cin >> port;

  // Creating socket file descriptor
  if ((server_fd = socket(AF_INET, SOCK_STREAM, 0)) == 0)
  {
      perror("socket failed");
      return -1;
  }

  // Forcefully attaching socket to the port 8080
  if (setsockopt(server_fd, SOL_SOCKET, SO_REUSEADDR | SO_REUSEPORT,
                                                &opt, sizeof(opt)))
  {
      cerr << "setsockopt";
      return -1;
  }
  address.sin_family = AF_INET;
  address.sin_addr.s_addr = INADDR_ANY;
  address.sin_port = htons( port );

  // Forcefully attaching socket to the port 8080
  if (bind(server_fd, (struct sockaddr *)&address, sizeof(address))<0)
  {
      cerr << "bind failed";
      return -1;
  }


  if (listen(server_fd, 3) < 0)
  {
      cerr << "listen";
      return -1;
  }



  while(!terminating){

    FD_ZERO (&set);
    FD_SET (server_fd, &set);
    int maxFD = server_fd;
    for (std::list<struct ClientInformation>::iterator it=clients.begin(); it != clients.end(); ++it){
      if(it->socket_fd > maxFD)
        maxFD = it->socket_fd;
      FD_SET (it->socket_fd, &set);
    }


    timeout.tv_sec = 0;
    timeout.tv_usec = 10000;

    select_return_value = select(maxFD + 1, &set, NULL, NULL, &timeout);

    if(select_return_value > 0){

      if(FD_ISSET(server_fd, &set)){
        new_socket = accept(server_fd, (struct sockaddr *)&address, (socklen_t*)&addrlen);

        if(new_socket <= 0){
          printf("Connection error.\n");
        }
        else{
          printf("%d\n", new_socket);
          struct ClientInformation client;
          client.socket_fd = new_socket;

          clients.push_back(client);
        }
      }

      for (std::list<struct ClientInformation>::iterator it=clients.begin(); it != clients.end(); ++it){
        if(FD_ISSET(it->socket_fd, &set)){
          memset(buffer, 0, 1024);

          valread = recv(it->socket_fd , buffer, 1024, 0);

          //client terminating
          if (valread == 0){
            it = clients.erase(it);
            --it;
            printf("A client is terminating.\n");
          }
          else if(strcmp("Open", buffer) == 0){
           echo = 1;
          }
          else if(strcmp("Close", buffer) == 0){
           echo = 0;
          }
          else{
            printf("\nReceived Message: %s\n", buffer);
            //send back the message
            if(echo == 1)
              send(it->socket_fd , buffer , valread , 0);
          }
        }
      }
    }

  }

  //close client sockets and server sockets, graceful shutdown
  for (std::list<struct ClientInformation>::iterator it=clients.begin(); it != clients.end(); ++it){
    shutdown(it->socket_fd, SHUT_RDWR);
    close(it->socket_fd);
  }

  shutdown(server_fd, SHUT_RDWR);
  close(server_fd);

  printf("Closing the server.\n");
}
