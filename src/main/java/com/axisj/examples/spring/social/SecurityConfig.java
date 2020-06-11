package com.axisj.examples.spring.social;

import com.axisj.examples.spring.social.security.CustomLoginFailureHandler;
import com.axisj.examples.spring.social.security.UserDetailsService;
import com.axisj.examples.spring.social.social.SocialUsersDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.embedded.EnableEmbeddedRedis;
import org.springframework.embedded.RedisServerPort;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.springframework.social.security.SocialUserDetailsService;
import org.springframework.social.security.SpringSocialConfigurer;

@EnableEmbeddedRedis
@EnableWebSecurity
//@EnableGlobalMethodSecurity(prePostEnabled = true)
//@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 864000)
@EnableRedisHttpSession
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private UserDetailsService userDetailsService;

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http
				.csrf().disable()
				.anonymous().and()
				.formLogin()
				.loginPage("/login")
				.defaultSuccessUrl("/", true)
				.loginProcessingUrl("/login/authenticate")
				.failureHandler(new CustomLoginFailureHandler())
			.and()
				.logout()
				.deleteCookies("SESSION")
				.logoutUrl("/logout")
				.logoutSuccessUrl("/login")
			.and()
				.authorizeRequests()
				.antMatchers(
						"/auth/**",
						"/login",
						"/error",
						"/signup",
						"/css/**",
						"/js/**"
				).permitAll()
				.antMatchers("/**").hasRole("USER")
			.and()
				.apply(new SpringSocialConfigurer());
	}

	@Bean
	public SocialUserDetailsService socialUsersDetailService() {
		return new SocialUsersDetailService(userDetailsService);
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Bean
	public DaoAuthenticationProvider daoAuthenticationProvider() {
		DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
		daoAuthenticationProvider.setUserDetailsService(userDetailsService);
		daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
		daoAuthenticationProvider.setHideUserNotFoundExceptions(false);
		return daoAuthenticationProvider;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(daoAuthenticationProvider());
	}

	@Override
	protected UserDetailsService userDetailsService() {
		return userDetailsService;
	}

//	@Bean
//	public JedisConnectionFactory connectionFactory( ) {
//		JedisConnectionFactory connection = new JedisConnectionFactory();
////		connection.setPort(port);
//		return connection;
//	}

	@Bean
	public JedisConnectionFactory connectionFactory(@RedisServerPort int port) {
		JedisConnectionFactory connection = new JedisConnectionFactory();
		connection.setPort(port);
		return connection;
	}

//	@Bean
//	public JedisConnectionFactory jedisConnectionFactory() {
//		System.out.println ("jedis11:"  );
//		JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory();
//		System.out.println ("jedis:" +jedisConnectionFactory  );
//		return jedisConnectionFactory;
//	}

	@Bean
	public CookieSerializer cookieSerializer()
	{
		DefaultCookieSerializer serializer = new DefaultCookieSerializer();
		// 위 레디스 처럼 serializer 의 각종 설정 가능.
		// tomcat context 로 설정한 쿠키 기능들도 여기서 설정가능.
		return serializer;
	}

}
