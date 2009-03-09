// Copyright (c) 2009 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

// This header is meant to be included in multiple passes, hence no traditional
// header guard.
// See ipc_message_macros.h for explanation of the macros and passes.

// Developer tools consist of the following parts:
//
// DevToolsAgent lives in the renderer of an inspected page and provides access
// to the pages resources, DOM, v8 etc. by means of IPC messages.
//
// DevToolsClient is a thin delegate that lives in the tools front-end renderer
// and converts IPC messages to frontend method calls and allows the frontend
// to send messages to the DevToolsAgent.
//
// All the messages are routed through browser process.
//
// Chain of communication between the components may be described by the
// following diagram:
//  ----------------------------
// | (tools frontend            |
// | renderer process)          |
// |                            |            --------------------
// |tools    <--> DevToolsClient+<-- IPC -->+ (browser process)  |
// |frontend                    |           |                    |
//  ----------------------------             ---------+----------
//                                                    ^
//                                                    |
//                                                   IPC
//                                                    |
//                                                    v
//                          --------------------------+--------
//                         | inspected page <--> DevToolsAgent |
//                         |                                   |
//                         | (inspected page renderer process) |
//                          -----------------------------------
//
// This file describes developer tools message types.

#include "chrome/common/ipc_message_macros.h"

// These are messages sent from DevToolsAgent to DevToolsClient through the
// browser.
IPC_BEGIN_MESSAGES(DevToolsClient)

  // Response message for DevToolsAgentMsg_DebugAttach.
  IPC_MESSAGE_CONTROL0(DevToolsClientMsg_DidDebugAttach)

  // WebKit and JavaScript error messages to log to the console
  // or debugger UI.
  IPC_MESSAGE_CONTROL1(DevToolsClientMsg_DebuggerOutput,
                       std::wstring /* msg */)

IPC_END_MESSAGES(DevToolsClient)


//-----------------------------------------------------------------------------
// These are messages sent from DevToolsClient to DevToolsAgent through the
// browser.
IPC_BEGIN_MESSAGES(DevToolsAgent)

  // Initialize the V8 debugger in the renderer.
  IPC_MESSAGE_CONTROL0(DevToolsAgentMsg_DebugAttach)

  // Shutdown the V8 debugger in the renderer.
  IPC_MESSAGE_CONTROL0(DevToolsAgentMsg_DebugDetach)

  // Break V8 execution.
  IPC_MESSAGE_CONTROL1(DevToolsAgentMsg_DebugBreak,
                       bool  /* force */)

  // Send a command to the V8 debugger.
  IPC_MESSAGE_CONTROL1(DevToolsAgentMsg_DebugCommand,
                       std::wstring  /* cmd */)

IPC_END_MESSAGES(DevToolsAgent)

