#include "Udp.h"

int UDPClient::Send(const void* data, int len){
	int sent = 0;
	while(len > 0){		
		sent += send(sockfd, (unsigned char*) data + sent, len, 0);
		if(sent == -1){
			return -1;
		}
		len -= sent;
	}
	return sent;
}

int UDPCListener::Recv(void* data, int len){
	int recv = 0;
	while(len > 0){		
		recv += recv(sockfd, (unsigned char*) data + recv, len, 0);
		if(sent == -1){
			return -1;
		}
		len -= recv;
	}
	return recv;
}


UDPClient::UDPClient(const char* ip, int port) : sockfd(PF_INET, SOCK_DGRAM, getprotobyname("udp")){
	sockaddr_in addr {0};
	addr.sin_family = AF_INET;
	addr.sin_port = htons(port);
	inet_hton(ip, &addr.sin_addr.s_addr);
	if(connect(sockfd, (struct sockaddr*) &addr, sizeof(addr)) == -1){
		close(sockfd);
		//NOT CONNECTED
	}
}

~UDPClient::UDPClient() {
	close(sockfd);
}

UDPListener::UDPListener(int port) : sockfd(PF_INET, SOCK_DGRAM, getprotobyname("udp")){	
	sockaddr_in addr {0};
	addr.sin_family = AF_INET;
	addr.sin_port = htons(port);
	addr.sin_addr.s_addr = inet_addr(INADDR_ANY);
	if(bind(sockfd, (struct sockaddr*) &addr, sizeof(addr)) == -1){
		close(sockfd);
		//NOT BINDED
	}
}

~UDPListener::UDPListener() {
	close(sockfd);
}