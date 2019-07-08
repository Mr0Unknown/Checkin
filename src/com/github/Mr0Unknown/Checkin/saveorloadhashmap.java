package com.github.Mr0Unknown.Checkin;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.inventory.ItemStack;

import java.io.*;
public class saveorloadhashmap {
    protected static <T extends Object> void saveglobalgift(T globalgift, String path) throws Exception {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path));
            oos.writeObject(globalgift);
            oos.flush();
            oos.close();
    }
    protected static <T extends Object> void saveothersitem(T othersitem, String path) throws Exception {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path));
            oos.writeObject(othersitem);
            oos.flush();
            oos.close();
    }
    protected static <T extends Object> T loadglobalgift(String path) throws Exception {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path));
            T result = (T)ois.readObject();
            return result;
    }
    protected static <T extends Object> T loadothersitem(String path) throws Exception {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path));
            T result = (T)ois.readObject();
            return result;
    }
    protected static final void loadedglobalgift(String path){
        try {
            File file = new File(path);
            if (file.exists()) {
                Main.globalgift = loadglobalgift(path);
            }
            else {
                file.createNewFile();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    protected static final void loadedothersitem(String path){
        try {
            File file = new File(path);
            if (file.exists()){
                Main.othersitem = loadothersitem(path);
            }
            else {
                file.createNewFile();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
