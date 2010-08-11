/*
Copyright 2007-2010 WebDriver committers
Copyright 2007-2010 Google Inc.

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

package org.openqa.selenium.remote;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class HttpRequest {
  private String response;

  public HttpRequest(Method method, String url, Object payload) throws Exception {
    if (method == Method.POST) {
      HttpPost post = new HttpPost(url);
      post.addHeader("Accept", "application/json");

      String content = new BeanToJsonConverter().convert(payload);

      System.out.println("content = " + content);

      try {
      post.setEntity(new StringEntity(content, "UTF-8"));
      } catch (Throwable e) {
        e.printStackTrace();
      }

      HttpResponse httpResponse = new DefaultHttpClient().execute(post);

      response = httpResponse.getEntity() == null ? null : EntityUtils.toString(httpResponse.getEntity()); 
      return;
    }

    throw new RuntimeException("Unsupported method");
  }

  public String getResponse() {
    return response;
  }

  public static enum Method {
    POST
  }
}
