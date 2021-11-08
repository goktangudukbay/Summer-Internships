//*************************************************************
// 					MUSTAFA GÖKTAN GÜDÜKBAY *
// 						Siyah AR-GE. *
//*************************************************************

//C++ Implementation of the UDP server.


#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <arpa/inet.h>
#include <netinet/in.h>
#include <csignal>
#include <iostream>

using namespace std;

#define PORT	 8080

//global variables
int shuttingDown = 0;

//graceful shutdown
void gracefulShutdown (int signum){
   shuttingDown = 1;
}

int main() {
  //variables
	int sockfd;
	char buffer[1024];
  pthread_t thread_id;
  int port;
  struct sockaddr_in servaddr, cliaddr;
  int n;
  socklen_t len;
  int echo;
  fd_set set;
  struct timeval timeout;
  int select_return_value;

  //code
  signal(SIGINT, gracefulShutdown);//Handling ctrl-c for graceful shutdown

  echo = 0;

  cout << "Server is opening. Enter the port number: ";
  cin >> port;


	// Creating socket file descriptor
	if ( (sockfd = socket(AF_INET, SOCK_DGRAM, 0)) < 0 ) {
		perror("socket creation failed");
		exit(EXIT_FAILURE);
	}

	memset(&servaddr, 0, sizeof(servaddr));
	memset(&cliaddr, 0, sizeof(cliaddr));

	// Filling server information
	servaddr.sin_family = AF_INET; // IPv4
  servaddr.sin_port = htons(port);
	servaddr.sin_addr.s_addr = inet_addr("127.0.0.1");

	// Bind the socket with the server address
	if ( bind(sockfd, (const struct sockaddr *)&servaddr,
			sizeof(servaddr)) < 0 )
	{
		perror("bind failed");
		exit(EXIT_FAILURE);
	}

	len = sizeof(cliaddr); //len is value/result

  while(!shuttingDown){
    memset(buffer, 0, 1024);

    //check if there is something to read using select, timeout is 100 microseconds
    timeout.tv_sec = 0;
    timeout.tv_usec = 100;
    FD_ZERO (&set);
    FD_SET (sockfd, &set);

    select_return_value = select(sockfd + 1, &set, NULL, NULL, &timeout);

    if(select_return_value <= 0)
      continue;

    n = recvfrom(sockfd, (char *)buffer, 1024,
          MSG_WAITALL, ( struct sockaddr *) &cliaddr,
          &len);
    buffer[n] = '\0';

    //client terminating
    if (n == 0)
      printf("A client is terminating.\n");

    else if(strcmp("Open", buffer) == 0)
     echo = 1;

    else if(strcmp("Close", buffer) == 0)
     echo = 0;

    else{
      printf("\nReceived Message: %s\n", buffer);
      //send back the message
      if(echo == 1)
        sendto(sockfd, buffer, strlen(buffer),
          MSG_CONFIRM, (const struct sockaddr *) &cliaddr,
          len);
    }
  }

  printf("Server is shutting down.\n");

  close(sockfd);
	return 0;
}
