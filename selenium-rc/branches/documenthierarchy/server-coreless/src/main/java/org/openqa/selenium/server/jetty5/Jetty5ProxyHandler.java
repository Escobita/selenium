package org.openqa.selenium.server.jetty5;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.mortbay.http.HttpConnection;
import org.mortbay.http.HttpException;
import org.mortbay.http.HttpFields;
import org.mortbay.http.HttpMessage;
import org.mortbay.http.HttpRequest;
import org.mortbay.http.HttpResponse;
import org.mortbay.http.HttpServer;
import org.mortbay.http.HttpTunnel;
import org.mortbay.http.SslListener;
import org.mortbay.util.IO;
import org.mortbay.util.InetAddrPort;
import org.mortbay.util.LineInput;
import org.mortbay.util.StringMap;
import org.mortbay.util.URI;
import org.openqa.selenium.server.browser.launchers.ResourceExtractor;
import org.openqa.selenium.server.configuration.SeleniumConfiguration;
import org.openqa.selenium.server.jetty.ModifiedIO;
import org.openqa.selenium.server.jetty.ProxyHandler;
import org.openqa.selenium.server.jetty.WebHandler;
import org.openqa.selenium.server.jetty.WebResponse;
import org.openqa.selenium.server.proxy.InjectionManager;

import cybervillains.ca.KeyStoreManager;

/**
 * Proxy request handler. A HTTP/1.1 Proxy. This implementation uses the JVMs URL implementation to
 * make proxy requests.
 * <p/>
 * The HttpTunnel mechanism is also used to implement the CONNECT method.
 *
 * @author Greg Wilkins (gregw)
 * @author Matthew Purland (integration into new Selenium Server with Jetty 5)
 * @author giacof@tiscali.it (chained proxy)
 * @version $Id: Jetty5ProxyHandler.java 213 2007-09-14 20:47:04Z andersh $
 */
public class Jetty5ProxyHandler extends AbstractJetty5Handler {
	private static Logger logger = Logger.getLogger(Jetty5ProxyHandler.class);
	
	private ProxyHandler proxyHandler;	
	private SeleniumConfiguration seleniumConfiguration;
	private InjectionManager injectionManager;
	private ModifiedIO modifiedIO;
	
	
	public Jetty5ProxyHandler(WebHandler webHandler, ProxyHandler proxyHandler, SeleniumConfiguration seleniumConfiguration, InjectionManager injectionManager, ModifiedIO modifiedIO) {
		super(webHandler);
		this.proxyHandler = proxyHandler;
		this.seleniumConfiguration = seleniumConfiguration;
		this.injectionManager = injectionManager;
		this.modifiedIO = modifiedIO;
		
    	final int serverPort = seleniumConfiguration.getPort();
    	_allowedConnectPorts.add(serverPort);
	}
	
    protected Set<String> _proxyHostsWhiteList;
    protected Set<String> _proxyHostsBlackList;
    protected int _tunnelTimeoutMs = 250;
    private boolean _anonymous = false;
    private transient boolean _chained = false;
    private final Map<String,SslRelay> _sslMap = new LinkedHashMap<String, SslRelay>();
    private String sslKeystorePath;
    private boolean useCyberVillains = true;

    /* ------------------------------------------------------------ */
    /**
     * Map of leg by leg headers (not end to end). Should be a set, but more efficient string map is
     * used instead.
     */
    protected StringMap _DontProxyHeaders = new StringMap();

    {
        Object o = new Object();
        _DontProxyHeaders.setIgnoreCase(true);
        _DontProxyHeaders.put(HttpFields.__ProxyConnection, o);
        _DontProxyHeaders.put(HttpFields.__Connection, o);
        _DontProxyHeaders.put(HttpFields.__KeepAlive, o);
        _DontProxyHeaders.put(HttpFields.__TransferEncoding, o);
        _DontProxyHeaders.put(HttpFields.__TE, o);
        _DontProxyHeaders.put(HttpFields.__Trailer, o);
        _DontProxyHeaders.put(HttpFields.__Upgrade, o);
    }

    /* ------------------------------------------------------------ */
    /**
     * Map of leg by leg headers (not end to end). Should be a set, but more efficient string map is
     * used instead.
     */
    protected StringMap _ProxyAuthHeaders = new StringMap();

    {
        Object o = new Object();
        _ProxyAuthHeaders.put(HttpFields.__ProxyAuthorization, o);
        _ProxyAuthHeaders.put(HttpFields.__ProxyAuthenticate, o);
    }

    /* ------------------------------------------------------------ */
    /**
     * Map of allows schemes to proxy Should be a set, but more efficient string map is used
     * instead.
     */
    protected StringMap _ProxySchemes = new StringMap();

    {
        Object o = new Object();
        _ProxySchemes.setIgnoreCase(true);
        _ProxySchemes.put(HttpMessage.__SCHEME, o);
        _ProxySchemes.put(HttpMessage.__SSL_SCHEME, o);
        _ProxySchemes.put("ftp", o);
    }

    /* ------------------------------------------------------------ */
    /**
     * Set of allowed CONNECT ports.
     */
    protected HashSet<Integer> _allowedConnectPorts = new HashSet<Integer>();

    {

        _allowedConnectPorts.add(80);
        //_allowedConnectPorts.add(serverPort);
        _allowedConnectPorts.add(8000);
        _allowedConnectPorts.add(8080);
        _allowedConnectPorts.add(8888);
        _allowedConnectPorts.add(443);
        _allowedConnectPorts.add(8443);
    }


    /* ------------------------------------------------------------ */
    /*
     */
    public void start() throws Exception {
        _chained = System.getProperty("http.proxyHost") != null || seleniumConfiguration.isForceProxyChainMode();
        super.start();
    }

    /* ------------------------------------------------------------ */

    /**
     * Get proxy host white list.
     *
     * @return Array of hostnames and IPs that are proxied, or an empty array if all hosts are
     *         proxied.
     */
    public String[] getProxyHostsWhiteList() {
        if (_proxyHostsWhiteList == null || _proxyHostsWhiteList.size() == 0)
            return new String[0];

        String[] hosts = new String[_proxyHostsWhiteList.size()];
        hosts = _proxyHostsWhiteList.toArray(hosts);
        return hosts;
    }

    /* ------------------------------------------------------------ */

    /**
     * Set proxy host white list.
     *
     * @param hosts Array of hostnames and IPs that are proxied, or null if all hosts are proxied.
     */
    public void setProxyHostsWhiteList(String[] hosts) {
        if (hosts == null || hosts.length == 0)
            _proxyHostsWhiteList = null;
        else {
            _proxyHostsWhiteList = new HashSet<String>();
            for (int i = 0; i < hosts.length; i++) {
                String host = hosts[i];
                if (host != null && host.trim().length() > 0)
                    _proxyHostsWhiteList.add(host);
            }
        }
    }

    /* ------------------------------------------------------------ */

    /**
     * Get proxy host black list.
     *
     * @return Array of hostnames and IPs that are NOT proxied.
     */
    public String[] getProxyHostsBlackList() {
        if (_proxyHostsBlackList == null || _proxyHostsBlackList.size() == 0)
            return new String[0];

        String[] hosts = new String[_proxyHostsBlackList.size()];
        hosts = _proxyHostsBlackList.toArray(hosts);
        return hosts;
    }

    /* ------------------------------------------------------------ */

    /**
     * Set proxy host black list.
     *
     * @param hosts Array of hostnames and IPs that are NOT proxied.
     */
    public void setProxyHostsBlackList(String[] hosts) {
        if (hosts == null || hosts.length == 0)
            _proxyHostsBlackList = null;
        else {
            _proxyHostsBlackList = new HashSet<String>();
            for (int i = 0; i < hosts.length; i++) {
                String host = hosts[i];
                if (host != null && host.trim().length() > 0)
                    _proxyHostsBlackList.add(host);
            }
        }
    }

    /* ------------------------------------------------------------ */
    public int getTunnelTimeoutMs() {
        return _tunnelTimeoutMs;
    }

    /* ------------------------------------------------------------ */

    /**
     * Tunnel timeout. IE on win2000 has connections issues with normal timeout handling. This
     * timeout should be set to a low value that will expire to allow IE to see the end of the
     * tunnel connection.
     */
    public void setTunnelTimeoutMs(int ms) {
        _tunnelTimeoutMs = ms;
    }

    /* ------------------------------------------------------------ */
    public void handle(String pathInContext, String pathParams, HttpRequest request, HttpResponse response) throws HttpException, IOException {
        URI uri = request.getURI();

        // Is this a CONNECT request?
        if (HttpRequest.__CONNECT.equalsIgnoreCase(request.getMethod())) {
            response.setField(HttpFields.__Connection, "close"); // TODO Needed for IE????
            handleConnect(pathInContext, pathParams, request, response);
            return;
        }

        try {
            // Do we proxy this?
            URL url = isProxied(uri);
        	StringBuffer requestURL = request.getRequestURL();
            // is this URL a /selenium URL?
            if (isSeleniumUrl(requestURL.toString())) {
                request.setHandled(false);
                return;
            }

            if (url == null) {
                if (isForbidden(uri))
                    sendForbid(request, response, uri);
                return;
            }

            proxyPlainTextRequest(url, pathInContext, pathParams, request, response);
        }
        catch (Exception e) {
            logger.warn("Could not proxy " + uri, e);
            // @todo uncomment?
            //LogSupport.ignore(logger, e);
            if (!response.isCommitted())
                response.sendError(HttpResponse.__400_Bad_Request);
        }
    }

    public boolean shouldInject(String path) {
    	String dontInjectRegex = seleniumConfiguration.getDontInjectRegex();
    	
        if (dontInjectRegex == null) {
            return true;
        }
        return !path.matches(dontInjectRegex);
    }    
    
    private boolean isSeleniumUrl(String url) {
        int slashSlash = url.indexOf("//");
        if (slashSlash == -1) {
            return false;
        }

        int nextSlash = url.indexOf("/", slashSlash + 2);
        if (nextSlash == -1) {
            return false;
        }

        int seleniumServer = url.indexOf("/selenium-server/");
        if (seleniumServer == -1) {
            return false;
        }

        // we do this complex checking because sometimes some sites/pages (such as ominture ads) embed the referrer URL,
        // which will include selenium stuff, in to the query parameter, which would fake out a simple String.contains()
        // call. This method is more robust and will catch this stuff.
        return seleniumServer == nextSlash; 
    }
    
    /**
     * @todo write javadoc
     * 
     * @param request
     * @param response
     * @param in
     * @param out
     * @return
     * @throws IOException
     */
    public long injectJavaScript(HttpRequest request, HttpResponse response, InputStream in, OutputStream out) throws IOException {
    	WebResponse webResponse = new Jetty5WebResponse(response);
    	
    	return injectionManager.injectJavaScript(request.getRequestURL().toString(), request.getPath(), webResponse, in, out);
//	    if (!contentTransformations.containsKey("__SELENIUM_JS__")) {
//	        init();   
//        }
//        
//        int len = 102400;
//        byte[] buf = new byte[len];
//        len = readStream(in, buf, len);
//        if (len == -1) {
//            return -1;
//        }
//        int lengthOfBOM = getBOMLength(buf); 
//        String data = new String(buf, lengthOfBOM, len);
//
//        boolean isKnownToBeHtml = htmlIdentifierManager.shouldBeInjected(request.getPath(), response.getContentType(), data);
//
//        String url = response.getHttpRequest().getRequestURL().toString();
////        if (seleniumConfiguration.getDebugURL().equals(url)) {
////            System.out.println("debug URL seen");
////        }
//       
//        if (!isKnownToBeHtml) {
//            out.write(buf, 0, len);
//        }
////        else if (lengthOfBOM>0) {
////            out.write(buf, 0, lengthOfBOM);
////        }
//        String sessionId = sessionManager.getLastSessionId();
//
//        long bytesCopied = len;
//
//        if (seleniumConfiguration.isDebugMode()) {
//            logger.debug(url + " (InjectionHelper looking)");
//        }
//        if (!isKnownToBeHtml) {
//            bytesCopied += modifiedIO.copy(in, out);
//        }
//        else {
//            if (seleniumConfiguration.isDebugMode()) {
//                logger.debug("injecting...");
//            }
//            response.removeField("Content-Length"); // added js will make it wrong, lead to page getting truncated
//            String injectionHtml = "/core/scripts/injection.html";
////            InputStream jsIn = new ClassPathResource(injectionHtml).getInputStream();
//            InputStream jsIn = new ClasspathResourceLocator().getResource(injectionHtml);
//            
//            contentTransformations.put("@SESSION_ID@", sessionId);
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            writeDataWithUserTransformations("", jsIn, baos);
//            jsIn.close();
//            baos.write(setSomeJsVars(sessionId));
//            for (String filename : userJsInjectionFiles) {
//                jsIn = new FileInputStream(filename);
//                IO.copy(jsIn, baos); 
//            }
//
//            int headIndex = -1; //data.toLowerCase().indexOf("<head>");
//            if (headIndex != -1) {
//                data = data.substring(0, headIndex + 6) + baos.toString() + data.substring(headIndex + 6);
//            } else {
//                data = baos.toString() + data;
//            }
//
//            bytesCopied += writeDataWithUserTransformations(data, in, out);
//        }
//
//        return bytesCopied;
    }

    protected long proxyPlainTextRequest(URL url, String pathInContext, String pathParams, HttpRequest request, HttpResponse response) throws IOException {
        if (logger.isDebugEnabled())
            logger.debug("PROXY URL=" + url);

        URLConnection connection = url.openConnection();
        connection.setAllowUserInteraction(false);
        
        boolean shouldInject = shouldInject(pathInContext);

        //FIXME, this needs to be done here to kill the params when proxying, but the problem is we don't know if we should actually inject
        //until way down in the injectionmanager.  For the time being, just hard coding the rules in shouldInject to ignore iamges, css, and js
        if (seleniumConfiguration.isProxyInjectionMode() && shouldInject)
            adjustRequestForProxyInjection(request, connection);

        // Set method
        HttpURLConnection http = null;
        if (connection instanceof HttpURLConnection) {
            http = (HttpURLConnection) connection;
            http.setRequestMethod(request.getMethod());
            http.setInstanceFollowRedirects(false);
        }

        // check connection header
        String connectionHdr = request.getField(HttpFields.__Connection);
        if (connectionHdr != null && (connectionHdr.equalsIgnoreCase(HttpFields.__KeepAlive) || connectionHdr.equalsIgnoreCase(HttpFields.__Close)))
            connectionHdr = null;

        // copy headers
        boolean xForwardedFor = false;
        boolean hasContent = false;
        Enumeration enm = request.getFieldNames();
        while (enm.hasMoreElements()) {
            // TODO could be better than this!
            String hdr = (String) enm.nextElement();

            if (_DontProxyHeaders.containsKey(hdr) || !_chained && _ProxyAuthHeaders.containsKey(hdr))
                continue;
            if (connectionHdr != null && connectionHdr.indexOf(hdr) >= 0)
                continue;

            if (HttpFields.__ContentType.equals(hdr))
                hasContent = true;

            Enumeration vals = request.getFieldValues(hdr);
            while (vals.hasMoreElements()) {
                String val = (String) vals.nextElement();
                if (val != null) {
                    // don't proxy Referer headers if the referer is Selenium!
                    if ("Referer".equals(hdr) && (-1 != val.indexOf("/selenium-server/"))) {
                        continue;
                    }

                    connection.addRequestProperty(hdr, val);
                    xForwardedFor |= HttpFields.__XForwardedFor.equalsIgnoreCase(hdr);
                }
            }
        }

        // Proxy headers
        if (!_anonymous)
            connection.setRequestProperty("Via", "1.1 (jetty)");
        if (!xForwardedFor)
            connection.addRequestProperty(HttpFields.__XForwardedFor, request.getRemoteAddr());

        // a little bit of cache control
        String cache_control = request.getField(HttpFields.__CacheControl);
        if (cache_control != null && (cache_control.indexOf("no-cache") >= 0 || cache_control.indexOf("no-store") >= 0))
            connection.setUseCaches(false);

        // customize Connection
        customizeConnection(pathInContext, pathParams, request, connection);


        InputStream proxy_in = null;
        OutputStream proxy_out = null;
        long bytesCopied = -1;

        try {
            connection.setDoInput(true);

            // do input thang!
            InputStream in = request.getInputStream();
            if (hasContent) {
                connection.setDoOutput(true);
                proxy_out = connection.getOutputStream();
                IO.copy(in, proxy_out);
            }

            // Connect
            connection.connect();
	
	        // handler status codes etc.
	        int code;
	        if (http != null) {
	            proxy_in = http.getErrorStream();
	
	            code = http.getResponseCode();
	            response.setStatus(code);
	            response.setReason(http.getResponseMessage());
	
	            String contentType = http.getContentType();
	            if (seleniumConfiguration.isDebugMode()) {
	                logger.debug("Content-Type is: " + contentType);
	            }
	        }
	
	        if (proxy_in == null) {
	            try {
	                proxy_in = connection.getInputStream();
	            }
	            catch (Exception e) {
	                // @todo uncomment?
	            	//LogSupport.ignore(log, e);
	                proxy_in = http.getErrorStream();
	            }
	        }
	
	        // clear response defaults.
	        response.removeField(HttpFields.__Date);
	        response.removeField(HttpFields.__Server);
	
	        // set response headers
	        int h = 0;
	        String hdr = connection.getHeaderFieldKey(h);
	        String val = connection.getHeaderField(h);
	        while (hdr != null || val != null) {
	            if (hdr != null && val != null && !_DontProxyHeaders.containsKey(hdr) && (_chained || !_ProxyAuthHeaders.containsKey(hdr)))
	                response.addField(hdr, val);
	            h++;
	            hdr = connection.getHeaderFieldKey(h);
	            val = connection.getHeaderField(h);
	        }
	        if (!_anonymous)
	            response.setField("Via", "1.1 (jetty)");
	
//	        response.removeField(HttpFields.__ETag); // possible cksum?  Stop caching...
//	        response.removeField(HttpFields.__LastModified); // Stop caching...
	
	        // Handled
	        request.setHandled(true);
	        if (proxy_in != null) {
	            boolean injectableResponse = http.getResponseCode() == HttpURLConnection.HTTP_OK ||
	                    (http.getResponseCode() >= 400 && http.getResponseCode() < 600);
	            if (seleniumConfiguration.isProxyInjectionMode() && injectableResponse && shouldInject(request.getPath())) {
	                // check if we should proxy this path based on the dontProxyRegex that can be user-specified
                    bytesCopied = injectJavaScript(request, response, proxy_in, response.getOutputStream());
	            }
	            else {
	                bytesCopied = modifiedIO.copy(proxy_in, response.getOutputStream());
	            }
	        }
        }
        catch (Exception e) {
            // @todo uncomment?
        	//LogSupport.ignore(log, e);
        }
        finally {
        	close(proxy_in);
        	close(proxy_out);
        }

        return bytesCopied;
    }

    private void close(Closeable c) {
    	if (c != null) {
    		try {
    			c.close();
    		}
    		catch (Exception e) { 
    			// do nothing if we can't cleanup
    		}
    	}
    }
    
    private void adjustRequestForProxyInjection(HttpRequest request, URLConnection connection) {
		request.setState(HttpMessage.__MSG_EDITABLE);
		if (request.containsField("If-Modified-Since")) {
			// TODO: still need to disable caching?  I want to prevent 304s during this development phase where 
			// I'm often changing the injection, and so need HTML caching to be absolutely defeated 
			request.removeField("If-Modified-Since");
			request.removeField("If-None-Match");            	
			connection.setUseCaches(false);  // maybe I don't need the stuff above?
		}
		request.removeField("Accept-Encoding");	// js injection is hard w/ gzip'd data, so try to prevent it ahead of time
		request.setState(HttpMessage.__MSG_RECEIVED);
	}

//    public static void main(String[] args) throws Exception {
//        Server server = new Server();
//        HttpContext httpContext = new HttpContext();
//        httpContext.setContextPath("/");
//        ProxyHandler proxy = new ProxyHandler();
//        proxy.useCyberVillains = false;
//        httpContext.addHandler(proxy);
//        server.addContext(httpContext);
//        SocketListener listener = new SocketListener();
//        listener.setPort(4444);
//        server.addListener(listener);
//        server.start();
//    }

    /* ------------------------------------------------------------ */
    public void handleConnect(String pathInContext, String pathParams, HttpRequest request, HttpResponse response) throws HttpException, IOException {
        URI uri = request.getURI();

        try {
            if (logger.isDebugEnabled())
                logger.debug("CONNECT: " + uri);
            InetAddrPort addrPort = new InetAddrPort(uri.toString());

            if (isForbidden(HttpMessage.__SSL_SCHEME, addrPort.getHost(), addrPort.getPort(), false)) {
                sendForbid(request, response, uri);
            } else {
                HttpConnection http_connection = request.getHttpConnection();
                http_connection.forceClose();

                HttpServer server = http_connection.getHttpServer();

                SslRelay listener;
                synchronized(_sslMap) {
                    listener = _sslMap.get(uri.toString());
                    if (listener==null)
                    {
                        // we do this because the URI above doesn't actually have the host broken up (it returns null on getHost())
                        String host = new URL("https://" + uri.toString()).getHost();

                        listener = new SslRelay(addrPort);

                        if (useCyberVillains) {
                            wireUpSslWithCyberVilliansCA(host, listener);
                        } else {
                            wireUpSslWithRemoteService(host, listener);
                        }

                        listener.setPassword("password");
                        listener.setKeyPassword("password");
                        server.addListener(listener);
                        try
                        {
                            listener.start();
                        }
                        catch(Exception e)
                        {
                            e.printStackTrace();
                            throw e;
                        }
                        _sslMap.put(uri.toString(),listener);
                    }
                }

                int port = listener.getPort();

                // Get the timeout
                int timeoutMs = 30000;
                Object maybesocket = http_connection.getConnection();
                if (maybesocket instanceof Socket) {
                    Socket s = (Socket) maybesocket;
                    timeoutMs = s.getSoTimeout();
                }

                // Create the tunnel
                HttpTunnel tunnel = newHttpTunnel(request, response, InetAddress.getLocalHost(), port, timeoutMs);

                if (tunnel != null) {
                    // TODO - need to setup semi-busy loop for IE.
                    if (_tunnelTimeoutMs > 0) {
                        tunnel.getSocket().setSoTimeout(_tunnelTimeoutMs);
                        if (maybesocket instanceof Socket) {
                            Socket s = (Socket) maybesocket;
                            s.setSoTimeout(_tunnelTimeoutMs);
                        }
                    }
                    tunnel.setTimeoutMs(timeoutMs);

                    customizeConnection(pathInContext, pathParams, request, tunnel.getSocket());
                    request.getHttpConnection().setHttpTunnel(tunnel);
                    response.setStatus(HttpResponse.__200_OK);
                    response.setContentLength(0);
                }
                request.setHandled(true);
            }
        }
        catch (Exception e) {
            System.err.println("handleConnect: ProxyHandler.java: " + e);
            // @todo uncomment?
            //LogSupport.ignore(log, e);
            response.sendError(HttpResponse.__500_Internal_Server_Error);
        }
    }

    private void wireUpSslWithRemoteService(String host, SslRelay listener) throws IOException {
        // grab a keystore that has been signed by a CA cert that has already been imported in to the browser
        // note: this logic assumes the tester is using *custom and has imported the CA cert in to IE/Firefox/etc
        // the CA cert can be found at http://dangerous-certificate-authority.openqa.org
        File keystore = File.createTempFile("selenium-rc-" + host, "keystore");
        String urlString = "http://dangerous-certificate-authority.openqa.org/genkey.jsp?padding=" + _sslMap.size() + "&domain=" + host;

        URL url = new URL(urlString);
        URLConnection conn = url.openConnection();
        conn.connect();
        InputStream is = conn.getInputStream();
        byte[] buffer = new byte[1024];
        int length;
        FileOutputStream fos = new FileOutputStream(keystore);
        while ((length = is.read(buffer)) != -1) {
            fos.write(buffer, 0, length);
        }
        fos.close();
        is.close();

        listener.setKeystore(keystore.getAbsolutePath());
        //listener.setKeystore("c:\\" + (_sslMap.size() + 1) + ".keystore");
    }

    private void wireUpSslWithCyberVilliansCA(String host, SslRelay listener) throws KeyStoreException, InvalidKeyException, SignatureException, CertificateException, NoSuchAlgorithmException, NoSuchProviderException, UnrecoverableKeyException {
        try {
            File root = File.createTempFile("seleniumSslSupport", host);
            root.delete();
            root.mkdirs();

            ResourceExtractor.extractResourcePath(getClass(), "/sslSupport", root);


            KeyStoreManager mgr = new KeyStoreManager(root);
            mgr.getCertificateByHostname(host);
            mgr.getKeyStore().deleteEntry(KeyStoreManager._caPrivKeyAlias);
            mgr.persist();

            listener.setKeystore(new File(root, "cybervillainsCA.jks").getAbsolutePath());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /* ------------------------------------------------------------ */
    protected HttpTunnel newHttpTunnel(HttpRequest request, HttpResponse response, InetAddress iaddr, int port, int timeoutMS) throws IOException {
        try {
            Socket socket = null;
            InputStream in = null;

            String chained_proxy_host = System.getProperty("http.proxyHost");
            if (chained_proxy_host == null) {
                socket = new Socket(iaddr, port);
                socket.setSoTimeout(timeoutMS);
                socket.setTcpNoDelay(true);
            } else {
                int chained_proxy_port = Integer.getInteger("http.proxyPort", 8888).intValue();

                Socket chain_socket = new Socket(chained_proxy_host, chained_proxy_port);
                chain_socket.setSoTimeout(timeoutMS);
                chain_socket.setTcpNoDelay(true);
                if (logger.isDebugEnabled()) logger.debug("chain proxy socket=" + chain_socket);

                LineInput line_in = new LineInput(chain_socket.getInputStream());
                byte[] connect = request.toString().getBytes(org.mortbay.util.StringUtil.__ISO_8859_1);
                chain_socket.getOutputStream().write(connect);

                String chain_response_line = line_in.readLine();
                HttpFields chain_response = new HttpFields();
                chain_response.read(line_in);

                // decode response
                int space0 = chain_response_line.indexOf(' ');
                if (space0 > 0 && space0 + 1 < chain_response_line.length()) {
                    int space1 = chain_response_line.indexOf(' ', space0 + 1);

                    if (space1 > space0) {
                        int code = Integer.parseInt(chain_response_line.substring(space0 + 1, space1));

                        if (code >= 200 && code < 300) {
                            socket = chain_socket;
                            in = line_in;
                        } else {
                            Enumeration iter = chain_response.getFieldNames();
                            while (iter.hasMoreElements()) {
                                String name = (String) iter.nextElement();
                                if (!_DontProxyHeaders.containsKey(name)) {
                                    Enumeration values = chain_response.getValues(name);
                                    while (values.hasMoreElements()) {
                                        String value = (String) values.nextElement();
                                        response.setField(name, value);
                                    }
                                }
                            }
                            response.sendError(code);
                            if (!chain_socket.isClosed())
                                chain_socket.close();
                        }
                    }
                }
            }

            if (socket == null)
                return null;
            return new HttpTunnel(socket, in, null);
        }
        catch (IOException e) {
            logger.debug(e);
            response.sendError(HttpResponse.__400_Bad_Request);
            return null;
        }
    }

    /* ------------------------------------------------------------ */

    /**
     * Customize proxy Socket connection for CONNECT. Method to allow derived handlers to customize
     * the tunnel sockets.
     */
    protected void customizeConnection(String pathInContext, String pathParams, HttpRequest request, Socket socket) {
    }

    /* ------------------------------------------------------------ */

    /**
     * Customize proxy URL connection. Method to allow derived handlers to customize the connection.
     */
    protected void customizeConnection(String pathInContext, String pathParams, HttpRequest request, URLConnection connection) {
    }

    /* ------------------------------------------------------------ */

    /**
     * Is URL Proxied. Method to allow derived handlers to select which URIs are proxied and to
     * where.
     *
     * @param uri The requested URI, which should include a scheme, host and port.
     * @return The URL to proxy to, or null if the passed URI should not be proxied. The default
     *         implementation returns the passed uri if isForbidden() returns true.
     */
    protected URL isProxied(URI uri) throws MalformedURLException {
        // Is this a proxy request?
        if (isForbidden(uri))
            return null;

        // OK return URI as untransformed URL.
        return new URL(uri.toString());
    }

    /* ------------------------------------------------------------ */

    /**
     * Is URL Forbidden.
     *
     * @return True if the URL is not forbidden. Calls isForbidden(scheme,host,port,true);
     */
    protected boolean isForbidden(URI uri) {
        String scheme = uri.getScheme();
        String host = uri.getHost();
        int port = uri.getPort();
        return isForbidden(scheme, host, port, true);
    }

    /* ------------------------------------------------------------ */

    /**
     * Is scheme,host & port Forbidden.
     *
     * @param scheme           A scheme that mast be in the proxySchemes StringMap.
     * @param host             A host that must pass the white and black lists
     * @param port             A port that must in the allowedConnectPorts Set
     * @param openNonPrivPorts If true ports greater than 1024 are allowed.
     * @return True if the request to the scheme,host and port is not forbidden.
     */
    protected boolean isForbidden(String scheme, String host, int port, boolean openNonPrivPorts) {
        // Check port
        if (port > 0 && !_allowedConnectPorts.contains(new Integer(port))) {
            if (!openNonPrivPorts || port <= 1024)
                return true;
        }

        // Must be a scheme that can be proxied.
        if (scheme == null || !_ProxySchemes.containsKey(scheme))
            return true;

        // Must be in any defined white list
        if (_proxyHostsWhiteList != null && !_proxyHostsWhiteList.contains(host))
            return true;

        // Must not be in any defined black list
        return _proxyHostsBlackList != null && _proxyHostsBlackList.contains(host);

    }

    /* ------------------------------------------------------------ */

    /**
     * Send Forbidden. Method called to send forbidden response. Default implementation calls
     * sendError(403)
     */
    protected void sendForbid(HttpRequest request, HttpResponse response, URI uri) throws IOException {
        response.sendError(HttpResponse.__403_Forbidden, "Forbidden for Proxy");
    }

    /* ------------------------------------------------------------ */

    /**
     * @return Returns the anonymous.
     */
    public boolean isAnonymous() {
        return _anonymous;
    }

    /* ------------------------------------------------------------ */

    /**
     * @param anonymous The anonymous to set.
     */
    public void setAnonymous(boolean anonymous) {
        _anonymous = anonymous;
    }

    public void setSslKeystorePath(String sslKeystorePath) {
        this.sslKeystorePath = sslKeystorePath;
    }

    private static class SslRelay extends SslListener
    {
        InetAddrPort _addr;

        SslRelay(InetAddrPort addr)
        {
            _addr=addr;
        }

        protected void customizeRequest(Socket socket, HttpRequest request)
        {
            super.customizeRequest(socket,request);
            URI uri=request.getURI();

            // Convert the URI to a proxy URL
            uri.setScheme("https");
            uri.setHost(_addr.getHost());
            uri.setPort(_addr.getPort());
        }
    }
}
