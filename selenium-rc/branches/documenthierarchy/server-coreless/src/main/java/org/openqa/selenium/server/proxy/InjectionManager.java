package org.openqa.selenium.server.proxy;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.mortbay.http.HttpFields;
import org.mortbay.http.HttpRequest;
import org.mortbay.http.HttpResponse;
import org.mortbay.util.IO;
import org.openqa.selenium.server.browser.HtmlIdentifierManager;
import org.openqa.selenium.server.client.Session;
import org.openqa.selenium.server.client.SessionManager;
import org.openqa.selenium.server.configuration.SeleniumConfiguration;
import org.openqa.selenium.server.jetty.ClasspathResourceLocator;
import org.openqa.selenium.server.jetty.ModifiedIO;
import org.openqa.selenium.server.jetty.ProxyHandler;
import org.openqa.selenium.server.jetty.WebResponse;
import org.openqa.selenium.server.jetty5.Jetty5ProxyHandler;
import org.openqa.selenium.server.jetty5.Jetty5WebResponse;

/**
 * @todo possibly change to injection manager and have an injection manager per session rather than static
 * 
 * @author Matthew Purland
 */
public class InjectionManager {
	private static Logger logger = Logger.getLogger(InjectionManager.class);
	
	private static HashMap<Session, HashMap<String, String>> jsStateInitializersBySession = new HashMap<Session, HashMap<String,String>>();
	// @todo Need to inject SessionManager??
	private static HashMap<Session, String> sessionToUniqueId = new HashMap<Session, String>();
    
    private HashMap<String, String> contentTransformations = new HashMap<String, String>();
    private List<String> userJsInjectionFiles = new LinkedList<String>(); 
    
	private SeleniumConfiguration seleniumConfiguration;
	private SessionManager sessionManager;
	private HtmlIdentifierManager htmlIdentifierManager;
	private ModifiedIO modifiedIO;
	
	private static final String INJECTION_HTML_FILE = "/core/scripts/injection.html";

	private byte[] injectionHtml;

	public InjectionManager(SeleniumConfiguration seleniumConfiguration, SessionManager sessionManager, HtmlIdentifierManager htmlIdentifierManager, ModifiedIO modifiedIO) {
		this.seleniumConfiguration = seleniumConfiguration;
		this.sessionManager = sessionManager;
		this.htmlIdentifierManager = htmlIdentifierManager;
		this.modifiedIO = modifiedIO;
		
		// If we're running in proxy injection mode, initialize it here
		if (seleniumConfiguration.isProxyInjectionMode()) {
			init();
		}
	}
    
    /**
     * re-read selenium js.  Don't maintain it indefinitely for now since then we would need to
     * restart the server to see changes.  Once the selenium js is firm, this should change.
     * @throws IOException 
     *
     */
    public void init() {
        String key = "__SELENIUM_JS__";
        
        StringBuffer sb = new StringBuffer();
        contentTransformations.put(key, sb.toString());
    }
    
	private static void appendFileContent(StringBuffer sb, String url) throws IOException {
//        InputStream in = new ClassPathResource(url).getInputStream();
		InputStream in = new ClasspathResourceLocator().getResource(url);
        if (in==null) {
            if (!url.endsWith("user-extensions.js")) {
                throw new RuntimeException("couldn't find " + url);
            }
        }
        else {
            byte[] buf = new byte[8192];
            while (true) {
                int len = in.read(buf, 0, 8192);
                if (len==-1) {
                    break;
                }
                sb.append(new String(buf, 0, len));
            }
        }
    }
	
	public long injectJavaScript(String requestURL, String requestPath, WebResponse webResponse, InputStream inputStream, OutputStream outputStream) throws IOException {
	    if (!contentTransformations.containsKey("__SELENIUM_JS__")) {
	        init();   
        }
        
        int len = 102400;
        byte[] buf = new byte[len];
        len = readStream(inputStream, buf, len);
        if (len == -1) {
            return -1;
        }
        int lengthOfBOM = getBOMLength(buf); 
        String data = new String(buf, lengthOfBOM, len);

        boolean shouldBeInjected = htmlIdentifierManager.shouldBeInjected(requestPath, webResponse.getContentType(), data);
        String sessionId = sessionManager.getLastSessionId();
        
        if (sessionId == null) {
        	shouldBeInjected = false;
        }
        
        //String url = response.getHttpRequest().getRequestURL().toString();
        String url = requestURL;
        
//        if (seleniumConfiguration.getDebugURL().equals(url)) {
//            System.out.println("debug URL seen");
//        }
       
        if (!shouldBeInjected) {
            outputStream.write(buf, 0, len);
        }
//        else if (lengthOfBOM>0) {
//            out.write(buf, 0, lengthOfBOM);
//        }


        long bytesCopied = len;

        if (seleniumConfiguration.isDebugMode()) {
            logger.debug(url + " (InjectionHelper looking)");
        }
        if (!shouldBeInjected) {
            bytesCopied += modifiedIO.copy(inputStream, outputStream);
        }
        else {
	        webResponse.removeField(HttpFields.__ETag); // possible cksum?  Stop caching...
	        webResponse.removeField(HttpFields.__LastModified); // Stop caching...
        	
            if (seleniumConfiguration.isDebugMode()) {
                logger.debug("injecting...");
            }
            webResponse.removeField("Content-Length"); // added js will make it wrong, lead to page getting truncated
            
            contentTransformations.put("@SESSION_ID@", sessionId);
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            InputStream injectionHtmlInputStream = new ByteArrayInputStream(getInjectionHtml());
            
            writeDataWithUserTransformations("", injectionHtmlInputStream, baos);
            baos.write(setSomeJsVars(sessionId));
            for (String filename : userJsInjectionFiles) {
                InputStream userJsIn = new FileInputStream(filename);
                modifiedIO.copy(userJsIn, baos);
            }

            int headIndex = -1; //data.toLowerCase().indexOf("<head>");
            if (headIndex != -1) {
                data = data.substring(0, headIndex + 6) + baos.toString() + data.substring(headIndex + 6);
            } else {
                data = baos.toString() + data;
            }

            bytesCopied += writeDataWithUserTransformations(data, inputStream, outputStream);
        }

        return bytesCopied;
	}

    private static int getBOMLength(byte[] buf) {
        if ((buf!=null) && (buf.length>=3) && (buf[0]==(byte) -17) && (buf[1]==(byte) -69) && (buf[2]==(byte) -65)) {
// jeez, what was that, you may be asking?  This comparison is quite wacky.  When I look at the same data hexdumped 
//            from a file on disk, the bytes are EF BB BF,  so I think I could be comparing against 0xef, 0xbb, and 0xbf.
//            But that doesn't work.  Here are some interesting evaluations from the Display view in my eclipse:
//
//            buf[0]
//                 (byte) -17
//            buf[1]
//                 (byte) -69
//            buf[2]
//                 (byte) -65
//            buf[3]
//                 (byte) 10
//            (int)(new String(buf)).charAt(0)
//                 (int) 239
//            (int)(new String(buf)).charAt(1)
//                 (int) 187
//            (int)(new String(buf)).charAt(2)
//                 (int) 191
//            (new String(buf)).charAt(2)
//                 (char) ¿
//            (int)(new String(buf)).charAt(3)
//                 (int) 10
//
//            what I would really like would be to recognize any BOM (cf http://en.wikipedia.org/wiki/Byte_Order_Mark).   I could easily set up
//            the appropriate comparisons if I knew how to translate from the hex form to some appropriate analogue for a Java comparison.
            return 3;
        }
        return 0; // there was no BOM
    }

    /**
     * read bufLen bytes into buf (unless EOF is seen first) from in.
     * @param in
     * @param buf
     * @param bufLen
     * @return number of bytes read
     * @throws IOException
     */
    private static int readStream(InputStream in, byte[] buf, int bufLen) throws IOException {
        int offset = 0;
        do {
            int bytesRead = in.read(buf, offset, bufLen - offset);
            if (bytesRead==-1) {
                break;
            }
            offset += bytesRead;
        } while (offset < bufLen);
        int bytesReadTotal = offset;
        return bytesReadTotal;
    }

    private long writeDataWithUserTransformations(String data, InputStream in, OutputStream out) throws IOException {
        long bytesWritten = 0;
        byte[] buf = new byte[8192];
        while (true) {
            for (String beforeRegexp : contentTransformations.keySet()) {
                String after = contentTransformations.get(beforeRegexp);
                if (after==null) {
                    System.out.println("Warning: no transformation seen for key " + beforeRegexp);
                }
                else {
                    try {
                        data = data.replaceAll(beforeRegexp, after);
                    }
                    catch (IllegalArgumentException e) {
                        // bad regexp or bad back ref in the 'after'.  
                        // Do a straight substitution instead.
                        // (This logic needed for injection.html's __SELENIUM_JS__
                        // replacement to work.)
                        data = data.replace(beforeRegexp, after);       
                    }
                }
            }
            out.write(data.getBytes());
            int len = in.read(buf);
            if (len == -1) {
                break;
            } else {
                bytesWritten += len;
            }
            data = new String(buf, 0, len);
        }

        return bytesWritten;
    }

    private byte[] setSomeJsVars(String sessionId) {
        StringBuffer moreJs = new StringBuffer();
        if (seleniumConfiguration.isDebugMode()) {
            moreJs.append("debugMode = true;\n");
        }
        moreJs.append("injectedSessionId = ")
            .append(sessionId)
            .append(";\n");
        return makeJsChunk(moreJs.toString());
    }

    // This logic may be useful on some browsers which don't support load listeners.
//    private static String usurpOnUnloadHook(String data, String string) {
//        Pattern framesetAreaRegexp = Pattern.compile("(<\\s*frameset.*?>)", Pattern.CASE_INSENSITIVE);
//        Matcher framesetMatcher = framesetAreaRegexp.matcher(data);
//        if (!framesetMatcher.find()) {
//            System.out.println("WARNING: looked like a frameset, but couldn't retrieve the frameset area");
//            return data;
//        }
//        String onloadRoutine = "selenium_frameRunTest()";
//        String frameSetText = framesetMatcher.group(1);
//        Pattern onloadRegexp = Pattern.compile("onload='(.*?)'", Pattern.CASE_INSENSITIVE);
//        Matcher onloadMatcher = onloadRegexp.matcher(frameSetText);
//        if (!onloadMatcher.find()) {
//            onloadRegexp = Pattern.compile("onload=\"(.*?)\"", Pattern.CASE_INSENSITIVE); // try double quotes
//            onloadMatcher = onloadRegexp.matcher(frameSetText);
//        }
//        if (onloadMatcher.find()) {
//            String oldOnloadRoutine = onloadMatcher.group(1);
//            frameSetText = onloadMatcher.replaceFirst("");
//            String escapedOldOnloadRoutine = null;
//            try {
//                escapedOldOnloadRoutine = URLEncoder.encode(oldOnloadRoutine, "UTF-8");
//            } catch (UnsupportedEncodingException e) {
//                throw new RuntimeException("could not handle " + oldOnloadRoutine + ": " + e);
//            }
//            onloadRoutine = "selenium_frameRunTest(unescape('" + escapedOldOnloadRoutine  + "'))";
//        }
//        
//        // either there was no existing onload, or it's been stripped out
//        Pattern framesetTagRegexp = Pattern.compile("<\\s*frameset", Pattern.CASE_INSENSITIVE);
//        frameSetText = framesetTagRegexp.matcher(frameSetText).replaceFirst("<frameset onload=\"" + onloadRoutine + "\"");
//        data = framesetMatcher.replaceFirst(frameSetText);
//        return data;
//    }

    private static byte[] makeJsChunk(String js) {
        StringBuffer sb = new StringBuffer("\n<script language=\"JavaScript\">\n");
        sb.append(js)
        .append("\n</script>\n");
        return sb.toString().getBytes();
    }

    public void addUserContentTransformation(String before, String after ) {
        contentTransformations.put(before, after);
        return;
    }
    
    public boolean addUserJsInjectionFile(String fileName) {
        File f = new File(fileName);
        if (!f.canRead()) {
            System.out.println("Error: cannot read user JavaScript injection file " + fileName);
            return false;
        }
        userJsInjectionFiles.add(fileName);
        return true;
    }

    public boolean userContentTransformationsExist() {
        return !contentTransformations.isEmpty();
    }

    /**
     * Check if there are any user js injections present.
     * 
     * @return Returns true if there are user js injections; false otherwise.
     */
    public boolean userJsInjectionsExist() {
        return !userJsInjectionFiles.isEmpty();
    }
    
	public byte[] getInjectionHtml() {
		if (injectionHtml == null)
		{
	        StringBuffer jsInBuffer = new StringBuffer();
	        try {
	        	appendFileContent(jsInBuffer, INJECTION_HTML_FILE);
	        }
	        catch (IOException ex) {
	        	logger.error(ex);
	        }
	        injectionHtml = jsInBuffer.toString().getBytes();
	        
		}
//		else {
//			injectionHtmlInputStream.reset();
//		}
		
		return injectionHtml;
}
}
