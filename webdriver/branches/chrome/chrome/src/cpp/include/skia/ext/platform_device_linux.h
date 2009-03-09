// Copyright (c) 2006-2008 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

#ifndef SKIA_EXT_PLATFORM_DEVICE_LINUX_H_
#define SKIA_EXT_PLATFORM_DEVICE_LINUX_H_

#include "SkDevice.h"

namespace skia {

// Blindly copying the mac hierarchy.
class PlatformDeviceLinux : public SkDevice {
 public:
  // Returns if the preferred rendering engine is vectorial or bitmap based.
  virtual bool IsVectorial() = 0;

 protected:
  // Forwards |bitmap| to SkDevice's constructor.
  PlatformDeviceLinux(const SkBitmap& bitmap);
};

}  // namespace skia

#endif  // SKIA_EXT_PLATFORM_DEVICE_LINUX_H_
