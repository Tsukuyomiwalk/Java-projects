package info.kgeorgiy.ja.latanov.i18n;

import org.junit.Test;

import java.text.*;
import java.util.*;

import static org.junit.Assert.assertEquals;

/**
 * @author created by Daniil Latanov
 */
public class TextStatisticsTest {

    @Test
    public void testExtractSentences() {
        String text = "Hello! How are you? I am fine. Nice to meet you.";
        Locale locale = Locale.ENGLISH;

        List<String> expectedSentences = Arrays.asList(
                "Hello!", "How are you?", "I am fine.", "Nice to meet you."
        );
        List<String> sentences = TextStatistics.extractSentences(text, locale);

        assertEquals(expectedSentences, sentences);
    }

    @Test
    public void testExtractWords() {
        String text = "Hello, how are you?";
        Locale locale = Locale.ENGLISH;

        List<String> expectedWords = Arrays.asList(
                "Hello", "how", "are", "you"
        );
        List<String> words = TextStatistics.extractWords(text, locale);

        assertEquals(expectedWords, words);
    }

    @Test
    public void testExtractNumbers() {
        String text = "The height is 185.5";
        Locale locale = Locale.US;
        NumberFormat numberFormat = NumberFormat.getInstance(locale);

        List<Double> expectedNumbers = List.of(185.5);
        List<Double> numbers = TextStatistics.extractNumbers(text, locale, numberFormat);

        assertEquals(expectedNumbers, numbers);
    }

    @Test
    public void testExtractMoneyAmounts() {
        String text = "The total amount is 200.75$";
        Locale locale = Locale.US;
        NumberFormat numberFormat = NumberFormat.getInstance(locale);
        numberFormat.setParseIntegerOnly(false);
        numberFormat.setGroupingUsed(false);


        List<Double> expectedMoneyAmounts = List.of(200.75);
        List<Double> moneyAmounts = TextStatistics.extractMoneyAmounts(text, locale, numberFormat);

        assertEquals(expectedMoneyAmounts, moneyAmounts);
    }

    @Test
    public void testExtractDates() throws ParseException {
        String text = "The event is scheduled for 31.05.2023";
        Locale locale = Locale.ENGLISH;

        List<Date> expectedDates = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", locale);
        Date expectedDate = dateFormat.parse("31.05.2023");
        expectedDates.add(expectedDate);

        List<Date> dates = TextStatistics.extractDates(text, locale);

        assertEquals(expectedDates, dates);
    }


    @Test
    public void testAverageSentenceLength() {
        List<String> sentences = List.of(
                "a",
                "bbb",
                "bb"
        );

        double expectedAverageLength = 2.0;
        double delta = 0.00001;

        double averageLength = TextStatistics.getAverageSentenceLength(sentences);

        assertEquals(expectedAverageLength, averageLength, delta);
    }


    @Test
    public void testAverageMoneyAmount() throws ParseException {
        List<String> amounts = List.of("100.50$", "200.75$", "300.25$", "400.50$");

        Locale locale = Locale.US;
        NumberFormat numberFormat = NumberFormat.getInstance(locale);
        numberFormat.setParseIntegerOnly(false);
        numberFormat.setGroupingUsed(false);

        List<Double> parsedAmounts = TextStatistics.extractMoneyAmounts(String.join(" ", amounts), locale, numberFormat);

        double expectedAverage = 250.5;
        double delta = 0.00001;

        double average = TextStatistics.getAverageMoneyAmount(parsedAmounts);

        assertEquals(expectedAverage, average, delta);
    }

    @Test
    public void testAverageNumber() throws ParseException {
        List<String> numbers = List.of("10", "20", "30", "40");

        Locale locale = Locale.US;
        NumberFormat numberFormat = NumberFormat.getInstance(locale);

        List<Double> parsedNumbers = TextStatistics.extractNumbers(String.join(" ", numbers), locale, numberFormat);

        double expectedAverage = 25.0;
        double delta = 0.00001;

        double average = TextStatistics.getAverageNumber(parsedNumbers);

        assertEquals(expectedAverage, average, delta);
    }

    @Test
    public void testAverageWordLength() {
        List<String> words = List.of("apple", "apple", "data", "da");

        double expectedAverageLength = 4.0;
        double delta = 0.00001;

        double averageLength = TextStatistics.getAverageWordLength(words);

        assertEquals(expectedAverageLength, averageLength, delta);
    }


}
