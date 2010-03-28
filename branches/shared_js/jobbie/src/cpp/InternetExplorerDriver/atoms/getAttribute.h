/* AUTO GENERATED - Do not edit by hand. */
/* See rake-tasts/js_to_header.rb instead. */

#ifndef GETATTRIBUTE_H
#define GETATTRIBUTE_H

wchar_t* GETATTRIBUTE[] = {
L"var d=this;",
L"function e(a){var b=typeof a;if(b==\"object\")if(a){if(a instanceof Array||!(a instanceof Object)&&Object.prototype.toString.call(a)==\"[object Array]\"||typeof a.length==\"number\"&&typeof a.splice!=\"undefined\"&&typeof a.propertyIsEnumerable!=\"undefined\"&&!a.propertyIsEnumerable(\"splice\"))return\"array\";if(!(a instanceof Object)&&(Object.prototype.toString.call(a)==\"[object Function]\"||typeof a.call!=\"undefined\"&&typeof a.propertyIsEnumerable!=\"undefined\"&&!a.propertyIsEnumerable(\"call\")))return\"function\"}else return\"null\";else if(b==",
L"\"function\"&&typeof a.call==\"undefined\")return\"object\";return b};var f=Array.prototype,i=f.indexOf?function(a,b,c){return f.indexOf.call(a,b,c)}:function(a,b,c){for(c=c==null?0:c<0?Math.max(0,a.length+c):c;c<a.length;c++)if(c in a&&a[c]===b)return c;return-1};function j(a,b){var c=0;a=String(a).replace(/^[\\s\\xa0]+|[\\s\\xa0]+$/g,\"\").split(\".\");b=String(b).replace(/^[\\s\\xa0]+|[\\s\\xa0]+$/g,\"\").split(\".\");for(var D=Math.max(a.length,b.length),l=0;c==0&&l<D;l++){var E=a[l]||\"\",F=b[l]||\"\",G=new RegExp(\"(\\\\d*)(\\\\D*)\",\"g\"),H=new RegExp(\"(\\\\d*)(\\\\D*)\",\"g\");do{var g=G.exec(E)||[\"\",\"\",\"\"],h=H.exec(F)||[\"\",\"\",\"\"];if(g[0].length==0&&h[0].length==0)break;c=k(g[1].length==0?0:parseInt(g[1],10),h[1].length==0?0:parseInt(h[1],10))||k(g[2].length==0,h[2].length==0)||k(g[2],",
L"h[2])}while(c==0)}return c}function k(a,b){if(a<b)return-1;else if(a>b)return 1;return 0}(Date.now||function(){return+new Date})();var m,n,o,p;function q(){return d.navigator?d.navigator.userAgent:null}p=o=n=m=false;var r;if(r=q()){var s=d.navigator;m=r.indexOf(\"Opera\")==0;n=!m&&r.indexOf(\"MSIE\")!=-1;o=!m&&r.indexOf(\"WebKit\")!=-1;p=!m&&!o&&s.product==\"Gecko\"}var t=n,u=p,v=o,w,x=\"\",y;if(m&&d.opera){var z=d.opera.version;x=typeof z==\"function\"?z():z}else{if(u)y=/rv\\:([^\\);]+)(\\)|;)/;else if(t)y=/MSIE\\s+([^\\);]+)(\\)|;)/;else if(v)y=/WebKit\\/(\\S+)/;if(y){var A=y.exec(q());x=A?A[1]:\"\"}}w=x;var B={};v&&(B[\"522\"]||(B[\"522\"]=j(w,\"522\")>=0));function C(a,b){if(e(a.hasAttribute)==\"function\")if(a.hasAttribute(b))return true;if(a[b]!==undefined)return true;for(var c in a)if(b==c)return true;return false}var I=[\"checked\",\"disabled\",\"readOnly\",\"selected\"];var J=function(a,b){if(!a)throw Error(\"Element has not been specified\");if(!b)throw Error(\"Attribute name must be set\");var c=b.toLowerCase();if(i(I,b)>=0){a=a[b];return!!(a&&a!=\"false\")}if(\"style\"==c){a=a.style;return a.cssText?a.cssText:\"\"}if(\"class\"==c)b=\"className\";if(\"readonly\"==c)b=\"readOnly\";if(!C(a,b))return null;return a[b]===undefined?a.getAttribute(b):a[b]},K=\"getAttribute\".split(\".\"),L=d;!(K[0]in L)&&L.execScript&&L.execScript(\"var \"+K[0]);",
L"for(var M;K.length&&(M=K.shift());)if(!K.length&&J!==undefined)L[M]=J;else L=L[M]?L[M]:(L[M]={});",
NULL
};

#endif
