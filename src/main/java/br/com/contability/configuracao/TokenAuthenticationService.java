package br.com.contability.configuracao;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class TokenAuthenticationService {
	
	// EXPIRATION_TIME = 10 dias
		static final long EXPIRATION_TIME = 860_000_000;
		static final String SECRET = "MySecret";
		static final String TOKEN_PREFIX = "Bearer";
		static final String HEADER_STRING = "Authorization";
		
		static void addAuthentication(HttpServletResponse response, String username) {
			String JWT = Jwts.builder()
					.setSubject(username)
					.setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
					.signWith(SignatureAlgorithm.HS512, SECRET)
					.compact();
			
			response.addHeader(HEADER_STRING, TOKEN_PREFIX + " " + JWT);
			
			String tokenClient = "{\"token\":\""+JWT+"\"}";

			try {
				response.getWriter().write(tokenClient);
				response.getWriter().flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		static Authentication getAuthentication(HttpServletRequest request) {
			String token = request.getHeader(HEADER_STRING);
			
			System.out.println("Token Problematico: "+token);
			
			if (token != null) {
				// faz parse do token
				String user = Jwts.parser()
						.setSigningKey(SECRET)
						.parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
						.getBody()
						.getSubject();
				
				if (user != null) {
					return new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());
				}
			}
			return null;
		}

}
