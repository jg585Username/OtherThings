package com.example.finalapp;

public class Item {
    public String name; //name attribute for item
    public String price; //price attribute to item (double not used for file reading/writing, has to be String)

    //default constructor to set up parameters
    public Item (String name, String price) {
        this.name = name;
        this.price = price;
    }

    //getters (no setters because that can be done fully with GUI)
    public String getName() { //gets name
        return name;
    }

    public String getPrice() { //gets price
        return price;
    }

    //toString method so item attributes appear in ListView (cart) tabs (like a receipt)
    public String toString (){
        return name + price;
    }

    public String arrayWrite() { //load all item attributes after saving shopping list
        return this.name + " " + this.price;
    }
}
