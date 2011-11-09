// Copyright 2011 Software Freedom Conservancy
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

#include <regex>
#include "server.h"
#include "logging.h"

#define SERVER_DEFAULT_PAGE "<html><head><title>WebDriver</title></head><body><p id='main'>This is the initial start page for the WebDriver server.</p></body></html>"
#define HTML_CONTENT_TYPE "text/html"
#define JSON_CONTENT_TYPE "application/json"

namespace webdriver {

Server::Server(const int port) {
  // It's possible to set the log level at compile time using this:
  LOG::Level("FATAL");
  LOG(INFO) << "Starting WebDriver server on port: " << port;
  this->port_ = port;
  this->PopulateCommandRepository();
}

Server::~Server(void) {
  SessionMap::iterator it = this->sessions_.begin();
  for (; it != this->sessions_.end(); ++it) {
    std::string session_id = it->first;
    this->ShutDownSession(session_id);
  }
}

void* Server::OnHttpEvent(enum mg_event event_raised,
                          struct mg_connection* conn,
                          const struct mg_request_info* request_info) {
  int handler_result_code = 0;
  if (event_raised == MG_NEW_REQUEST) {
    handler_result_code = reinterpret_cast<Server*>(request_info->user_data)->
        ProcessRequest(conn, request_info);
  }

  return reinterpret_cast<void*>(handler_result_code);
}

bool Server::Start() {
  char buffer[10];
  _itoa_s(this->port(), buffer, 10, 10);
  const char* options[] = { "listening_ports", buffer,
                            "access_control_list", "-0.0.0.0/0,+127.0.0.1",
                            "enable_keep_alive", "yes",
                            NULL };
  context_ = mg_start(&OnHttpEvent, this, options);
  if (context_ == NULL) {
    return false;
  }
  return true;
}

void Server::Stop() {
  if (context_) {
    mg_stop(context_);
    context_ = NULL;
  }
}

int Server::ProcessRequest(struct mg_connection* conn,
    const struct mg_request_info* request_info) {
  int http_response_code = NULL;
  std::string http_verb = request_info->request_method;
  std::string request_body = "{}";
  if (http_verb == "POST") {
    request_body = this->ReadRequestBody(conn, request_info);
  }

  if (strcmp(request_info->uri, "/") == 0) {
    this->SendHttpOk(conn,
                     request_info,
                     SERVER_DEFAULT_PAGE,
                     HTML_CONTENT_TYPE);
    http_response_code = 200;
  } else {
    std::string serialized_response = this->DispatchCommand(request_info->uri,
                                                             http_verb,
                                                             request_body);
    http_response_code = this->SendResponseToClient(conn,
                                                    request_info,
                                                    serialized_response);
  }

  return http_response_code;
}

std::string Server::CreateSession() {
  SessionHandle session_handle= this->InitializeSession();
  std::string session_id = session_handle->session_id();
  this->sessions_[session_id] = session_handle;
  return session_id;
}

void Server::ShutDownSession(const std::string& session_id) {
  SessionMap::iterator it = this->sessions_.find(session_id);
  if (it != this->sessions_.end()) {
    it->second->ShutDown();
    this->sessions_.erase(session_id);
  }
}

std::string Server::ReadRequestBody(struct mg_connection* conn,
    const struct mg_request_info* request_info) {
  std::string request_body = "";
  int content_length = 0;
  for (int header_index = 0; header_index < 64; ++header_index) {
    if (request_info->http_headers[header_index].name == NULL) {
      break;
    }
    if (strcmp(request_info->http_headers[header_index].name,
               "Content-Length") == 0) {
      content_length = atoi(request_info->http_headers[header_index].value);
      break;
    }
  }
  if (content_length == 0) {
    request_body = "{}";
  } else {
    std::vector<char> buffer(content_length + 1);
    int bytes_read = 0;
    while (bytes_read < content_length) {
      bytes_read += mg_read(conn,
                            &buffer[bytes_read],
                            content_length - bytes_read);
    }
    buffer[content_length] = '\0';
    request_body.append(&buffer[0]);
  }

  return request_body;
}

std::string Server::DispatchCommand(const std::string& uri,
                                     const std::string& http_verb,
                                     const std::string& command_body) {
  std::string session_id = "";
  std::string locator_parameters = "";
  std::string serialized_response = "";
  int command = this->LookupCommand(uri,
                                    http_verb,
                                    &session_id,
                                    &locator_parameters);
  if (command == NoCommand) {
    if (locator_parameters.size() != 0) {
      // Hand-code the response for an invalid HTTP verb for URL
      serialized_response.append("{ \"status\" : 405, ");
      serialized_response.append("\"sessionId\" : \"<no session>\", ");
      serialized_response.append("\"value\" : \"");
      serialized_response.append(locator_parameters);
      serialized_response.append("\" }");
    } else {
      // Hand-code the response for an unknown URL
      serialized_response.append("{ \"status\" : 404, ");
      serialized_response.append("\"sessionId\" : \"<no session>\", ");
      serialized_response.append("\"value\" : \"Command not found: ");
      serialized_response.append(http_verb);
      serialized_response.append(" ");
      serialized_response.append(uri);
      serialized_response.append("\" }");
    }
  } else {
    if (command == NewSession) {
      session_id = this->CreateSession();
    }

    SessionHandle session_handle = NULL;
    if (!this->LookupSession(session_id, &session_handle)) {
      // Hand-code the response for an invalid session id
      serialized_response.append("{ \"status\" : 404, ");
      serialized_response.append("\"sessionId\" : \"");
      serialized_response.append(session_id);
      serialized_response.append("\", ");
      serialized_response.append("\"value\" : \"session ");
      serialized_response.append(session_id);
      serialized_response.append(" does not exist\" }");
    } else {
      // Compile the serialized JSON representation of the command by hand.
      std::stringstream command_value_stream;
      command_value_stream << command;
      std::string command_value = command_value_stream.str();

      std::string serialized_command = "{ \"command\" : " + command_value;
      serialized_command.append(", \"locator\" : ");
      serialized_command.append(locator_parameters);
      serialized_command.append(", \"parameters\" : ");
      serialized_command.append(command_body);
      serialized_command.append(" }");
      bool session_is_valid = session_handle->ExecuteCommand(
          serialized_command,
          &serialized_response);
      if (!session_is_valid) {
        this->ShutDownSession(session_id);
      }
    }
  }
  return serialized_response;
}


bool Server::LookupSession(const std::string& session_id,
                           SessionHandle* session_handle) {
  SessionMap::iterator it = this->sessions_.find(session_id);
  if (it == this->sessions_.end()) {
    return false;
  }
  *session_handle = it->second;
  return true;
}

int Server::SendResponseToClient(struct mg_connection* conn,
                                 const struct mg_request_info* request_info,
                                 const std::string& serialized_response) {
  int return_code = 0;
  if (serialized_response.size() > 0) {
    Response response;
    response.Deserialize(serialized_response);
    return_code = response.status_code();
    if (return_code == 0) {
      this->SendHttpOk(conn,
                       request_info,
                       serialized_response,
                       JSON_CONTENT_TYPE);
      return_code = 200;
    } else if (return_code == 303) {
      std::string location = response.value().asString();
      response.SetSuccessResponse(response.value());
      this->SendHttpSeeOther(conn, request_info, location);
      return_code = 303;
    } else if (return_code == 400) {
      this->SendHttpBadRequest(conn, request_info, serialized_response);
      return_code = 400;
    } else if (return_code == 404) {
      this->SendHttpNotFound(conn, request_info, serialized_response);
      return_code = 404;
    } else if (return_code == 405) {
      std::string parameters = response.value().asString();
      this->SendHttpMethodNotAllowed(conn, request_info, parameters);
      return_code = 405;
    } else if (return_code == 501) {
      this->SendHttpNotImplemented(conn,
                                   request_info,
                                   "");
      return_code = 501;
    } else {
      this->SendHttpInternalError(conn, request_info, serialized_response);
      return_code = 500;
    }
  }
  return return_code;
}

// The standard HTTP Status codes are implemented below.  Chrome uses
// OK, See Other, Not Found, Method Not Allowed, and Internal Error.
// Internal Error, HTTP 500, is used as a catch all for any issue
// not covered in the JSON protocol.
void Server::SendHttpOk(struct mg_connection* connection,
                        const struct mg_request_info* request_info,
                        const std::string& body,
                        const std::string& content_type) {
  std::ostringstream out;
  out << "HTTP/1.1 200 OK\r\n"
    << "Content-Length: " << strlen(body.c_str()) << "\r\n"
    << "Content-Type: " << content_type << "; charset=UTF-8\r\n"
    << "Vary: Accept-Charset, Accept-Encoding, Accept-Language, Accept\r\n"
    << "Accept-Ranges: bytes\r\n"
    << "Connection: close\r\n\r\n";
  if (strcmp(request_info->request_method, "HEAD") != 0) {
    out << body << "\r\n";
  }

  mg_write(connection, out.str().c_str(), out.str().size());
}

void Server::SendHttpBadRequest(struct mg_connection* const connection,
                                const struct mg_request_info* request_info,
                                const std::string& body) {
  std::ostringstream out;
  out << "HTTP/1.1 400 Bad Request\r\n"
    << "Content-Length: " << strlen(body.c_str()) << "\r\n"
    << "Content-Type: application/json; charset=UTF-8\r\n"
    << "Vary: Accept-Charset, Accept-Encoding, Accept-Language, Accept\r\n"
    << "Accept-Ranges: bytes\r\n"
    << "Connection: close\r\n\r\n";
  if (strcmp(request_info->request_method, "HEAD") != 0) {
    out << body << "\r\n";
  }

  mg_printf(connection, "%s", out.str().c_str());
}

void Server::SendHttpInternalError(struct mg_connection* connection,
                                   const struct mg_request_info* request_info,
                                   const std::string& body) {
  std::ostringstream out;
  out << "HTTP/1.1 500 Internal Server Error\r\n"
    << "Content-Length: " << strlen(body.c_str()) << "\r\n"
    << "Content-Type: application/json; charset=UTF-8\r\n"
    << "Vary: Accept-Charset, Accept-Encoding, Accept-Language, Accept\r\n"
    << "Accept-Ranges: bytes\r\n"
    << "Connection: close\r\n\r\n";
  if (strcmp(request_info->request_method, "HEAD") != 0) {
    out << body << "\r\n";
  }

  mg_write(connection, out.str().c_str(), out.str().size());
}

void Server::SendHttpNotFound(struct mg_connection* const connection,
                              const struct mg_request_info* request_info,
                              const std::string& body) {
  std::ostringstream out;
  out << "HTTP/1.1 404 Not Found\r\n"
    << "Content-Length: " << strlen(body.c_str()) << "\r\n"
    << "Content-Type: application/json; charset=UTF-8\r\n"
    << "Vary: Accept-Charset, Accept-Encoding, Accept-Language, Accept\r\n"
    << "Accept-Ranges: bytes\r\n"
    << "Connection: close\r\n\r\n";
  if (strcmp(request_info->request_method, "HEAD") != 0) {
    out << body << "\r\n";
  }

  mg_printf(connection, "%s", out.str().c_str());
}

void Server::SendHttpMethodNotAllowed(
    struct mg_connection* connection,
    const struct mg_request_info* request_info,
    const std::string& allowed_methods) {
  std::ostringstream out;
  out << "HTTP/1.1 405 Method Not Allowed\r\n"
    << "Content-Type: text/html\r\n"
    << "Content-Length: 0\r\n"
    << "Allow: " << allowed_methods << "\r\n\r\n";

  mg_write(connection, out.str().c_str(), out.str().size());
}

void Server::SendHttpNotImplemented(struct mg_connection* connection,
                                    const struct mg_request_info* request_info,
                                    const std::string& body) {
  std::ostringstream out;
  out << "HTTP/1.1 501 Not Implemented\r\n\r\n";

  mg_write(connection, out.str().c_str(), out.str().size());
}

void Server::SendHttpSeeOther(struct mg_connection* connection,
                              const struct mg_request_info* request_info,
                              const std::string& location) {
  std::ostringstream out;
  out << "HTTP/1.1 303 See Other\r\n"
    << "Location: " << location << "\r\n"
    << "Content-Type: text/html\r\n"
    << "Content-Length: 0\r\n\r\n";

  mg_write(connection, out.str().c_str(), out.str().size());
}

int Server::LookupCommand(const std::string& uri,
                          const std::string& http_verb,
                          std::string* session_id,
                          std::string* locator) {
  int value = NoCommand;
  UrlMap::const_iterator it = this->commands_.begin();
  for (; it != this->commands_.end(); ++it) {
    std::vector<std::string> locator_param_names;
    std::string url_candidate = it->first;
    size_t param_start_pos = url_candidate.find_first_of(":");
    while (param_start_pos != std::string::npos) {
      size_t param_len = std::string::npos;
      size_t param_end_pos = url_candidate.find_first_of("/", param_start_pos);
      if (param_end_pos != std::string::npos) {
        param_len = param_end_pos - param_start_pos;
      }

      // Skip the colon
      std::string param_name = url_candidate.substr(param_start_pos + 1,
                                                    param_len - 1);
      locator_param_names.push_back(param_name);
      if (param_name == "sessionid" || param_name == "id") {
        url_candidate.replace(param_start_pos, param_len, "([0-9a-fA-F-]+)");
      } else {
        url_candidate.replace(param_start_pos, param_len, "([^/]+)");
      }
      param_start_pos = url_candidate.find_first_of(":");
    }

    std::tr1::regex matcher("^" + url_candidate + "$");
    std::tr1::match_results<std::string::const_iterator> matches;
    if (std::tr1::regex_search(uri, matches, matcher)) {
      VerbMap::const_iterator verb_iterator = it->second.find(http_verb);
      if (verb_iterator != it->second.end()) {
        value = verb_iterator->second;
        std::string param = "{";
        size_t param_count = locator_param_names.size();
        for (unsigned int i = 0; i < param_count; i++) {
          if (i != 0) {
            param.append(",");
          }

          std::string locator_param_value = matches[i + 1].str();
          param.append(" \"");
          param.append(locator_param_names[i]);
          param.append("\" : \"");
          param.append(locator_param_value);
          param.append("\"");
          if (locator_param_names[i] == "sessionid") {
            session_id->append(locator_param_value);
          }
        }

        param.append(" }");
        locator->append(param);
        break;
      } else {
        verb_iterator = it->second.begin();
        for (; verb_iterator != it->second.end(); ++verb_iterator) {
          if (locator->size() != 0) {
            locator->append(",");
          }
          locator->append(verb_iterator->first);
        }
      }
    }
  }

  return value;
}

void Server::PopulateCommandRepository() {
  this->commands_["/status"]["GET"] = Status;
  this->commands_["/session"]["POST"] = NewSession;
  this->commands_["/session/:sessionid"]["GET"] = GetSessionCapabilities;
  this->commands_["/session/:sessionid"]["DELETE"] = Quit;
  this->commands_["/session/:sessionid/window_handle"]["GET"] = GetCurrentWindowHandle;
  this->commands_["/session/:sessionid/window_handles"]["GET"] = GetWindowHandles;
  this->commands_["/session/:sessionid/url"]["GET"] = GetCurrentUrl;
  this->commands_["/session/:sessionid/url"]["POST"] = Get;
  this->commands_["/session/:sessionid/forward"]["POST"] = GoForward;
  this->commands_["/session/:sessionid/back"]["POST"] = GoBack;
  this->commands_["/session/:sessionid/refresh"]["POST"] = Refresh;
  this->commands_["/session/:sessionid/execute"]["POST"] = ExecuteScript;
  this->commands_["/session/:sessionid/execute_async"]["POST"] = ExecuteAsyncScript;
  this->commands_["/session/:sessionid/screenshot"]["GET"] = Screenshot;
  this->commands_["/session/:sessionid/frame"]["POST"] = SwitchToFrame;
  this->commands_["/session/:sessionid/window"]["POST"] = SwitchToWindow;
  this->commands_["/session/:sessionid/window"]["DELETE"] = Close;
  this->commands_["/session/:sessionid/cookie"]["GET"] = GetAllCookies;
  this->commands_["/session/:sessionid/cookie"]["POST"] = AddCookie;
  this->commands_["/session/:sessionid/cookie"]["DELETE"] = DeleteAllCookies;
  this->commands_["/session/:sessionid/cookie/:name"]["DELETE"] = DeleteCookie;
  this->commands_["/session/:sessionid/source"]["GET"] = GetPageSource;
  this->commands_["/session/:sessionid/title"]["GET"] = GetTitle;
  this->commands_["/session/:sessionid/element"]["POST"] = FindElement;
  this->commands_["/session/:sessionid/elements"]["POST"] = FindElements;
  this->commands_["/session/:sessionid/timeouts/implicit_wait"]["POST"] = ImplicitlyWait;
  this->commands_["/session/:sessionid/timeouts/async_script"]["POST"] = SetAsyncScriptTimeout;
  this->commands_["/session/:sessionid/element/active"]["POST"] = GetActiveElement;
  this->commands_["/session/:sessionid/element/:id/element"]["POST"] = FindChildElement;
  this->commands_["/session/:sessionid/element/:id/elements"]["POST"] = FindChildElements;
  this->commands_["/session/:sessionid/element/:id"]["GET"] = DescribeElement;
  this->commands_["/session/:sessionid/element/:id/click"]["POST"] = ClickElement;
  this->commands_["/session/:sessionid/element/:id/text"]["GET"] = GetElementText;
  this->commands_["/session/:sessionid/element/:id/submit"]["POST"] = SubmitElement;
  this->commands_["/session/:sessionid/element/:id/value"]["GET"] = GetElementValue;
  this->commands_["/session/:sessionid/element/:id/value"]["POST"] = SendKeysToElement;
  this->commands_["/session/:sessionid/element/:id/name"]["GET"] = GetElementTagName;
  this->commands_["/session/:sessionid/element/:id/clear"]["POST"] = ClearElement;
  this->commands_["/session/:sessionid/element/:id/selected"]["GET"] = IsElementSelected;
  this->commands_["/session/:sessionid/element/:id/enabled"]["GET"] = IsElementEnabled;
  this->commands_["/session/:sessionid/element/:id/displayed"]["GET"] = IsElementDisplayed;
  this->commands_["/session/:sessionid/element/:id/location"]["GET"] = GetElementLocation;
  this->commands_["/session/:sessionid/element/:id/location_in_view"]["GET"] = GetElementLocationOnceScrolledIntoView;
  this->commands_["/session/:sessionid/element/:id/size"]["GET"] = GetElementSize;
  this->commands_["/session/:sessionid/element/:id/css/:propertyName"]["GET"] = GetElementValueOfCssProperty;
  this->commands_["/session/:sessionid/element/:id/attribute/:name"]["GET"] = GetElementAttribute;
  this->commands_["/session/:sessionid/element/:id/equals/:other"]["GET"] = ElementEquals;
  this->commands_["/session/:sessionid/screenshot"]["GET"] = Screenshot;
  this->commands_["/session/:sessionid/orientation"]["GET"] = GetOrientation;
  this->commands_["/session/:sessionid/orientation"]["POST"] = SetOrientation;

  this->commands_["/session/:sessionid/:windowHandle/size"]["GET"] = GetWindowSize;
  this->commands_["/session/:sessionid/:windowHandle/size"]["POST"] = SetWindowSize;
  this->commands_["/session/:sessionid/:windowHandle/position"]["GET"] = GetWindowPosition;
  this->commands_["/session/:sessionid/:windowHandle/position"]["POST"] = SetWindowPosition;

  this->commands_["/session/:sessionid/accept_alert"]["POST"] = AcceptAlert;
  this->commands_["/session/:sessionid/dismiss_alert"]["POST"] = DismissAlert;
  this->commands_["/session/:sessionid/alert_text"]["GET"] = GetAlertText;
  this->commands_["/session/:sessionid/alert_text"]["POST"] = SendKeysToAlert;

  this->commands_["/session/:sessionid/keys"]["POST"] = SendKeysToActiveElement;
  this->commands_["/session/:sessionid/moveto"]["POST"] = MouseMoveTo;
  this->commands_["/session/:sessionid/click"]["POST"] = MouseClick;
  this->commands_["/session/:sessionid/doubleclick"]["POST"] = MouseDoubleClick;
  this->commands_["/session/:sessionid/buttondown"]["POST"] = MouseButtonDown;
  this->commands_["/session/:sessionid/buttonup"]["POST"] = MouseButtonUp;

  this->commands_["/session/:sessionid/ime/available_engines"]["GET"] = ListAvailableImeEngines;
  this->commands_["/session/:sessionid/ime/active_engines"]["GET"] = GetActiveImeEngine;
  this->commands_["/session/:sessionid/ime/activated"]["GET"] = IsImeActivated;
  this->commands_["/session/:sessionid/ime/activate"]["POST"] = ActivateImeEngine;
  this->commands_["/session/:sessionid/ime/deactivate"]["POST"] = DeactivateImeEngine;

  this->commands_["/session/:sessionId/touch/click"]["POST"] = TouchClick;
  this->commands_["/session/:sessionId/touch/down"]["POST"] = TouchDown;
  this->commands_["/session/:sessionId/touch/up"]["POST"] = TouchUp;
  this->commands_["/session/:sessionId/touch/move"]["POST"] = TouchMove;
  this->commands_["/session/:sessionId/touch/scroll"]["POST"] = TouchScroll;
  this->commands_["/session/:sessionId/touch/doubleclick"]["POST"] = TouchDoubleClick;
  this->commands_["/session/:sessionId/touch/longclick"]["POST"] = TouchLongClick;
  this->commands_["/session/:sessionId/touch/flick"]["POST"] = TouchFlick;
}

}  // namespace webdriver
