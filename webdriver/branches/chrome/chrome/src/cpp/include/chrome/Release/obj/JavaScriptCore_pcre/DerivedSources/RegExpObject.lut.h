// Automatically generated from /cygdrive/c/Users/NOEL~1.GOO/SRC-CH~2/src/webkit/../third_party/WebKit/JavaScriptCore/runtime/RegExpObject.cpp using /cygdrive/c/Users/NOEL~1.GOO/SRC-CH~2/src/webkit/../third_party/WebKit/JavaScriptCore/create_hash_table. DO NOT EDIT!

#include "Lookup.h"

namespace JSC {

static const struct HashTableValue regExpTableValues[6] = {
   { "global", DontDelete|ReadOnly|DontEnum, (intptr_t)regExpObjectGlobal, (intptr_t)0 },
   { "ignoreCase", DontDelete|ReadOnly|DontEnum, (intptr_t)regExpObjectIgnoreCase, (intptr_t)0 },
   { "multiline", DontDelete|ReadOnly|DontEnum, (intptr_t)regExpObjectMultiline, (intptr_t)0 },
   { "source", DontDelete|ReadOnly|DontEnum, (intptr_t)regExpObjectSource, (intptr_t)0 },
   { "lastIndex", DontDelete|DontEnum, (intptr_t)regExpObjectLastIndex, (intptr_t)setRegExpObjectLastIndex },
   { 0, 0, 0, 0 }
};

extern const struct HashTable regExpTable =
#if ENABLE(PERFECT_HASH_SIZE)
    { 31, regExpTableValues, 0 };
#else
    { 17, 15, regExpTableValues, 0 };
#endif

} // namespace
