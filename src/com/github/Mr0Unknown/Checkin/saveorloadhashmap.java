package com.github.Mr0Unknown.Checkin;

import java.util.HashMap;
import org.bukkit.inventory.ItemStack;

import java.io.*;
public class saveorloadhashmap {
    protected static final void saveglobalgift(HashMap<String,HashMap<String,ItemStack>> globalgift, String path){
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path));
            oos.writeObject(globalgift);
            oos.flush();
            oos.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    protected static final void saveothersitem(HashMap<String,HashMap<Integer,ItemStack>> othersitem, String path) {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path));
            oos.writeObject(othersitem);
            oos.flush();
            oos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    protected static final HashMap<String, HashMap<String, ItemStack>> loadglobalgift(String path) {
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path));
            Object result = ois.readObject();
            return (HashMap<String,HashMap<String, ItemStack>>)result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    protected static final HashMap<String, HashMap<Integer,ItemStack>> loadothersitem(String path) {
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path));
            Object result = ois.readObject();
            return (HashMap<String,HashMap<Integer, ItemStack>>)result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    protected static final void loadedhashmap(String path){
        Main access = new Main();
        File file = new File(path);
        if(file.exists()){
            if(path.equalsIgnoreCase(access.globalgiftpath)) {
                Main.globalgift = loadglobalgift(path);
            }
            else if(path.equalsIgnoreCase(access.othersitempath)){
                Main.othersitem = loadothersitem(path);
            }
        }
    }
}
