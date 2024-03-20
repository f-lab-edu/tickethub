package flab.tickethub.support.config

import flab.tickethub.support.constant.ApiEndpoint
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain

@EnableWebSecurity
@Configuration
class SecurityConfig {

    @Bean
    fun securityFilterChain(httpSecurity: HttpSecurity): SecurityFilterChain {
        httpSecurity
            .csrf { it.disable() }
            .cors { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }

        httpSecurity
            .httpBasic { it.disable() }
            .formLogin { it.disable() }
            .logout { it.disable() }

        httpSecurity
            .authorizeHttpRequests {
                it
                    .requestMatchers(
                        HttpMethod.POST,
                        ApiEndpoint.MEMBER,
                        ApiEndpoint.MEMBER + ApiEndpoint.LOGIN_ENDPOINT
                    )
                    .permitAll()
                it.anyRequest().authenticated()
            }

        return httpSecurity.build()
    }

    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()

}