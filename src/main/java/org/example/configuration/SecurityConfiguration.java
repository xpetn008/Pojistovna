package org.example.configuration;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import java.io.IOException;
import java.util.Collection;

@Configuration
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
@ComponentScan(basePackages = {"org.example.configuration", "org.example.controllers", "org.example.data", "org.example.models"})
public class SecurityConfiguration {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        return http.authorizeHttpRequests()
                    .requestMatchers("/images/**", "/styles/**", "/scripts/**", "/fonts/**").permitAll() // Statické zdroje - obrázky, styly atd.
                    .requestMatchers("/", "/registrace", "/prihlaseni", "/kontakt").permitAll() //Hlavní stránky přístupné bez přihlášení
                    .requestMatchers("/logout").authenticated()
                    .requestMatchers("/admin/**").hasRole("ADMIN")
                    .requestMatchers("/user/**").hasRole("USER")
                .and()
                .formLogin()
                    .loginPage("/prihlaseni")
                    .loginProcessingUrl("/prihlaseni")
                    .successHandler(successHandler())
                    .usernameParameter("email")
                    .permitAll()
                .and()
                .logout()
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/")
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID")
                .and().build();
    }
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationSuccessHandler successHandler(){
        return new SimpleUrlAuthenticationSuccessHandler(){
            @Override
            protected void handle(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException{
                String targetUrl = determineTargetUrl(authentication);
                getRedirectStrategy().sendRedirect(request, response, targetUrl);
            }

            protected String determineTargetUrl(Authentication authentication){
                Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
                if (authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN"))){
                    return "/admin";
                }else{
                    return "/user";
                }
            }
        };
    }
}
