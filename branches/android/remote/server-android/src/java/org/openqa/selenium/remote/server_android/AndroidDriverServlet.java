/*
Copyright 2007-2009 WebDriver committers
Copyright 2007-2009 Google Inc.

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

package org.openqa.selenium.remote.server_android;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletConfig;

import org.openqa.selenium.android.AndroidDriver;
import org.openqa.selenium.remote.server.DriverServlet;

import android.content.Context;
import android.util.Log;

public class AndroidDriverServlet extends DriverServlet {
  @Override
  public void init(ServletConfig config) throws ServletException
  {
      super.init(config);
      Context ctx =
        (Context)getServletContext().getAttribute("org.mortbay.ijetty.context");
      if (ctx == null)
        Log.e("Context", "is null");
      else
        Log.e("Context", ctx.getPackageName());
      AndroidDriver.setContext(ctx);
  }
  
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    if (request.getPathInfo() == null)
      response.sendRedirect(request.getRequestURI() + "/");
    else
      super.doGet(request, response);
  }
}
