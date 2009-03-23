#include <string.h>
#ifndef CHROME_SCRIPT_H__
#define CHROME_SCRIPT_H__

static const int FUNCTION_TYPE_RETURN   = 1;
static const int FUNCTION_TYPE_NORETURN = 2;

// -----------------------------------------------------------------------------
// Function related
// -----------------------------------------------------------------------------
static const std::wstring ANON_RETURN(
    L"new function(){this.rv_=(%ls);}().rv_");

static const std::wstring ANON_NORETURN(
    L"new function(){this.rv_=true; try{ %ls }catch(e){this.rv_=false;}\
    }().rv_ && true");

// Note: document.location or document.URL is not reliable attribute of document
//       hence must query actual frame/iframe src.
static const std::wstring GET_CURRENT_URL(
    L"new function(){this.rv_=''+document.URL;}().rv_");

// -----------------------------------------------------------------------------
// Element related
// -----------------------------------------------------------------------------
static const std::wstring FINDER_BY_ID(
    L"new function(id,eid){this.e_='';var e=document.getElementById(id);\
    if(e){this.e_=''+id+','+e.nodeName;}}('%ls','e_%d').e_");

static const std::wstring GET_ELEMENT_BY_ID(
    L"new function(id){this.o_ = document.getElementById(id);}('%ls').o_");


// -----------------------------------------------------------------------------
// Frames related
// -----------------------------------------------------------------------------
// THINGS TO REMEMBER: XPATH index starts with 1.
static const std::wstring FRAME_COUNT(
    L"new function(){this.rv_=0; try{\
    this.rv_=document.getElementsByTagName('frame').length + \
      document.getElementsByTagName('iframe').length;\
    }catch(e){this.rv_=0;}}().rv_");

// TODO(amitabh): find the depth to get exact frame path.
static const std::wstring FRAME_IDENTIFIER_BY_INDEX(
    L"new function(x){this.rv_='/home/frameset/frame'; try{\
    var l=document.getElementsByTagName('frame');\
    if (l.length==0) {l=document.getElementsByTagName('iframe'); this.rv_='/home/body/iframe';}\
    if(x<=l.length) {this.rv_+='['+x+'],'+l[x-1].src;} else { this.rv_ += ','+l[0].src; }\
    } catch(e){this.rv_='';}}(%d).rv_");

#endif  // CHROME_SCRIPT_H__
