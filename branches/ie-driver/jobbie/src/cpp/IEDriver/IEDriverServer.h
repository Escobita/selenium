#pragma once
#include <string>
#include <vector>
#include <map>
#include <regex>
#include <sstream>
#include "BrowserManager.h"
#include "mongoose.h"

#define SERVER_DEFAULT_PAGE "<html><head><title>WebDriver</title></head><body><p id='main'>This is the initial start page for the IE WebDriver.</p></body></html>"

using namespace std;

extern "C"
{
class IEDriverServer
{
public:
	IEDriverServer(int port);
	virtual ~IEDriverServer(void);
	HWND m_managerHwnd;
	int processRequest(struct mg_connection *conn, const struct mg_request_info *request_info);

private:
	int lookupCommand(std::string uri, std::string httpVerb, std::wstring *locator);
	std::map<std::string, std::map<std::string, int>> m_commandRepository;
	void populateCommandRepository(void);
	std::wstring sendCommandToManager(std::wstring serializedCommand);
	void SendWelcomePage(mg_connection *connection, const mg_request_info *request_info);
	void SendHttpOk(mg_connection *connection, const mg_request_info *request_info, std::wstring body);
	void SendHttpInternalError(mg_connection *connection, const mg_request_info *request_info, std::wstring body);
	void SendHttpSeeOther(mg_connection *connection, const mg_request_info *request_info, std::string body);
};
	IEDriverServer* server;
	static void * event_handler(enum mg_event event_raised, 
								struct mg_connection *conn, 
								const struct mg_request_info *request_info);

	struct mg_context *ctx;
	__declspec(dllexport) IEDriverServer* StartServer(int port);
	__declspec(dllexport) void StopServer(IEDriverServer* server);
}
