
public class Runner {

    public static void main(String[] args) {
        // TODO code application logic here
        WordManager wm = new WordManager();
        for (int i = 0; i < 10; i++){
            String s = wm.getWord();
            System.out.println(s);
        }
    }
    
}
