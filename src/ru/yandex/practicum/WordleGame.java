package ru.yandex.practicum;

import java.io.PrintWriter;
import java.util.*;

public class WordleGame {

    public static final int MAX_STEPS = 6;
    public static final String WIN_PATTERN = "+++++";
    private boolean isWon = false;


    private String answer;
    private int steps;
    private WordleDictionary dictionary;
    private final PrintWriter logs;
    Random random = new Random();

    //История пользователя
    private LinkedHashMap<String, String> userHistory = new LinkedHashMap<>();
    //идеальное попадание
    private char [] exactPosition = new char[dictionary.WORD_LENGTH];
    //буквы которые пресутствуют ("^" и "+")
    private Set<Character> presentLetters = new HashSet<>();
    //буквы которые отсутсвуют
    private Set<Character> absentLetters = new HashSet<>();
    //Неверная позиция для присутсвующих букв
    private final List<Set<Character>> wrongPositions = new ArrayList<>(dictionary.WORD_LENGTH);
    //Список оставшихся слов
    private final List<String> availableHints;

    //попытка начать игру
    public WordleGame(WordleDictionary dictionary, PrintWriter logs) {
        if (dictionary == null || dictionary.isEmpty()) {
            throw new DictionaryIsEmptyException("Словарь пуст, невозможно начать игру. ");
        }
        this.dictionary = dictionary;
        this.logs = logs;
        this.answer = dictionary.getRandomWord();
        this.steps = MAX_STEPS;
        this.availableHints = new ArrayList<>(dictionary.getWords());

        for (int i = 0; i < dictionary.WORD_LENGTH; i++) {
            wrongPositions.add(new HashSet<>());
        }

        logs.println("Игра началась. загаданное слово: " + answer);
    }

    //Конструктор для тестов
    public WordleGame(WordleDictionary dictionary, String predefinedAnswer, PrintWriter logs) {
        if (dictionary == null || dictionary.isEmpty()) {
            throw new DictionaryIsEmptyException("Невозможно начать игру с пустым словарем.");
        }
        this.dictionary = dictionary;
        this.logs = logs;
        this.steps = MAX_STEPS;
        this.answer = WordleDictionary.formatWord(predefinedAnswer);
        this.availableHints = new ArrayList<>(dictionary.getWords());

        for (int i = 0; i < WordleDictionary.WORD_LENGTH; i++) {
            wrongPositions.add(new HashSet<>());
        }
        logs.println("Тестовая игра создана. Загаданное слово: " + this.answer);
    }

    public boolean isGameOver() {
        return isWon || steps <= 0;
    }

    public boolean isWon() {
        return isWon;
    }

    public int getSteps() {
        return steps;
    }

    public String getAnswer() {
        return answer;
    }

    public Map<String, String> getUserHistory() {
        return userHistory;
    }


    public String makeSteps(String userInput) throws WordNotFoundInDictionaryException, InvalidWordFormatException {
        if (isGameOver()) {
            logs.println("Игра закончилась, нельзя сделать ход. ");
            throw new IllegalStateException("Игра уже завершена. ");
        }

        String word = dictionary.formatWord(userInput);
        logs.println("попытка ввода слова '" + word + "' .");

        if (word.length() != dictionary.WORD_LENGTH) {
            throw new InvalidWordFormatException("Слово должно состоять ровно из 5 русских букв. ");
        }

        if (!dictionary.checkRussianWord(word)) {
            throw new InvalidWordFormatException("Слово должно состоять из 5 русских букв. ");
        }

        if (!word.equals(answer) && !dictionary.containsWords(word)) {
            throw new WordNotFoundInDictionaryException("Такого слова нет в словаре. ");
        }

        steps--;

        if (word.equals(answer)) {
            logs.println("Слово " + word + " отгадано! Осталось попыток " + steps + " .");
            isWon = true;
            userHistory.put(word, WIN_PATTERN);
            return WIN_PATTERN;
        }

        String hint = dictionary.checkGuess(word, answer);
        userHistory.put(word, hint);
        updateHintConstraints(word, hint);
        filterAvailableHints();

        logs.println("Ход засчитан: " + word + " -> " + hint + "\n"
        + "Осталось шагов: " + steps + "\n"
        + "Доступно подсказок: " + availableHints.size());
        return hint;
    }

    public void updateHintConstraints(String guess, String pattern) {
        for (int i = 0; i < dictionary.WORD_LENGTH; i++) {
            char s = guess.charAt(i);
            char symbol = pattern.charAt(i);
            if (symbol == '+') {
                exactPosition[i] = s;
                presentLetters.add(s);
            } else if (symbol == '^') {
                presentLetters.add(s);
                wrongPositions.get(i).add(s);
            }
        }

        for (int i = 0; i < dictionary.WORD_LENGTH; i++) {
            char s = guess.charAt(i);
            char symbol = pattern.charAt(i);
            if (symbol == '-') {
                if (!presentLetters.contains(s)) {
                    absentLetters.add(s);
                } else {
                    wrongPositions.get(i).add(s);
                }
            }
        }
    }

    public void filterAvailableHints() {
        availableHints.removeIf(hint -> {

            if (userHistory.containsKey(hint)) {
                return true;
            }
            for (int i = 0; i < dictionary.WORD_LENGTH; i++) {
                char s = hint.charAt(i);

                if (absentLetters.contains(s)) {
                    return true;
                }

                if (exactPosition[i] != '\0' && exactPosition[i] != s) {
                    return true;
                }

                if (wrongPositions.get(i).contains(s)) {
                    return true;
                }
            }

            for (Character present : presentLetters) {
                if (hint.indexOf(present) == -1) {
                    return true;
                }
            }
            return false;
        });
    }

    public String getHint() {
        filterAvailableHints();

        if (availableHints.isEmpty()) {
            logs.println("Список доступных подсказок пуст. ");
            throw new IllegalStateException("Не осталось подсказок. ");
        }

        String help = availableHints.get(random.nextInt(availableHints.size()));
        logs.println("Выдана подсказка " + help);
        return help;
    }



}
