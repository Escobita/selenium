package org.seleniumhq;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RedirectFilter implements Filter {
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse res = (HttpServletResponse) servletResponse;
        String request = req.getRequestURI();
        String url = "http://selenium.seleniumhq.org/projects/ide";

        if (request.equals("/changelog.jsp")) {
            url = "http://seleniumhq.org/download/previous.html";
        } else if (request.equals("/contribute.jsp")) {
            url = "http://seleniumhq.org/about/getting-involved.html";
        } else if (request.equals("/devForums.jsp")) {
            url = "http://seleniumhq.org/support/";
        } else if (request.equals("/documentation.jsp")) {
            url = "http://seleniumhq.org/documentation/";
        } else if (request.equals("/download.jsp")) {
            url = "http://seleniumhq.org/download/";
        } else if (request.equals("/license.jsp")) {
            url = "http://seleniumhq.org/about/license.html";
        } else if (request.equals("/members.jsp")) {
            url = "http://seleniumhq.org/about/contributors.html";
        } else if (request.equals("/news.jsp")) {
            url = "http://seleniumhq.org/about/news.html";
        } else if (request.equals("/reporting.jsp")) {
            url = "http://seleniumhq.org/support/";
        } else if (request.equals("/rss.jsp")) {
            url = "http://seleniumhq.org/support/";
        } else if (request.equals("/source.jsp")) {
            url = "http://seleniumhq.org/download/source.html";
        } else if (request.equals("/userForums.jsp")) {
            url = "http://seleniumhq.org/support/";
        }

        res.sendRedirect(url);
    }

    public void destroy() {
    }
}
