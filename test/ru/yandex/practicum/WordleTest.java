package ru.yandex.practicum;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WordleTest {

    private static PrintWriter testLog;
    private WordleDictionary dictionary;
    private WordleGame game;

    @BeforeAll
    static void setUpLogger() {
        testLog = new PrintWriter(System.out, true);
    }

    @BeforeEach
    void setUpGame() {
        List<String> words = Arrays.asList(
                "герой", "гонец", "город", "голос", "кошка", "книга", "поезд", "зебра", "олово"
        );
        dictionary = new WordleDictionary(words, testLog);
        game = new WordleGame(dictionary, "герой", testLog);
    }

    @Test
    void shouldNormalizeWordCorrectly() {
        assertEquals("ежик", WordleDictionary.formatWord("  ЁЖИК "));
        assertEquals("герой", WordleDictionary.formatWord("ГеРоЙ"));
    }

    @Test
    void shouldValidateRussianFiveLetterWords() {
        assertTrue(dictionary.checkRussianWord("герой"));
        assertFalse(dictionary.checkRussianWord("дом"));
        assertFalse(dictionary.checkRussianWord("самолет"));
        assertFalse(dictionary.checkRussianWord("table"));
    }

    @Test
    void shouldEvaluateGuessAccordingToRules() {
        assertEquals("+^-^-", dictionary.checkGuess("гонец", "герой"));
        // Проверка повторяющихся букв через LinkedHashMap ("олово" против "герой" — только одна 'о' должна быть '^')
        assertEquals("^----", dictionary.checkGuess("олово", "герой"));
    }

    @Test
    void shouldNotDecreaseStepsOnInvalidInput() {
        assertThrows(InvalidWordFormatException.class, () -> game.makeSteps("кот"));
        assertEquals(6, game.getSteps());

        assertThrows(InvalidWordFormatException.class, () -> game.makeSteps("apple"));
        assertEquals(6, game.getSteps());

        assertThrows(WordNotFoundInDictionaryException.class, () -> game.makeSteps("абвгд"));
        assertEquals(6, game.getSteps());
    }

    @Test
    void shouldWinGameWhenWordIsGuessed() throws Exception {
        assertEquals("+^-^-", game.makeSteps("гонец"));
        assertEquals(5, game.getSteps());
        assertFalse(game.isGameOver());

        assertEquals("+++++", game.makeSteps("герой"));
        assertTrue(game.isWon());
        assertTrue(game.isGameOver());
    }

    @Test
    void shouldWinGameUsingOnlyComputerHints() throws Exception {
        while (!game.isGameOver()) {
            String hint = game.getHint();
            assertNotNull(hint);
            assertTrue(dictionary.containsWords(hint));
            game.makeSteps(hint);
        }
        assertTrue(game.isWon());
    }
}