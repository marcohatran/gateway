package com.ekyc.gateway.controller;

import com.ekyc.gateway.exception.InvalidUsernameOrPasswordException;
import com.ekyc.gateway.model.AuthenticationRequest;
import com.ekyc.gateway.model.AuthenticationResponse;
import com.ekyc.gateway.service.CustomUserDetailsService;
import com.ekyc.gateway.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;

@RestController
@RequestMapping("/api/v1/security")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AuthenticationController implements Serializable {
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtService jwtService;

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(@RequestBody final AuthenticationRequest authenticationRequest) throws Exception {

        try {
            final var upatk = new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword());
            final var authentication = this.authenticationManager.authenticate(upatk);
        } catch (AuthenticationException ex) {
            throw new InvalidUsernameOrPasswordException("Invalid username or password");
        }

        final var userDetails = this.customUserDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        final var token = this.jwtService.generateToken(userDetails);

//        return ResponseEntity.ok(AuthenticationResponse.builder().jwt(token).build());
        return ResponseEntity.ok(new AuthenticationResponse(token));
    }

}
