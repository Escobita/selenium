// Copyright (c) 2006-2008 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

#ifndef np_v8object_h
#define np_v8object_h

#include "third_party/npapi/bindings/npruntime.h"
#include <v8.h>

namespace WebCore {
    class DOMWindow;
}

extern NPClass* NPScriptObjectClass;

// A V8NPObject is a NPObject which carries additional V8-specific
// information.  It is allocated and deallocated by AllocV8NPObject()
// and FreeV8NPObject() methods.
struct V8NPObject {
    NPObject object;
    v8::Persistent<v8::Object> v8Object;
    WebCore::DOMWindow* rootObject;
};

struct PrivateIdentifier {
    union {
        const NPUTF8* string;
        int32_t number;
    } value;
    bool isString;
};

NPObject* NPN_CreateScriptObject(NPP npp, v8::Handle<v8::Object>, WebCore::DOMWindow*);
NPObject* NPN_CreateNoScriptObject();

#endif // np_v8object_h

