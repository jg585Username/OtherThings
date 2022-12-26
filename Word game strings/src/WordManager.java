
import java.util.ArrayList;


 
public class WordManager {
    //list for words
    private ArrayList<String> wordList = new ArrayList<String>();
    // list of used words
    private ArrayList<String> usedWordList = new ArrayList<String>();

    
    public WordManager(){
        loadWords();
    }
    
    public String getWord(){
        int index = (int) (Math.random()* wordList.size());
        String check = wordList.get(index);
        usedWordList.add(check);
        wordList.remove(index);
        if (wordList.size() == 0)
            reset();
        
        return check;
    }
    
    public void reset(){
        wordList = usedWordList;
        for (int i = 0; i < usedWordList.size(); i++){
            String w = usedWordList.get(i);
            wordList.add(w);
        }
        usedWordList.clear();
    }
    
    public void loadWords(){
        //add 4 words to the word list
            wordList.add("Cyrus");
            wordList.add("apple");
            wordList.add("magazine");
            wordList.add("horse");
        
    }
    
}
