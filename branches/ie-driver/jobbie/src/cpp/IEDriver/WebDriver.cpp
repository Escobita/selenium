#include "StdAfx.h"
#include "WebDriver.h"

void * event_handler(enum mg_event event_raised, 
					 struct mg_connection *conn, 
					 const struct mg_request_info *request_info) {
	int returnCode = NULL;
	if (event_raised == MG_NEW_REQUEST) {
		returnCode = server->ProcessRequest(conn, request_info);
	}

	return &returnCode;
}

webdriver::IEDriverServer* StartServer(int port) {
	char buffer[6];
	_itoa(port, buffer, 10);
	char* options[] = { "listening_ports", buffer, "access_control_list", "-0.0.0.0/0,+127.0.0.1", NULL };
	server = new webdriver::IEDriverServer(port);
	ctx = mg_start(event_handler, (const char **)options);
	return server;
}

void StopServer(webdriver::IEDriverServer *server) {
	::SendMessage(server->manager_window_handle(), WM_CLOSE, NULL, NULL);
	mg_stop(ctx);
}