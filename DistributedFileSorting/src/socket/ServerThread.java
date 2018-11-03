package socket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.Arrays;

import sort.Quicksort;
import util.Constants;

public class ServerThread implements Runnable {

	private int port;

	public ServerThread(int port) {
		this.port = port;
	}

	@Override
	public void run() {
		try {
			DatagramSocket socket = new DatagramSocket(port);
			try {
				while (true) {
					startServer(socket);
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (SocketException e1) {
			e1.printStackTrace();
		}

	}

	private void startServer(DatagramSocket socket) throws IOException {
		byte[] buf = new byte[Constants.BUFFER_SIZE];
		DatagramPacket packet = new DatagramPacket(buf, buf.length);
		socket.receive(packet);

		// resize buffer if packet length is less than max buffer size 
		if (packet.getLength() < buf.length) {
			buf = Arrays.copyOf(buf, packet.getLength());
		}

		buf = sortData(buf);

		packet = new DatagramPacket(buf, buf.length, packet.getAddress(), packet.getPort());
		socket.send(packet);
	}

	private byte[] sortData(byte[] buf) {
		// Convert byte array to int array
		IntBuffer intBuf = ByteBuffer.wrap(buf).order(ByteOrder.BIG_ENDIAN).asIntBuffer();
		int[] intArr = new int[intBuf.remaining()];
		intBuf.get(intArr);

		Quicksort.sort(intArr);

		// Convert int array to byte array
		ByteBuffer byteBuffer = ByteBuffer.allocate(intArr.length * 4);
		IntBuffer intBuffer = byteBuffer.asIntBuffer();
		intBuffer.put(intArr);

		return byteBuffer.array();
	}
}
