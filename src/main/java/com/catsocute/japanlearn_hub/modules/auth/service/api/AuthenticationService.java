package com.catsocute.japanlearn_hub.modules.auth.service.api;

import com.catsocute.japanlearn_hub.modules.auth.dto.request.AuthenticationRequest;
import com.catsocute.japanlearn_hub.modules.auth.dto.request.IntrospectRequest;
import com.catsocute.japanlearn_hub.modules.auth.dto.response.AuthenticationResponse;
import com.catsocute.japanlearn_hub.modules.auth.dto.response.IntrospectResponse;

import java.text.ParseException;

public interface AuthenticationService {
    /**
     * authenticate
     * @author catsocute
     * @param request {AuthenticationRequest}
     * @return AuthenticationResponse
     */
    AuthenticationResponse authenticate(AuthenticationRequest request);

    /**
     * introspect token
     * @author catsocute
     * @param request {IntrospectRequest}
     * @return IntrospectResponse
     */
    IntrospectResponse introspect(IntrospectRequest request) throws ParseException;
}
