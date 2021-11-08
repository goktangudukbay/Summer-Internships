//C++ Implementation of the UDP server.


#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <arpa/inet.h>
#include <netinet/in.h>
#include <iostream>

using namespace std;

#define PORT	 8080

//global variables
int terminating = 0;

//Handle ctrl-c
void signalHandler (int signum){
  terminating = 1;
}

int main() {
  //variables
	int sockfd;
	char buffer[1024];
	struct sockaddr_in servaddr;
  char ip[16];
  int port;
  int n;
  socklen_t len;
  fd_set set;
  struct timeval timeout;
  int select_return_value;
  int send_return_value;

  //code
  cout << "Enter the IP address: ";
  cin >> ip;

  cout << "Enter the Port number: ";
  cin >> port;

	// Creating socket file descriptor
	if ( (sockfd = socket(AF_INET, SOCK_DGRAM, 0)) < 0 ) {
		perror("socket creation failed");
		exit(EXIT_FAILURE);
	}

	memset(&servaddr, 0, sizeof(servaddr));

	// Filling server information
	servaddr.sin_family = AF_INET;
	servaddr.sin_port = htons(port);
	servaddr.sin_addr.s_addr = inet_addr(ip);;


  printf("Message to send ('q' to quit, 'Open' to open echo, 'Close' to close echo)\n\n");

  while(!terminating){
    memset(buffer,0,1024);

    //check if there is echo
    timeout.tv_sec = 0;
    timeout.tv_usec = 100;
    FD_ZERO (&set);
    FD_SET (sockfd, &set);
    FD_SET (0, &set);

    select_return_value = select(sockfd + 1, &set, NULL, NULL, &timeout);

    if(select_return_value){

      if(FD_ISSET(0, &set)){
          cin.getline(buffer, sizeof(buffer));

          if (strcmp("q", buffer) == 0){
            printf("Terminating\n");
            break;
          }

          send_return_value = sendto(sockfd, buffer, strlen(buffer),
            MSG_CONFIRM, (const struct sockaddr *) &servaddr,
              sizeof(servaddr));

          printf("send_return_value: %d\n", send_return_value);

          if(send_return_value == -1)
            printf("Could not send the message.\n");
        }

        if(FD_ISSET(sockfd, &set)){
          memset(buffer,0,1024);

          n = recvfrom(sockfd, (char *)buffer, 1024,
              MSG_WAITALL, (struct sockaddr *) &servaddr,
              &len);
  
          buffer[n] = '\0';

          printf("ECHO: %s\n", buffer);
        }
      }
  }

	close(sockfd);
	return 0;
}
