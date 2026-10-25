package ru.yandex.practicum;

import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;


public class WordleDictionary {

    private List<String> words;
    private final PrintWriter logs;
    private final Random randomWord = new Random();
    public static final int WORD_LENGTH = 5;

    public WordleDictionary(List<String> words, PrintWriter logs) {
        this.words = words;
        this.logs = logs;
    }

    //Геттер
    public List<String> getWords() {
        return words;
    }

    //Размер
    public int sizeWords() {
        return words.size();
    }

    //Проверка на 5 русских букв
    public static boolean checkRussianWord(String word) {
        if (word == null || word.length() != WORD_LENGTH) {
            return false;
        }
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            if (c < 'а' || c > 'я') {
                return false;
            }
        }
        return true;
    }

    // проверка на содержимость
    public boolean isEmpty() {
        return words.isEmpty();
    }

    // попытка получить случайное слово из словаря
    public String getRandomWord() {
        if (isEmpty()) {
            logs.println("Попытка выбрать слово из пустого словаря.");
            throw new DictionaryIsEmptyException("словарь пуст");
        }
        return words.get(randomWord.nextInt(words.size()));
    }

    //содержится ли слово
    public boolean containsWords(String word) {
        return words.contains(formatWord(word));
    }

    //формат слова
    public static String formatWord(String word) {
        if (word == null) {
            return "";
        }
        return word.trim().toLowerCase().replace('ё', 'е');
    }

    //сравнение слова игрока с загаданным словом
    public static String checkGuess(String guessWord, String answer) {

        LinkedHashMap<Character, Integer> letterCounts = new LinkedHashMap<>();
        char[] result = new char[WORD_LENGTH];

        //записываем буквы в символы
        for (int i = 0; i < WORD_LENGTH; i++) {
            char letterChar = answer.charAt(i);
            letterCounts.put(letterChar, letterCounts.getOrDefault(letterChar, 0) + 1);
        }

        //проверяем на правильное расположение нужной буквы
        for (int i = 0; i < WORD_LENGTH; i++) {
            char letterChar = guessWord.charAt(i);
            if (letterChar == answer.charAt(i)) {
                result[i] = '+';
                letterCounts.put(letterChar, letterCounts.getOrDefault(letterChar, 0) - 1);
            }
        }
        //проверяем на неверное расположение или отсутствие
        for (int i = 0; i < WORD_LENGTH; i++) {

            if (result[i] == '+') {
                continue;
            }

            char letterChar = guessWord.charAt(i);
            int count = letterCounts.getOrDefault(letterChar, 0);

            if (count > 0) {
                result[i] = '^';
                letterCounts.put(letterChar, count - 1);
            } else {
                result[i] = '-';
            }
        }
        StringBuilder sb = new StringBuilder(WORD_LENGTH);
        for (char a : result) {
            sb.append(a);
        }
        return sb.toString();
    }

}
