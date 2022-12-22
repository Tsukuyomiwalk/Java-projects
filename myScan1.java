
import java.io.*;
import java.nio.charset.StandardCharsets;

public class myScan1 {
    private final Reader readliner;
    private String bufferLine = "";
    private int currentCh = 0;
    private final char[] BufferCh = new char[1024];
    private String nextLine = "";
    private boolean Closed = false;
    private int lengthBufferCh = 0;
    private String nextAnyInt = "";
    private int linecounter = 0;

    public myScan1(Reader readliner) {
        this.readliner = readliner;
    }

    public static String clear(String str) {
        return "";
    }

    private boolean readyToRead() throws IOException {
        if (!hasNext()) {
            return false;
        } else {
            lengthBufferCh = readliner.read(BufferCh);
            return true;
        }
    }

    public myScan1(String str) throws IOException {
        readliner = new InputStreamReader(new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
        readyToRead();
    }

    public myScan1(InputStream stream) throws IOException {
        readliner = new InputStreamReader(stream, StandardCharsets.UTF_8);
        readyToRead();
    }

    public myScan1(File file) throws IOException {
        readliner = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
        readyToRead();
    }

    public String next() throws IOException {
        if (Closed) {
            throw new IllegalStateException("The scanner has already closed, 'Finally' shouldn't be there)");
        }
        StringBuilder str = new StringBuilder();
        int counter = 0;
        while (hasNext()) {
            char ch = nextChar();
            if (!characterCheckerStr(ch)) {
                counter = counter + 1;
            } else {
                if (counter > 0) {
                    currentCh--;
                    break;
                }
                if (!nextLinechecker(ch)) {
                    linecounter = linecounter + 1;
                }
            }
            if (!characterCheckerStr(ch)) {
                str.append(ch);
            }
        }
        return str.toString();
    }

    public char nextChar() throws IOException {
        int chInBuff = 0;
        if (currentCh < BufferCh.length) {
            chInBuff = BufferCh[currentCh];
            currentCh = currentCh + 1;
        } else {
            currentCh = Math.abs(BufferCh.length - currentCh);
            if (!readyToRead()) {
                chInBuff = currentCh;
            } else {
                if (chInBuff != currentCh) {
                    return (char) chInBuff;
                }
            }
            chInBuff = BufferCh[currentCh];
            currentCh = currentCh + 1;
        }
        return (char) chInBuff;
    }

    public boolean hasNext() throws IOException {
        boolean trigger;
        if (currentCh < lengthBufferCh) {
            trigger = true;
            return trigger;
        } else {
            trigger = readliner.ready();
            return trigger;
        }
    }

    public boolean nextLinechecker(char ch) throws IOException {
        int count = 0;
        if (ch == '\n') {
            count = count + 1;
            return false;
        } else {
            return true;
        }
    }

    public boolean characterCheckerStr(char ch) throws IOException {
        boolean cons = true;
        int count = 0;
        if (!Character.isWhitespace(ch)) {
            if (!(Character.isLetter(ch) || Character.getType(ch) == Character.DASH_PUNCTUATION ||
                    ch == '\'') || Character.isDigit(ch)) {
                count = count + 1;
            } else {
                cons = false;
            }
        }
        return cons;
    }

    public String nextString() throws IOException {
        if (Closed) {
            throw new IllegalStateException("The scanner has already closed, 'Finally' shouldn't be there)");
        }
        StringBuilder str = new StringBuilder();
        int counter = 0;
        while (true) {
            if (!hasNext()) {
                break;
            } else {
                char ch = nextChar();
                if (characterCheckerStr(ch)) {
                    if (counter > 0) {
                        currentCh--;
                        break;
                    }
                } else {
                    str.append(ch);
                    counter = counter + 1;
                }
                if (!nextLinechecker(ch)) {
                    linecounter = linecounter + 1;
                }
            }
        }
        return str.toString();
    }

    public int getLinecounter() throws IOException {
        return linecounter;
    }

    public int nextInt() throws IOException {
        if (Closed) {
            throw new IllegalStateException("The scanner has already closed, 'Finally' shouldn't be there)");
        }
        if (hasNextInt()) {
            bufferLine = nextAnyInt;
            nextAnyInt = clear(nextAnyInt);
            return Integer.parseInt(bufferLine);
        } else {
            throw new IOException("Can't find any Int");
        }
    }

    public boolean hasNextInt() throws IOException {
        boolean cons = true;
        if (!nextAnyInt.isEmpty()) {
            return cons;
        }
        StringBuilder str = new StringBuilder();
        int counter = 0;
        while (true) {
            if (hasNext()) {
                char ch = nextChar();
                if (!Character.isWhitespace(ch)) {
                    counter = counter + 1;
                } else if (counter > 0) {

                    break;
                }
                if (!Character.isWhitespace(ch)) {
                    str.append(ch);
                }
            } else {
                break;
            }
        }
        nextAnyInt = str.toString();
        try {
            Integer.parseInt(nextAnyInt);
        } catch (NumberFormatException e) {
            cons = false;
        }
        return cons;
    }

    public boolean hasNextLine() throws IOException {
        if (!nextLine.isEmpty()) {
            return true;
        }
        boolean isEmpty = false;
        StringBuilder lineStr = new StringBuilder();
        while (true) {
            char ch = nextChar();
            if (nextLinechecker(ch) && hasNext()) {
                if (nextLinechecker(ch)) {
                    lineStr.append(ch);
                }
                if (characterCheckerStr(ch)) {
                    isEmpty = true;
                }
                if (!nextLinechecker(ch) && !isEmpty) {
                    lineStr.append(ch);
                }
            } else {
                break;
            }
        }
        if (lineStr.toString().isEmpty()) {
            return false;
        }
        nextLine = lineStr.toString();
        return true;
    }

    public String nextLine() throws IOException {
        if (Closed) {
            throw new IllegalStateException("The scanner has already closed, 'Finally' shouldn't be there)");
        }
        if (hasNextLine()) {
            bufferLine = nextLine;
            nextLine = clear(nextLine);
            return bufferLine;
        } else throw new IOException("Hasn't got anything to read");
    }

    public void close() {
        if (!Closed) {
            Closed = true;
            if (readliner != null) {
                try {
                    readliner.close();
                } catch (IOException e) {
                    System.out.println("The scanner has already closed, 'Finally' shouldn't be there)");
                }
            }
        }
    }
}
