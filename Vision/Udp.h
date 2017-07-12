#ifndef UDP_H_
#define UDP_H_
#include <memory>
class UDPClient {
public:
	UDPClient(const char* ip, int port);
	~UDPClient();
	int Send(const void *data, int len);
private:
	int sockfd;
}

class UDPListener {	
public:
	UDPListener(int port);
	~UDPListener();
	int Recv(void *data, int len);
private:
	int sockfd;
	
}

#endif