package org.openqa.selenium.server;

import org.mortbay.http.*;
import org.mortbay.http.handler.ResourceHandler;
import org.mortbay.jetty.Server;
import org.mortbay.util.Resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class SeleniumProxy {
    private Server server;
    private SeleniumDriverResourceHandler driver;

    public static void main(String[] args) throws Exception {
        int port = 8080;
        boolean interactive = false;

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if ("-port".equals(arg)) {
                port = Integer.parseInt(args[i + 1]);
            }

            if ("-interactive".equals(arg)) {
                interactive = true;
            }
        }

        final SeleniumProxy seleniumProxy = new SeleniumProxy(port);
        Thread jetty = new Thread(new Runnable() {
            public void run() {
                try {
                    seleniumProxy.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        if (interactive) {
            jetty.setDaemon(true);
        }

        jetty.start();

        if (interactive) {
            Thread.sleep(500);
            System.out.println("Entering interactive mode... type selenium commands here (ie: open|http://www.yahoo.com)");
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            String userInput;

            while ((userInput = stdIn.readLine()) != null) {
                if ("quit".equals(userInput)) {
                    System.out.println("Stopping...");
                    seleniumProxy.stop();
                    break;
                }

                final URL url = new URL("http://localhost:" + port + "/selenium/driver?commandRequest=" + userInput);
                Thread t = new Thread(new Runnable() {
                    public void run() {
                        try {
                            System.out.println("---> Requesting " + url.toString());
                            URLConnection conn = url.openConnection();
                            conn.connect();
                            conn.getContent();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                t.start();
            }
        }

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                try {
                    System.out.println("Shutting down...");
                    seleniumProxy.stop();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }));
    }

    public SeleniumProxy(int port) throws Exception {
        server = new Server();
        SocketListener socketListener = new SocketListener();
        socketListener.setPort(port);
        server.addListener(socketListener);

        HttpContext root = new HttpContext();
        root.setContextPath("/");
        ProxyHandler rootProxy = new ProxyHandler();
        root.addHandler(rootProxy);
        server.addContext(null, root);

        final HttpContext context = new HttpContext();
        context.setContextPath("/selenium");
        context.addHandler(new ResourceHandler() {
            public void handle(String string, String string1, HttpRequest httpRequest, HttpResponse httpResponse) throws HttpException, IOException {
                httpResponse.setField("Expires", "-1"); // never cached.
                super.handle(string, string1, httpRequest, httpResponse);
            }

            protected Resource getResource(final String s) throws IOException {
                ClassPathResource r = new ClassPathResource("/selenium" + s);
                context.getResourceMetaData(r);
                return r;
            }
        });
        server.addContext(null, context);

        HttpContext driver = new HttpContext();
        driver.setContextPath("/selenium/driver");
        this.driver = new SeleniumDriverResourceHandler();
        context.addHandler(this.driver);
        server.addContext(null, driver);
    }

    public void start() throws Exception {
        server.start();
    }

    public void stop() throws InterruptedException {
        server.stop();
    }
}
