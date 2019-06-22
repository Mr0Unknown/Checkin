package com.github.Mr0Unknown.Checkin;

import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.inventory.ItemStack;
import sun.java2d.pipe.SpanShapeRenderer;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Checkin.Main Class
 *
 * @author HeFan
 * @date 2019/5/26
 */
public class Main extends JavaPlugin {
    private FileConfiguration config;
    private FileConfiguration times;
    private FileConfiguration decision;
    private Integer howdays;
    private Integer howminutes;
    private Integer howseconds;
    private String prefix = new String("Checkin");
    protected final String othersitempath = new String(getDataFolder() + File.separator+"othersitem.bin");

    /**创建以globalgift为命名的HashMap类型成员变量来储存礼包*/
    protected static HashMap<String, HashMap<String, ItemStack>> globalgift = new HashMap<String, HashMap<String, ItemStack>>();
    protected final String globalgiftpath = new String(getDataFolder() + File.separator + "globalgift.bin");
    /***/

    /**创建以othersitem为命名的HashMap类型成员变量来储存领取礼包时多余的物品*/
    protected static HashMap<String,HashMap<Integer,ItemStack>> othersitem =  new HashMap<String, HashMap<Integer, ItemStack>>();
    /***/

    @Override
    public void onEnable() {
        getLogger().info("签到插件[Checkin]1.5.1版本已载入");
        getLogger().info("签到插件[Checkin]版权归 盒饭HeFan 所有");
        getLogger().info("使用插件过程中若出现问题请联系作者QQ:382327683");
        File configFile = new File(this.getDataFolder(), "config.yml");
        File timesfile = new File(this.getDataFolder(),"times.yml");
        File decisionfile = new File(this.getDataFolder(),"decision.yml");
        if (!this.getDataFolder().exists()) {
            getDataFolder().mkdir();
        }
        /**下面调用该类的方法createfile来判定文件的存在与创建*/
            this.createfile(decisionfile);
            decision = YamlConfiguration.loadConfiguration(decisionfile);
            this.createfile(timesfile);
            times = YamlConfiguration.loadConfiguration(timesfile);
            this.createfile(configFile);
        /***/
        if (!configFile.exists()) {
            saveDefaultConfig();
        }
        this.reloadConfig();
        config = YamlConfiguration.loadConfiguration(configFile);

        /**加载和创建HashMap*/
        File othersitemfile = new File(this.getDataFolder(),"othersitem.bin");
        File globalgiftfile = new File(this.getDataFolder(),"globalgift.bin");
        this.createfile(globalgiftfile);
        if (globalgiftfile.exists()){
            //加载HashMap globalgift
            saveorloadhashmap.loadedhashmap(globalgiftpath);
        }
        this.createfile(globalgiftfile);
        if (othersitemfile.exists()) {
            //加载HashMap loadedhashmap
            saveorloadhashmap.loadedhashmap(othersitempath);
        }
        /***/

    }

    @Override
    public void onDisable() {
        getLogger().info("已卸载签到插件[Checkin]1.5.1");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase(prefix)) {
            if (args[0].equalsIgnoreCase("create")) {
                //把sender强行转换成Player类型
                Player player = (Player)sender;
                if (player.hasPermission("Checkin.create")) {
                    //判断args[1](礼包名)是否为空
                    if (args[1].length() == 0) {
                        //调用donthavegiftname给玩家发送消息
                        this.donthavegiftname(sender);
                    }
                    else {
                        //giftname获取礼包名字
                        String giftname = args[1];
                        //判断是否有该礼包
                        if (globalgift.containsKey(args[1])) {
                            sender.sendMessage("§c礼包[" + args[1] + "§a]已存在");
                        }
                        else {
                            //把变量giftname作为HashMap的键加到globalgift并且设置值为null先
                            globalgift.put(giftname, null);
                            sender.sendMessage("§a礼包[" + giftname + "]创建成功");
                            //保存globalgift到本地
                            saveorloadhashmap.saveglobalgift(globalgift, globalgiftpath);
                        }
                    }
                }
                else {
                    //调用方法donhavePermission给玩家发送没权限的信息
                    this.donthavePermission(sender);
                }
            }

            else if (args[0].equalsIgnoreCase("add")) {
                //把sender强行转换成Player类
                Player player = (Player)sender;
                if (player.hasPermission("Checkin.add")) {

                    /**判断是不是控制台在操作*/
                    if (!(sender instanceof Player)) {
                        sender.sendMessage("该指令不允许在控制台使用!");
                    }
                    /***/

                    else {
                        if (args[1].length() == 0) {
                            this.donthavegiftname(sender);
                        }
                        else {
                            //获取礼包名
                            String giftname = args[1];
                            //判断是否有该礼包
                            if (globalgift.containsKey(giftname)) {
                                //创建临时储存物品的HashMap
                                HashMap<String,ItemStack> happens = new HashMap<String,ItemStack>();
                                //获取玩家手中物品
                                ItemStack hand = player.getInventory().getItemInHand();
                                //玩家背包只有33格，所以space最多为33
                                for (Integer space = 1; space <= 33 ; space++) {
                                    /**以循环和if语句给globalgift加新值并且 值(HashMap<String,ItemStack>) 中的键不会重复和被覆盖*/
                                    /**礼包暂时无法删除指定物品 将在后续版本更新*/
                                    if (space <= 33) {
                                        if (!globalgift.get(giftname).containsKey(space.toString())) {
                                            //把这个物品储存到globalgift的指定值
                                            globalgift.get(giftname).put(space.toString(), hand);
                                            saveorloadhashmap.saveglobalgift(globalgift, globalgiftpath);
                                            break;
                                        }
                                    }
                                    else if (space > 33) {
                                        sender.sendMessage("§c已经超出礼包数量设置范围,最多只能设置33个");
                                        break;
                                    }
                                }
                                /***/
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
                Player player = (Player)sender;
                if (player.hasPermission("Checkin.remove")) {
                    if (args[1].length() == 0) {
                        this.donthavegiftname(sender);
                    }
                    else {
                        String giftname = args[1];
                        //判定礼包存在不存在
                        if (globalgift.containsKey(giftname)) {
                            //删除以giftname为名的键
                            globalgift.remove(giftname);
                            //保存
                            saveorloadhashmap.saveglobalgift(globalgift, globalgiftpath);
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
                Player player = (Player)sender;
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
                                    //获取礼包名字 这样比较写鲜明
                                    String giftname = args[2];
                                    //判断是否存在该礼包
                                    if(globalgift.containsKey(giftname)){
                                        //创建decisionfile并调用createfile判断是否有创建，若有不处理，无就创建
                                        File decisionfile = new File(this.getDataFolder(),"decision.yml");
                                            this.createfile(decisionfile);
                                        try{
                                            //decide用于判断玩家输入的是不是非数字
                                            //若不是数字则会转到catch语句执行
                                            //args[3]为天数
                                            int decide = Integer.parseInt(args[3]);
                                            String done = Integer.toString(decide);
                                            //写入decision中
                                            decision.set(giftname+".time",done);
                                            //保存decision.yml
                                            this.savefile(decisionfile,decision);
                                        }
                                        catch (Exception e){
                                            e.printStackTrace();
                                            sender.sendMessage("天数必须是数字(若是小数则取小数点前的值),不能是其他类型");
                                        }
                                    }
                                }
                                else if(args[1].equalsIgnoreCase("权限")){
                                    String giftname = args[2];
                                    //创建decisionfile并调用createfile判断是否有创建，若有不处理，无就创建
                                    File decisionfile = new File(this.getDataFolder(),"decision.yml");
                                    this.createfile(decisionfile);
                                    decision.set(giftname+".permission",args[3]);
                                    try {
                                        this.savefile(decisionfile,decision);
                                    }
                                    catch (Exception e){
                                        e.printStackTrace();
                                    }
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
                Player player = (Player)sender;
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
                            File decisionfile = new File(this.getDataFolder(),"decision.yml");

                            /**判断decision.yml中礼包是否设置过permission(权限)time(相隔天数)*/
                            boolean booleanpermission = decision.contains(giftname+".permission");
                            boolean booleantime = decision.contains(giftname+".time");
                            /***/

                            //只设置过permission(权限)
                            if (booleantime == false){
                                if (booleanpermission == true) {
                                    if (player.hasPermission(decision.getString(giftname + ".permission"))) {
                                        this.getgift(sender, player, giftname);
                                    }
                                }
                            }
                            //只设置过time(时间)
                            else if (booleanpermission == false){
                                if (booleantime == true) {
                                    boolean decide = this.booleangetgift(sender,player,giftname,times,decision);
                                    if (decide == true){
                                        this.getgift(sender,player,giftname);
                                        SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
                                        Date now = new Date();
                                        String temporay = date.format(now);
                                        times.set(player.getName()+"."+giftname,temporay);
                                        File timesfile = new File(this.getDataFolder(),"times.yml");
                                        this.savefile(timesfile,times);
                                    }
                                    else {
                                        sender.sendMessage("§c距离下一次领取礼包世界还有"+howdays.toString()+"时"+howminutes.toString()+"分"+howseconds.toString()+"秒");
                                    }
                                }
                            }
                            //两者都设置了
                            else if (booleantime == true) {
                                if (booleanpermission == true) {
                                    if (player.hasPermission(decision.getString(giftname + ".permission"))) {
                                        boolean decide = this.booleangetgift(sender, player, giftname, times, decision);
                                        if (decide == true) {
                                            this.getgift(sender, player, giftname);
                                        }
                                        else{
                                            sender.sendMessage("§c距离下一次领取礼包世界还有"+howdays.toString()+"时"+howminutes.toString()+"分"+howseconds.toString()+"秒");
                                        }
                                    }
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
                Player player = (Player)sender;
                if (player.hasPermission("Checkin.getothers")) {
                    if (othersitem.containsKey(player.getName())) {
                        for (Integer space = 1; space <= 33; space++) {
                            if (player.getInventory().firstEmpty() == -1) {
                                sender.sendMessage("§c你的背包空间不足!");
                            } else {
                                player.getInventory().addItem(othersitem.get(player.getName()).get(space));
                                othersitem.get(player.getName()).remove(space);
                                saveorloadhashmap.saveothersitem(othersitem, othersitempath);
                                sender.sendMessage("成功领取之前剩下的礼包物品");
                            }
                        }
                    }
                }
            }
            else if (args[0].equalsIgnoreCase("list")) {
                Player player = (Player) sender;
                if (player.hasPermission("Checkin.list")){
                    sender.sendMessage("§a已经存在的礼包:");
                for (HashMap.Entry<String, HashMap<String, ItemStack>> entry : globalgift.entrySet()) {
                    sender.sendMessage("§a" + entry.getKey());
                }
                sender.sendMessage("§a--------------------");
                }
            }
            else if (args[0].equalsIgnoreCase("reload")){
                Player player = (Player) sender;
                if (player.hasPermission("Checkin.reload")) {
                    this.reloadConfig();
                    File timesfile = new File(this.getDataFolder(), "times.yml");
                    File decisionfile = new File(this.getDataFolder(), "decision.yml");
                    File configfile = new File(this.getDataFolder(), "config.yml");
                    if (timesfile.exists()) {
                        times = YamlConfiguration.loadConfiguration(timesfile);
                    } else {
                        this.createfile(timesfile);
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
                }
            }
            else if (args[0].equalsIgnoreCase("help")) {
                this.help(sender);
            }
            else {
                sender.sendMessage("§c指令错误!请使用"+prefix+" help查询指令");
            }
        }
        return false;
    }

    private void donthavePermission(CommandSender sender) {
        sender.sendMessage("§c你没有权限这么做");
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
        sender.sendMessage("§a/" + prefix + " create [礼包名] ----创建礼包");
        sender.sendMessage("§a/" + prefix + " add [礼包名 ]----把手中物品添加进礼包");
        sender.sendMessage("§a/" + prefix + " remove [礼包名] ----删除礼包");
        sender.sendMessage("§a/" + prefix + " set [类型] [礼包名] [天数/权限] ----礼包类型分别是 时间 权限");
        sender.sendMessage("§a/" + prefix + " get [礼包名] ----领取礼包");
        sender.sendMessage("§a/" + prefix + " getothers ----领取上次没领取完的礼包");
        sender.sendMessage("§a/" + prefix + " reload ----重载配置文件");
        sender.sendMessage("§a/" + prefix + " list ----礼包列表");
    }

    private void donthavethisgift(CommandSender sender, String giftname) {
        sender.sendMessage("§c" + giftname + "这个礼包不存在");
    }

    private void donthavegiftname(CommandSender sender){
        sender.sendMessage("§c礼包名不能为空");
    }

    private void getgift(CommandSender sender,Player player,String giftname){
        if(globalgift.containsKey(giftname)) {
            //count变量用来储存有多少个物品没有领完
            Integer count = new Integer(0);
            //decide变量作为判断礼包是否领取完
            boolean decide;
            if (globalgift.get(giftname) == null) {
                sender.sendMessage("§c这个礼包内容是空的");
                decide = false;
            }
            else{
                decide = false;
                //遍历globalgift的值(HashMap<String,ItemStack>)
                for(HashMap.Entry<String,ItemStack> entry:globalgift.get(giftname).entrySet()) {
                    HashMap<Integer, ItemStack> temporary = player.getInventory().addItem(entry.getValue());
                    //判断是否为空的HashMap
                    if (temporary.isEmpty()) {
                        decide = true;
                    }
                    //这里会写两条是不知道第一个引导值是0还是1
                    else if (temporary.containsKey(Integer.valueOf(0))) {
                        for (Integer space = 1; space <= 33; space++) {
                            if (!othersitem.get(player.getName()).containsKey(space)) {
                                othersitem.get(player.getName()).put(space, temporary.get(Integer.valueOf(0)));
                                decide = false;
                                break;
                            }
                        }
                        count++;
                    }
                    else if (temporary.containsKey(Integer.valueOf(1))) {
                        //保存没有加入背包的物品
                        for (Integer space = 1; space <= 33; space++) {
                            if (!othersitem.get(player.getName()).containsKey(space)) {
                                othersitem.get(player.getName()).put(space, temporary.get(Integer.valueOf(1)));
                                saveorloadhashmap.saveothersitem(othersitem,othersitempath);
                                decide = false;
                                break;
                            }
                        }
                        count++;
                    }
                }
                if (decide == true) {
                    sender.sendMessage("§a成功领取礼包[" + giftname + "]");
                }
                else {
                    sender.sendMessage("§c你的背包空间不足!还有"+count+"没有领取");
                    sender.sendMessage("§c输入"+prefix+" getothers可领取剩下的");
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
    private boolean booleangetgift (CommandSender sender,Player player,String giftname,FileConfiguration times,FileConfiguration decision){
        SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
        Date now = new Date();
        boolean decide;
        if (times.contains(player.getName()+"."+giftname)){
            String nowtime = date.format(now);
            String agotime = times.getString(player.getName()+"."+giftname);
            int days;
            try {
                long to = date.parse(nowtime).getTime();
                long from = date.parse(agotime).getTime();
                //强行转换成整数类型，直接去掉商的小数。
                days = (int)((to-from)/(1000*60*60*24));
                decide = Integer.parseInt(decision.getString(giftname+".time"))<=days;
                int minutes = (int)((to-from)/(1000*60*60));
                int seconds = (int)((to-from)/(1000*60));
                howdays = days;
                howminutes = minutes;
                howseconds = seconds;
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