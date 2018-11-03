package test;

import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import sort.Quicksort;

class QuicksortTest {

	@Test
	public void testQuickSort() {
		int[] numbers = { 6, 1, 3, 5, 8, 12, 13, 9, 2 };

		Quicksort.sort(numbers);

		if (!validate(numbers)) {
			fail("Failed");
		}
	}

	private boolean validate(int[] numbers) {
		for (int i = 0; i < numbers.length - 1; i++) {
			if (numbers[i] > numbers[i + 1]) {
				return false;
			}
		}
		return true;
	}
}
