package com.example.twistle.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

public class SessionAuthInterceptor implements HandlerInterceptor{

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws IOException{
        HttpSession session = req.getSession(false);
        Object user = session == null ? null : session.getAttribute("loggedInUser");
        if (user == null){
            res.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
        return true;
    }
}
