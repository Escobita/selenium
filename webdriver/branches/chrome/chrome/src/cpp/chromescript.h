#include <string.h>
#ifndef CHROME_SCRIPT_H__
#define CHROME_SCRIPT_H__

static const int FUNCTION_TYPE_RETURN   = 1;
static const int FUNCTION_TYPE_NORETURN = 2;

static const std::wstring ANON_RETURN(
    L"new function(){this.rv_=(%ls);}().rv_");

static const std::wstring ANON_NORETURN(
    L"new function(){this.rv_=true; try{ %ls }catch(e){this.rv_=false;}}().rv_ && true");

static const std::wstring FINDER_BY_ID(
    L"new function(id,eid){this.e_=''; var e=document.getElementById(id); if(e){this.e_=''+id+','+e.nodeName;}}('%ls','e_%d').e_");

static const std::wstring GET_ELEMENT_BY_ID(
    L"new function(id){this.o_ = document.getElementById(id);}('%ls').o_");

#endif  // CHROME_SCRIPT_H__
