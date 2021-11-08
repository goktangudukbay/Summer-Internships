//C++ Implementation of the TCP single-threaded client that 
//uses standard library list.

#include <stdio.h>
#include <sys/socket.h>
#include <arpa/inet.h>
#include <unistd.h>
#include <string.h>
#include <iostream>
#include <csignal>

using namespace std;

//global variables
int terminating = 0;

//Handle ctrl-c
void signalHandler (int signum){
  terminating = 1;
}

int main(int argc, char const *argv[])
{

    //variables
    struct sockaddr_in serv_addr;
    char buffer[1024] = {0};

    fd_set rfds;
    struct timeval tv;
    int retval;
    int len;
    int send_return_value;
    char ip[16];
    int port;
    int inputSelect;
    int sock = 0;
    int valread;

    //code
    cout << "Enter the IP address: ";
    cin >> ip;

    cout << "Enter the Port number: ";
    cin >> port;

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

    printf("Message to send ('q' to quit, 'Open' to open echo, 'Close' to close echo)\n\n");


    while(!terminating){
      memset(buffer,0,1024);

      FD_ZERO(&rfds);
      FD_SET(sock, &rfds);
      FD_SET(0, &rfds);

      tv.tv_sec = 0;
      tv.tv_usec = 100000;

      retval = select(sock + 1, &rfds, NULL, NULL, &tv);

      if(retval > 0){
        if(FD_ISSET(0, &rfds)){
          cin.getline(buffer, sizeof(buffer));

          if (strcmp("q", buffer) == 0){
            printf("Terminating\n");
            break;
          }

          send_return_value = send(sock , buffer , strlen(buffer), 0);//send the message

          if(send_return_value == -1)
            printf("Could not send the message.\n");

        }
        if(FD_ISSET(sock, &rfds)){
          valread = recv(sock, buffer, 1024,0);
          if(valread == 0){
            printf("Server is closed, terminating.\n");
            break;
          }
          printf("ECHO: %s\n", buffer);
        }
      }
    }

    if(terminating)
      printf("Terminating with ctrl-c\n");

    return 0;
}
