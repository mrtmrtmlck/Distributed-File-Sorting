package socket;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import util.Constants;

public class Server {
	public static void main(String[] args) throws IOException {
		System.out.println("Server started...");
		Runnable server;
		ExecutorService executor = Executors.newFixedThreadPool(Constants.PORT_LIST.length);
		for (int port : Constants.PORT_LIST) {
			server = new ServerThread(port);
			executor.execute(server);
		}
	}
}
