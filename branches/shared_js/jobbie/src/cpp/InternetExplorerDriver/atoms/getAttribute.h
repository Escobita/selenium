/* AUTO GENERATED - Do not edit by hand. */
/* See rake-tasts/js_to_header.rb instead. */

#ifndef GETATTRIBUTE_H
#define GETATTRIBUTE_H

wchar_t* GETATTRIBUTE[] = {
L"var e=this;",
L"function f(a){var b=typeof a;if(b==\"object\")if(a){if(a instanceof Array||!(a instanceof Object)&&Object.prototype.toString.call(a)==\"[object Array]\"||typeof a.length==\"number\"&&typeof a.splice!=\"undefined\"&&typeof a.propertyIsEnumerable!=\"undefined\"&&!a.propertyIsEnumerable(\"splice\"))return\"array\";if(!(a instanceof Object)&&(Object.prototype.toString.call(a)==\"[object Function]\"||typeof a.call!=\"undefined\"&&typeof a.propertyIsEnumerable!=\"undefined\"&&!a.propertyIsEnumerable(\"call\")))return\"function\"}else return\"null\";else if(b==",
L"\"function\"&&typeof a.call==\"undefined\")return\"object\";return b};var g=Array.prototype,j=g.indexOf?function(a,b,c){return g.indexOf.call(a,b,c)}:function(a,b,c){for(c=c==null?0:c<0?Math.max(0,a.length+c):c;c<a.length;c++)if(c in a&&a[c]===b)return c;return-1};function k(a,b){var c=0;a=String(a).replace(/^[\\s\\xa0]+|[\\s\\xa0]+$/g,\"\").split(\".\");b=String(b).replace(/^[\\s\\xa0]+|[\\s\\xa0]+$/g,\"\").split(\".\");for(var d=Math.max(a.length,b.length),m=0;c==0&&m<d;m++){var E=a[m]||\"\",F=b[m]||\"\",G=new RegExp(\"(\\\\d*)(\\\\D*)\",\"g\"),H=new RegExp(\"(\\\\d*)(\\\\D*)\",\"g\");do{var h=G.exec(E)||[\"\",\"\",\"\"],i=H.exec(F)||[\"\",\"\",\"\"];if(h[0].length==0&&i[0].length==0)break;c=l(h[1].length==0?0:parseInt(h[1],10),i[1].length==0?0:parseInt(i[1],10))||l(h[2].length==0,i[2].length==0)||l(h[2],",
L"i[2])}while(c==0)}return c}function l(a,b){if(a<b)return-1;else if(a>b)return 1;return 0}(Date.now||function(){return+new Date})();var n,o,p,q;function r(){return e.navigator?e.navigator.userAgent:null}q=p=o=n=false;var s;if(s=r()){var t=e.navigator;n=s.indexOf(\"Opera\")==0;o=!n&&s.indexOf(\"MSIE\")!=-1;p=!n&&s.indexOf(\"WebKit\")!=-1;q=!n&&!p&&t.product==\"Gecko\"}var u=o,v=q,w=p,x,y=\"\",z;if(n&&e.opera){var A=e.opera.version;y=typeof A==\"function\"?A():A}else{if(v)z=/rv\\:([^\\);]+)(\\)|;)/;else if(u)z=/MSIE\\s+([^\\);]+)(\\)|;)/;else if(w)z=/WebKit\\/(\\S+)/;if(z){var B=z.exec(r());y=B?B[1]:\"\"}}x=y;var C={};w&&(C[\"522\"]||(C[\"522\"]=k(x,\"522\")>=0));var D=[\"checked\",\"disabled\",\"readOnly\",\"selected\"];var I=function(a,b){if(!a)throw Error(\"Element has not been specified\");if(!b)throw Error(\"Attribute name must be set\");var c=b.toLowerCase();if(\"style\"==c)return\"\";if(\"class\"==c)b=\"className\";if(\"readonly\"==c)b=\"readOnly\";var d;a:{c=b;if(f(a.hasAttribute)==\"function\")if(a.hasAttribute(c)){d=true;break a}for(d in a)if(c==d){d=true;break a}d=false}if(!d)return null;a=a[b]===undefined?a.getAttribute(b):a[b];if(j(D,b)>=0)a=a!=\"\"&&a!=\"false\"&&a!=false;return a},J=\"getAttribute\".split(\".\"),K=e;",
L"!(J[0]in K)&&K.execScript&&K.execScript(\"var \"+J[0]);for(var L;J.length&&(L=J.shift());)if(!J.length&&I!==undefined)K[L]=I;else K=K[L]?K[L]:(K[L]={});",
NULL
};

#endif
