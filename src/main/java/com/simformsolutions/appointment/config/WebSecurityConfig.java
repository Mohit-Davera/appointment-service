package com.simformsolutions.appointment.config;

import com.simformsolutions.appointment.handler.CustomSuccessHandler;
import com.simformsolutions.appointment.service.details.UserDetailsServiceImpl;
import com.simformsolutions.appointment.service.oauth.CustomOAuth2UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.net.URI;

@Configuration

public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private CustomOAuth2UserService oauthUserService;

    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;

    @Override
    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailsServiceImpl();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationSuccessHandler successHandler() {
        return new CustomSuccessHandler();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(authenticationProvider());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/data", "/speciality/testing", "/login/oauth2/**", "/css/**", "/js/**").permitAll()
                .anyRequest().authenticated()

                .and()
                .formLogin()
                .loginPage("/login")
                .usernameParameter("email")
                .passwordParameter("password")
                .defaultSuccessUrl("/home")
                .permitAll()

                .and()
                .oauth2Login()
                .loginPage("/login")
                .userInfoEndpoint()
                .userService(oauthUserService)

                .and()
                .successHandler(successHandler())

                .and()
                .logout().logoutSuccessHandler(oidcLogoutSuccessHandler())
                /*.logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
.logoutSuccessUrl("/login").deleteCookies("JSESSIONID")
.invalidateHttpSession(true)*/

                .and()
                .exceptionHandling().accessDeniedPage("/403")

                .and().csrf().disable();
    }
    private OidcClientInitiatedLogoutSuccessHandler oidcLogoutSuccessHandler(){
        OidcClientInitiatedLogoutSuccessHandler successHandler = new OidcClientInitiatedLogoutSuccessHandler(clientRegistrationRepository);

        successHandler.setPostLogoutRedirectUri("http://localhost:8082/");
        return successHandler;
    }
}
