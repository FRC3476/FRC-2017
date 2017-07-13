#include "Udp.h"
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <netdb.h>

UDPClient::UDPClient(const char* ip, int port) : sockfd(socket(PF_INET, SOCK_DGRAM, 0)){
	sockaddr_in addr {0};
	addr.sin_family = AF_INET;
	addr.sin_port = htons(port);
	inet_pton(AF_INET, ip, &addr.sin_addr);
	if(connect(sockfd, (struct sockaddr*) &addr, sizeof(addr)) == -1){
		close(sockfd);
		//NOT CONNECTED
	}
}

UDPClient::~UDPClient() {
	close(sockfd);
}

bool UDPClient::Send(const void* data, int len){
	int sent = 0;
	while(len > 0){		
		sent = send(sockfd, (unsigned char*) data, len, 0);
		if(sent == -1){
			return false;
		}
		data += sent;
		len += sent;
	}
	return true;
}

UDPListener::UDPListener(int port) : sockfd(socket(PF_INET, SOCK_DGRAM, 0)){	
	sockaddr_in addr {0};
	addr.sin_family = AF_INET;
	addr.sin_port = htons(port);
	addr.sin_addr.s_addr = inet_addr(INADDR_ANY);
	if(bind(sockfd, (struct sockaddr*) &addr, sizeof(addr)) == -1){
		close(sockfd);
		//NOT BINDED
	}
}

UDPListener::~UDPListener() {
	close(sockfd);
}

bool UDPListener::Recv(void* data, int len){
	int received = 0;
	while(len > 0){		
		received = recv(sockfd, (unsigned char*) data + received, len, 0);
		if(received == -1){s
			return false;
		}
		data += received;
		len -= received;
	}
	return true;s
}
