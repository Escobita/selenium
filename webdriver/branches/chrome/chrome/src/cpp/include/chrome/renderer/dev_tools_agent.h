// Copyright (c) 2009 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

#ifndef CHROME_RENDERER_DEV_TOOLS_AGENT_H_
#define CHROME_RENDERER_DEV_TOOLS_AGENT_H_

#include <string>

#include "base/basictypes.h"
#include "base/ref_counted.h"
#include "base/scoped_ptr.h"
#include "chrome/common/ipc_channel_proxy.h"
#include "webkit/glue/debugger_bridge.h"

class MessageLoop;
class RenderView;

// Inspected page end of communication channel between the render process of
// the page being inspected and tools UI renderer process. All messages will
// go through browser process. On the renderer side of the tools UI there's
// a corresponding ToolsClient object.
class DevToolsAgent : public IPC::ChannelProxy::MessageFilter,
                      public DebuggerBridge::Delegate {
 public:
  // DevToolsAgent is a field of the RenderView. The view is supposed to remove
  // this agent from message filter list on IO thread before dying.
  explicit DevToolsAgent(RenderView* view, MessageLoop* view_loop);
  virtual ~DevToolsAgent();

 private:
  // Sends message to DevToolsClient. May be called on any thread.
  void Send(const IPC::Message& tools_client_message);

  // Sends message to DevToolsClient. Must be called on IO thread. Takes
  // ownership of the message.
  void SendFromIOThread(IPC::Message* message);

  // IPC::ChannelProxy::MessageFilter overrides. Called on IO thread.
  virtual void OnFilterAdded(IPC::Channel* channel);
  virtual bool OnMessageReceived(const IPC::Message& message);
  virtual void OnFilterRemoved();

  // Debugger::Delegate callback method to handle debugger output.
  void DebuggerOutput(const std::wstring& out);

  // Evaluate javascript URL in the renderer
  void EvaluateScript(const std::wstring& script);

  // All these OnXXX methods will be executed in IO thread so that we can
  // handle debug messages even when v8 is stopped.
  void OnDebugAttach();
  void OnDebugDetach();
  void OnDebugBreak(bool force);
  void OnDebugCommand(const std::wstring& cmd);

  scoped_refptr<DebuggerBridge> debugger_;

  RenderView* view_;
  MessageLoop* view_loop_;

  IPC::Channel* channel_;
  MessageLoop* io_loop_;

  DISALLOW_COPY_AND_ASSIGN(DevToolsAgent);
};

#endif  // CHROME_RENDERER_DEV_TOOLS_AGENT_H_

