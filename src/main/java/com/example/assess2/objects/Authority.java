package com.example.assess2.objects;

import org.springframework.security.core.GrantedAuthority;

public class Authority implements GrantedAuthority {

	private static final long serialVersionUID = 1L;
	private String auth;

	public Authority(String string) {
		this.auth = string;
	}

	@Override
	public String getAuthority() {
		return auth;
	}

	public void setAuth(String auth) {
		this.auth = auth;
	}

}
