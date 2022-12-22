package md2html;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class Md2Html {
    public static void main(String[] args) {
        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(
                    new FileInputStream(args[0]), StandardCharsets.UTF_8));
            String line = input.readLine();
            try (input) {
                try (BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
                        new FileOutputStream(args[1]), StandardCharsets.UTF_8))) {
                    while (true) {
                        if (line != null) {
                            StringBuilder str = new StringBuilder();
                            while (line.isEmpty()) {
                                line = input.readLine();
                            }
                            while (true) {
                                if (line == null || line.isEmpty()) {
                                    break;
                                } else {
                                    if (str.length() > 0) {
                                        str.append('\n');
                                    }
                                    str.append(line);
                                    line = input.readLine();
                                }
                            }
                            if (str.length() <= 0) {
                                continue;
                            }
                            String s = str.toString();
                            HtmlParser result = new HtmlParser(s);
                            String res = result.toString();
                            out.write(res);
                            out.newLine();
                        } else {
                            break;
                        }
                    }
                }
            } catch (InvalidObjectException e) {
                System.out.println("Wrong arguments" + e.getMessage());
            }
        } catch (FileNotFoundException e) {
            System.out.print("File not found: " + e.getMessage());
        } catch (UnsupportedEncodingException e) {
            System.out.println("Something went wrong with encoding: " + e.getMessage());
        }catch (IOException e) {
            System.out.print("IO error: " + e.getMessage());
        }
    }
}