#ifndef UDP_H_
#define UDP_H_
#include <memory>
class UDPClient {
public:
	UDPClient(const char* ip, int port);
	~UDPClient();
	int SendData(const void *data, int len);
private:
	std::unique_ptr<int, close> sockfd;
}

class UDPListener {	
public:
	UDPListener(int port);
	~UDPListener();
private:
	std::unique_ptr<int, close> sockfd;
	
}

#endif