package ru.yandex.practicum;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;


public class Wordle {

    private static final String DICTIONARY_FILE = "words_ru.txt";
    private static final String LOG_FILE = "wordle.log";

    public static void main(String[] args) {

        try (PrintWriter logs = new PrintWriter(new FileWriter(LOG_FILE, StandardCharsets.UTF_8), true);
             Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8)) {

            logs.println("    Начало игры Wordle!    ");

            try {
                WordleDictionaryLoader loader = new WordleDictionaryLoader(logs);
                WordleDictionary dictionary = loader.loadDictionary(DICTIONARY_FILE);
                WordleGame game = new WordleGame(dictionary, logs);

                playGame(game, scanner, logs);
            } catch (Exception e) {
                logs.println("Непредвиденая ошибка работы программы: " + e.getMessage());
                e.printStackTrace(logs);
                System.out.println("Произошла непредвиденая ошибка работы программы, подробности записаны в log. ");
            }
        } catch (IOException e) {
            System.out.println("Не удалось создать log файлов: " + e.getMessage());
        }
    }

    private static void playGame(WordleGame wordleGame, Scanner scanner, PrintWriter log) {
        System.out.println("Добро пожаловать в игру Wordle!");
        System.out.println("Было загадано слово, у вас есть " + wordleGame.MAX_STEPS + " попыток.");
        System.out.println("Для получения подсказки нажмите Enter в пустой строке. \n");

        while (!wordleGame.isGameOver()) {
            System.out.println("-> ");
            String input = scanner.nextLine();

            if (input.trim().isEmpty()) {
                input = wordleGame.getHint();
                System.out.println("> " + input);
            }

            try {
                String result = wordleGame.makeSteps(input);
                System.out.println("> " + result);
            } catch (InvalidWordFormatException | WordNotFoundInDictionaryException e) {
                log.println("Игровое исключение при вводе " + input + ": " + e.getMessage());
                System.out.println(e.getMessage());
            }
        }

        System.out.println();
        if (wordleGame.isWon()) {
            System.out.println("Поздравляем! вы угадали слово! ");
        } else {
            System.out.println("К сожалению попытки закончились. ");
        }

        System.out.println("Загаданое слово " + wordleGame.getAnswer());
        System.out.println("    Игра звершена!    ");
    }

}
