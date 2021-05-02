package com.cau.cc.security.config;

import com.cau.cc.security.filter.LoginProcessingFilter;
import com.cau.cc.security.handler.AjaxAuthenticationFailureHandler;
import com.cau.cc.security.handler.AjaxAuthenticationSuccessHandler;
import com.cau.cc.security.handler.CustomLogourSuccessHandler;
import com.cau.cc.security.provider.AjaxAuthenticationProvider;
import com.cau.cc.security.service.CustomerUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.session.DefaultCookieSerializerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    protected PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }


    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     * 내가만든 handler Bean 설정 -> filter에 등록
     */
    @Bean
    public AuthenticationSuccessHandler ajaxAuthenticationSuccessHandler(){
        return new AjaxAuthenticationSuccessHandler();
    }
    @Bean
    public AuthenticationFailureHandler ajaxAuthenticationFailureHandler(){
        return new AjaxAuthenticationFailureHandler();
    }


    /**
     * 내가 만든 필터 생성
     * 제공되는 authenticationManagerBean 사용
     */
    @Bean
    public LoginProcessingFilter loginProcessingFilter() throws Exception {
        LoginProcessingFilter loginProcessingFilter = new LoginProcessingFilter();
        loginProcessingFilter.setAuthenticationManager(authenticationManagerBean());
        loginProcessingFilter.setAuthenticationSuccessHandler(ajaxAuthenticationSuccessHandler());
        loginProcessingFilter.setAuthenticationFailureHandler(ajaxAuthenticationFailureHandler());
        return loginProcessingFilter;
    }

    @Autowired
    CustomerUserDetailsService customerUserDetailsService;

    /**
     * 내가 만든 provider 등록
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(ajaxAuthenticationProvider());
    }

    @Bean
    public AuthenticationProvider ajaxAuthenticationProvider() {
        return new AjaxAuthenticationProvider();
    }


    /**
     * cors
     * @param
     * @throws Exception
     */
    // CORS 허용 적용 (Global)
    @Bean
    public CorsConfigurationSource corsConfigurationSource(){
        System.out.println("----------------cors config-----------------------");

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.addAllowedOriginPattern("*");
        configuration.setAllowedMethods(Arrays.asList("GET","POST","PUT","PATCH","OPTIONS","DELETE"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        System.out.println("----------------cors config end-----------------------");
        return source;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .cors().and()
                .authorizeRequests()
                .antMatchers("/","/profile/**","/register",
                        "/login","/h2-console/**",
//                        "/api/swagger",
//                        "/api/v2/api-docs","/api/configuration/ui",
//                        "/api/swagger-resources/**",
//                        "/api/configuration/security",
//                        "/api/swagger-ui.html",
//                        "/api/swagger-ui/**",
//                        "/api/webjars/**",
//                        "/api/v2/**",
                        "/swagger",
                        "/v2/api-docs","/configuration/ui",
                        "/swagger-resources/**",
                        "/configuration/security",
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/webjars/**",
                        "/configuration/security",
                        "/v2/**",
                        "/profile/**",
                        "/upload",
                        "/email","/verify",
                        "/matching/**","/major/**",
                        "/test",
                        "/socket",
                        "/major/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .logout()
                    .permitAll()
                    .logoutUrl("/logout")
                    .deleteCookies("JSESSIONID")
                    .logoutSuccessHandler(new CustomLogourSuccessHandler());
        //필터 Username filter 앞에 등록
        http.addFilterBefore(loginProcessingFilter(), UsernamePasswordAuthenticationFilter.class);

        //this will allow frames withsame origin which is much more safe
        http.headers().frameOptions().disable();
    }


}
