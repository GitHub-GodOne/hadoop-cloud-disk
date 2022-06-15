package com.example.class_design.service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class GetCookie {
    public static String  getCook(HttpServletRequest re){
        for (Cookie cook:re.getCookies()){
            if("cookie".equals(cook.getName())){
                return "/" + cook.getValue();
            }
        }
        return null;
    }

}
