package socket;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import util.Constants;

public class ClientThread implements Runnable {

	private DatagramSocket socket;
	private int port;
	private byte[] data;
	private HashMap<File, Integer> fileChunkCountPair;

	public ClientThread(byte[] data, DatagramSocket socket, int port, HashMap<File, Integer> fileChunkCountPair) {
		this.data = data;
		this.socket = socket;
		this.port = port;
		this.fileChunkCountPair = fileChunkCountPair;
	}

	@Override
	public void run() {
		try {
			startClient();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void startClient() throws IOException {
		File file = new File(Thread.currentThread().getName() + ".txt");
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));

		DataInputStream dis = new DataInputStream(new BufferedInputStream(new ByteArrayInputStream(data)));
		byte[] chunk = new byte[Constants.BUFFER_SIZE];
		int bytesAmount = dis.read(chunk);
		int chunkNumber = 0;
		while (bytesAmount > 0) {
			sendReceive(chunk, writer);

			int remaining = dis.available();
			if (remaining > 0 && remaining < chunk.length) {
				chunk = new byte[remaining];
			}

			bytesAmount = dis.read(chunk);
			chunkNumber++;
		}

		dis.close();
		writer.close();

		fileChunkCountPair.put(file, chunkNumber);
	}

	private void sendReceive(byte[] chunk, BufferedWriter writer) throws IOException {
		InetAddress address = InetAddress.getLocalHost();
		boolean isDone = false;
		int counter = 0;
		ArrayList<Integer> usedPorts = new ArrayList<>();
		while (!isDone) {
			DatagramPacket packet;
			if (counter == Constants.MAX_ATTEMPT) {
				if (!usedPorts.contains(Integer.valueOf(port))) {
					usedPorts.add(port);
				}

				// if current server is unavailable try another server
				port = getAnotherPort(port, usedPorts);

				counter = 0;
			}

			packet = new DatagramPacket(chunk, chunk.length, address, port);
			socket.send(packet);

			byte[] receivedData = new byte[chunk.length];
			packet = new DatagramPacket(receivedData, receivedData.length);

			try {
				socket.receive(packet);
				writeSortedChunksToFile(receivedData, writer);
				isDone = true;
			} catch (SocketTimeoutException e) {
				isDone = false;
				counter++;
			}
		}
	}

	private void writeSortedChunksToFile(byte[] receivedData, BufferedWriter writer) throws IOException {
		// Convert byte array to int array
		IntBuffer intBuf = ByteBuffer.wrap(receivedData).order(ByteOrder.BIG_ENDIAN).asIntBuffer();
		int[] intArr = new int[intBuf.remaining()];
		intBuf.get(intArr);

		for (int i : intArr) {
			writer.write(String.valueOf(i));
			writer.newLine();
		}
	}

	private int getAnotherPort(int currentPort, ArrayList<Integer> usedPorts) {
		for (int port : Constants.PORT_LIST) {
			if (!usedPorts.contains(Integer.valueOf(port))) {
				return port;
			}
		}

		return currentPort;
	}
}
