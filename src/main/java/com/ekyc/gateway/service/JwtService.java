package com.ekyc.gateway.service;

import com.ekyc.gateway.config.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.lang.StringUtils;
import lombok.RequiredArgsConstructor;
//import lombok.var;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JwtService implements Serializable {
    private final JwtConfig jwtConfig;

    public String extractUsername(final String token){
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(final String token){
        return extractClaim(token,Claims::getExpiration);
    }

    public <T> T extractClaim(final String token, final Function<Claims,T> claimsResolver){
        final var claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Claims extractAllClaims(final String token){
        return Jwts.parser().setSigningKey(this.jwtConfig.getSecret()).parseClaimsJws(token).getBody();
    }

    public boolean isTokenExpired(final String token){
        return extractExpiration(token).before(new Date());
    }

    public boolean isNotTokenExpired(final String token){
        return !isTokenExpired(token);
    }

    public String generateToken(final UserDetails userDetails){
        Map<String,Object> claims = new HashMap<>();
        return createToken(claims,userDetails.getUsername());
    }

    public String createToken(final Map<String,Object> claims, final String subject){
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration( new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 ) )
                .signWith(SignatureAlgorithm.HS512,this.jwtConfig.getSecret())
                .compact();
    }

    public boolean validateToken(final String token, final UserDetails userDetails){
        final var username = extractUsername(token);
        return StringUtils.equals(username,userDetails.getUsername()) && isNotTokenExpired(token);
    }
}
