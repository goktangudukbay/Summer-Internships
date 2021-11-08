//*************************************************************
// 					MUSTAFA GÖKTAN GÜDÜKBAY *
// 						Siyah AR-GE. *
//*************************************************************

//C++ Implementation of the TCP server 
//that uses standard library vector.

#include <unistd.h>
#include <stdio.h>
#include <sys/socket.h>
#include <stdlib.h>
#include <netinet/in.h>
#include <string.h>
#include <iostream>
#include <csignal>
#include <vector>


using namespace std;

struct ThreadInformation{
  pthread_t thread_id;
};

//global variables
std::vector<struct ThreadInformation*> myList;
int server_fd;
struct sockaddr_in address;
int opt = 1;
int addrlen = sizeof(address);
int echo = 0;//no echo at the beginning
int shuttingDown = 0;
struct ThreadInformation* head = NULL;

//lock variable
pthread_mutex_t lock;

//thread function to listen
void* receiver(void* arg){
  char buffer[1024];
  int socket_id = *((int*) arg);
  int valread;

  fd_set set;
  struct timeval timeout;
  int select_return_value;

  while(!shuttingDown){
    memset(buffer, 0, 1024);

    //check if there is something to read using select, timeout is 100 microseconds
    timeout.tv_sec = 0;
    timeout.tv_usec = 100;
    FD_ZERO (&set);
    FD_SET (socket_id, &set);

    select_return_value = select(socket_id + 1, &set, NULL, NULL, &timeout);

    if(select_return_value > 0)
      valread = recv(socket_id , buffer, 1024, 0);
    else
      continue;

    //client terminating
    if (valread == 0){
      //lock
      pthread_mutex_lock(&lock);
      pthread_t thread_id = pthread_self();

      for (int i = 0; i < myList.size(); i++) {
		    if(myList.at(i)->thread_id == thread_id){
				myList.erase(myList.begin() + i);
				break;
			}
	  }

      printf("A client is terminating.\n");
      //unlock
      pthread_mutex_unlock(&lock);
      break;
    }

    if(strcmp("Open", buffer) == 0){
    //lock
     pthread_mutex_lock(&lock);
     echo = 1;
     //unlock
     pthread_mutex_unlock(&lock);
   }
    else if(strcmp("Close", buffer) == 0){
    //lock
     pthread_mutex_lock(&lock);
     echo = 0;
     //unlock
     pthread_mutex_unlock(&lock);
    }
    else{
      printf("\nReceived Message: %s\n", buffer);
      //send back the message
      if(echo == 1)
        send(socket_id , buffer , valread , 0);
    }
  }

  shutdown(socket_id, SHUT_RDWR);
  close(socket_id);
  delete (int*) arg;
  pthread_exit(NULL);
}

void gracefulShutdown (int signum){
  //lock
   pthread_mutex_lock(&lock);
   shuttingDown = 1;
   //unlock
   shutdown(server_fd, SHUT_RDWR);
   close(server_fd);
   pthread_mutex_unlock(&lock);
}

void cleaningMethod(){
  //clean
  printf("Terminating the server.\n");

  for (int i = 0; i < myList.size();) {
    pthread_join(myList.at(i)->thread_id, NULL);
    delete myList.at(i);
    myList.erase(myList.begin() + i);
	}
}

int main(int argc, char const *argv[])
{
    //variables
    int new_socket;//connected socket number of the client
    int* temp_socket;
    pthread_t thread_id;
    int port;

    cout << "Server is opening. Enter the port number: ";
    cin >> port;

    signal(SIGINT, gracefulShutdown);

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

    //loop to listen constantly
    while(shuttingDown == 0){
      if ((new_socket = accept(server_fd, (struct sockaddr *)&address, (socklen_t*)&addrlen))<0)
      {
        continue;
      }
      else{
        temp_socket = new int;
        *temp_socket = new_socket;

        // Creating a new thread
        pthread_create(&thread_id, NULL, &receiver, (void *)temp_socket);
        struct ThreadInformation* temp = new ThreadInformation;
        temp->thread_id = thread_id;    
        myList.push_back(temp);
      }
    }


    cleaningMethod();
    return 0;
}
