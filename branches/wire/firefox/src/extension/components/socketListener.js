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

  this.method_ = '';
  this.requestUrl_ = '';
  this.headers_ = {};
  this.body_ = '';
  this.contentLengthRemaining_ = 0;
}


/**
 * Charset used for socket I/O.
 * @type {string}
 */
SocketListener.CHARSET = 'UTF-8';


/**
 * Signals the start of a request. Each request lasts for the life of the
 * underlying socket connection and represents a session with a FirefoxDriver
 * client. 
 * @see {nsIRequestObserver#onStartRequest}
 */
SocketListener.prototype.onStartRequest = function(request, context) {
  this.method_ = '';
  this.requestUrl_ = '';
  this.headers_ = {};
  this.body_ = '';
  this.contentLengthRemaining_ = 0;
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

  // Our nsIConverterInputStream will not handle HTTPS connections, so if this
  // is an HTTPS request, we'll blow up and the socket will be closed.
  // TODO: support HTTPS?
  var charCountRead = this.inputStream_.readString(count, incoming);
  if (!charCountRead) {
    // Well...just close the connection.
    this.transport_.close(0);
    return;
  }

  try {
    if (this.contentLengthRemaining_ == 0) {
      this.parseRequest_(incoming.value);
    }
    else {
      this.body_ += incoming.value;

      // This rigmarole is because readString returns the number
      // of characters read, but we need the number of bytes
      // to tell if we're done reading the stream.
      var escaped = encodeURIComponent(incoming.value);
      var escapedCharCount = 0;
      if (escaped.indexOf('%', 0) != -1) {
        escapedCharCount = escaped.split('%').length - 1;
      }
      var bytesRead = escaped.length - (2 * escapedCharCount);
            
      // If we read more data than the Content-Length header specified, then too
      // much data was sent by the client, and we consider this a malformed
      // request.
      // N.B. This algorithm isn't the best, and could stand to be refined.
      if (this.contentLengthRemaining_ - bytesRead < 0) {
        throw 'POST or PUT request body is longer than Content-Length header';
      }
      this.contentLengthRemaining_ -= bytesRead;
    }
  } catch (ex) {
    var description = (typeof ex == 'string') ? '400 Bad Request' :
                                                '500 Internal Server Error';
    var message = 'HTTP/1.1 ' + description + '\r\n\r\n' + ex.toString();

    this.outputStream_.write(message, message.length);
    this.outputStream_.flush();
    this.transport_.close(0);
  }

  if (this.contentLengthRemaining_ <= 0) {
    // We're done.
    this.inputStream_.close();
    var clientRequest = new Request(this.method_, this.requestUrl_, this.headers_, this.body_);
    var clientResponse = new Response(clientRequest, this.outputStream_);
    this.dispatcher_.dispatch(clientRequest, clientResponse);
  }
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
  
  this.method_ = requestLine.shift().toUpperCase();
  var path = requestLine.shift();
  var protocol = requestLine.shift().toUpperCase();

  // We don't support HTTPS requests. If we get one, an error will occur above,
  // causing the socket to close.
  if (protocol.indexOf('HTTP') != 0) {
    throw 'Not an HTTP request: <' + protocol + '>';
  }

  // Make sure we were given a valid HTTP method.
  if (typeof Request.Method[this.method_] == 'undefined') {
    throw 'Invalid HTTP method: "' + this.method_ + '"';
  }

  this.headers_ = {};
  for (var line; line = lines.shift(); ) {
    var parts = trim(line).match(/([^:\s]*)\s*:\s*([^\s]*)/);
    this.headers_[parts[1].toLowerCase()] = parts[2];
  }

  // Make sure the host was specified. We need this for correct redirects.
  if (typeof this.headers_['host'] == 'undefined') {
    throw 'No "Host" header specified';
  }

  // Reconstitute the original request URL.
  this.requestUrl_ = 'http://' + this.headers_['host'] + path;
  try {
    this.requestUrl_ = Components.classes["@mozilla.org/network/io-service;1"].
        getService(Components.interfaces.nsIIOService).
        newURI('http://' + this.headers_['host'] + path, null, null).
        QueryInterface(Components.interfaces.nsIURL);
  } catch (ex) {
    throw 'Error parsing request URL: ' + this.requestUrl_;
  }

  if (this.method_ == Request.Method.POST || this.method_ == Request.Method.PUT) {
    // POST and PUT requests must send a Content-Length header.
    if (typeof this.headers_['content-length'] == 'undefined') {
      throw 'No "Content-Length" header specified for POST or PUT request';
    }
    // If the headers and body are sent in a single socket write, it is expected
    // that the entire body is sent in the request, and there is no further body
    // to process. Otherwise, we capture how much data we expect to be sent and
    // send a 100-Continue response.
    var body = lines.shift() || '';
    if (body.length > 0) {
      this.body_ = body;
    }
    else {
      this.contentLengthRemaining_ = parseInt(this.headers_['content-length']);
      var continueMessage = 'HTTP/1.1 100 Continue\r\n\r\n';
      this.outputStream_.write(continueMessage, continueMessage.length);
      this.outputStream_.flush();
    }
  }
};
