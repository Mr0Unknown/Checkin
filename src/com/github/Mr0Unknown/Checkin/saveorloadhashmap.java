package com.github.Mr0Unknown.Checkin;

import java.util.HashMap;
import org.bukkit.inventory.ItemStack;

import java.io.*;
public  class saveorloadhashmap {
    protected void savehashmap(HashMap<String,HashMap<String,ItemStack>> map, String path){
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path));
            oos.writeObject(map);
            oos.flush();
            oos.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    protected HashMap<String, HashMap<String, ItemStack>> loadhashmap(String path) {
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path));
            Object result = ois.readObject();
            return (HashMap<String,HashMap<String, ItemStack>>)result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public void loadedhashmap(){
        Main access = new Main();
        String path = access.getDataFolder()+ File.separator+"allgift.bin";
        File file = new File(path);
        if(file.exists()){
            access.allgift = loadhashmap(path);
        }
    }
}
