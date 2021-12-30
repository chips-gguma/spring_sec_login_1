package com.sp.fc.web.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;

@EnableWebSecurity(debug = true)
@EnableGlobalMethodSecurity(prePostEnabled = true)  // 설정되어 있던 ROLE대로 작동하도록 함. 관리자 페이지 접근 불가
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .inMemoryAuthentication()
                .withUser(
                        User.withDefaultPasswordEncoder()
                                .username("user1")
                                .password("1111")
                                .roles("USER")
                ).withUser(
                        User.withDefaultPasswordEncoder()
                                .username("admin")
                                .password("2222")
                                .roles("ADMIN")
                );

    }

    @Bean
    RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy("ROLE_ADMIN > ROLE_USER");   // 계층 구조를 admin이 user보다 높게 설정하여 관리자로 로그인하면 user 페이지에 접근 가능하도록 함
        return roleHierarchy;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests(request->{
                    request
                            .antMatchers("/").permitAll() // root 페이지는 모든 사람들의 권한 허용
                            .anyRequest().authenticated() // 요청은 인증 받은 사람만 (보안)
                    ;
                })
                // request에 대한 filter 추가 (명시하지 않으면 DefaultLoginPageGenerationFilter)
                .formLogin(
                        login->login.loginPage("/login")
                                .permitAll()
                                .defaultSuccessUrl("/", false)
                                .failureUrl("/login-error")
                )
                .logout(logout->logout.logoutSuccessUrl("/"))   // 로그아웃해도 메인페이지에 남아 있음
                .exceptionHandling(exception->exception.accessDeniedPage("/access-denied")) // 에러 발생시 접근 권한 거부 페이지 이동
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
