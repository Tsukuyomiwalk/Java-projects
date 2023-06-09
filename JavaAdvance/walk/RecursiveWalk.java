package info.kgeorgiy.ja.latanov.walk;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
/**
 * @author created by Daniil Latanov
 */
public class RecursiveWalk {

    private static final String ZEROES = "0".repeat(64) + " ";

    public static void main(String[] args) {
        if (args == null || args.length != 2 || args[0] == null || args[1] == null) {
            System.err.println("Invalid arguments");
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(args[0]))) {
            try {
                Files.createDirectories(Path.of(args[1]).getParent());
            } catch (Exception e) {
                System.err.println("NULL" + e.getMessage());
            }
            try (BufferedWriter writer = Files.newBufferedWriter(Path.of(args[1]))) {
                String line;
                File file;
                while ((line = reader.readLine()) != null) {
                    try {
                        file = new File(line);
                        recur(writer, file, getDigest());
                    } catch (InvalidPathException | IOException  e) {
                        writer.write(ZEROES + line + System.lineSeparator());
                    }
                }
            } catch (IOException | InvalidPathException e) {
                System.err.println("IO Exception" + e.getMessage());
            }
        } catch (IOException e) {
            System.err.println("IO Exception" + e.getMessage());
        }
    }

    private static MessageDigest getDigest() {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            System.err.println();
        }
        return digest;
    }

    private static void recur(BufferedWriter writer, File line, MessageDigest digest) throws IOException, InvalidPathException {
        byte[] arr = new byte[1024];
        if (line.isDirectory()) {
            File[] files = line.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        recur(writer, file, digest);
                    } else {
                        hashCalc(file, digest, writer, arr);
                    }
                }
            } else {
                writer.write(ZEROES + line.getPath() + System.lineSeparator());
            }
        } else {
            hashCalc(line, digest, writer, arr);
        }
    }

    private static void hashCalc(File line, MessageDigest digest, BufferedWriter writer, byte[] arr) throws IOException {
        try {
            BufferedInputStream dis = new BufferedInputStream(new FileInputStream(line));
            int c;
            while ((c = dis.read(arr)) >= 0) {
                Objects.requireNonNull(digest).update(arr, 0, c);
            }
            try {
                writer.write(getHashToString(digest) + " " + line.toPath() + System.lineSeparator());
            } catch (IOException | InvalidPathException e) {
                writer.write(ZEROES + line.toPath() + System.lineSeparator());
            }
        } catch (FileNotFoundException | InvalidPathException e) {
            writer.write(ZEROES + line.toPath() + System.lineSeparator());
        }
    }

    private static StringBuilder getHashToString(MessageDigest digest) {
        byte[] out = Objects.requireNonNull(digest).digest();
        StringBuilder sb = new StringBuilder();
        for (byte o : out) {
            sb.append(String.format("%02x", o));
        }
        return sb;
    }
}
