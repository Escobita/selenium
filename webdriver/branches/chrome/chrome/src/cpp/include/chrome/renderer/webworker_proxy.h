// Copyright (c) 2009 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

#ifndef CHROME_RENDERER_WEBWORKER_PROXY_H_
#define CHROME_RENDERER_WEBWORKER_PROXY_H_

#include "base/basictypes.h"
#include "chrome/common/ipc_channel.h"
#include "webkit/glue/webworker.h"

class GURL;
class RenderView;

namespace IPC {
class Message;
}

// This class provides an implementation of WebWorker that the renderer provides
// to the glue.  This class converts function calls to IPC messages that are 
// dispatched in the worker process by WebWorkerClientProxy.  It also receives
// IPC messages from WebWorkerClientProxy which it converts to function calls to
// WebWorkerClient.
class WebWorkerProxy : public WebWorker,
                       public IPC::Channel::Listener {
 public:
  WebWorkerProxy(IPC::Message::Sender* sender, WebWorkerClient* client);
  virtual ~WebWorkerProxy();

  // WebWorker implementation.
  // These functions are called by WebKit (after the data types have been
  // converted by glue code).
  virtual void StartWorkerContext(const GURL& script_url,
                                  const string16& user_agent,
                                  const string16& source_code);
  virtual void TerminateWorkerContext();
  virtual void PostMessageToWorkerContext(const string16& message);
  virtual void WorkerObjectDestroyed();

  // IPC::Channel::Listener implementation.
  void OnMessageReceived(const IPC::Message& message);

 private:
  bool Send(IPC::Message* message);

  IPC::Message::Sender* sender_;
  int route_id_;

  // Used to communicate to the WebCore::Worker object in response to IPC
  // messages.
  WebWorkerClient* client_;

  DISALLOW_COPY_AND_ASSIGN(WebWorkerProxy);
};

#endif  // CHROME_RENDERER_WEBWORKER_PROXY_H_
