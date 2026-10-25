package ru.yandex.practicum;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;


public class WordleDictionaryLoader {
    private final PrintWriter logs;

    WordleDictionaryLoader(PrintWriter logs) {
        this.logs = logs;
    }

    public WordleDictionary loadDictionary(String fileName) {
        logs.println("Начала загрузки словаря " + fileName);
        LinkedHashMap<String, Integer> countUniqueWords = new LinkedHashMap<>();

        try (FileReader fileReader = new FileReader(fileName, StandardCharsets.UTF_8);
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String normalized = WordleDictionary.formatWord(line);

                if (WordleDictionary.checkRussianWord(normalized)) {
                    countUniqueWords.put(normalized, countUniqueWords.getOrDefault(normalized, 0) + 1);
                }
            }

        } catch (IOException e) {
            logs.println("Ошибка при чтение файла словаря " + e.getMessage());
            throw new DictionaryIsEmptyException("Не удалось загрузить файл словаря " + fileName);
        }

        if (countUniqueWords.isEmpty()) {
            logs.println("После фильтрации словарь пуст");
            throw new DictionaryIsEmptyException("Словарь в файле " + fileName +
                    " пуст или не содержит подходящих слов.");
        }

        logs.println("Словарь успешно загружен, количество уникальных слов " + countUniqueWords.size());
        List<String> words = new ArrayList<>(countUniqueWords.keySet());
        return new WordleDictionary(words, logs);
    }
}
