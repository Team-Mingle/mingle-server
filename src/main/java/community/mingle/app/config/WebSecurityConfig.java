package community.mingle.app.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.cors.CorsConfigurationSource;

/**
 * 스프링 시큐리티 추가로 401 error 난거 해결방법 (이해 필요)
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final AuthUserService authUserService;
    private final AuthSuccessHandler successHandler;
    private final AuthFailureHandler failureHandler;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
//        http.csrf().disable();
        http
                .cors()
                .configurationSource(corsConfigurationSource())
                .and()
                .exceptionHandling()
//                .authenticationEntryPoint(AuthenticationEntryPoint)
                .and()
                .csrf().disable()
                .headers().frameOptions().disable()
                .and()
                .formLogin().disable()
                .httpBasic().disable()
                .authorize


    }

    private CorsConfigurationSource corsConfigurationSource() {
    }
}


RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter
{

- Cors()
.configurationSource(corsConfigurationSource())
        . and ()
.exceptionHandling()
.authenticationEntryPoint (new RestAuthenticationEntryPoint())
        .and ()
. csrf ( ).disable( )
•headers (). frame@ptions () .disable()
