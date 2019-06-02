package com.github.Mr0Unknown.Checkin;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.HashMap;

/**
 * Checkin.Main Class
 *
 * @author HeFan
 * @date 2019/5/26
 */
public class Main extends JavaPlugin implements Listener {
    private FileConfiguration config;
    private FileConfiguration item;
    private String qianzhui = new String("qiandao");
    private HashMap<String,Inventory> map = new HashMap<>();
    @Override
    public void onEnable() {
        getLogger().info("签到插件Checkin1.0.0版本已载入");
        getServer().getPluginManager().registerEvents(this,this);
        File configFile = new File(this.getDataFolder(),"config.yml");
        File itemFile = new File(this.getDataFolder(),"item.yml");
        if(!configFile.exists()){
            this.saveResource("config.yml",true);
        }
        if(!itemFile.exists()){
            this.saveResource("item.yml",true);
        }
        this.config = YamlConfiguration.loadConfiguration(configFile);
        this.item = YamlConfiguration.loadConfiguration(itemFile);
    }

    @Override
    public void onDisable() {
        getLogger().info("已卸载签到插件Checkin1.0.0");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase(qianzhui)) {
            if (args[0].equalsIgnoreCase("create")) {
                if(args[1].length() == 0){
                    sender.sendMessage("§a请按照/qiandao create [礼包名] [显示标题] 创建");
                }
                else{
                    sender.sendMessage("§a已经创建礼包["+args[1]+"§a]");
                    //较为明显 此为礼包名
                    String giftname = args[1];
                    //较为明显 此为礼包标题
                    String title = args[2];
                    //创建容器
                    Inventory gift = Bukkit.createInventory(null,54,title);
                        map.put(giftname, gift);
                }
            }
            else if (args[0].equalsIgnoreCase("remove")){
                String giftname = args[1];
                //判定礼包存在不存在
                if(map.containsKey(giftname)){
                    map.remove(giftname);
                }
                else{
                    sender.sendMessage("§c该礼包不存在");
                }
            }
            else if (args[0].equalsIgnoreCase("add")){
                if(!(sender instanceof Player)){
                    sender.sendMessage("该指令不允许在控制台使用!");
                }
                else {
                    Player player = (Player) sender;
                    //获取礼包名
                    String giftname = args[1];
                    //获取玩家手中物品
                    ItemStack hand = player.getInventory().getItemInHand();
                    //从HashMap的变量名为map中找出键为giftname的值(Inventory)
                    if (map.containsKey(giftname)) {
                        map.get(giftname).addItem(hand);
                    }
                }
            }
            else if (args[0].equalsIgnoreCase("get")){

            }
            else if (args[0].equalsIgnoreCase("show")){

            }
            else if (args[0].equalsIgnoreCase("help")){
                sender.sendMessage("§a/"+qianzhui+" create [礼包名] [显示标题]----创建礼包");
                sender.sendMessage("§a/"+qianzhui+" remove [礼包名]----删除礼包");
                sender.sendMessage("§a/"+qianzhui+" add [礼包名]----把手中物品添加进礼包");
                sender.sendMessage("§a/"+qianzhui+" get [礼包名]----领取礼包");
                sender.sendMessage("§a/"+qianzhui+" show [礼包名]----展示礼包");
            }
            else {
                sender.sendMessage("§a/"+qianzhui+" create [礼包名] [显示标题]----创建礼包");
                sender.sendMessage("§a/"+qianzhui+" remove [礼包名]----删除礼包");
                sender.sendMessage("§a/"+qianzhui+" add [礼包名]----把手中物品添加进礼包");
                sender.sendMessage("§a/"+qianzhui+" get [礼包名]----领取礼包");
                sender.sendMessage("§a/"+qianzhui+" show [礼包名]----展示礼包");
            }
        }
        return false;
    }
}