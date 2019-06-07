package com.github.Mr0Unknown.Checkin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;

public final class Listener implements org.bukkit.event.Listener {
    @EventHandler
    public void getgift(InventoryClickEvent event){
        if(event.getWhoClicked() instanceof Player == false){
            //执行什么
        }
    }
}
