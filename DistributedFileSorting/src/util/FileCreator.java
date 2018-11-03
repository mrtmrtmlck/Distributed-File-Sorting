package util;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class FileCreator {

	public static File CreateUnsortedFile() throws IOException {
		File file = new File("OriginalFile(Binary Format).txt");
		DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));

		File f2 = new File("OriginalFile.txt");
		BufferedWriter bw = new BufferedWriter(new FileWriter(f2));

		Random rand = new Random();

		int randInt;
		int count = 0;
		while (count != Constants.NUMBER_OF_RECORDS) {
			randInt = rand.nextInt(Constants.NUMBER_OF_RECORDS) + 1;

			dos.writeInt(randInt);

			bw.write(String.valueOf(randInt));
			bw.newLine();

			count++;
		}

		dos.close();
		bw.close();

		return file;
	}
}
