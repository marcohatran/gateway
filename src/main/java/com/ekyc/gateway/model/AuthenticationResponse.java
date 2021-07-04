package com.ekyc.gateway.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthenticationResponse implements Serializable {
    private String jwt;

    public String getJwt(){
        return jwt;
    }
}
