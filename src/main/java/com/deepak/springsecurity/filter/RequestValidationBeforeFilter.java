package com.deepak.springsecurity.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.util.StringUtils;

import javax.swing.plaf.PanelUI;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class RequestValidationBeforeFilter implements Filter {
    public static final String AUTHENTICATION_SCEME_BASEC = "Basic";
    private Charset credentialsCharset = StandardCharsets.UTF_8;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String header = req.getHeader("Authorization");
        if (header != null) {
            if (StringUtils.startsWithIgnoreCase(header, "Bearer ")) {
                byte[] base64Token = header.substring(6).getBytes("UTF-8");
                byte[] decoded;
                try {
                    decoded = java.util.Base64.getDecoder().decode(base64Token);
                    String token = new String(decoded,credentialsCharset);
                    int delim = token.indexOf(":");
                    if (delim == -1) {
                        throw new IllegalArgumentException("Invalid basic authentication token");
                    }
                    String email = token.substring(0, delim);
                    if (email.toLowerCase().contains("test")) {
                        res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        return;
                    }
                } catch (IllegalArgumentException e) {
                    throw new BadCredentialsException(
                            "Failed to decode basic authentication token");
                }
            }
        }
        chain.doFilter(request, response);
    }
}
