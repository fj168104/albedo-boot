package com.albedo.java.common.config;

import com.albedo.java.common.base.BaseInit;
import com.albedo.java.common.security.CustomizeAccessDecisionManager;
import com.albedo.java.common.security.Http401UnauthorizedEntryPoint;
import com.albedo.java.common.security.SecurityConstants;
import com.albedo.java.common.security.SecurityUtil;
import com.albedo.java.common.security.jwt.JWTConfigurer;
import com.albedo.java.common.security.jwt.TokenProvider;
import com.albedo.java.common.security.service.InvocationSecurityMetadataSourceService;
import com.albedo.java.util.JedisUtil;
import com.albedo.java.util.domain.GlobalJedis;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.data.repository.query.SecurityEvaluationContextExtension;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@BaseInit
@ComponentScan(basePackages = { "com.albedo.java.*"})
public class SecurityAutoConfiguration extends WebSecurityConfigurerAdapter {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final CustomizeAccessDecisionManager customizeAccessDecisionManager;
    private final InvocationSecurityMetadataSourceService invocationSecurityMetadataSourceService;
    private final AlbedoProperties albedoProperties;
    private final UserDetailsService userDetailsService;
    private final TokenProvider tokenProvider;
    private final CorsFilter corsFilter;

    public SecurityAutoConfiguration(AuthenticationManagerBuilder authenticationManagerBuilder, CustomizeAccessDecisionManager customizeAccessDecisionManager,
                                     InvocationSecurityMetadataSourceService invocationSecurityMetadataSourceService,
                                     AlbedoProperties albedoProperties,
                                     UserDetailsService userDetailsService,
                                     TokenProvider tokenProvider, CorsFilter corsFilter) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.customizeAccessDecisionManager = customizeAccessDecisionManager;
        this.invocationSecurityMetadataSourceService = invocationSecurityMetadataSourceService;
        this.albedoProperties = albedoProperties;
        this.userDetailsService = userDetailsService;
        this.tokenProvider = tokenProvider;
        this.corsFilter = corsFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @PostConstruct
    public void init() {
        try {
            authenticationManagerBuilder
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
        } catch (Exception e) {
            throw new BeanInitializationException("Security configuration failed", e);
        }
    }
    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
            .antMatchers(HttpMethod.OPTIONS, "/**")
            .antMatchers("/i18n/**")
            .antMatchers("/content/**")
            .antMatchers("/statics/**")
            .antMatchers("/assets/**/*.{js,html}")
            .antMatchers("/test/**");
    }

    @Bean
    @ConditionalOnMissingBean
    public AuthenticationEntryPoint authenticationEntryPoint(){
        return new Http401UnauthorizedEntryPoint(albedoProperties);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        List<String> authorizes = albedoProperties.getSecurity().getAuthorizes();
        authorizes.add(albedoProperties.getAdminPath("/**"));
        authorizes.addAll(Lists.newArrayList(SecurityConstants.authorize));
        SecurityConstants.authorize = new String[authorizes.size()];
        authorizes.toArray(SecurityConstants.authorize);

        String adminPath = albedoProperties.getAdminPath();

        String[] permissAll = new String[SecurityConstants.authorizePermitAll.length+SecurityConstants.authorizeAdminPermitAll.length];

        for (int i = 0; i < SecurityConstants.authorizePermitAll.length; i++) {
            permissAll[i] = SecurityConstants.authorizePermitAll[i];
        }
        for (int i = SecurityConstants.authorizePermitAll.length,j=0; i < permissAll.length; i++,j++) {
            permissAll[i] = adminPath+SecurityConstants.authorizeAdminPermitAll[j];
        }

        http.addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint())
            .and()
            .csrf()
            .disable()
            .headers()
            .frameOptions()
            .disable()
            .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
            .antMatchers(albedoProperties.getAdminPath(SecurityConstants.loginUrl)).permitAll()
            .antMatchers(albedoProperties.getAdminPath(SecurityConstants.authLogin)).permitAll()
            .antMatchers(albedoProperties.getAdminPath(SecurityConstants.logoutUrl)).permitAll()
            .antMatchers(permissAll).permitAll()
            .antMatchers(SecurityConstants.authorize).authenticated()
            .withObjectPostProcessor(new ObjectPostProcessor<FilterSecurityInterceptor>() {
                @Override
                public <O extends FilterSecurityInterceptor> O postProcess(O fsi) {
                    fsi.setSecurityMetadataSource(securityMetadataSource());
                    return fsi;
                }
            }).accessDecisionManager(customizeAccessDecisionManager)
            .and()
            .apply(securityConfigurerAdapter());

    }

    @Bean
    public FilterInvocationSecurityMetadataSource securityMetadataSource() {
        return invocationSecurityMetadataSourceService;
    }

    private JWTConfigurer securityConfigurerAdapter() {
        return new JWTConfigurer(tokenProvider, albedoProperties);
    }

    @Bean
    public SecurityEvaluationContextExtension securityEvaluationContextExtension() {
        return new SecurityEvaluationContextExtension();
    }


    public void afterPropertiesSet() {
        SecurityUtil.clearUserJedisCache();
        JedisUtil.removeSys(GlobalJedis.RESOURCE_MODULE_DATA_MAP);
        invocationSecurityMetadataSourceService.getResourceMap();
    }

}
