package org.mm.core.util;

import java.util.UUID;

public class ClientKeyUtil {

	public static final String apiKey() {
		return randomUUID();
	}
	
	public static final String webKey() {
		return randomUUID();
	}
	
	public static final String captchaKey() {
		return randomUUID();
	}
	
	public static final String randomUUID() {
		String uuid = UUID.randomUUID().toString();
		uuid = uuid.replace("-", "");
		return uuid;
	}

}
