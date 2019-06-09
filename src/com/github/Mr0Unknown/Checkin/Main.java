package com.github.Mr0Unknown.Checkin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.inventory.ItemStack;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Checkin.Main Class
 *
 * @author HeFan
 * @date 2019/5/26
 */
public class Main extends JavaPlugin {
    saveorloadhashmap access = new saveorloadhashmap();
    private FileConfiguration config;
    private String prefix = new String("qiandao");
    protected static HashMap<String, HashMap<String, ItemStack>> globalgift = new HashMap<String, HashMap<String, ItemStack>>();
    protected final String globalgiftpath = new String(getDataFolder() + File.separator + "globalgift.bin");

    @Override
    public void onEnable() {
        getLogger().info("签到插件Checkin1.0.0版本已载入");
        getServer().getPluginManager().registerEvents(new Listener(), this);
        File configFile = new File(this.getDataFolder(), "config.yml");
        if (!this.getDataFolder().exists()) {
            getDataFolder().mkdir();
        }
        if (!configFile.exists()) {
            saveDefaultConfig();
        }
        this.reloadConfig();
        config = YamlConfiguration.loadConfiguration(configFile);
        File globalgiftfile = new File(this.getDataFolder(),"globalgift.bin");
        File partgiftfile = new File(this.getDataFolder(),"partgift.bin");
        if(!globalgiftfile.exists()) {
            access.saveglobalgift(globalgift,globalgiftpath);
            access.loadedhashmap(globalgiftpath);
        }
        else {
            access.loadedhashmap(globalgiftpath);
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("已卸载签到插件Checkin1.0.0");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase(prefix)) {
            Player player = (Player) sender;
            if (args[0].equalsIgnoreCase("create")) {
                if (player.hasPermission("Checkin.create")) {
                    if (args[1].length() == 0) {
                        this.donthavegiftname(sender);
                    }
                    else {
                        //获取礼包名字
                        String giftname = args[1];
                        if (globalgift.containsKey(args[1])) {
                            sender.sendMessage("§c礼包[" + args[1] + "§a]已存在");
                        }
                        else {
                            globalgift.put(giftname, null);
                            sender.sendMessage("§a礼包[" + giftname + "]创建成功");
                            access.saveglobalgift(globalgift, globalgiftpath);
                        }
                    }
                }
                else {
                    this.donthavePermission(sender);
                }
            }

            else if (args[0].equalsIgnoreCase("add")) {
                if (player.hasPermission("Checkin.add")) {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage("该指令不允许在控制台使用!");
                    }
                    else {
                        if (args[1].length() == 0) {
                            this.donthavegiftname(sender);
                        }
                        else {
                            //获取礼包名
                            String giftname = args[1];
                            if (globalgift.containsKey(giftname)) {
                                //创建临时储存物品的HashMap
                                HashMap<String,ItemStack> happens = new HashMap<String,ItemStack>();
                                //获取玩家手中物品
                                ItemStack hand = player.getInventory().getItemInHand();
                                //玩家背包只有33格，所以space最多为33
                                for (Integer space = 0; space <= 33 + 1; space++) {
                                    if (space <= 33) {
                                        if (!globalgift.get(giftname).containsKey(space.toString())) {
                                            //把这个物品储存到globalgift的指定值
                                            globalgift.get(giftname).put(space.toString(), hand);
                                            access.saveglobalgift(globalgift, globalgiftpath);
                                            break;
                                        }
                                    }
                                    else if (space > 33) {
                                        sender.sendMessage("§c已经超出礼包数量设置范围,最多只能设置33个");
                                        break;
                                    }
                                }
                            }
                            else {
                                this.donthavethisgift(sender,giftname);
                            }
                        }
                    }
                }
                else {
                    this.donthavePermission(sender);
                }
            }
            else if (args[0].equalsIgnoreCase("remove")) {
                if (player.hasPermission("Checkin.remove")) {
                    if (args[1].length() == 0) {
                        this.donthavegiftname(sender);
                    }
                    else {
                        String giftname = args[1];
                        //判定礼包存在不存在
                        if (globalgift.containsKey(giftname)) {
                            globalgift.remove(giftname);
                            access.saveglobalgift(globalgift, globalgiftpath);
                        }
                        else {
                            this.donthavethisgift(sender, giftname);
                        }
                    }
                }
                else {
                    this.donthavePermission(sender);
                }
            }
            else if (args[0].equalsIgnoreCase("set")){
                if (player.hasPermission("Checkin.set")){
                    if (args[1].length() == 0){
                        sender.sendMessage("§c类型不能为空");
                    }
                    else {
                        if (args[2].length() == 0) {
                            sender.sendMessage("§c礼包名不能为空");
                        }
                        else{
                            if(args[3].length() == 0){
                                sender.sendMessage("[天数/权限]不能为空");
                            }
                            else{
                                if(args[1].equalsIgnoreCase("时间")){

                                }
                                else if(args[1].equalsIgnoreCase("权限")){

                                }
                                else{
                                    sender.sendMessage("§c不存在该类型");
                                }
                            }
                        }
                    }
                }
            }
            else if (args[0].equalsIgnoreCase("get")) {
                if (player.hasPermission("Checkin.get")) {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage("该指令不允许在控制台使用!");
                    }
                    else{
                        if(args[1].length()==0){
                            this.donthavegiftname(sender);
                        }
                        else {
                            String giftname = args[1];
                            if(globalgift.containsKey(giftname)){
                                if(globalgift.get(giftname)==null){
                                    sender.sendMessage("§c这个礼包内容是空的");
                                }
                                else{
                                    for(HashMap.Entry<String,ItemStack> entry:globalgift.get(giftname).entrySet()) {
                                        for (Integer space=0;space<=33;space++){
                                           player.getInventory().addItem(entry.getValue());
                                        }
                                    }
                                    sender.sendMessage("§a成功领取礼包["+giftname+"]");
                                }
                            }
                            else{
                                this.donthavethisgift(sender,giftname);
                            }
                        }
                    }
                }
                else {
                    this.donthavePermission(sender);
                }
            }
            else if (args[0].equalsIgnoreCase("list")){
                sender.sendMessage("§a已经存在的礼包:");
                for (HashMap.Entry<String,HashMap<String,ItemStack>> entry:globalgift.entrySet()){
                    sender.sendMessage("§a"+entry.getKey());
                }
                sender.sendMessage("§a--------------------");
            }
            else if (args[0].equalsIgnoreCase("help")) {
                this.help(sender);
            } else {
                sender.sendMessage("§c指令错误!请使用"+prefix+" help查询指令");
            }
        }
        return false;
    }

    private void donthavePermission(CommandSender sender) {
        sender.sendMessage("§c你没有权限这么做");
    }

    private void help(CommandSender sender) {
        sender.sendMessage("§a/" + prefix + " create [礼包名]----创建礼包");
        sender.sendMessage("§a/" + prefix + " add [礼包名]----把手中物品添加进礼包");
        sender.sendMessage("§a/" + prefix + " remove [礼包名]----删除礼包");
        sender.sendMessage("§a/" + prefix + " set [类型] [礼包名] [天数/权限]----礼包类型分别是 时间 权限");
        sender.sendMessage("§a/" + prefix + " get [礼包名]----领取礼包");
        sender.sendMessage("§a/" + prefix + " list----礼包列表");
    }

    private void donthavethisgift(CommandSender sender, String giftname) {
        sender.sendMessage("§c" + giftname + "这个礼包不存在");
    }
    private void donthavegiftname(CommandSender sender){
        sender.sendMessage("§c礼包名不能为空");
    }
}