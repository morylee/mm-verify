package org.mm.core.util;

import java.util.Random;

public class RandomUtil {

	public static int randomInt(int origin, int bound) {
		Random random = new Random();
		return random.nextInt(bound - origin) + origin;
	}

}
