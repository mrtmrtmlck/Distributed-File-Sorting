package sort;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class ExternalSort {

	public static void merge(int sortedSegmentCount, int segmentSize, File fileToBeMerged) throws IOException {
		// recursively merge segments until all segments are merged
		if (sortedSegmentCount > 1) {
			mergeSortedNumbers(sortedSegmentCount, segmentSize, fileToBeMerged);
			sortedSegmentCount++;
			merge(sortedSegmentCount / 2, segmentSize * 2, fileToBeMerged);
		}
	}

	private static void mergeSortedNumbers(int sortedSegmentCount, int segmentSize, File fileToBeMerged)
			throws IOException {

		File sortedFile = new File("merged.txt");

		BufferedReader reader = new BufferedReader(new FileReader(fileToBeMerged));
		BufferedWriter writer = new BufferedWriter(new FileWriter(sortedFile));

		for (int i = 0; i < sortedSegmentCount; i += 2) {
			mergeSegments(segmentSize, reader, writer);
		}

		reader.close();
		writer.close();

		String name = fileToBeMerged.getName();
		fileToBeMerged.delete();
		sortedFile.renameTo(new File(name));

	}

	private static void mergeSegments(int segmentSize, BufferedReader reader, BufferedWriter writer)
			throws IOException {

		File tempfile1 = new File("temp1.txt");
		File tempfile2 = new File("temp2.txt");
		ArrayList<File> tempFiles = new ArrayList<>();

		tempFiles.add(tempfile1);
		tempFiles.add(tempfile2);

		for (File tempFile : tempFiles) {
			BufferedWriter writerTemp = new BufferedWriter(new FileWriter(tempFile));
			writeSegmentToFile(segmentSize, reader, writerTemp);
			writerTemp.close();
		}

		mergeMultipleFiles(tempFiles, writer);

		for (File tempFile : tempFiles) {
			tempFile.delete();
		}
	}

	private static void writeSegmentToFile(int segmentSize, BufferedReader reader, BufferedWriter writerTemp)
			throws IOException {
		String temp;
		int j = 0;
		while (j < segmentSize && (temp = reader.readLine()) != null) {
			writerTemp.write(temp);
			writerTemp.newLine();
			j++;
		}
	}

	public static void mergeMultipleFiles(ArrayList<File> chunks, BufferedWriter writer) throws IOException {
		BufferedReader[] readers = new BufferedReader[chunks.size()];

		Integer[] numbersToBeCompared = new Integer[chunks.size()];
		for (int i = 0; i < chunks.size(); i++) {
			readers[i] = new BufferedReader(new FileReader(chunks.get(i)));
			updateNumbersToBeCompared(readers, numbersToBeCompared, i);
		}

		int minIndex = 0;
		int numberToWrite;
		while (!checkIfReadersDone(numbersToBeCompared)) {
			minIndex = getIndexOfMinValue(numbersToBeCompared);
			numberToWrite = numbersToBeCompared[minIndex];
			updateNumbersToBeCompared(readers, numbersToBeCompared, minIndex);

			writer.write(String.valueOf(numberToWrite));
			writer.newLine();
		}

		for (int i = 0; i < readers.length; i++) {
			readers[i].close();
		}
	}

	private static void updateNumbersToBeCompared(BufferedReader[] readers, Integer[] numbersToBeCompared, int i)
			throws IOException {
		String nextVal = readers[i].readLine();
		if (nextVal != null) {
			numbersToBeCompared[i] = Integer.valueOf(nextVal);
		} else {
			numbersToBeCompared[i] = null;
		}
	}

	private static boolean checkIfReadersDone(Integer[] arr) {
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] != null) {
				return false;
			}
		}

		return true;
	}

	private static int getIndexOfMinValue(Integer[] arr) {
		int index = 0;
		Integer min = arr[index];

		for (int i = 1; i < arr.length; i++) {
			if (arr[i] != null && (min == null || arr[i] < min)) {
				min = arr[i];
				index = i;
			}
		}

		return index;
	}
}