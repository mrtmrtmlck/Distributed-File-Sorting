package test;

import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import sort.ExternalSort;

class ExternalSortTest {

	@Test
	void testMergeMultipleFiles() throws IOException {
		int[][] sortedArrays = { { 3, 4, 5, 6, 21, 25, 33 }, { 1, 5, 7, 9, 13, 14 }, { 4, 8, 11, 12 } };

		ArrayList<File> files = new ArrayList<>();

		int counter = 1;
		for (int[] sortedArray : sortedArrays) {
			File file = new File("sortedFile" + counter + ".txt");
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));

			for (int i : sortedArray) {
				bw.write(String.valueOf(i));
				bw.newLine();
			}

			files.add(file);

			bw.close();

			counter++;
		}

		File mergedFile = new File("testMergeMultipleFiles.txt");
		BufferedWriter bufWriter = new BufferedWriter(new FileWriter(mergedFile));

		ExternalSort.mergeMultipleFiles(files, bufWriter);

		bufWriter.close();

		if (!validate(mergedFile)) {
			fail("Failed: testMergeMultipleFiles");
		}
	}

	@Test
	void testMerge() throws IOException {
		int sortedSegmentCount = 4;
		int segmentSize = 3;

		// there are 4 (sortedSegmentCount) segments in the array
		// all segments are sorted individually
		// there are 3 (segmentSize) numbers in each segment, except the last segment
		int[] array = { 3, 7, 10, 1, 2, 15, 2, 8, 11, 16 };

		File file = new File("testMerge.txt");
		BufferedWriter bw = new BufferedWriter(new FileWriter(file));

		for (int i : array) {
			bw.write(String.valueOf(i));
			bw.newLine();
		}

		bw.close();

		ExternalSort.merge(sortedSegmentCount, segmentSize, file);

		if (!validate(file)) {
			fail("Failed: testMerge");
		}
	}

	private boolean validate(File file) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		ArrayList<Integer> numbers = new ArrayList<>();
		String num;

		while ((num = reader.readLine()) != null) {
			numbers.add(Integer.parseInt(num));
		}

		reader.close();

		for (int i = 0; i < numbers.size() - 1; i++) {
			if (numbers.get(i) > numbers.get(i + 1)) {
				return false;
			}
		}

		return true;
	}
}
