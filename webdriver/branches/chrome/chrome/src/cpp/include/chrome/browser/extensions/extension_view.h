// Copyright (c) 2006-2009 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

#ifndef CHROME_BROWSER_EXTENSIONS_EXTENSION_VIEW_H_
#define CHROME_BROWSER_EXTENSIONS_EXTENSION_VIEW_H_

#include "chrome/browser/renderer_host/render_view_host_delegate.h"

// TODO(port): Port these files.
#if defined(OS_WIN)
#include "chrome/browser/views/hwnd_html_view.h"
#else
#include "chrome/common/temp_scaffolding_stubs.h"
#endif

class Profile;
struct WebPreferences;

// This class is the browser component of an extension component's RenderView.
// It handles setting up the renderer process, if needed, with special
// priviliges available to extensions.  The view may be drawn to the screen or
// hidden.
class ExtensionView : public HWNDHtmlView,
                      public RenderViewHostDelegate {
 public:
  ExtensionView(const GURL& url, Profile* profile);

  // HWNDHtmlView
  virtual void CreatingRenderer();

  // RenderViewHostDelegate
  virtual Profile* GetProfile() const { return profile_; }
  virtual WebPreferences GetWebkitPrefs();
  virtual void RunJavaScriptMessage(
      const std::wstring& message,
      const std::wstring& default_prompt,
      const int flags,
      IPC::Message* reply_msg,
      bool* did_suppress_message);

 private:
  Profile* profile_;
};

#endif  // CHROME_BROWSER_EXTENSIONS_EXTENSION_VIEW_H_
