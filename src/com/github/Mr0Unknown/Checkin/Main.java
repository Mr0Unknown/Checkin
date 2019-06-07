package com.github.Mr0Unknown.Checkin;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.inventory.ItemStack;

import java.io.*;
import java.util.HashMap;

/**
 * Checkin.Main Class
 *
 * @author HeFan
 * @date 2019/5/26
 */
public class Main extends JavaPlugin {
    private FileConfiguration config;
    private FileConfiguration item;
    private String qianzhui = new String("qiandao");
    protected HashMap<String, HashMap<String, ItemStack>> allgift = new HashMap<String, HashMap<String, ItemStack>>();
    protected HashMap<String, ItemStack> songift = new HashMap<String, ItemStack>();

    @Override
    public void onEnable() {
        getLogger().info("签到插件Checkin1.0.0版本已载入");
        getServer().getPluginManager().registerEvents(new Listener(), this);
        File configFile = new File(this.getDataFolder(), "config.yml");
        File itemFile = new File(this.getDataFolder(), "item.yml");
        if (!configFile.exists()) {
            this.saveResource("config.yml", true);
        }
        if (!itemFile.exists()) {
            this.saveResource("item.yml", true);
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
            Player player = (Player) sender;
            saveorloadhashmap access = new saveorloadhashmap();
            if (args[0].equalsIgnoreCase("create")) {
                if (player.hasPermission("Checkin.create")) {
                    if (args[1].length() == 0) {
                        sender.sendMessage("§a请按照/qiandao create [礼包名]创建");
                    } else {
                        //获取礼包名字
                        String giftname = args[1];
                        if (allgift.containsKey(args[1])) {
                            sender.sendMessage("§c礼包[" + args[1] + "§a]已存在");
                        } else {
                            allgift.put(giftname, null);
                            sender.sendMessage("§a礼包[" + giftname + "]创建成功");
                            access.savehashmap(allgift, getDataFolder() + File.separator + "allgift.bin");
                        }
                    }
                } else {
                    this.donthavePermission(sender);
                }
            } else if (args[0].equalsIgnoreCase("remove")) {
                if (player.hasPermission("Checkin.remove")) {
                    access.loadedhashmap();
                    String giftname = args[1];
                    //判定礼包存在不存在
                    if (allgift.containsKey(giftname)) {
                        allgift.remove(giftname);
                    } else {
                        sender.sendMessage("§c该礼包不存在");
                    }
                } else {
                    this.donthavePermission(sender);
                }
            } else if (args[0].equalsIgnoreCase("add")) {
                if (player.hasPermission("Checkin.add")) {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage("该指令不允许在控制台使用!");
                    } else {
                        access.loadedhashmap();
                        //获取礼包名
                        String giftname = args[1];
                        if (allgift.containsKey(giftname)) {
                            //获取玩家手中物品
                            ItemStack hand = player.getInventory().getItemInHand();
                        }
                    }
                } else {
                    this.donthavePermission(sender);
                }
            } else if (args[0].equalsIgnoreCase("get")) {
                if (player.hasPermission("Checkin.get")) {
                    if (sender instanceof Player) {
                    }
                }
            } else if (args[0].equalsIgnoreCase("help")) {
                this.help(sender);
            } else {
                this.help(sender);
            }
        }
        return false;
    }

    public void donthavePermission(CommandSender sender) {
        sender.sendMessage("§c你没有权限这么做");
    }

    public void help(CommandSender sender) {
        sender.sendMessage("§a/" + qianzhui + " create [礼包名] [显示标题]----创建礼包");
        sender.sendMessage("§a/" + qianzhui + " remove [礼包名]----删除礼包");
        sender.sendMessage("§a/" + qianzhui + " add [礼包名]----把手中物品添加进礼包");
        sender.sendMessage("§a/" + qianzhui + " get [礼包名]----领取礼包");
    }

    public void donthavethisgift(CommandSender sender, String giftname) {
        sender.sendMessage("§c" + giftname + "这个礼包不存在");
    }
}