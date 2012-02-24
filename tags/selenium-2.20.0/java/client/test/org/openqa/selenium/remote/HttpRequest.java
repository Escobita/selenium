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

import com.google.common.base.Throwables;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.openqa.selenium.remote.internal.HttpClientFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;


public class HttpRequest {

  private String response;

  public HttpRequest(Method method, String url, Object payload) {
    HttpUriRequest request = method.prepare(url, payload);
    execute(request);
  }

  private void execute(HttpUriRequest method) {
    try {
      HttpClientFactory httpClientFactory = new HttpClientFactory();
      final HttpClient defaultHttpClient = httpClientFactory.getHttpClient();
      HttpEntity httpEntity = null;
      try {
        HttpResponse res = defaultHttpClient.execute(method);
        httpEntity = res.getEntity();
        if (httpEntity != null) {
          this.response = EntityUtils.toString(httpEntity);
        }
      } finally {
        if (httpEntity != null) {
          EntityUtils.consume(httpEntity);
        }
        method.abort();
        httpClientFactory.close();
      }
    } catch (IOException e) {
      throw Throwables.propagate(e);
    }
  }

  public String getResponse() {
    return response;
  }

  // I don't know why I like enums with methods, but I do.
  public static enum Method {
    DELETE {
      @Override
      public HttpUriRequest prepare(String url, Object payload) {
        return new HttpDelete(url);
      }
    },
    GET {
      @Override
      public HttpUriRequest prepare(String url, Object payload) {
        HttpGet get = new HttpGet(url);
        get.addHeader("Accept", "application/json, */*");
        get.addHeader("Accept-Charset", "utf-8");
        get.addHeader("Content-Type", "application/json; charset=utf8");
        return get;
      }
    },
    POST {
      @Override
      public HttpUriRequest prepare(String url, Object payload) {
        HttpPost post = new HttpPost(url);
        post.addHeader("Accept", "application/json, */*");
        post.addHeader("Accept-Charset", "utf-8");
        post.addHeader("Content-Type", "application/json; charset=utf8");

        if (payload != null) {
          String content = new BeanToJsonConverter().convert(payload);
          try {
            post.setEntity(new StringEntity(content, "UTF-8"));
          } catch (UnsupportedEncodingException e) {
            throw Throwables.propagate(e);
          }
        }
        return post;
      }
    },
    PUT {
      @Override
      public HttpUriRequest prepare(String url, Object payload) {
        return new HttpPut(url);
      }
    };

    public abstract HttpUriRequest prepare(String url, Object payload);
  }
}
