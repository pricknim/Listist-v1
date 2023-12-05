package edu.codespring.listist.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@WebFilter(urlPatterns = {"/usermain", "/songs", "/myplaylists", "/createplaylist", "/playlist"})
public class LoggedinFilter extends HttpFilter {
    private static final Logger LOG = LoggerFactory.getLogger(LoggedinFilter.class);

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        String username = (String) req.getSession().getAttribute("username");
        if(username == null) {
            LOG.info("Unauthorized access.");
            ServletContext sc = getServletContext();
            sc.getRequestDispatcher("/main").forward(req, res);
        }
        else {
            chain.doFilter(req, res);
        }
    }
}
