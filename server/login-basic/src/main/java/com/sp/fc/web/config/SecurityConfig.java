package com.sp.fc.web.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity(debug = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests(request->{
                    request
                            .antMatchers("/").permitAll() // root 페이지는 모든 사람들의 권한 허용
                            .anyRequest().authenticated() // 요청은 인증 받은 사람만 (보안)
                    ;
                })
                ;
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring() // 웹 리소스에 대해서는 시큐리티 filter가 적용되지 않도록
                .requestMatchers(
                        PathRequest.toStaticResources().atCommonLocations() // 디버깅을 걸고 보면 css, js 파일 내려받는 것 확인 가능
                )
                ;
    }
}
