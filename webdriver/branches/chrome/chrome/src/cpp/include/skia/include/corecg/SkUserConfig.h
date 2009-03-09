/* include/corecg/SkUserConfig.h
**
** Copyright 2006, Google Inc.
**
** Licensed under the Apache License, Version 2.0 (the "License"); 
** you may not use this file except in compliance with the License. 
** You may obtain a copy of the License at 
**
**     http://www.apache.org/licenses/LICENSE-2.0 
**
** Unless required by applicable law or agreed to in writing, software 
** distributed under the License is distributed on an "AS IS" BASIS, 
** WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
** See the License for the specific language governing permissions and 
** limitations under the License.
*/

#ifndef SkUserConfig_DEFINED
#define SkUserConfig_DEFINED

// remove the x if you want to force us into SK_DEBUG mode
#ifdef SK_RELEASEx
    #undef SK_RELEASE
    #define SK_DEBUG
#endif

#ifdef ANDROID
    #include <utils/misc.h>

#if 0
    // force fixed
    #define SK_SCALAR_IS_FIXED
    #undef  SK_SCALAR_IS_FLOAT
#else
    // force floats
    #ifdef SK_SCALAR_IS_FIXED
        #undef  SK_SCALAR_IS_FIXED
    #endif
    #define SK_SCALAR_IS_FLOAT
#endif

    #define SK_CAN_USE_FLOAT
    #define SkLONGLONG int64_t

    #if __BYTE_ORDER == __BIG_ENDIAN
        #define SK_CPU_BENDIAN
        #undef  SK_CPU_LENDIAN
    #else
        #define SK_CPU_LENDIAN
        #undef  SK_CPU_BENDIAN
    #endif

    // define SkDebugf to record file/line
    #define SkDebugf(...) Android_SkDebugf(__FILE__, __LINE__, \
                                           __FUNCTION__, __VA_ARGS__)
    void Android_SkDebugf(const char* file, int line, 
                          const char* function, const char* format, ...);
#endif

/*  This file is included before all other headers, except for SkPreConfig.h.
    That file uses various heuristics to make a "best guess" at settings for
    the following build defines.

    However, in this file you can override any of those decisions by either
    defining new symbols, or #undef symbols that were already set.
*/

// experimental for now
#define SK_SUPPORT_MIPMAP

#ifdef SK_DEBUG
    #define SK_SUPPORT_UNITTEST
    /* Define SK_SIMULATE_FAILED_MALLOC to have
     * sk_malloc throw an exception. Use this to
     * detect unhandled memory leaks. */
    //#define SK_SIMULATE_FAILED_MALLOC
    //#define SK_FIND_MEMORY_LEAKS
#endif

#ifdef SK_BUILD_FOR_BREW
    #include "SkBrewUserConfig.h"
#endif

// ===== Begin Chrome-specific definitions =====

#define SK_SCALAR_IS_FLOAT
#undef SK_SCALAR_IS_FIXED

// Log the file and line number for assertions.
#define SkDebugf(...) SkDebugf_FileLine(__FILE__, __LINE__, false, __VA_ARGS__)
void SkDebugf_FileLine(const char* file, int line, bool fatal,
                       const char* format, ...);

// Marking the debug print as "fatal" will cause a debug break, so we don't need
// a separate crash call here.
#define SK_DEBUGBREAK(cond) do { if (!(cond)) { \
    SkDebugf_FileLine(__FILE__, __LINE__, true, \
    "%s:%d: failed assertion \"%s\"\n", \
    __FILE__, __LINE__, #cond); } } while (false)

#if defined(SK_BUILD_FOR_WIN32)

#define SK_BUILD_FOR_WIN

// VC8 doesn't support stdint.h, so we define those types here.
#define SK_IGNORE_STDINT_DOT_H
typedef signed char int8_t;
typedef unsigned char uint8_t;
typedef short int16_t;
typedef unsigned short uint16_t;
typedef int int32_t;
typedef unsigned uint32_t;
#define SK_A32_SHIFT    24
#define SK_R32_SHIFT    16
#define SK_G32_SHIFT    8
#define SK_B32_SHIFT    0

// VC doesn't support __restrict__, so make it a NOP.
#undef SK_RESTRICT
#define SK_RESTRICT

// Skia uses this deprecated bzero function to fill zeros into a string.
#define bzero(str, len) memset(str, 0, len)

#elif defined(SK_BUILD_FOR_MAC)

#define SK_CPU_LENDIAN
#undef  SK_CPU_BENDIAN
// we want (memory order) RGBA
#define SK_A32_SHIFT    24
#define SK_R32_SHIFT    0
#define SK_G32_SHIFT    8
#define SK_B32_SHIFT    16

#elif defined(SK_BUILD_FOR_UNIX)

#ifdef SK_CPU_BENDIAN
// Below we set the order for ARGB channels in registers. I suspect that, on
// big endian machines, you can keep this the same and everything will work.
// The in-memory order will be different, of course, but as long as everything
// is reading memory as words rather than bytes, it will all work. However, if
// you find that colours are messed up I thought that I would leave a helpful
// locator for you. Also see the comments in
// base/gfx/bitmap_platform_device_linux.h
#error Read the comment at this location
#endif

// For Linux we want to match the most common X visual, which is
// ARGB (in registers)
#define SK_A32_SHIFT 24
#define SK_R32_SHIFT 16
#define SK_G32_SHIFT 8
#define SK_B32_SHIFT 0

#endif

// The default crash macro writes to badbeef which can cause some strange
// problems. Instead, pipe this through to the logging function as a fatal
// assertion.
#define SK_CRASH() SkDebugf_FileLine(__FILE__, __LINE__, true, "SK_CRASH")

// TODO(brettw) bug 6373: Re-enable Skia assertions. This is blocked on fixing
// some of our transparency handling which generates purposely-invalid colors,
// in turn causing assertions.
//#ifndef NDEBUG
//  #define SK_DEBUG
//  #undef SK_RELEASE
  #undef SK_SUPPORT_UNITTEST  // This is only necessary in debug mode since
                              // we've disabled assertions. When we re-enable
                              // them, this line can be removed.
//#else
  #define SK_RELEASE
  #undef SK_DEBUG
//#endif

// ===== End Chrome-specific definitions =====

#endif
