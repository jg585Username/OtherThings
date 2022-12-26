
import java.util.ArrayList;


public class test {
    public static void main(String[] args) {
        ArrayList<String> names = new ArrayList<String>();
        names.add("Adam");
        names.add("Ben");
        names.add(1, "Claire");
        names.set(0, "Ann");
        String s = names.get(2);
        names.remove(1);
        int size = names.size();
        //XXXX int boolean  double
        
        ArrayList<Integer> nums = new ArrayList<Integer>();
        
        //fill this with 10 random values
        for (int i = 0; i < 10; i++){
            int random = ((int) (Math.random()*20)+1);
            nums.add(random);
            System.out.println(random);
        }
            
        //what's the largest value?
        int max = nums.get(0);
        for (int i = 0; i < nums.size(); i++){
            int value = nums.get(i);
            if (value > max)
                max = value;
        }
        System.out.println("max is: " + max);
    }
    
}
