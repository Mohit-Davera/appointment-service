package com.simformsolutions.appointment.handler;

import com.simformsolutions.appointment.service.UserService;
import com.simformsolutions.appointment.service.oauth.CryptoOidcUser;
import com.simformsolutions.appointment.service.oauth.CryptoPrincipal;
import com.simformsolutions.appointment.service.oauth.CustomOAuth2User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Service
public class CustomSuccessHandler implements AuthenticationSuccessHandler {

    private static final Logger log = LoggerFactory.getLogger(CustomSuccessHandler.class);
    @Autowired
    private UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException{
        log.info("AuthenticationSuccessHandler invoked");
        log.info("Authentication name: {}", authentication.getName());
        if(!userService.checkIfExists(authentication.getName())){
            if(authentication instanceof CustomOAuth2User){
                CustomOAuth2User oauthUser = (CustomOAuth2User) authentication.getPrincipal();
                userService.processOAuthPostLogin(oauthUser);
            }
            if (authentication instanceof OAuth2AuthenticationToken) {
                OidcUser oidcUser = (OidcUser)authentication.getPrincipal();
                userService.processOAuthPostLogin(new CryptoOidcUser(oidcUser));
                log.info(oidcUser.toString());
            }
        }
        response.sendRedirect("/home");

    }
}
