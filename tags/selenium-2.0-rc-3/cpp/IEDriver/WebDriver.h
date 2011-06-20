// Copyright 2011 WebDriver committers
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

#ifndef WEBDRIVER_IE_WEBDRIVER_H_
#define WEBDRIVER_IE_WEBDRIVER_H_

#include "Server.h"

#define EXPORT __declspec(dllexport)

#ifdef __cplusplus
extern "C" {
#endif

webdriver::Server* server = NULL;
static void* event_handler(enum mg_event event_raised, 
							struct mg_connection* conn, 
							const struct mg_request_info* request_info);

struct mg_context* ctx = NULL;
int handler_result_code;
EXPORT webdriver::Server* StartServer(int port);
EXPORT void StopServer(webdriver::Server* myserver);
EXPORT int GetServerSessionCount();
EXPORT bool ServerIsRunning();
EXPORT int GetServerPort();

#ifdef __cplusplus
}
#endif

#endif // WEBDRIVER_IE_WEBDRIVER_H_
