package info.kgeorgiy.ja.latanov.i18n;

import java.io.*;
import java.text.*;
import java.util.*;

import static info.kgeorgiy.ja.latanov.i18n.Out.out;

public class TextStatistics {

    static ResourceBundle bundle;

    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Usage: TextStatistics <inputFile> <outputLocale> <numberFormat>");
            return;
        }

        String inputLocal = args[0];
        String outputLocal = args[1];
        String inputFile = args[2];
        Locale outputLocale = Locale.forLanguageTag(outputLocal);
        Locale inputLocale = Locale.forLanguageTag(inputLocal);
        NumberFormat numberFormat = NumberFormat.getInstance(inputLocale);

        bundle = ResourceBundle.getBundle("info/kgeorgiy/ja/latanov/i18n/Standart", outputLocale);


        out(args, inputFile, outputLocale, numberFormat);
    }


    public static String readTextFile(String filePath) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
            }
        }
        return sb.toString();
    }

    public static List<String> extractSentences(String text, Locale locale) {
        List<String> sentences = new ArrayList<>();
        BreakIterator sentenceIterator = BreakIterator.getSentenceInstance(locale);
        sentenceIterator.setText(text);
        int start = sentenceIterator.first();
        int end = sentenceIterator.next();
        while (end != BreakIterator.DONE) {
            String sentence = text.substring(start, end).trim();
            if (!sentence.isEmpty()) {
                sentences.add(sentence);
            }
            start = end;
            end = sentenceIterator.next();
        }
        return sentences;
    }

    static List<String> extractWords(String text, Locale locale) {
        List<String> words = new ArrayList<>();
        BreakIterator wordIterator = BreakIterator.getWordInstance(locale);
        wordIterator.setText(text);
        int start = wordIterator.first();
        int end = wordIterator.next();
        while (end != BreakIterator.DONE) {
            String word = text.substring(start, end).trim();
            if (!word.isEmpty() && containsLetters(word)) {
                words.add(word);
            }
            start = end;
            end = wordIterator.next();
        }
        return words;
    }

    static boolean containsLetters(String word) {
        for (char c : word.toCharArray()) {
            if (Character.isLetter(c)) {
                return true;
            }
        }
        return false;
    }

    static List<Double> extractNumbers(String text, Locale locale, NumberFormat numberFormat) {
        List<Double> numbers = new ArrayList<>();
        BreakIterator wordIterator = BreakIterator.getWordInstance(locale);
        wordIterator.setText(text);
        int start = wordIterator.first();
        int end = wordIterator.next();
        while (end != BreakIterator.DONE) {
            String word = text.substring(start, end).trim();
            if (!word.isEmpty()) {
                NumberFormat customFormat = (NumberFormat) numberFormat.clone();
                ParsePosition parsePosition = new ParsePosition(0);
                Number number = customFormat.parse(word, parsePosition);
                if (parsePosition.getIndex() == word.length()) {
                    if (number != null) {
                        numbers.add(number.doubleValue());
                    }
                }
            }
            start = end;
            end = wordIterator.next();
        }
        return numbers;
    }


    static List<Double> extractMoneyAmounts(String text, Locale locale, NumberFormat numberFormat) {
        List<Double> moneyAmounts = new ArrayList<>();
        BreakIterator wordIterator = BreakIterator.getWordInstance(locale);
        wordIterator.setText(text);
        int start = wordIterator.first();
        int end = wordIterator.next();
        while (end != BreakIterator.DONE) {
            ParsePosition parsePosition = new ParsePosition(start);
            Number number = numberFormat.parse(text, parsePosition);
            if (parsePosition.getIndex() == end) {
                if (number != null) {
                    moneyAmounts.add(number.doubleValue());
                }
            }
            start = end;
            end = wordIterator.next();
        }
        return moneyAmounts;
    }



    static List<Date> extractDates(String text, Locale locale) {
        List<Date> dates = new ArrayList<>();
        BreakIterator sentenceIterator = BreakIterator.getWordInstance(locale);
        sentenceIterator.setText(text);
        int start = sentenceIterator.first();
        int end = sentenceIterator.next();
        while (end != BreakIterator.DONE) {
            String sentence = text.substring(start, end).trim();
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", locale);
                Date date = dateFormat.parse(sentence);
                dates.add(date);
            } catch (ParseException ignored) {
            }
            start = end;
            end = sentenceIterator.next();
        }
        return dates;
    }


    static int countDistinct(List<?> list) {
        Set<Object> distinctValues = new HashSet<>(list);
        return distinctValues.size();
    }

    static String getShortestSentence(List<String> sentences) {
        return sentences.stream()
                .min(Comparator.comparingInt(String::length))
                .orElse("");
    }

    static String getLongestSentence(List<String> sentences) {
        return sentences.stream()
                .max(Comparator.comparingInt(String::length))
                .orElse("");
    }

    static int getShortestSentenceLength(List<String> sentences) {
        return sentences.stream()
                .mapToInt(String::length)
                .min()
                .orElse(0);
    }

    static int getLongestSentenceLength(List<String> sentences) {
        return sentences.stream()
                .mapToInt(String::length)
                .max()
                .orElse(0);
    }

    static double getAverageSentenceLength(List<String> sentences) {
        if (sentences.isEmpty()) {
            return 0;
        }
        int totalLength = sentences.stream()
                .mapToInt(String::length)
                .sum();
        return (double) totalLength / sentences.size();
    }


    static String getShortestWord(List<String> words) {
        return words.stream()
                .min(Comparator.comparingInt(String::length))
                .orElse("");
    }

    static String getLongestWord(List<String> words) {
        return words.stream()
                .max(Comparator.comparingInt(String::length))
                .orElse("");
    }

    static int getShortestWordLength(List<String> words) {
        return words.stream()
                .mapToInt(String::length)
                .min()
                .orElse(0);
    }

    static int getLongestWordLength(List<String> words) {
        return words.stream()
                .mapToInt(String::length)
                .max()
                .orElse(0);
    }

    static double getAverageWordLength(List<String> words) {
        if (words.isEmpty()) {
            return 0;
        }
        int totalLength = words.stream()
                .mapToInt(String::length)
                .sum();
        return (double) totalLength / words.size();
    }

    static double getMinimumNumber(List<Double> numbers) {
        return numbers.stream()
                .mapToDouble(Double::doubleValue)
                .min()
                .orElse(0);
    }

    static double getMaximumNumber(List<Double> numbers) {
        return numbers.stream()
                .mapToDouble(Double::doubleValue)
                .max()
                .orElse(0);
    }

    static double getAverageNumber(List<Double> numbers) {
        if (numbers.isEmpty()) {
            return 0;
        }
        double sum = numbers.stream()
                .mapToDouble(Double::doubleValue)
                .sum();
        return sum / numbers.size();
    }

    static double getMinimumMoneyAmount(List<Double> moneyAmounts) {
        return moneyAmounts.stream()
                .mapToDouble(Double::doubleValue)
                .min()
                .orElse(0);
    }

    static double getMaximumMoneyAmount(List<Double> moneyAmounts) {
        return moneyAmounts.stream()
                .mapToDouble(Double::doubleValue)
                .max()
                .orElse(0);
    }

    static double getAverageMoneyAmount(List<Double> moneyAmounts) {
        if (moneyAmounts.isEmpty()) {
            return 0;
        }
        double sum = moneyAmounts.stream()
                .mapToDouble(Double::doubleValue)
                .sum();
        return sum / moneyAmounts.size();
    }

    static Date getMinimumDate(List<Date> dates) {
        return dates.stream()
                .min(Date::compareTo).orElse(null);
    }

    static Date getMaximumDate(List<Date> dates) {
        return dates.stream()
                .max(Date::compareTo)
                .orElse(null);
    }

    static Date getAverageDate(List<Date> dates) {
        if (dates.isEmpty()) {
            return null;
        }
        long averageTime = (long) dates.stream()
                .mapToLong(Date::getTime)
                .average()
                .orElse(0);
        return new Date(averageTime);
    }

}