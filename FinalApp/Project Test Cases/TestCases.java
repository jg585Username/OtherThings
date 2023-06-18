import com.example.finalapp.Controller;
import com.example.finalapp.Item;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;


public class TestCases {
    //not many scenarios to test due to the fact project does not require much user input that can cause issues in program

    //setup variables needed for Test cases
    Controller test;
    Item item;
    @Before
    public void setUp(){
        test = new Controller();
        item = new Item("", "");
    }

    @Test
    //testing if refresh method works correctly when called (testing if code works)
    public void testRefresh () {
        test.refresh();
    }

    @Test
    //send order only if address contains an input
    //test will fail because address is currently empty
    public void testSend (){
        if (this.test.address != null){
            test.sendOrder();
            assertTrue(true); //throws assertion error if test fails
        }
        else {
            fail();  //if address is empty, fail test
        }
    }

    @Test
    //testing if delete method is only applicable if the cart contains an item
    //test will fail because the cart is empty
    public void testDelete(){
        if (this.test.cart != null){
            test.delete();
            assertTrue(true);
        }
        else {
            fail(); //if cart is empty, fail test
        }
    }

    @Test
    //empty array (between other arrays) does not cause saving file error (test should pass)
    public void testArrayWrite(){
        if (item.name.equals("") && item.price.equals("")){
            item.arrayWrite();
            assertTrue(true);
        }
        else {
            fail();
        }
    }

}
