// Copyright (c) 2009 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

#ifndef CHROME_WORKER_WEBWORKERCLIENT_PROXY_H_
#define CHROME_WORKER_WEBWORKERCLIENT_PROXY_H_

#include <vector>

#include "base/basictypes.h"
#include "base/scoped_ptr.h"
#include "chrome/common/ipc_channel.h"
#include "googleurl/src/gurl.h"
#include "webkit/glue/webworkerclient.h"

class WebWorker;

// This class receives IPCs from the renderer and calls the WebCore::Worker
// implementation (after the data types have been converted by glue code).  It
// is also called by the worker code and converts these function calls into
// IPCs that are sent to the renderer, where they're converted back to function
// calls by WebWorkerProxy.
class WebWorkerClientProxy : public WebWorkerClient,
                             public IPC::Channel::Listener {
 public:
  WebWorkerClientProxy (const GURL& url, int route_id);
  ~WebWorkerClientProxy ();

  // WebWorkerClient implementation.
  void PostMessageToWorkerObject(const string16& message);
  void PostExceptionToWorkerObject(
      const string16& error_message,
      int line_number,
      const string16& source_url);
  void PostConsoleMessageToWorkerObject(
      int destination,
      int source,
      int level,
      const string16& message,
      int line_number,
      const string16& source_url);
  void ConfirmMessageFromWorkerObject(bool has_pending_activity);
  void ReportPendingActivity(bool has_pending_activity);
  void WorkerContextDestroyed();

  // IPC::Channel::Listener implementation.
  void OnMessageReceived(const IPC::Message& message);

 private:
  bool Send(IPC::Message* message);

  // The source url for this worker.
  GURL url_;

  int route_id_;

  // Whether we've received StartWorkerContext message.
  bool started_worker_;

  scoped_ptr<WebWorker> impl_;

  // Stores messages that arrived before the StartWorkerContext message.
  std::vector<IPC::Message*> queued_messages_;

  DISALLOW_COPY_AND_ASSIGN(WebWorkerClientProxy);
};

#endif  // CHROME_WORKER_WEBWORKERCLIENT_PROXY_H_
