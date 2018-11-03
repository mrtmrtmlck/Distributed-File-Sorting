package socket;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramSocket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import sort.ExternalSort;
import util.Constants;
import util.FileCreator;

public class Client {

	public static void main(String[] args) throws IOException {
		System.out.println("Sorting started... Please wait... Time: " + getCurrentTime());

		File file = FileCreator.CreateUnsortedFile();

		ExecutorService executor = Executors.newFixedThreadPool(Constants.PORT_LIST.length);

		DatagramSocket socket = new DatagramSocket();
		socket.setSoTimeout(Constants.MAX_WAIT_TIME);

		DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
		HashMap<File, Integer> fileChunkCountPair = new HashMap<>();

		int fileLength = (int) file.length();
		int partSize = fileLength / Constants.PORT_LIST.length;
		int remainingPart = fileLength - (partSize * Constants.PORT_LIST.length);
		byte[] buffer = new byte[partSize];
		int bytesAmount = dis.read(buffer);
		int index = 0;
		Runnable client;
		while (bytesAmount > 0) {
			client = new ClientThread(buffer, socket, Constants.PORT_LIST[index], fileChunkCountPair);
			executor.execute(client);
			index++;

			if (remainingPart > 0 && index == Constants.PORT_LIST.length - 1) {
				buffer = new byte[partSize + remainingPart];
			}

			bytesAmount = dis.read(buffer);
		}

		executor.shutdown();

		while (!executor.isTerminated()) {
		}

		dis.close();
		socket.close();

		file.delete(); // delete unnecessary file

		System.out.println("Merging in progress... Please wait... Time: " + getCurrentTime());

		mergePartitions(fileChunkCountPair);

		System.out.println("FINISH!  Time: " + getCurrentTime());
	}

	private static void mergePartitions(HashMap<File, Integer> fileChunkCountPair) throws IOException {
		ArrayList<File> files = new ArrayList<>();

		// merge files individually
		fileChunkCountPair.forEach((file, chunkCount) -> {
			try {
				ExternalSort.merge(chunkCount, Constants.BUFFER_SIZE / 4, file);
			} catch (IOException e) {
				e.printStackTrace();
			}
			files.add(file);
		});

		File finalFile = new File("SortedFile.txt");
		BufferedWriter writer = new BufferedWriter(new FileWriter(finalFile));
		// merge all files into one file
		ExternalSort.mergeMultipleFiles(files, writer);
		writer.close();

		for (File file : files) {
			// delete unnecessary files
			file.delete();
		}

		displayFile(finalFile);
	}

	private static String getCurrentTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

		return sdf.format(Calendar.getInstance().getTime());
	}

	private static void displayFile(File file) throws IOException {
		System.out.println("First " + Constants.DISPLAY_SIZE + " items in Sorted File: ");
		BufferedReader reader = new BufferedReader(new FileReader(file));

		int counter = 0;
		while (counter != Constants.DISPLAY_SIZE) {
			System.out.println(reader.readLine());
			counter++;
		}

		reader.close();
	}
}
