// Copyright (c) 2006-2008 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

// An implementation of BrowserProcess for unit tests that fails for most
// services. By preventing creation of services, we reduce dependencies and
// keep the profile clean. Clients of this class must handle the NULL return
// value, however.

#ifndef CHROME_TEST_TESTING_BROWSER_PROCESS_H_
#define CHROME_TEST_TESTING_BROWSER_PROCESS_H_

#include "build/build_config.h"

#include <string>

#include "base/string_util.h"
#include "base/waitable_event.h"
#include "chrome/browser/browser_process.h"
#include "chrome/common/notification_service.h"
#include "base/logging.h"

class TestingBrowserProcess : public BrowserProcess {
 public:
  TestingBrowserProcess()
      : shutdown_event_(new base::WaitableEvent(true, false)) {
  }

  virtual ~TestingBrowserProcess() {
  }

  virtual void EndSession() {
  }

  virtual ResourceDispatcherHost* resource_dispatcher_host() {
    return NULL;
  }

  virtual MetricsService* metrics_service() {
    return NULL;
  }

  virtual base::Thread* io_thread() {
    return NULL;
  }

  virtual base::Thread* file_thread() {
    return NULL;
  }

  virtual base::Thread* db_thread() {
    return NULL;
  }

  virtual ProfileManager* profile_manager() {
    return NULL;
  }

  virtual PrefService* local_state() {
    return NULL;
  }

  virtual WebAppInstallerService* web_app_installer_service() {
    return NULL;
  }

  virtual IconManager* icon_manager() {
    return NULL;
  }

  virtual sandbox::BrokerServices* broker_services() {
    return NULL;
  }

  virtual DebuggerWrapper* debugger_wrapper() {
    return NULL;
  }

  virtual ClipboardService* clipboard_service() {
    return NULL;
  }

  virtual GoogleURLTracker* google_url_tracker() {
    return NULL;
  }

  virtual void InitBrokerServices(sandbox::BrokerServices*) {
  }

  virtual AutomationProviderList* InitAutomationProviderList() {
    return NULL;
  }

  virtual void InitDebuggerWrapper(int port) {
  }

  virtual unsigned int AddRefModule() {
    return 1;
  }
  virtual unsigned int ReleaseModule() {
    return 1;
  }

  virtual bool IsShuttingDown() {
    return false;
  }

  virtual views::AcceleratorHandler* accelerator_handler() {
    return NULL;
  }

  virtual printing::PrintJobManager* print_job_manager() {
    return NULL;
  }

  virtual const std::wstring& GetApplicationLocale() {
    static std::wstring* value = NULL;
    if (!value)
      value = new std::wstring(L"en");
    return *value;
  }

  virtual MemoryModel memory_model() { return HIGH_MEMORY_MODEL; }

  virtual base::WaitableEvent* shutdown_event() { return shutdown_event_.get(); }

 private:
  NotificationService notification_service_;
  scoped_ptr<base::WaitableEvent> shutdown_event_;
  DISALLOW_COPY_AND_ASSIGN(TestingBrowserProcess);
};

#endif  // CHROME_TEST_TESTING_BROWSER_PROCESS_H_
