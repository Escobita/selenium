// Copyright (c) 2006-2008 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

#ifndef NET_DISK_CACHE_EVICTION_H_
#define NET_DISK_CACHE_EVICTION_H_

#include "base/basictypes.h"
#include "base/compiler_specific.h"
#include "base/task.h"
#include "net/disk_cache/disk_format.h"
#include "net/disk_cache/rankings.h"

namespace disk_cache {

class BackendImpl;
class EntryImpl;

// This class implements the eviction algorithm for the cache and it is tightly
// integrated with BackendImpl.
class Eviction {
 public:
  Eviction() : backend_(NULL), ALLOW_THIS_IN_INITIALIZER_LIST(factory_(this)) {}
  ~Eviction() {}

  void Init(BackendImpl* backend);

  // Deletes entries from the cache until the current size is below the limit.
  // If empty is true, the whole cache will be trimmed, regardless of being in
  // use.
  void TrimCache(bool empty);

  // Updates the ranking information for an entry.
  void UpdateRank(EntryImpl* entry, bool modified);

  // Notifications of interesting events for a given entry.
  void OnOpenEntry(EntryImpl* entry);
  void OnCreateEntry(EntryImpl* entry);
  void OnDoomEntry(EntryImpl* entry);
  void OnDestroyEntry(EntryImpl* entry);

 private:
  void ReportTrimTimes(EntryImpl* entry);
  Rankings::List GetListForEntry(EntryImpl* entry);

  BackendImpl* backend_;
  Rankings* rankings_;
  IndexHeader* header_;
  int max_size_;
  ScopedRunnableMethodFactory<Eviction> factory_;

  DISALLOW_COPY_AND_ASSIGN(Eviction);
};

}  // namespace disk_cache

#endif  // NET_DISK_CACHE_EVICTION_H_
