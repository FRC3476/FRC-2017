#include "Udp.h"

int UDPClient::SendData(const void *data, int len){
	unsigned char *pData = (unsigned char *) data;
	while(len > 0){		
		int sent = send(sockfd, pData, len, 0);
		if(sent == -1){
			return -1;
		}
		pData += sent;
		len -= sent;
	}
	return pData;
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

UDPListener::UDPListener(int port) : sockfd(PF_INET, SOCK_DGRAM, getprotobyname("udp")){	
	sockaddr_in addr {0};
	addr.sin_family = AF_INET;
	addr.sin_port = htons(port);
	addr.sin_addr.s_addr = inet_addr(INADDR_ANY);
	if(bing(sockfd, (struct sockaddr*) &addr, sizeof(addr)) == -1){
		close(sockfd);
		//NOT BINDED
	}
}