package com.github.Mr0Unknown.Checkin;

import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.inventory.ItemStack;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map;

/**
 * Checkin.Main Class
 *
 * @author HeFan
 * @date 2019/5/26
 */
public class Main extends JavaPlugin {
    private FileConfiguration config;
    private FileConfiguration players;
    private FileConfiguration decision;
    private Integer howdays;
    private String prefix = new String();
    private String messageprefix = new String();
    protected String othersitempath = new String(this.getDataFolder() + File.separator + "Binary" + File.separator + "othersitem.bin");
    /**创建以globalgift为命名的HashMap类型成员变量来储存礼包*/
    protected static HashMap<String,HashMap<String, ItemStack>> globalgift = new HashMap<>();
    protected String globalgiftpath = new String(this.getDataFolder() + File.separator + "Binary" + File.separator + "globalgift.bin");
    /***/

    /**创建以othersitem为命名的HashMap类型成员变量来储存领取礼包时多余的物品*/
    protected static HashMap<String,HashMap<Integer,ItemStack>> othersitem =  new HashMap<>();
    /***/

    @Override
    public void onEnable() {
        getLogger().info("签到插件[Checkin]1.5.1版本已载入");
        getLogger().info("使用插件过程中若出现问题请联系作者QQ:382327683");
        File configFile = new File(this.getDataFolder(), "config.yml");
        File playersfile = new File(this.getDataFolder(),"players.yml");
        File decisionfile = new File(this.getDataFolder(),"decision.yml");
        if (!this.getDataFolder().exists()) {
            getDataFolder().mkdir();
        }
        String binarydirpath = new String(this.getDataFolder() + File.separator + "Binary");
        File binarydir = new File(binarydirpath);
        if (!binarydir.exists()){
            binarydir.mkdir();
        }
        /**下面调用该类的方法createfile来判定文件的存在与创建*/
            this.createfile(decisionfile);
            decision = YamlConfiguration.loadConfiguration(decisionfile);
            this.createfile(playersfile);
            players = YamlConfiguration.loadConfiguration(playersfile);
            this.createfile(configFile);
        /***/
        if (configFile.exists() && configFile.length() == 0 ) {
            this.saveResource("config.yml",true);
        }
        this.reloadConfig();
        config = YamlConfiguration.loadConfiguration(configFile);
        prefix = config.getString("prefix.command");
        messageprefix = config.getString("prefix.message").replace("&","§");

        /**加载和创建HashMap*/
        File othersitemfile = new File(this.getDataFolder() + File.separator + "Binary" ,"othersitem.bin");
        File globalgiftfile = new File(this.getDataFolder() + File.separator + "Binary","globalgift.bin");
        if (globalgiftfile.exists()){
            //加载HashMap globalgift
            saveorloadhashmap.loadedglobalgift(globalgiftpath);
        }
        else {
            try {
                globalgiftfile.createNewFile();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        if (othersitemfile.exists()) {
            //加载HashMap othersitem
            saveorloadhashmap.loadedothersitem(othersitempath);
        }
        else {
            try{
                othersitemfile.createNewFile();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        /***/

    }

    @Override
    public void onDisable() {
        File playersfile = new File(this.getDataFolder(),"players.yml");
        File decisionfile = new File(this.getDataFolder(), "decision.yml");
        File configfile = new File(this.getDataFolder(), "config.yml");
        try {
            players.save(playersfile);
            decision.save(decisionfile);
            config.save(configfile);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        try {
            saveorloadhashmap.saveglobalgift(globalgift, globalgiftpath);
            saveorloadhashmap.saveothersitem(othersitem, othersitempath);
        }
        catch (Exception e){
            getLogger().info("服务器保存礼包哈希图时出现了错误:");
            e.printStackTrace();
        }
        getLogger().info("已卸载签到插件[Checkin]1.5.1");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase(prefix)) {
            if (args.length < 1) {
                this.help(sender);
            }
            else {
                if (args[0].equalsIgnoreCase("create")) {
                    if (sender.hasPermission("Checkin.create")) {
                        //判断args[1](礼包名)是否为空
                        if (args.length < 2) {
                            //调用donthavegiftname给玩家发送消息
                            this.donthavegiftname(sender);
                        }
                        else {
                            //giftname获取礼包名字
                            String giftname = args[1];
                            //判断是否有该礼包
                            if (globalgift.containsKey(args[1])) {
                                sender.sendMessage(messageprefix + "§c礼包[" + args[1] + "§a]已存在");
                            } else {
                                //把变量giftname作为HashMap的键加到globalgift并且设置值为null先
                                globalgift.put(giftname, null);
                                sender.sendMessage(messageprefix + "§a礼包[" + giftname + "]创建成功");
                                /**decision设置可领取次数 默认为-1可无限领取 可通过其他指令修改*/
                                decision.set(giftname + ".how",-1);
                                File decisionfile = new File(this.getDataFolder(),"decision.yml");
                                this.savefile(decisionfile,decision);
                                /***/
                                //保存globalgift到本地
                                try {
                                    saveorloadhashmap.saveglobalgift(globalgift, globalgiftpath);
                                }
                                catch (Exception e){
                                    getLogger().info("服务器在保存礼包哈希图时出现了点错误:");
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    else {
                        //调用方法donhavePermission给玩家发送没权限的信息
                        this.donthavePermission(sender);
                    }
                }
                else if (args[0].equalsIgnoreCase("add")) {
                    if (sender.hasPermission("Checkin.add")) {
                        /**判断是不是控制台在操作*/
                        if (!(sender instanceof Player)) {
                            this.servercoulddothat(sender);
                        }
                        /***/
                        else {
                            //把sender强行转换成Player类
                            Player player = (Player) sender;
                            if (args.length < 2) {
                                this.donthavegiftname(sender);
                            }
                            else {
                                //获取礼包名
                                String giftname = args[1];
                                //判断是否有该礼包
                                if (globalgift.containsKey(giftname)) {
                                    //创建临时储存物品的HashMap并赋值为giftname的值
                                    HashMap<String,ItemStack> happens = new HashMap<>();
                                    /**下面给happens赋值 并且使用addgift方法添加礼物 addgift*/
                                        happens = globalgift.get(giftname);
                                        this.addgift(player,happens,giftname);
                                     /***/
                                }
                                else {
                                    this.donthavethisgift(sender, giftname);
                                }
                            }
                        }
                    }
                    else {
                        this.donthavePermission(sender);
                    }
                }
                else if (args[0].equalsIgnoreCase("remove")) {
                    if (sender.hasPermission("Checkin.remove")) {
                        if (args.length < 2) {
                            this.donthavegiftname(sender);
                        }
                        else {
                            String giftname = args[1];
                            //判定礼包存在不存在
                            if (globalgift.containsKey(giftname)) {
                                //删除以giftname为名的键
                                globalgift.remove(giftname);
                                //保存
                                sender.sendMessage(messageprefix + "§c已经成功删除礼包["+giftname+"]");
                                try {
                                    saveorloadhashmap.saveglobalgift(globalgift, globalgiftpath);
                                }
                                catch (Exception e){
                                    getLogger().info("服务器在保存哈希图礼包时出现了错误:");
                                    e.printStackTrace();
                                }
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
                else if (args[0].equalsIgnoreCase("set")) {
                    if (sender.hasPermission("Checkin.set")) {
                        if (args.length < 2) {
                            sender.sendMessage(messageprefix + "§c类型不能为空");
                        } else {
                            if (args.length < 3) {
                                sender.sendMessage(messageprefix + "§c礼包名不能为空");
                            } else {
                                if (args.length < 4) {
                                    sender.sendMessage(messageprefix + "[所需时间/权限/次数]不能为空");
                                } else {
                                    if (args[1].equalsIgnoreCase("时间")) {
                                        //获取礼包名字 这样比较写鲜明
                                        String giftname = args[2];
                                        //判断是否存在该礼包
                                        if (globalgift.containsKey(giftname)) {
                                            //创建decisionfile并调用createfile判断是否有创建，若有不处理，无就创建
                                            File decisionfile = new File(this.getDataFolder(), "decision.yml");
                                            this.createfile(decisionfile);
                                            try {
                                                //decide用于判断玩家输入的是不是非数字
                                                //若不是数字则会转到catch语句执行
                                                //args[3]为天数
                                                //写入decision中
                                                int done = Integer.parseInt(args[3]);
                                                decision.set(giftname + ".time",done);
                                                //保存decision.yml
                                                this.savefile(decisionfile, decision);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                                sender.sendMessage(messageprefix + "天数必须是数字(若是小数则自动取小数点前的值),不能是其他类型");
                                            }
                                            sender.sendMessage(messageprefix + "§a成功把礼包["+giftname+"]设置成每隔"+decision.getString(giftname+".time")+"天可领取");
                                        }
                                        else{
                                            this.donthavethisgift(sender,giftname);
                                        }
                                    }
                                    else if (args[1].equalsIgnoreCase("权限")) {
                                        String giftname = args[2];
                                        if (globalgift.containsKey(giftname)) {
                                            //创建decisionfile并调用createfile判断是否有创建，若有不处理，无就创建
                                            File decisionfile = new File(this.getDataFolder(), "decision.yml");
                                            this.createfile(decisionfile);
                                            decision.set(giftname + ".permission", args[3]);
                                            try {
                                                this.savefile(decisionfile, decision);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            sender.sendMessage(messageprefix + "§a成功把礼包["+giftname+"]设置成有该权限:"+decision.getString(giftname+".permission")+" 可领取");
                                        }
                                    }
                                    else if (args[1].equalsIgnoreCase("次数")){
                                        String giftname = args[2];
                                        try{
                                            Integer how = new Integer(args[3]);
                                            File decisionfile = new File(this.getDataFolder(),"decision.yml");
                                            this.createfile(decisionfile);
                                            decision.set(giftname + ".how",args[3]);
                                        }
                                        catch (Exception e){
                                            e.printStackTrace();
                                            decision.set(giftname + ".how",-1);
                                            sender.sendMessage("次数必须是整数,请重新设置");
                                        }

                                    }
                                    else {
                                        sender.sendMessage(messageprefix + "§c不存在该类型");
                                    }
                                }
                            }
                        }
                    }
                }
                else if (args[0].equalsIgnoreCase("get")) {
                    if (sender.hasPermission("Checkin.get")) {
                        if (!(sender instanceof Player)) {
                            sender.sendMessage(messageprefix + "该指令不允许在控制台使用!");
                        }
                        else{
                            Player player = (Player) sender;
                            if(args.length < 2){
                                this.donthavegiftname(sender);
                            }
                            else {
                                String giftname = args[1];
                                File decisionfile = new File(this.getDataFolder(),"decision.yml");

                                /**判断decision.yml中礼包是否设置过permission(权限)time(相隔天数)*/
                                boolean booleanpermission = decision.contains(giftname+".permission");
                                boolean booleantime = decision.contains(giftname+".time");
                                Integer giftshow = decision.getInt(giftname + ".how");
                                int playershow;
                                boolean booleanplayershow = players.contains(player.getName()+ "." +giftname + ".how");
                                if (booleanplayershow == true) {
                                    playershow = players.getInt(player.getName()+ "." +giftname + ".how");
                                }
                                else{
                                    players.set(player.getName() + "." +giftname + ".how",0);
                                    playershow = 0;
                                }
                                /***/
                                //只设置过permission(权限)
                                if (booleantime == false){
                                    if (booleanpermission == true) {
                                        if (giftshow == -1) {
                                            if (player.hasPermission(decision.getString(giftname + ".permission"))) {
                                                this.getgift(sender, player, giftname);
                                            } else {
                                                sender.sendMessage(messageprefix + "§c你没有权限领取该礼包");
                                            }
                                        }
                                        else {
                                            if (giftshow > playershow){
                                                if (player.hasPermission(decision.getString(giftname + ".permission"))) {
                                                    this.getgift(sender, player, giftname);
                                                    ++playershow;
                                                    players.set(player.getName()+"."+giftname+".how",playershow);
                                                    File playersfile = new File(this.getDataFolder(),"players.yml");
                                                    this.savefile(playersfile,players);
                                                } else {
                                                    sender.sendMessage(messageprefix + "§c你没有权限领取该礼包");
                                                }
                                            }
                                            else {
                                                sender.sendMessage(messageprefix + "§c你已经领取了"+giftshow.toString()+"次该礼包了,不能再领取了");
                                            }
                                        }
                                    }
                                }
                                //只设置过time(时间)
                                if (booleanpermission == false){
                                    if (giftshow == -1) {
                                        if (booleantime == true) {
                                            boolean decide = this.booleangetgift(sender, player, giftname, players, decision);
                                            if (decide == true) {
                                                this.getgift(sender, player, giftname);
                                                Calendar calendar = Calendar.getInstance();
                                                int year = calendar.get(Calendar.YEAR);
                                                int month = calendar.get(Calendar.MONTH);
                                                int day = calendar.get(Calendar.DAY_OF_MONTH);
                                                players.set(player.getName() + "." + giftname + ".time.year",year);
                                                players.set(player.getName() + "." + giftname + ".time.month",month);
                                                players.set(player.getName() + "." + giftname + ".time.day",day);
                                                File playersfile = new File(this.getDataFolder(), "players.yml");
                                                this.savefile(playersfile, players);
                                            }
                                            else {
                                                sender.sendMessage(messageprefix + "§c距离下一次领取礼包[" + giftname + "]时间需" + howdays.toString() + "天之内");
                                            }
                                        }
                                    }
                                    else {
                                        if (giftshow > playershow){
                                            if (booleantime == true) {
                                                boolean decide = this.booleangetgift(sender, player, giftname, players, decision);
                                                if (decide == true) {
                                                    this.getgift(sender, player, giftname);
                                                    SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
                                                    Date now = new Date();
                                                    String temporay = date.format(now);
                                                    players.set(player.getName() + "." + giftname + ".time", temporay);
                                                    ++playershow;
                                                    players.set(player.getName()+"."+giftname+".how",playershow);
                                                    File playersfile = new File(this.getDataFolder(),"players.yml");
                                                    this.savefile(playersfile,players);
                                                } else {
                                                    sender.sendMessage(messageprefix + "§c距离下一次领取礼包[" + giftname + "]时间需" + howdays.toString() + "天之内");
                                                }
                                            }
                                        }
                                        else{
                                            sender.sendMessage(messageprefix + "§c你已经领取了"+giftshow.toString()+"次该礼包了,不能再领取了");
                                        }
                                    }
                                }
                                //两者都设置了
                                if (booleantime == true) {
                                    if (giftshow == -1) {
                                        if (booleanpermission == true) {
                                            if (player.hasPermission(decision.getString(giftname + ".permission"))) {
                                                boolean decide = this.booleangetgift(sender, player, giftname, players, decision);
                                                if (decide == true) {
                                                    this.getgift(sender, player, giftname);
                                                } else {
                                                    sender.sendMessage(messageprefix + "§c距离下一次领取礼包[" + giftname + "]时间需" + howdays.toString() + "天之内");
                                                }
                                            } else {
                                                sender.sendMessage(messageprefix + "§c您没有权限这么做。");
                                            }
                                        }
                                    }
                                    else {
                                        if (giftshow > playershow){
                                            if (booleanpermission == true) {
                                                if (player.hasPermission(decision.getString(giftname + ".permission"))) {
                                                    boolean decide = this.booleangetgift(sender, player, giftname, players, decision);
                                                    if (decide == true) {
                                                        this.getgift(sender, player, giftname);
                                                        ++playershow;
                                                        players.set(player.getName()+"."+giftname+".how",playershow);
                                                        File playersfile = new File(this.getDataFolder(),"players.yml");
                                                        this.savefile(playersfile,players);
                                                    }
                                                    else {
                                                        sender.sendMessage(messageprefix + "§c距离下一次领取礼包[" + giftname + "]时间需" + howdays.toString() + "天之内");
                                                    }
                                                } else {
                                                    sender.sendMessage(messageprefix + "§c您没有权限这么做。");
                                                }
                                            }
                                        }
                                        else{
                                            sender.sendMessage(messageprefix + "§c你已经领取了"+giftshow.toString()+"次该礼包了,不能再领取了");
                                        }
                                    }
                                }
                                if (booleantime == booleanpermission){
                                    if (booleantime == false) {
                                        sender.sendMessage("§c该礼包没有设置领取时所需条件 请联系服主设置");
                                    }
                                }
                            }
                        }
                    }
                    else {
                        this.donthavePermission(sender);
                    }
                }
                else if (args[0].equalsIgnoreCase("getothers")){
                    if (!(sender instanceof Player)) {
                        this.servercoulddothat(sender);
                    }
                    else {
                        Player player = (Player) sender;
                        if (sender.hasPermission("Checkin.getothers")) {
                            if (othersitem.containsKey(player.getName())) {
                                for (Integer space = 1; space <= 33; space++) {
                                    if (player.getInventory().firstEmpty() == -1) {
                                        sender.sendMessage(messageprefix + "§c你的背包空间不足!");
                                    }
                                    else {
                                        player.getInventory().addItem(othersitem.get(player.getName()).get(space));
                                        HashMap<Integer,ItemStack> happens = new HashMap<>();
                                        happens = othersitem.get(player.getName());
                                        player.getInventory().addItem(happens.get(space));
                                        happens.remove(space);
                                        othersitem.put(player.getName(),happens);
                                        try {
                                            saveorloadhashmap.saveothersitem(othersitem, othersitempath);
                                        }
                                        catch (Exception e){
                                            getLogger().info("服务器在保存剩余物品的哈希图时出现了错误:");
                                            e.printStackTrace();
                                        }
                                        sender.sendMessage( messageprefix + "成功领取之前剩下的礼包物品");
                                    }
                                }
                            }
                            else {
                                sender.sendMessage(messageprefix + "§c你已经领取完所有剩下的礼包物品");
                            }
                        }
                    }
                }
                else if (args[0].equalsIgnoreCase("list")) {
                    if (sender.hasPermission("Checkin.list")){
                        sender.sendMessage(messageprefix + ":");
                        sender.sendMessage("§a已经存在的礼包:");
                        sender.sendMessage("§a--------------------");
                        for (HashMap.Entry<String,HashMap<String, ItemStack>> entry : globalgift.entrySet()) {
                            sender.sendMessage("§a" + entry.getKey());
                        }
                        sender.sendMessage("§a--------------------");
                    }
                }
                else if (args[0].equalsIgnoreCase("reload")) {
                    if (sender.hasPermission("Checkin.reload")) {
                        this.reloadConfig();
                        File playersfile = new File(this.getDataFolder(), "players.yml");
                        File decisionfile = new File(this.getDataFolder(), "decision.yml");
                        File configfile = new File(this.getDataFolder(), "config.yml");
                        if (playersfile.exists()) {
                            players = YamlConfiguration.loadConfiguration(playersfile);
                        } else {
                            this.createfile(playersfile);
                        }
                        if (decisionfile.exists()) {
                            decision = YamlConfiguration.loadConfiguration(decisionfile);
                        } else {
                            this.createfile(decisionfile);
                        }
                        if (configfile.exists()) {
                            config = YamlConfiguration.loadConfiguration(configfile);
                        } else {
                            this.saveDefaultConfig();
                        }
                        String binarydirpath = new String(this.getDataFolder() + File.separator + "Binary");
                        File binarydir = new File(binarydirpath);
                        if (!binarydir.exists()){
                            binarydir.mkdir();
                        }
                        File othersitemfile = new File(this.getDataFolder() + File.separator + "Binary" ,"othersitem.bin");
                        File globalgiftfile = new File(this.getDataFolder() + File.separator + "Binary","globalgift.bin");
                        if (globalgiftfile.exists()){
                            //加载HashMap globalgift
                            saveorloadhashmap.loadedglobalgift(globalgiftpath);
                        }
                        else {
                            try {
                                globalgiftfile.createNewFile();
                            }
                            catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                        if (othersitemfile.exists()) {
                            //加载HashMap othersitem
                            saveorloadhashmap.loadedothersitem(othersitempath);
                        }
                        else {
                            try{
                                othersitemfile.createNewFile();
                            }
                            catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                        sender.sendMessage(messageprefix + "配置文件已经重载");

                    }
                }
                else if (args[0].equalsIgnoreCase("help")) {
                    this.help(sender);
                }
                else {
                    sender.sendMessage(messageprefix + "§c指令错误!请使用" + prefix + " help查询指令");
                }
            }
        }
        return false;
    }
    private void donthavePermission(CommandSender sender) {
        sender.sendMessage(messageprefix + "§c你没有权限这么做");
    }

    private void servercoulddothat(CommandSender sender){
        sender.sendMessage(messageprefix + "控制台不能使用该指令");
    }
    private void createfile(File file){
        //判断是否有该文件
        try{
            if (!file.exists()) {
                file.createNewFile();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private void savefile(File file,FileConfiguration configuration){
        try{
            configuration.save(file);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private void help(CommandSender sender) {
        sender.sendMessage("§a签到插件命令:");
        sender.sendMessage("§a--------------------");
        sender.sendMessage("§a/" + prefix + " create [礼包名] ----创建礼包");
        sender.sendMessage("§a/" + prefix + " add [礼包名]----把手中物品添加进礼包");
        sender.sendMessage("§a/" + prefix + " remove [礼包名] ----删除礼包");
        sender.sendMessage("§a/" + prefix + " set [类型] [礼包名] [天数/权限/次数] ----礼包类型分别是 时间 权限 次数");
        sender.sendMessage("§a/" + prefix + " get [礼包名] ----领取礼包");
        sender.sendMessage("§a/" + prefix + " getothers ----领取上次没领取完的礼包");
        sender.sendMessage("§a/" + prefix + " reload ----重载配置文件");
        sender.sendMessage("§a/" + prefix + " list ----礼包列表");
        sender.sendMessage("§a--------------------");
    }

    private void donthavethisgift(CommandSender sender, String giftname) {
        sender.sendMessage(messageprefix + "§c" + giftname + "这个礼包不存在");
    }

    private void donthavegiftname(CommandSender sender){
        sender.sendMessage(messageprefix + "§c礼包名不能为空");
    }

    private void getgift(CommandSender sender,Player player,String giftname){
        if(globalgift.containsKey(giftname)) {
            //count变量用来储存有多少个物品没有领完
            Integer count = new Integer(0);
            //decide变量作为判断礼包是否领取完
            boolean decide;
            if (globalgift.get(giftname) == null) {
                sender.sendMessage(messageprefix + "§c这个礼包内容是空的");
                decide = false;
            }
            else{
                decide = false;
                //遍历globalgift的值(HashMap<String,ItemStack>)
                HashMap<Integer, ItemStack> full = new HashMap<>();
                HashMap<Integer, ItemStack> temporary = new HashMap<>();
                for(HashMap.Entry<String,ItemStack> entry:globalgift.get(giftname).entrySet()) {
                    temporary = player.getInventory().addItem(entry.getValue());
                    if (temporary == null){
                        decide = false;
                    }
                    //判断是否为空的HashMap
                    if (temporary.isEmpty()) {
                        decide = true;
                    }
                    else {
                        for (HashMap.Entry<Integer,ItemStack> entry2:temporary.entrySet()) {
                            full.put(entry2.getKey(),entry2.getValue());
                        }
                        for (Integer space = 0; space <= 33; space++) {
                            if (!(othersitem.get(player.getName()).containsKey(space))) {
                                othersitem.put(player.getName(),full);
                                decide = false;
                                break;
                            }
                        }
                        count++;
                    }
                }
                if (decide == true) {
                    sender.sendMessage(messageprefix + "§a成功领取礼包[" + giftname + "]");
                }
                else {
                    sender.sendMessage(messageprefix + "§c你的背包空间不足!还有"+count+"个物品没有领取");
                    sender.sendMessage(messageprefix + "§c输入"+prefix+" getothers可领取剩下的");
                }
                if (count != 0) {
                    //播放捡起物品的音效
                    player.playSound(player.getLocation(), Sound.ITEM_PICKUP, 1.0f, 1.0f);
                }
            }
        }
        else{
            this.donthavethisgift(sender,giftname);
        }
    }

    private void addgift(Player player,HashMap<String,ItemStack> happens,String giftname){
        //获取玩家手中物品
        ItemStack hand = player.getInventory().getItemInHand();
        //玩家背包只有33格，所以space最多为33
        for (Integer space = 1; space <= 33 ; space++) {
            /**以循环和if语句给globalgift加新值并且 值(HashMap<String,ItemStack>) 中的键不会重复和被覆盖*/
            /**礼包暂时无法删除指定物品 将在后续版本更新*/
            if (space <= 33) {
                String keyname = new String(space.toString());
                //用if else语句处理happens赋值为null时用containsKey方法抛出的错误NullPointerException异常
                if (happens != null) {
                    if (!(happens.containsKey(keyname))) {
                        //把这个物品储存到globalgift的指定值
                        happens.put(space.toString(), hand);
                        globalgift.put(giftname, happens);
                        try {
                            saveorloadhashmap.saveglobalgift(globalgift, globalgiftpath);
                        }
                        catch (Exception e){
                            getLogger().info("服务器在保存哈希图礼包时出现了错误:");
                            e.printStackTrace();
                        }
                        player.sendMessage("§a成功添加物品到礼包");
                        break;
                    }
                }
                //若等于null则不执行是否存在该键值(globalgift.get(giftname)获取的值---HashMap<String,ItemStack>)
                else {
                    happens = new HashMap<>();
                    happens.put(space.toString(),hand);
                    globalgift.put(giftname,happens);
                    try {
                        saveorloadhashmap.saveglobalgift(globalgift, globalgiftpath);
                    }
                    catch (Exception e){
                        getLogger().info("服务器在保存哈希图礼包时出现了错误:");
                        e.printStackTrace();
                    }
                    player.sendMessage("§a成功添加物品到礼包");
                    break;
                }
            }
            else if (space > 33) {
                player.sendMessage(messageprefix + "§c已经超出礼包数量设置范围,最多只能设置33个");
                break;
            }
        }
        /***/
    }

    private boolean booleangetgift (CommandSender sender,Player player,String giftname,FileConfiguration players,FileConfiguration decision){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        boolean decide;
        if (players.contains(player.getName()+"."+giftname+".time")){
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int agoyear = players.getInt(player.getName()+"."+giftname+".time.year");
            int agomonth = players.getInt(player.getName()+"."+giftname+".time.month");
            int agoday = players.getInt(player.getName()+"."+giftname+".time.day");
            Integer days;
            try {
                long from = calendar.getTimeInMillis();
                //强行转换成整数类型，直接去掉商的小数。
                int cdday = decision.getInt(giftname+".time");
                int needday = agoday+cdday;
                calendar.set(agoyear,agomonth,needday);
                long to = calendar.getTimeInMillis();
                if (to>from){
                    howdays = (int)(to-from)/(1000*60*60*24);
                }
                decide = to<=from;
            }
            catch(Exception e){
                e.printStackTrace();
                decide = false;
            }
        }
        else {
            decide = true;
        }
        return decide;
    }
}