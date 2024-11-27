package org.site.BoU.JWT;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class JwtAuthenticationFilter implements Filter {

    private JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        Cookie[] cookies = httpServletRequest.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("JWT".equals(cookie.getName())) {
                    String token = cookie.getValue();
                    if (jwtTokenProvider.validateToken(token)) {
                        String loginFromToken = jwtTokenProvider.getLoginFromToken(token);
                        String roles = jwtTokenProvider.getRolesFromToken(token);
                        List<GrantedAuthority> authorities = Arrays.stream(roles.split(","))
                                .map(SimpleGrantedAuthority::new)
                                .collect(Collectors.toList());
                        Authentication authentication = new UsernamePasswordAuthenticationToken(loginFromToken, null, authorities);
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                    break;
                }
            }
        }
        filterChain.doFilter(request, response);
    }
//    @Override
//    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
//            throws IOException, ServletException {
//
//        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
//        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
//
//        Cookie[] cookies = httpServletRequest.getCookies();
//        if (cookies != null) {
//            for (Cookie cookie : cookies) {
//                if ("JWT".equals(cookie.getName())) {
//                    String token = cookie.getValue();
//                    if (jwtTokenProvider.validateToken(token)) {
//                        String loginFromToken = jwtTokenProvider.getLoginFromToken(token);
////                        String roles = jwtTokenProvider.getRolesFromToken(token);
//
//
//                        SecurityContextHolder.getContext().setAuthentication(
//                                new UsernamePasswordAuthenticationToken(loginFromToken, null, new ArrayList<>())
//                        );
//                    }
//                    break;
//                }
//            }
//        }
//        filterChain.doFilter(request, response);
//    }

}

