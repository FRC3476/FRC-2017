#ifndef UDP_H_
#define UDP_H_
#include <memory>

class UDPClient {
public:
	UDPClient(const char* ip, int port);
	~UDPClient();
	bool Send(const void *data, int len);
private:
	int sockfd;
};

class UDPListener {	
public:
	UDPListener(int port);
	~UDPListener();
	bool Recv(void *data, int len);
private:
	int sockfd;
};
#endif