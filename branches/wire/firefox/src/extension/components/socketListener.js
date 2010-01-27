/*
 Copyright 2007-2009 WebDriver committers
 Copyright 2007-2009 Google Inc.
 Portions copyright 2007 ThoughtWorks, Inc

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

/**
 * @fileoverview Defines a class that reads commands from a socket and
 * dispatches them to the nsICommandProcessor. When the response is ready, it is
 * serialized and sent back to the client through the socket.
 */


/**
 * Communicates with a client by reading and writing from a socket.
 * @param {Dispatcher} dispatcher The instance to send all parsed requests to.
 * @param {nsISocketTransport} transport The connected socket transport.
 * @constructor
 * @extends {nsIStreamListener}
 */
function SocketListener(dispatcher, transport) {

  /**
   * The instance to send all parsed requests to.
   * @type {Dispatcher}
   * @private
   */
  this.dispatcher_ = dispatcher;

  /**
   * Transport for the socket this instance will read/write to.
   * @type {nsISocketTransport}
   * @private
   */
  this.transport_ = transport;

  /**
   * Output stream for the socket transport.
   * @type {nsIOutputStream}
   * @private
   */
  this.outputStream_ = transport.openOutputStream(
      Components.interfaces.nsITransport.OPEN_BLOCKING,
      /*segmentSize=*/0,
      /*segmentCount=*/0);

  var socketInputStream = transport.openInputStream(
      /*flags=*/0,
      /*segmentSize=*/0,
      /*segmentCount=*/0);

  /**
   * Input stream used to write to the socket transport.
   * @type {nsIConverterInputStream}
   * @private
   */
  this.inputStream_ = Components.
      classes["@mozilla.org/intl/converter-input-stream;1"].
      createInstance(Components.interfaces.nsIConverterInputStream);

  this.inputStream_.init(socketInputStream, SocketListener.CHARSET, 0, 0x0000);

  var pump = Components.classes["@mozilla.org/network/input-stream-pump;1"].
      createInstance(Components.interfaces.nsIInputStreamPump);
  pump.init(socketInputStream,
      /*streamPos=*/-1,
      /*streamLen=*/-1,
      /*segmentSize=*/0,
      /*segmentCount=*/0,
      /*closeWhenDone=*/true);
  pump.asyncRead(this, null);

  /**
   * The converter used when writing data back to the socket.
   * @type {nsIScriptableUnicodeConverter}
   * @private
   */
  this.converter_ = Components.
      classes['@mozilla.org/intl/scriptableunicodeconverter'].
      createInstance(Components.interfaces.nsIScriptableUnicodeConverter);

  this.converter_.charset = SocketListener.CHARSET;
}


/**
 * Charset used for socket I/O.
 * @type {string}
 */
SocketListener.CHARSET = 'UTF-8';


/**
 * The set of valid HTTP methods.
 * @enum {string}
 */
SocketListener.HTTP_METHODS = {
  'DELETE': 'DELETE',
  'GET': 'GET',
  'HEAD': 'HEAD',
  'OPTIONS': 'OPTIONS',
  'POST': 'POST',
  'PUT': 'PUT',
  'TRACE': 'TRACE'
};


/**
 * Signals the start of a request. Each request lasts for the life of the
 * underlying socket connection and represents a session with a FirefoxDriver
 * client. 
 * @see {nsIRequestObserver#onStartRequest}
 */
SocketListener.prototype.onStartRequest = function(request, context) {
};


/**
 * Signals the end of a request (e.g. the underlying socket connection was
 * closed).
 * @see {nsIRequestObserver#onStopRequest}
 */
SocketListener.prototype.onStopRequest = function(request, context, status) {
};


/**
 * Called whenever another chunk of data is ready to be read from the socket.
 * @param {nsIRequest} request The data's origin.
 * @param {nsISupports} context User defined context.
 * @param {nsIInputStream} inputStream The input stream containing the data
 *     chunk.
 * @param {number} offset The total number of bytes read by previous calls to
 *     {@code #onDataAvailable}.
 * @param {number} count The number of bytes available in the stream.
 * @see {nsIStreamListener#onDataAvailable}
 */
SocketListener.prototype.onDataAvailable = function(request, context,
                                                    inputStream, offset,
                                                    count) {
  // This will blow up if we get an HTTPS request.  Oh well.
  var incoming = {};

  var bytesRead = this.inputStream_.readString(count, incoming);
  if (!bytesRead) {
    // Well...just close the connection.
    this.transport_.close(0);
    return;
  }

  var request;
  try {
    request = this.parseRequest_(incoming.value);
  } catch (ex) {
    var description = (typeof ex == 'string') ? '400 Bad Request' :
                                                '500 Internal Server Error';
    var message = 'HTTP/1.1 ' + description + '\r\n\r\n' + ex.toString();

    this.outputStream_.write(message, message.length);
    this.outputStream_.flush();
    this.transport_.close(0);
  }

  var response = new Response(request, this.outputStream_);
  this.dispatcher_.dispatch(request, response);
};


/**
 * Parses a request received on this socket.
 * @param {string} data The raw request data.
 */
SocketListener.prototype.parseRequest_ = function(data) {
  var lines = data.split('\r\n');

  function trim(str) {
    return str.replace(/^[\s\xa0]+|[\s\xa0]+$/g, '');
  }

  var requestLine = trim(lines.shift()).split(/\s+/);
  if (requestLine.length < 3) {
    throw 'Error parsing request line';
  }

  var method = requestLine.shift().toUpperCase();
  var path = requestLine.shift();
  var protocol = requestLine.shift().toUpperCase();

  if (protocol.indexOf('HTTP') != 0) {
    throw 'Not an HTTP request: <' + protocol + '>';
  }

  // Make sure we were given a valid HTTP method.
  if (typeof SocketListener.HTTP_METHODS[method] == 'undefined') {
    throw 'Invalid HTTP method: "' + method + '"';
  }

  var headers = {};
  for (var line; line = lines.shift();) {
    var parts = trim(line).match(/([^:\s]*)\s*:\s*([^\s]*)/);
    headers[parts[1].toLowerCase()] = parts[2];
  }

  // Make sure the host was specified. We need this for correct redirects.
  if (typeof headers['host'] == 'undefined') {
    throw 'No "Host" header specified';
  }

  // Reconstitute the original request URL.
  var requestUrl = 'http://' + headers['host'] + path;
  try {
    requestUrl = Components.classes["@mozilla.org/network/io-service;1"].
      getService(Components.interfaces.nsIIOService).
      newURI('http://' + headers['host'] + path, null, null).
      QueryInterface(Components.interfaces.nsIURL);
  } catch (ex) {
    throw 'Error parsing request URL: ' + requestUrl;
  }

  // See if we need to parse the request body.
  var body = null;
  if (method == SocketListener.HTTP_METHODS.POST ||
      method == SocketListener.HTTP_METHODS.PUT) {
    body = lines.shift() || '';
  }

  // We're done.
  this.inputStream_.close();
  return new Request(method, requestUrl, headers, body);
};
