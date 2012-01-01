/*
 Copyright 2011 Software Freedom Conservancy.

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

package org.openqa.selenium.remote.server.testing;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Locale;

public class FakeHttpServletResponse extends HeaderContainer
    implements HttpServletResponse {

  private final StringWriter stringWriter = new StringWriter();
  private final ServletOutputStream servletOutputStream =
      new StringServletOutputStream(stringWriter);
  private int status = HttpServletResponse.SC_OK;

  public int getStatus() {
    return status;
  }
  
  public String getBody() {
    return stringWriter.toString();
  }

  /////////////////////////////////////////////////////////////////////////////
  //
  //  HttpServletResponse methods.
  //
  /////////////////////////////////////////////////////////////////////////////

  public void addCookie(Cookie cookie) {
    throw new UnsupportedOperationException();
  }

  public String encodeURL(String s) {
    throw new UnsupportedOperationException();
  }

  public String encodeRedirectURL(String s) {
    throw new UnsupportedOperationException();
  }

  public String encodeUrl(String s) {
    throw new UnsupportedOperationException();
  }

  public String encodeRedirectUrl(String s) {
    throw new UnsupportedOperationException();
  }

  public void sendError(int i, String s) throws IOException {
    throw new UnsupportedOperationException();
  }

  public void sendError(int i) throws IOException {
    throw new UnsupportedOperationException();
  }

  public void sendRedirect(String s) throws IOException {
    setStatus(SC_SEE_OTHER);
    setHeader("Location", s);
  }

  public void setStatus(int i) {
    this.status = i;
  }

  public void setStatus(int i, String s) {
    throw new UnsupportedOperationException();
  }

  public String getCharacterEncoding() {
    throw new UnsupportedOperationException();
  }

  public String getContentType() {
    throw new UnsupportedOperationException();
  }

  public ServletOutputStream getOutputStream() throws IOException {
    return servletOutputStream;
  }

  public PrintWriter getWriter() throws IOException {
    throw new UnsupportedOperationException();
  }

  public void setCharacterEncoding(String s) {
    String type = getHeader("content-type");
    setHeader("content-type", type + "; charset=" + s);
  }

  public void setContentLength(int i) {
    setIntHeader("content-length", i);
  }

  public void setContentType(String type) {
    setHeader("content-type", type);
  }

  public void setBufferSize(int i) {
    throw new UnsupportedOperationException();
  }

  public int getBufferSize() {
    throw new UnsupportedOperationException();
  }

  public void flushBuffer() throws IOException {
    throw new UnsupportedOperationException();
  }

  public void resetBuffer() {
    throw new UnsupportedOperationException();
  }

  public boolean isCommitted() {
    throw new UnsupportedOperationException();
  }

  public void reset() {
    throw new UnsupportedOperationException();
  }

  public void setLocale(Locale locale) {
    throw new UnsupportedOperationException();
  }

  public Locale getLocale() {
    throw new UnsupportedOperationException();
  }

  private static class StringServletOutputStream extends ServletOutputStream {

    private final PrintWriter printWriter;

    private StringServletOutputStream(StringWriter stringWriter) {
      this.printWriter = new PrintWriter(stringWriter);
    }

    @Override
    public void write(int i) throws IOException {
      printWriter.write(i);
    }
  }
}
