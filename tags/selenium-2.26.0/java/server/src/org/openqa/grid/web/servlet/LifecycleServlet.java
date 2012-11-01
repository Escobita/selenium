/*
Copyright 2012 Selenium committers
Copyright 2012 Software Freedom Conservancy

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


package org.openqa.grid.web.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openqa.grid.common.exception.GridException;
import org.openqa.grid.internal.Registry;
import org.openqa.selenium.browserlaunchers.Sleeper;

/**
 * API to manage grid lifecycle
 */
public class LifecycleServlet extends RegistryBasedServlet {

  public LifecycleServlet() {
    super(null);
  }

  public LifecycleServlet(Registry registry) {
    super(registry);
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    process(request, response);
  }



  protected void process(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    response.setContentType("text/html");
    response.setCharacterEncoding("UTF-8");
    response.setStatus(200);
    String action = request.getParameter("action");
    if ("shutdown".equals(action)) {
      Runnable initiateHubShutDown = new Runnable() {
        public void run() {
          Sleeper.sleepTight(500);
          System.exit(0);
        }
      };
      Thread isd = new Thread(initiateHubShutDown);
      isd.setName("initiateHubShutDown");
      isd.start();
    } else {
      throw new GridException("Unknown lifecycle action: " + action);
    }
    response.getWriter().close();
  }
}
