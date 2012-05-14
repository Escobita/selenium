/*
Copyright 2007-2009 Selenium committers

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

package org.openqa.selenium.remote.server.renderer;

import com.google.common.base.Charsets;

import org.openqa.selenium.remote.BeanToJsonConverter;
import org.openqa.selenium.remote.server.HttpRequest;
import org.openqa.selenium.remote.server.HttpResponse;
import org.openqa.selenium.remote.server.rest.RestishHandler;
import org.openqa.selenium.remote.server.rest.Renderer;

import java.nio.charset.Charset;

public class JsonResult implements Renderer {

  protected final String propertyName;

  public JsonResult(String propertyName) {
    if (propertyName.startsWith(":")) {
      this.propertyName = propertyName.substring(1);
    } else {
      this.propertyName = propertyName;
    }
  }

  public void render(HttpRequest request, HttpResponse response, RestishHandler handler)
      throws Exception {
    Object result = request.getAttribute(propertyName);

    String json = new BeanToJsonConverter().convert(result);
    byte[] data = Charsets.UTF_8.encode(json).array();

    response.setContentType("application/json");
    response.setEncoding(Charsets.UTF_8);
    response.setContent(data);
    response.end();
  }
}
