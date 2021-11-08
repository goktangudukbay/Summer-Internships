//C++ Implementation of the TCP client that uses linked list.


#include <stdio.h>
#include <sys/socket.h>
#include <arpa/inet.h>
#include <unistd.h>
#include <string.h>
#include <iostream>
#include <csignal>


using namespace std;

//global variables
int serverClosed = 0;
int termination = 0;
int sock = 0;

//Handle ctrl-c
void signalHandler (int signum){
  termination = 1;
}

void* readerThread(void* arg){

  int select_return_value1, select_return_value2;
  fd_set set;
  struct timeval timeout;
  int valread;
  char buffer[1024];

  while(!serverClosed & !termination){
    timeout.tv_sec = 0;
    timeout.tv_usec = 100;
    FD_ZERO (&set);
    FD_SET (sock, &set);

    select_return_value1 = select(sock + 1, &set, NULL, NULL, &timeout);

    if (select_return_value1 > 0) {
      memset(buffer,0,1024);
      valread = recv(sock, buffer, 1024,0);
      if(valread == 0){
        printf("Server is closed, terminating.\n");
        serverClosed = 1;
        break;
      }

      printf("ECHO: %s\n", buffer);
    }
  }

  pthread_exit(NULL);
}

int main(int argc, char const *argv[])
{

    //variables
    struct sockaddr_in serv_addr;
    char buffer[1024] = {0};
    pthread_t thread_id;

    fd_set rfds;
    struct timeval tv;
    int retval;
    int len;
    int send_return_value;
    char ip[16];
    int port;


    cout << "Enter the IP address: ";
    cin >> ip;

    cout << "Enter the Port number: ";
    cin >> port;


    //code
    if ((sock = socket(AF_INET, SOCK_STREAM, 0)) < 0)
    {
        printf("\n Socket creation error \n");
        return -1;
    }

    serv_addr.sin_family = AF_INET;
    serv_addr.sin_port = htons(port);

    // Convert IPv4 and IPv6 addresses from text to binary form
    if(inet_pton(AF_INET, ip, &serv_addr.sin_addr)<=0)
    {
        printf("\nInvalid address/ Address not supported \n");
        return -1;
    }

    if (connect(sock, (struct sockaddr *)&serv_addr, sizeof(serv_addr)) < 0)
    {
        printf("\nConnection Failed \n");
        return -1;
    }


    signal(SIGINT, signalHandler);

    // Creating a new thread
    pthread_create(&thread_id, NULL, &readerThread, NULL);

    printf("Message to send ('q' to quit, 'Open' to open echo, 'Close' to close echo)\n\n");


    while(!termination){
      memset(buffer,0,1024);


      while(!serverClosed & !termination){
        FD_ZERO(&rfds);
        FD_SET(0, &rfds);

        /* Wait up to five seconds. */
        tv.tv_sec = 5;
        tv.tv_usec = 0;

        retval = select(1, &rfds, NULL, NULL, &tv);
        if(retval > 0)
          break;
       }
      if(serverClosed | termination)
        break;
      

      cin.getline(buffer, sizeof(buffer));

      if (strcmp("q", buffer) == 0){
        termination = 1;
        printf("Terminating\n");
        break;
      }

      send_return_value = send(sock , buffer , strlen(buffer) , 0);//send the message

      if(send_return_value == -1)
        cout << "Could not send the mssage." << endl;
    }

    pthread_join(thread_id, NULL);
    return 0;
}
