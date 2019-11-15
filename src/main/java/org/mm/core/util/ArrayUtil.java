package org.mm.core.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ArrayUtil {
	
	public static Integer[] upsetNumbers(int numbers) {
		List<Integer> numberArr = new ArrayList<Integer>();
		for (int i = 0; i < numbers; i++) {
			numberArr.add(i);
		}
		return upsetNumbers(numberArr);
	}
	
	public static Integer[] upsetNumbers(Integer[] numbers) {
		List<Integer> numberArr = Arrays.asList(numbers);
		return upsetNumbers(numberArr);
	}
	
	public static Integer[] upsetNumbers(List<Integer> numbers) {
		Collections.shuffle(numbers);
		Integer[] newNumbers = new Integer[numbers.size()];
		newNumbers = numbers.toArray(newNumbers);
		return newNumbers;
	}

}
