package md2html;

import java.util.Map;
import java.util.Objects;

public class HtmlParser {
    Map<String, String> htmlSet = Map.of(
            "%", "var","*", "em","_", "em", ">", "&gt;",
            "__", "strong","**",
            "strong", "--", "s","`",
            "code", "<",
            "&lt;", "&", "&amp;");

    protected final StringBuilder res;
    protected int height;

    public HtmlParser(String str) {
        res = new StringBuilder();
        int headlvl = getHead(str);
        height = 0;
        if (headlvl > 0) {
            Head(str, headlvl);
        } else {
            Paragraph(str);
        }
    }

    private void Head(String str, int headerlvl) {
        height = headerlvl + 1;
        res.append("<h");
        res.append(headerlvl);
        res.append(">");
        mergeParse(res, str, "][");
        res.append("</h");
        res.append(headerlvl);
        res.append(">");
    }

    private void Paragraph(String str) {
        res.append("<p>");
        mergeParse(res, str, "][");
        res.append("</p>");
    }


    private int getHead(String str) {
        int HeadLvl = 0;
        while (str.charAt(HeadLvl) == '#') {
            HeadLvl++;
        }
        if (Character.isWhitespace(str.charAt(HeadLvl))) {
            return HeadLvl;
        } else {
            return 0;
        }
    }

    public String toString() {
        return res.toString();
    }

    private void htmlConventorCl(StringBuilder res, String str) {
        res.append("<");
        res.append(str);
        res.append(">");
    }

    private StringBuilder htmlConventorOp(StringBuilder res, String str) {
        res.append("</");
        res.append(str);
        res.append(">");
        return res;
    }

    private StringBuilder mergeParse(StringBuilder res, String str, String num) {
        String markdownSetNum = "";
        String htmlSetNum = "";
        while (height < str.length()) {
            int ch = str.charAt(height);
            if (ch == '*' || ch == '_') {
                if (ch != str.charAt(height + 1)) {
                    markdownSetNum = Character.toString(ch);
                    htmlSetNum = htmlSet.get(markdownSetNum);
                } else {
                    if (height >= str.length() - 1) {
                        markdownSetNum = Character.toString(ch);
                        htmlSetNum = htmlSet.get(markdownSetNum);
                    } else {
                        markdownSetNum = str.substring(height, height + 2);
                        htmlSetNum = htmlSet.get(markdownSetNum);
                        height++;
                    }
                }
                if (!markdownSetNum.equals(num) || markdownSetNum.length() <= 0) {
                    height++;
                    if (markdownSetNum.length() <= 0) {
                        continue;
                    }
                    StringBuilder newLine = mergeParse(new StringBuilder(), str, markdownSetNum);
                    if (newLine.substring(newLine.length() - htmlSetNum.length() - 1, newLine.length() - 1).equals(htmlSetNum)) {
                        htmlConventorCl(res, htmlSetNum);
                        res.append(newLine);
                        height++;
                    } else {
                        res.append(markdownSetNum);
                        res.append(newLine);
                    }
                    markdownSetNum = "";
                } else {
                    return htmlConventorOp(res, htmlSetNum);
                }
            } else {
                if (ch == '-') {
                    if (height >= str.length() - 1) {
                        res.append(Character.toString(ch));
                    } else {
                        if (ch != str.charAt(height + 1)) {
                            res.append(Character.toString(ch));
                        } else {
                            markdownSetNum = str.substring(height, height + 2);
                            htmlSetNum = htmlSet.get(markdownSetNum);
                            height++;
                        }
                    }
                } else {
                    if (ch == '`') {
                        markdownSetNum = Character.toString(ch);
                        htmlSetNum = htmlSet.get(markdownSetNum);
                    } else {
                        if (ch == '%') {
                            markdownSetNum = Character.toString(ch);
                            htmlSetNum = htmlSet.get(markdownSetNum);
                        } else {
                            if (ch != '&' && ch != '>' && ch != '<') {
                                if (ch == '\\') {
                                    height++;
                                    res.append(str.charAt(height));
                                } else {
                                    res.append((char) ch);
                                }
                            } else {
                                res.append(htmlSet.get(Character.toString(ch)));
                            }
                        }
                    }
                }
                if (!markdownSetNum.equals(num) || markdownSetNum.length() <= 0) {
                    height++;
                    if (markdownSetNum.length() <= 0) {
                        continue;
                    }
                    StringBuilder newLine = mergeParse(new StringBuilder(), str, markdownSetNum);
                    if (!newLine.substring(newLine.length() - htmlSetNum.length() - 1, newLine.length() - 1).equals(htmlSetNum)) {
                        res.append(markdownSetNum);
                        res.append(newLine);
                        markdownSetNum = "";
                    } else {
                        htmlConventorCl(res, htmlSetNum);
                        res.append(newLine);
                        height++;
                        markdownSetNum = "";
                    }
                } else {
                    return htmlConventorOp(res, htmlSetNum);
                }
            }
        }
        return res;
    }
}