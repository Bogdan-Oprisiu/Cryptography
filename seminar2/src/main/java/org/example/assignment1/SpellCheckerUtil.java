package org.example.assignment1;

import com.swabunga.spell.engine.SpellDictionaryHashMap;
import com.swabunga.spell.event.SpellChecker;

import java.io.File;
import java.io.IOException;

public class SpellCheckerUtil {
    private SpellChecker spellChecker;

    public SpellCheckerUtil(String dictionaryPath) {
        try {
            File dictFile = new File("C:/Uni/Cryptography/seminar2/src/main/resources/english.0");
            SpellDictionaryHashMap dictionary = new SpellDictionaryHashMap(dictFile);
            spellChecker = new SpellChecker(dictionary);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Check if a given word is valid English.
    public boolean isEnglishWord(String word) {
        // Jazzy is case-insensitive, so we can pass the word as is.
        return spellChecker.isCorrect(word);
    }
}
