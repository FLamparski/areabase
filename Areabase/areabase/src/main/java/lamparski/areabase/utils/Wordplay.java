package lamparski.areabase.utils;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Possibly won't ever work.
 */
public class Wordplay {
    public static String findMostCommon(List<String> theLot){
        /*
           Domestic blah blah robots
           Domestic blah blah animals
         - Domestic blah stuff
         ______________________________
           Domestic blah                <= the most common element and thus needs to be removed
         */
        String mostCommon;
        BiMap<String, Integer> wordMap = HashBiMap.create();
        for(int i = 0; i < theLot.size(); i++){
            String s1 = theLot.get(i);
            List<String> words1 = Arrays.asList(s1.split("^[A-Za-z]+"));
            for(int j = 0; j < theLot.size(); j++){
                if(i != j){
                    String s2 = theLot.get(j);
                    List<String> words2 = Arrays.asList(s2.split("^[A-Za-z]+"));
                    words1.retainAll(words2);
                    for(String word : words1){
                        if(wordMap.containsKey(word)){
                            int n = wordMap.get(word);
                            wordMap.put(word, ++n);
                        } else {
                            wordMap.put(word, 1);
                        }
                    }
                }
            }
        }
        Integer maxVal = Collections.max(wordMap.values());
        return null;
    }
}
