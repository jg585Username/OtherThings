package com.example.finalapp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.util.ArrayList;

public class Controller {
    //initialize fruits/vegetables
    @FXML
    private Button apple;
    @FXML
    private Button orange;
    @FXML
    private Button lettuce;
    @FXML
    private Button tomato;
    @FXML
    private Button pear;
    @FXML
    private Button cucumber;
    //initialize meats
    @FXML
    private Button beef;
    @FXML
    private Button pork;
    @FXML
    private Button cod;
    @FXML
    private Button chicken;
    @FXML
    private Button ribs;
    //initialize essential foods
    @FXML
    private Button soda;
    @FXML
    private Button crackers;
    @FXML
    private Button chips;
    @FXML
    private Button bread;
    @FXML
    private Button milk;
    //initialize other products
    @FXML
    private Button detergent;
    @FXML
    private Button dogFood;
    //initialize GUI user features
    @FXML
    public ListView <Item> cart;
    @FXML
    private Label subtotal;
    @FXML
    public Button deleteItem;
    @FXML
    public Button loadCart;
    @FXML
    public Button saveCart;
    @FXML
    public Button send;
    @FXML
    public TextField address;
    @FXML
    public TextField phoneNumber;

    //foods arraylist (which should be public static to be accessible across all classes) will contain all the foods in cart (read for data persistence class)
    public static ArrayList<Item> foods = new ArrayList<>();
    //constructor used to create new items with individual attributes and then to set button text on GUI and set subtotal to zero dollars
    public void initialize (){
        subtotal.setText("Total: $0.00");
        Item a = new Item("Apple: $", "0.50"); apple.setText(a.getName() + a.getPrice());
        Item b = new Item("Orange: $", "0.75"); orange.setText(b.getName() + b.getPrice());
        Item c = new Item("Lettuce: $", "1.00"); lettuce.setText(c.getName() + c.getPrice());
        Item d = new Item("Tomato: $", "0.80"); tomato.setText(d.getName() + d.getPrice());
        Item e = new Item("Pear: $", "0.60"); pear.setText(e.getName() + e.getPrice());
        Item f = new Item("Cucumber: $", "0.90"); cucumber.setText(f.getName() + f.getPrice());
        Item g = new Item("Ground-beef-1lb: $", "15.00"); beef.setText(g.getName() + g.getPrice());
        Item h = new Item("Ground-pork-1/2lb: $", "10.00"); pork.setText(h.getName() + h.getPrice());
        Item j = new Item("Cod-steak: $", "20.00"); cod.setText(j.getName() + j.getPrice());
        Item k = new Item("Whole-chicken: $", "20.00"); chicken.setText(k.getName() + k.getPrice());
        Item l = new Item("Full-ribs: $", "30.00"); ribs.setText(l.getName() + l.getPrice());
        Item m = new Item("Coke-6pk: $", "3.50"); soda.setText(m.getName() + m.getPrice());
        Item n = new Item("Triscuit-200g: $", "2.50"); crackers.setText(n.getName() + n.getPrice());
        Item o = new Item("Doritos-350g: $", "5.00"); chips.setText(o.getName() + o.getPrice());
        Item p = new Item("Wonder-loaf: $", "3.00"); bread.setText(p.getName() + p.getPrice());
        Item q = new Item("Milk-2%-2L: $", "7.25"); milk.setText(q.getName() + q.getPrice());
        Item r = new Item("Tide-2L: $", "8.99"); detergent.setText(r.getName() + r.getPrice());
        Item s = new Item("Purina-DogChow-18lb: $", "26.25"); dogFood.setText(s.getName() + s.getPrice());
    }

    //method to refresh ListView everytime something is added/deleted from cart, also recalculates total cost everytime cart is changed
    //requires: nothing
    //modifies: sum, cart
    //effects: subtotal, display (listview) of items in cart
    public void refresh (){
        //this method doesn't work without this seemingly redundant if statement
        if (this.cart != null){ //cart can't be empty
            double sum = 0;
            double rounded = 0; //variable used to prevent Java issue with long floats
            for (Item i : cart.getItems()){ //foods
                sum += Double.parseDouble(i.getPrice()); //convert String price into double type to do calculation
                rounded = Math.round(sum * 100.00)/ 100.00; //prevents long floats
            }
            subtotal.setText("Total: $" + rounded); //show total cost of items in cart
            cart.getItems().clear(); //clear cart (ListView display)
            for (Item item : foods) {
                cart.getItems().add(item); //for items in foods also add to the cart
            }
        }
    }

    //method that adds items to cart listview and foods arraylist on user action of clicking the product's respective button
    //requires: nothing
    //modifies: cart, foods
    //effects: the ListView (items in cart displayed) changes & subtotal
    public void addItem (ActionEvent event){
        Button button = (Button) event.getSource();
        if (button.getId().equals("apple")){
            cart.getItems().add(new Item("Apple: $", "0.50"));
            foods.add(new Item("Apple: $", "0.50"));
        }
        if (button.getId().equals("orange")){
            cart.getItems().add(new Item("Orange: $", "0.75"));
            foods.add(new Item("Orange: $", "0.75"));
        }
        if (button.getId().equals("lettuce")){
            cart.getItems().add(new Item("Lettuce: $", "1.00"));
            foods.add(new Item("Lettuce: $", "1.00"));
        }
        if (button.getId().equals("tomato")){
            cart.getItems().add(new Item("Tomato: $", "0.80"));
            foods.add(new Item("Tomato: $", "0.80"));
        }
        if (button.getId().equals("pear")){
            cart.getItems().add(new Item("Pear: $", "0.60"));
            foods.add(new Item("Pear: $", "0.60"));
        }
        if (button.getId().equals("cucumber")){
            cart.getItems().add(new Item("Cucumber: $", "0.90"));
            foods.add(new Item("Cucumber: $", "0.90"));
        }
        if (button.getId().equals("beef")){
            cart.getItems().add(new Item("Ground-beef-1lb: $", "15.00"));
            foods.add(new Item("Ground beef 1lb: $", "15.00"));
        }
        if (button.getId().equals("pork")){
            cart.getItems().add(new Item("Ground-pork-1/2lb: $", "10.00"));
            foods.add(new Item("Ground-pork-1/2lb: $", "10.00"));
        }
        if (button.getId().equals("cod")){
            cart.getItems().add(new Item("Cod-steak: $", "20.00"));
            foods.add(new Item("Cod-steak: $", "20.00"));
        }
        if (button.getId().equals("chicken")){
            cart.getItems().add(new Item("Whole-chicken: $", "20.00"));
            foods.add(new Item("Whole-chicken: $", "20.00"));
        }
        if (button.getId().equals("ribs")){
            cart.getItems().add(new Item("Full-ribs: $", "30.00"));
            foods.add(new Item("Full-ribs: $", "30.00"));
        }
        if (button.getId().equals("soda")){
            cart.getItems().add(new Item("Coke-6pk: $", "3.50"));
            foods.add(new Item("Coke-6pk: $", "3.50"));
        }
        if (button.getId().equals("crackers")){
            cart.getItems().add(new Item("Triscuit-200g: $", "2.50"));
            foods.add(new Item("Triscuit-200g: $", "2.50"));
        }
        if (button.getId().equals("chips")){
            cart.getItems().add(new Item("Doritos-350g: $", "5.00"));
            foods.add(new Item("Doritos-350g: $", "5.00"));
        }
        if (button.getId().equals("bread")){
            cart.getItems().add(new Item("Wonder-loaf: $", "3.00"));
            foods.add(new Item("Wonder-loaf: $", "3.00"));
        }
        if (button.getId().equals("milk")){
            cart.getItems().add(new Item("Milk-2%-2L: $", "7.25"));
            foods.add(new Item("Milk-2%-2L: $", "7.25"));
        }
        if (button.getId().equals("detergent")){
            cart.getItems().add(new Item("Tide-2L: $", "8.99"));
            foods.add(new Item("Tide-2L: $", "8.99"));
        }
        if (button.getId().equals("dogFood")){
            cart.getItems().add(new Item("Purina-DogChow-18lb: $", "26.25"));
            foods.add(new Item("Purina-DogChow-18lb: $", "26.25"));
        }
        refresh();
    }
    //method that removes desired item from cart and foods
    //requires: nothing
    //modifies: cart, foods
    //effects: items displayed in cart (ListView), subtotal
    public void delete () {
        Item chosen = cart.getSelectionModel().getSelectedItem();
        foods.remove(chosen);
        cart.getItems().remove(chosen); //also need to remove from listview
        refresh();
    }

    public void sendOrder (){
        String temp = phoneNumber.getText(); //prevents person from putting not numbers into phone number field
        for (int i = 0; i < temp.length(); i++){
            String numbers = "0123456789"; //pull each input character out and compare to check if it's not a number
            if (!numbers.contains(temp.substring(i,i+1))){ //if field contains no numbers, do not send order
                phoneNumber.setText("Input a #!"); //set field with a warning
                return; //return to prevent latter code from running
            }
        }

        if (address.getText().equals("") || address.getText().equals("Input an address!")){ //do not send order if address field is empty and to prevent spamming error message as address
            address.setText("Input an address!");
            return; //return to prevent latter code from running
        }

        if (phoneNumber.getText().equals("")){ //do not send order if phone number field is empty
            phoneNumber.setText("Input a #!");
        }

        else { //if none of those if statements are triggered, "send" order and close the window
            Stage stage = (Stage) send.getScene().getWindow();
            stage.close();
        }
    }

    //have a method that can be assigned as fx:id for gui button to call the actual method
    public void saveList() {
        DataPersistence.save();
    }
    //have a method that can be assigned as fx:id for gui button to call the actual method
    public void loadList() {
        DataPersistence.loadSave();
        refresh();
        refresh(); //to refresh subtotal (refresh needs to be called twice to update subtotal properly)
    }


}