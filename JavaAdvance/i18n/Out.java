package info.kgeorgiy.ja.latanov.i18n;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static info.kgeorgiy.ja.latanov.i18n.TextStatistics.*;
/**
 * @author created by Daniil Latanov
 */
public class Out {
    static void out(String[] args, String inputFile, Locale outputLocale, NumberFormat numberFormat) {
        try {
            String text = readTextFile(inputFile);

            List<String> sentences = extractSentences(text, outputLocale);
            List<String> words = extractWords(text, outputLocale);
            List<Double> numbers = extractNumbers(text, outputLocale, numberFormat);
            List<Double> moneyAmounts = extractMoneyAmounts(text, outputLocale, numberFormat);
            List<Date> dates = extractDates(text, outputLocale);

            int distinctSentences = countDistinct(sentences);
            int distinctWords = countDistinct(words);
            int distinctNumbers = countDistinct(numbers);
            int distinctMoneyAmounts = countDistinct(moneyAmounts);
            int distinctDates = countDistinct(dates);

            String shortestSentence = getShortestSentence(sentences);
            String longestSentence = getLongestSentence(sentences);
            int shortestSentenceLength = getShortestSentenceLength(sentences);
            int longestSentenceLength = getLongestSentenceLength(sentences);
            double averageSentenceLength = getAverageSentenceLength(sentences);

            String shortestWord = getShortestWord(words);
            String longestWord = getLongestWord(words);
            int shortestWordLength = getShortestWordLength(words);
            int longestWordLength = getLongestWordLength(words);
            double averageWordLength = getAverageWordLength(words);

            double minimumNumber = getMinimumNumber(numbers);
            double maximumNumber = getMaximumNumber(numbers);
            double averageNumber = getAverageNumber(numbers);

            double minimumMoneyAmount = getMinimumMoneyAmount(moneyAmounts);
            double maximumMoneyAmount = getMaximumMoneyAmount(moneyAmounts);
            double averageMoneyAmount = getAverageMoneyAmount(moneyAmounts);

            Date minimumDate = getMinimumDate(dates);

            Date maximumDate = getMaximumDate(dates);

            Date averageDate = getAverageDate(dates);

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(args[3]))) {

                writer.write(bundle.getString("inputFile") + " " + inputFile);
                writer.newLine();
                writer.write(bundle.getString("summaryStatistics"));
                writer.newLine();
                writer.write(bundle.getString("summaryStatistics.sentencesCount") + " " + sentences.size() + ".");
                writer.newLine();
                writer.write(bundle.getString("summaryStatistics.wordsCount") + " " + words.size() + ".");
                writer.newLine();
                writer.write(bundle.getString("summaryStatistics.numbersCount") + " " + numbers.size() + ".");
                writer.newLine();
                writer.write(bundle.getString("summaryStatistics.moneyAmountsCount") + " " + moneyAmounts.size() + ".");
                writer.newLine();
                writer.write(bundle.getString("summaryStatistics.datesCount") + " " + dates.size() + ".");
                writer.newLine();
                writer.write(bundle.getString("sentenceStatistics"));
                writer.newLine();
                writer.write(bundle.getString("sentenceStatistics.sentencesCount") + " " + distinctSentences + " (" + distinctSentences + " " + bundle.getString("unique") + ").");
                writer.newLine();
                writer.write(bundle.getString("sentenceStatistics.shortestSentence") + " \"" + shortestSentence + "\".");
                writer.newLine();
                writer.write(bundle.getString("sentenceStatistics.longestSentence") + " " + "\"" + longestSentence + "\".");
                writer.newLine();
                writer.write(bundle.getString("sentenceStatistics.shortestSentenceLength") + " " + shortestSentenceLength + " (\"" + shortestSentence + "\").");
                writer.newLine();
                writer.write(bundle.getString("sentenceStatistics.longestSentenceLength") + " " + longestSentenceLength + " (\"" + longestSentence + "\").");
                writer.newLine();
                writer.write(bundle.getString("sentenceStatistics.averageSentenceLength") + " " + averageSentenceLength + ".");
                writer.newLine();
                writer.write(bundle.getString("wordStatistics"));
                writer.newLine();
                writer.write(bundle.getString("wordStatistics.wordsCount") + " " + distinctWords + " (" + distinctWords + " " + bundle.getString("unique") + ").");
                writer.newLine();
                writer.write(bundle.getString("wordStatistics.shortestWord") + " \"" + shortestWord + "\".");
                writer.newLine();
                writer.write(bundle.getString("wordStatistics.longestWord") + " \"" + longestWord + "\".");
                writer.newLine();
                writer.write(bundle.getString("wordStatistics.shortestWordLength") + " " + shortestWordLength + " (\"" + shortestWord + "\").");
                writer.newLine();
                writer.write(bundle.getString("wordStatistics.longestWordLength") + " " + longestWordLength + " (\"" + longestWord + "\").");
                writer.newLine();
                writer.write(bundle.getString("wordStatistics.averageWordLength") + " " + averageWordLength + ".");

                writer.newLine();
                writer.write(bundle.getString("numberStatistics"));
                writer.newLine();
                writer.write(bundle.getString("numberStatistics.numbersCount") + " " + distinctNumbers + " (" + distinctNumbers + " " + bundle.getString("unique") + ").");
                writer.newLine();
                writer.write(bundle.getString("numberStatistics.minimumNumber") + " " + minimumNumber + ".");
                writer.newLine();
                writer.write(bundle.getString("numberStatistics.maximumNumber") + " " + maximumNumber + ".");
                writer.newLine();
                writer.write(bundle.getString("numberStatistics.averageNumber") + " " + averageNumber + ".");
                writer.newLine();


                writer.newLine();
                writer.write(bundle.getString("moneyAmountStatistics"));
                writer.newLine();
                writer.write(bundle.getString("moneyAmountStatistics.moneyAmountsCount") + " " + distinctMoneyAmounts + " (" + distinctMoneyAmounts + " " + bundle.getString("unique") + ").");
                writer.newLine();
                NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(outputLocale);
                String minimumMoneyAmountFormatted = currencyFormat.format(minimumMoneyAmount);
                String maximumMoneyAmountFormatted = currencyFormat.format(maximumMoneyAmount);
                String averageMoneyAmountFormatted = currencyFormat.format(averageMoneyAmount);

                writer.write(bundle.getString("moneyAmountStatistics.minimumMoneyAmount") + " " + minimumMoneyAmountFormatted + ".");
                writer.newLine();
                writer.write(bundle.getString("moneyAmountStatistics.maximumMoneyAmount") + " " + maximumMoneyAmountFormatted + ".");
                writer.newLine();
                writer.write(bundle.getString("moneyAmountStatistics.averageMoneyAmount") + " " + averageMoneyAmountFormatted + ".");

                writer.newLine();

                writer.write(bundle.getString("dateStatistics"));
                writer.newLine();
                writer.write(bundle.getString("dateStatistics.datesCount") + " " + distinctDates + " (" + distinctMoneyAmounts + " " + bundle.getString("unique") + ").");
                writer.newLine();
                writer.write(bundle.getString("dateStatistics.minimumDate") + " " + minimumDate + ".");
                writer.newLine();
                writer.write(bundle.getString("dateStatistics.maximumDate") + " " + maximumDate + ".");
                writer.newLine();
                writer.write(bundle.getString("dateStatistics.averageDate") + " " + averageDate + ".");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
