/* AUTO GENERATED - Do not edit by hand. */
/* See rake-tasts/js_to_header.rb instead. */

#ifndef GETSIZE_H
#define GETSIZE_H

wchar_t* GETSIZE[] = {
L"var d=this,e=Date.now||function(){return+new Date};function g(a,c){this.width=a;this.height=c}g.prototype.toString=function(){return\"(\"+this.width+\" x \"+this.height+\")\"};function j(a,c){var b=0;a=String(a).replace(/^[\\s\\xa0]+|[\\s\\xa0]+$/g,\"\").split(\".\");c=String(c).replace(/^[\\s\\xa0]+|[\\s\\xa0]+$/g,\"\").split(\".\");for(var q=Math.max(a.length,c.length),f=0;b==0&&f<q;f++){var r=a[f]||\"\",F=c[f]||\"\",G=new RegExp(\"(\\\\d*)(\\\\D*)\",\"g\"),H=new RegExp(\"(\\\\d*)(\\\\D*)\",\"g\");do{var h=G.exec(r)||[\"\",\"\",\"\"],i=H.exec(F)||[\"\",\"\",\"\"];if(h[0].length==0&&i[0].length==0)break;b=k(h[1].length==0?0:parseInt(h[1],10),i[1].length==0?0:parseInt(i[1],10))||k(h[2].length==0,i[2].length==0)||k(h[2],",
L"i[2])}while(b==0)}return b}function k(a,c){if(a<c)return-1;else if(a>c)return 1;return 0}e();var l,m,n,o;function p(){return d.navigator?d.navigator.userAgent:null}o=n=m=l=false;var s;if(s=p()){var t=d.navigator;l=s.indexOf(\"Opera\")==0;m=!l&&s.indexOf(\"MSIE\")!=-1;n=!l&&s.indexOf(\"WebKit\")!=-1;o=!l&&!n&&t.product==\"Gecko\"}var u=l,v=m,w=o,x=n,y,z=\"\",A;if(u&&d.opera){var B=d.opera.version;z=typeof B==\"function\"?B():B}else{if(w)A=/rv\\:([^\\);]+)(\\)|;)/;else if(v)A=/MSIE\\s+([^\\);]+)(\\)|;)/;else if(x)A=/WebKit\\/(\\S+)/;if(A){var C=A.exec(p());z=C?C[1]:\"\"}}y=z;var D={};x&&(D[\"522\"]||(D[\"522\"]=j(y,\"522\")>=0));var E=function(a){var c=u&&!(D[\"10\"]||(D[\"10\"]=j(y,\"10\")>=0)),b;a:{b=a.nodeType==9?a:a.ownerDocument||a.document;if(b.defaultView&&b.defaultView.getComputedStyle)if(b=b.defaultView.getComputedStyle(a,\"\")){b=b.display;break a}b=null}if((b||(a.currentStyle?a.currentStyle.display:null)||a.style.display)!=\"none\")return c?new g(a.offsetWidth||a.clientWidth,a.offsetHeight||a.clientHeight):new g(a.offsetWidth,a.offsetHeight);b=a.style;var q=b.display,f=b.visibility,r=b.position;b.visibility=\"hidden\";b.position=",
L"\"absolute\";b.display=\"inline\";if(c){c=a.offsetWidth||a.clientWidth;a=a.offsetHeight||a.clientHeight}else{c=a.offsetWidth;a=a.offsetHeight}b.display=q;b.position=r;b.visibility=f;return new g(c,a)},I=\"getSize\".split(\".\"),J=d;!(I[0]in J)&&J.execScript&&J.execScript(\"var \"+I[0]);for(var K;I.length&&(K=I.shift());)if(!I.length&&E!==undefined)J[K]=E;else J=J[K]?J[K]:(J[K]={});",
NULL
};

#endif
