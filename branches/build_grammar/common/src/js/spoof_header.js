
// Override some pieces that might reasonably be expected to exist, but don't 
// when in an extension

var window = window || {};

var navigator = navigator || { userAgent: "Mozilla/5.0 (Windows; U; Windows NT 6.0; en-US; rv:1.9.1.7) Gecko/20091221 Firefox/3.5.7 (.NET CLR 3.5.30729)" };

var top = top || {};
