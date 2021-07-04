package com.ekyc.gateway.filter;

import com.ekyc.gateway.service.CustomUserDetailsService;
import com.ekyc.gateway.service.JwtService;
//import com.sun.tools.internal.ws.wsdl.document.jaxws.Exception;
import lombok.RequiredArgsConstructor;
import lombok.var;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Component
public class JwtRequestFilter extends OncePerRequestFilter {
    public static final String AUTHORIZATION = "Authorization";
    public static final String BEARER_ = "Bearer ";
    private CustomUserDetailsService customUserDetailsService;
    private JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {

        final var authorization = httpServletRequest.getHeader(AUTHORIZATION);

        if (StringUtils.isNotBlank(authorization) && StringUtils.startsWith(authorization, BEARER_)) {

            final var jwt = StringUtils.substring(authorization, 7);
            final var username = jwtService.extractUsername(jwt);

            if (StringUtils.isNotBlank(username) && SecurityContextHolder.getContext().getAuthentication() == null) {

                final var userDetails = this.customUserDetailsService.loadUserByUsername(username);
                final var valid = jwtService.validateToken(jwt, userDetails);

                if (valid) {
                    final var upat = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    upat.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
                    SecurityContextHolder.getContext().setAuthentication(upat);
                }

            }

            filterChain.doFilter(httpServletRequest, httpServletResponse);
        }

    }

}







