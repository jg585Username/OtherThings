package com.example.finalapp;

import javafx.stage.FileChooser;

import java.io.*;

import static com.example.finalapp.Controller.foods;
//this class will be responsible for saving/loading text.files (containing saved shopping list)
public class DataPersistence {
    public static final FileChooser fc = new FileChooser(); //FileChooser used to invoke file opening dialogs for choosing files

    //method to load previously saved lists
    //requires: nothing (enclosing try/catch prevents error when no file is used)
    //modifies: foods, ListView displaying cart
    //effects: loads in saved cart (ListView is refreshed to show loaded items)
    public static void loadSave() {
        try {
            foods.clear(); //clear the current list to load in previously saved list
            //opens computer file manager
            fc.setTitle("Choose previously saved list");
            File openFile = fc.showOpenDialog(null);
            fc.getExtensionFilters().clear();
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt")); //read only text files
            //saves path to String path
            String path = openFile.getAbsolutePath();
            FileReader fr = new FileReader(path);
            BufferedReader br = new BufferedReader(fr);
            String line;
            while ((line = br.readLine()) != null) { //while line is not empty, add item (reads attributes in each line) to foods
                String [] data = line.split("\\s+"); //split anything with space into array
                foods.add(new Item(data[0] + " " + data[1], data[2])); //[index pos] on txt file
            }
            Controller hc = new Controller();
            hc.refresh(); //call refresh method to refresh ListView display & subtotal
        } catch (Exception ignored) {} //try catch prevents error from displaying in console when cancelling loading file
    }
    //method to save friend list
    //requires: nothing (enclosing try/catch prevents error when no file is saved)
    //modifies: nothing (in the code)
    //effects: saves a text file containing saved items in cart to your computer
    public static void save () {
        try {
            //opens computer file manager
            fc.setTitle("Save list");
            File saveFile = fc.showSaveDialog(null);
            fc.getExtensionFilters().clear();
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt")); //limits to text files only
            //saves path to String path
            String path = saveFile.getAbsolutePath();
            FileWriter fw = new FileWriter(path, false);
            BufferedWriter bw = new BufferedWriter(fw); //BufferWriter writes text to character-output streams (e.g. writes arrays)
            for (Item item : foods)
                bw.write(item.arrayWrite() + "\n");
            bw.close();
        } catch (Exception ignored) {} //try catch prevents error from displaying in console when cancelling saving a file
    }


}
