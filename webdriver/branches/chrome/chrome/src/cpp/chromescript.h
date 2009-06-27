#include <string.h>

#ifndef CHROME_SCRIPT_H__
#define CHROME_SCRIPT_H__

static const int FUNCTION_TYPE_RETURN   = 1;
static const int FUNCTION_TYPE_NORETURN = 2;

// -----------------------------------------------------------------------------
//                          Function related
// -----------------------------------------------------------------------------
static const std::wstring ANON_RETURN(
    L"new function(){\
    this.rv_=(%ls);}().rv_");


static const std::wstring ANON_NORETURN(
    L"new function(){\
    this.rv_=true; try{ %ls }catch(e){this.rv_=false;}}().rv_ && true");


static const std::wstring GET_ELEMENT_BY_ID(
    L"new function(id){\
      this.o_ = document.getElementById(id);\
    }(\"%ls\").o_");


static const std::wstring GET_ELEMENT_BY_NONID(
    L"new function(tn,ceid){\
      this.o_ = '';\
      var es = document.getElementsByTagName(tn);\
      for (var i=0; i<es.length; i++) {\
        if (es[i].hasAttribute('ceid') && es[i].getAttribute('ceid') == ceid) {\
          this.o_ = es[i];\
	        break;\
        }\
      }\
    }(\"%ls\",\"%ls\").o_");


static const std::wstring SIMULATE_CLICK(
	  L"new function(o){\
      if (document.createEvent){\
        var eO = document.createEvent('MouseEvent');\
        eO.initEvent('click', true, true); o.dispatchEvent(eO);\
      } else if (document.createEventObject){\
        o.fireEvent('onclick');\
      } else { o.click(); }\
    }(%ls)");


static const std::wstring SIMULATE_SUBMIT(
    L"new function(o){\
      while (o){\
        if (o.nodeName == 'FORM' || o.nodeName == 'form'){ o.submit(); break; }\
        o = o.parentNode;\
      }\
    }(%ls)");


// TODO(AmitabhSaikia):
//  1. document.location or document.URL is not reliable attribute of document
//      hence must query actual frame/iframe src.
static const std::wstring GET_CURRENT_URL(
    L"new function(){this.rv_=''+document.URL;}().rv_");


// -----------------------------------------------------------------------------
//                            Element related
// -----------------------------------------------------------------------------

// FINDER_BY_ID(id, eid, pfx)
//  @params id  - Id to be searched.
//  @params eid - Element id current counter.
//  @params pfx - Prefix to be used before counter.
//
//  @returns [last_eid,N,tagname,id1,id2,...,eidN]
//    Where,
//      last_eid - Element id after the operations.
//      N        - Number of elements returned.
//      tagname  - Tagname of the element.
//      id1...N  - Element ids assigned to the found elements.
static const std::wstring FINDER_BY_ID(
    L"new function (id,eid,pfx){\
      this.e_ = [''+eid,'0',''];\
      var e = document.getElementById(id);\
      if (e){ this.e_[1]=''+1; this.e_[2]=''+e.nodeName; this.e_.push(''+e.id); }\
      this.e_ = this.e_.join(',');\
    }('%ls',%d,'%d').e_");


// FINDER_BY_TAGNAME(tn, sz, eid, pfx)
//  @params tn  - Tagname to be searched.
//  @params sz  - True for all, False for 1.
//  @params eid - Element id current counter.
//  @params pfx - Prefix to be used before counter.
//
//  @returns [last_eid,N,tagname,eid1,eid2,...,eidN]
//    Where,
//      last_eid - Element id after the operations.
//      N        - Number of elements returned.
//      tagname  - Tagname of the element.
//      id1...N  - Element ids assigned to the found elements.
static const std::wstring FINDER_BY_TAGNAME(
    L"new function (tn,sz,eid,pfx){\
      this.e_ = [''+eid,'0',''+tn];\
      var es = document.getElementsByTagName(tn);\
      var em = (sz ? es.length : 1);\
      var cntr = 0;\
      for (var i=0; i<es.length; i++) {\
        cntr++;\
        if (es[i].hasAttribute('ceid')) {\
          this.e_.push(''+es[i].getAttribute('ceid'));\
          if (cntr == em) break;\
        } else {\
          var sid = ''+pfx+'_'+(eid++);\
          es[i].setAttribute('ceid',sid);\
          this.e_.push(sid);\
          if (cntr == em) break;\
        }\
      }\
      this.e_[0] = ''+eid;\
      this.e_[1] = ''+cntr;\
      this.e_ = this.e_.join(',');\
    }('%ls',%d,%d,'%d').e_");


// TODO(AmitabhSaikia):
//  1. Remove the potential race condition due to document.evaluate.
//  2. Improve to match classname like <input class='clzA ... clzZ'>
//
// FINDER_BY_CLASSNAME(cls, sz, eid, pfx)
//  @params cls - Class name to be searched.
//  @params sz  - True for all, False for 1.
//  @params eid - Element id current counter.
//  @params pfx - Prefix to be used before counter.
//
//  @returns [last_eid,N,tagname1,eid1,tagname2,eid2,...,tagnameN,eidN]
//    Where,
//      last_eid - Element id after the operations.
//      N        - Number of elements returned.
//      tagname  - Tagname of the element.
//      id1...N  - Element ids assigned to the found elements.
static const std::wstring FINDER_BY_CLASSNAME(
    L"new function(cls,sz,eid,pfx){\
      this.e_ = [''+eid,'0'];\
      var xpath = '//*[@class=\\''+cls+'\\']';\
      var es = document.evaluate(xpath, document, null, XPathResult.ORDERED_NODE_SNAPSHOT_TYPE, null);\
      var em = (sz ? es.snapshotLength : 1);\
      var cntr = 0;\
      for(var i=0; i<em; i++) {\
        cntr++;\
        var e = es.snapshotItem(i);\
        this.e_.push(''+e.nodeName);\
        if (e.hasAttribute('ceid')) {\
          this.e_.push(''+e.getAttribute('ceid'));\
          if (cntr == em) break;\
        } else {\
          var sid = ''+pfx+'_'+(eid++);\
          e.setAttribute('ceid',sid);\
          this.e_.push(sid);\
          if (cntr == em) break;\
        }\
      }\
      this.e_[0] = ''+eid;\
      this.e_[1] = ''+cntr;\
      this.e_ = this.e_.join(',');\
    }('%ls',%d,%d,'%d').e_");


// TODO(AmitabhSaikia):
//  1. Remove the potential race condition due to document.evaluate.
//
// FINDER_BY_NAME(nam, sz, eid, pfx)
//  @params nam - Name to be searched.
//  @params sz  - True for all, False for 1.
//  @params eid - Element id current counter.
//  @params pfx - Prefix to be used before counter.
//
//  @returns [last_eid,N,tagname1,eid1,tagname2,eid2,...,tagnameN,eidN]
//    Where,
//      last_eid - Element id after the operations.
//      N        - Number of elements returned.
//      tagname  - Tagname of the element.
//      id1...N  - Element ids assigned to the found elements.
static const std::wstring FINDER_BY_NAME(
    L"new function(nam,sz,eid,pfx){\
      this.e_ = [''+eid,'0'];\
      var xpath = '//*[@name=\\''+nam+'\\']';\
      var es = document.evaluate(xpath, document, null, XPathResult.ORDERED_NODE_SNAPSHOT_TYPE, null);\
      var em = (sz ? es.snapshotLength : 1);\
      var cntr = 0;\
      for(var i=0; i<em; i++) {\
        cntr++;\
        var e = es.snapshotItem(i);\
        this.e_.push(''+e.nodeName);\
        if (e.hasAttribute('ceid')) {\
          this.e_.push(''+e.getAttribute('ceid'));\
          if (cntr == em) break;\
        } else {\
          var sid = ''+pfx+'_'+(eid++);\
          e.setAttribute('ceid',sid);\
          this.e_.push(sid);\
          if (cntr == em) break;\
        }\
      }\
      this.e_[0] = ''+eid;\
      this.e_[1] = ''+cntr;\
      this.e_ = this.e_.join(',');\
    }('%ls',%d,%d,'%d').e_");


// TODO(AmitabhSaikia):
//  1. Remove the potential race condition due to document.evaluate.
//
// FINDER_BY_LINKTEXT(txt, sz, eid, pfx)
//  @params txt - Text to be searched.
//  @params sz  - True for all, False for 1.
//  @params eid - Element id current counter.
//  @params pfx - Prefix to be used before counter.
//
//  @returns [last_eid,N,tagname1,eid1,tagname2,eid2,...,tagnameN,eidN]
//    Where,
//      last_eid - Element id after the operations.
//      N        - Number of elements returned.
//      tagname  - Tagname of the element.
//      id1...N  - Element ids assigned to the found elements.
static const std::wstring FINDER_BY_LINKTEXT(
    L"new function(txt,sz,eid,pfx) {\
      this.e_ = [''+eid,'0'];\
      var xpath = '//A|//a';\
      var es = document.evaluate(xpath, document, null, XPathResult.ORDERED_NODE_SNAPSHOT_TYPE, null);\
      var em = (sz ? es.snapshotLength : 1);\
      var cntr = 0;\
      for(var i=0; i<es.snapshotLength; i++) {\
        cntr++;\
        var e = es.snapshotItem(i);\
        if (txt == e.innerHTML) {\
          this.e_.push(''+e.nodeName);\
          if (e.hasAttribute('ceid')) {\
            this.e_.push(''+e.getAttribute('ceid'));\
          } else {\
            var sid = ''+pfx+'_'+(eid++);\
            e.setAttribute('ceid',sid);\
            this.e_.push(sid);\
          }\
          if (cntr == em) break;\
        }\
      }\
      this.e_[0] = ''+eid;\
      this.e_[1] = ''+cntr;\
      this.e_ = this.e_.join(',');\
    }('%ls',%d,%d,'%d').e_");


// TODO(AmitabhSaikia):
//  1. Remove the potential race condition due to document.evaluate.
//
// FINDER_BY_PARTIALLINKTEXT(ptxt, sz, eid, pfx)
//  @params ptxt - Partial Text to be searched.
//  @params sz   - True for all, False for 1.
//  @params eid  - Element id current counter.
//  @params pfx  - Prefix to be used before counter.
//
//  @returns [last_eid,N,tagname1,eid1,tagname2,eid2,...,tagnameN,eidN]
//    Where,
//      last_eid - Element id after the operations.
//      N        - Number of elements returned.
//      tagname  - Tagname of the element.
//      id1...N  - Element ids assigned to the found elements.
static const std::wstring FINDER_BY_PARTIALLINKTEXT(
    L"new function(ptxt,sz,eid,pfx) {\
      this.e_ = [''+eid,'0'];\
      var xpath = '//A|//a';\
      var es = document.evaluate(xpath, document, null, XPathResult.ORDERED_NODE_SNAPSHOT_TYPE, null);\
      var em = (sz ? es.snapshotLength : 1);\
      var cntr = 0;\
      for(var i=0; i<em; i++) {\
        cntr++;\
        var e = es.snapshotItem(i);\
        var txt = '' + e.innerHTML;\
        if (txt.search(ptxt) > 0) {\
          this.e_.push(''+e.nodeName);\
          if (e.hasAttribute('ceid')) {\
            this.e_.push(''+e.getAttribute('ceid'));\
          } else {\
            var sid = ''+pfx+'_'+(eid++);\
            e.setAttribute('ceid',sid);\
            this.e_.push(sid);\
          }\
          if (cntr == em) break;\
        }\
      }\
      this.e_[0] = ''+eid;\
      this.e_[1] = ''+cntr;\
      this.e_ = this.e_.join(',');\
    }('%ls',%d,%d,'%d').e_");


// TODO(AmitabhSaikia):
//  1. Remove the potential race condition due to document.evaluate.
//
// FINDER_BY_XPATH(xpath, sz, eid, pfx)
//  @params xpath - Text to be searched.
//  @params sz  - True for all, False for 1.
//  @params eid - Element id current counter.
//  @params pfx - Prefix to be used before counter.
//
//  @returns [last_eid,N,tagname1,eid1,tagname2,eid2,...,tagnameN,eidN]
//    Where,
//      last_eid - Element id after the operations.
//      N        - Number of elements returned.
//      tagname  - Tagname of the element.
//      id1...N  - Element ids assigned to the found elements.
static const std::wstring FINDER_BY_XPATH(
    L"new function(xpath,sz,eid,pfx) {\
      this.e_ = [''+eid,'0'];\
      var es = document.evaluate(xpath, document, null, XPathResult.ORDERED_NODE_SNAPSHOT_TYPE, null);\
      var em = (sz ? es.snapshotLength : 1);\
      var cntr = 0;\
      for(var i=0; i<es.snapshotLength; i++) {\
        cntr++;\
        var e = es.snapshotItem(i);\
        if (e.hasAttribute('ceid')) {\
          this.e_.push(''+e.nodeName);\
          this.e_.push(''+e.getAttribute('ceid'));\
          if (cntr == em) break;\
        } else {\
          this.e_.push(''+e.nodeName);\
          var sid = ''+pfx+'_'+(eid++);\
          e.setAttribute('ceid',sid);\
          this.e_.push(sid);\
          if (cntr == em) break;\
        }\
      }\
      this.e_[0] = ''+eid;\
      this.e_[1] = ''+cntr;\
      this.e_ = this.e_.join(',');\
    }(\"%ls\",%d,%d,'%d').e_");



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
