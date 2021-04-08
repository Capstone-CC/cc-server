package com.cau.cc.security.config;
import com.cau.cc.cors.CORSFilter;
import com.cau.cc.security.filter.LoginProcessingFilter;
import com.cau.cc.security.handler.AjaxAuthenticationFailureHandler;
import com.cau.cc.security.handler.AjaxAuthenticationSuccessHandler;
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


//    /**
//     * cors
//     * @param
//     * @throws Exception
//     */
//    // CORS 허용 적용 (Global)
//    @Bean
//    public CorsConfigurationSource corsConfigurationSource(){
//        System.out.println("----------------cors config-----------------------");
//
//        CorsConfiguration configuration = new CorsConfiguration();
//
//        configuration.addAllowedOriginPattern("*");
//        configuration.addAllowedOriginPattern("http://cauconnect.com");
//        configuration.addAllowedOriginPattern("http://3.36.250.224:3030");
//        configuration.setAllowedMethods(Arrays.asList("*"));
//        configuration.setAllowedHeaders(Arrays.asList("*"));
//        configuration.setAllowCredentials(true);
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration);
//
//        System.out.println("----------------cors config end-----------------------");
//        return source;
//    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
//                .cors().configurationSource(corsConfigurationSource).and()
//                .cors().and()
                .authorizeRequests()
//                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
//                .antMatchers(HttpMethod.OPTIONS, "/api/**").permitAll() //OPTIONS 메소드 허락
                .antMatchers("/","/api/register","/api/login","/h2-console/**","/api/email","/api/verify","/api/matching/**").permitAll();
        //필터 Username filter 앞에 등록
        http.addFilterBefore(loginProcessingFilter(), UsernamePasswordAuthenticationFilter.class);
        //http.addFilterBefore(corsFilter(), SecurityContextPersistenceFilter.class);
        // thi s will ignore only h2-console csrf, spring security 4+
        //        // http.csrf().ignoringAntMatchers("/h2-console/**");
        //        //this will allow frames withsame origin which is much more safe
        http.headers().frameOptions().disable();

    }


}
