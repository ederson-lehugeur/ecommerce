package br.com.ecommerce.services;

import org.springframework.security.core.context.SecurityContextHolder;

import br.com.ecommerce.security.UserDetailsSpringSecurity;

public class UserService {

	public static UserDetailsSpringSecurity authenticated() {
		try {
			return (UserDetailsSpringSecurity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		} catch (Exception e) {
			return null;
		}
	}
}
