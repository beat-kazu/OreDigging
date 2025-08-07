package plugin.oreDigging.Command;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SplittableRandom;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import plugin.oreDigging.Data.PlayerInfo;
import plugin.oreDigging.Main;
import plugin.oreDigging.PlayerScoreData;
import plugin.oreDigging.mapper.data.PlayerScore;

public class oreDiggingDone implements CommandExecutor , Listener {

  public static final String LIST = "list";
  public static final String DELL = "delete";

  private int gameTime = 20;

  private int coal_cnt = 0 ;
  private int copper_cnt= 0;
  private int lapis_cnt= 0;
  private int iron_cnt= 0;
  private int gold_cnt= 0;
  private int red_cnt = 0;
  private int dia_cnt = 0;
  private int eme_cnt = 0;
  private int quartz_cnt = 0;

  private Main main;
  private PlayerScoreData playerScoreData = new PlayerScoreData();
  private List<PlayerInfo> playerInfoList = new ArrayList<>();

  private Map<String, Integer> OreBreakCount =  new HashMap<String, Integer>();

  public oreDiggingDone(Main main) {
    this.main = main;
  }

  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
      @NotNull String label, @NotNull String[] args) {
    if(sender instanceof Player player){
      if(playerInfoList.isEmpty()) {
        addNewPlayer(player);
      }else{
        for(PlayerInfo playerInfo:playerInfoList){
          if(!playerInfo.getPlayerName().equals(player.getName())){
            addNewPlayer(player);
          }
        }
      }

      if(args.length ==1 && LIST.equals(args[0]) ) {
        sendPlayerScoreList(player);
        return false;
      }else{
        if(args.length ==1 && DELL.equals(args[0]) ){
          playerScoreData.deleteAll();
          player.sendMessage("DB削除しました？");
          return  false;
        }
      }
      gameTime = 20;
      World world = player.getWorld();

      initPlayerStatus(player);
      player.sendTitle("**　Game start　**","** 5秒間の挑戦です！ **",0,60,10);

      Bukkit.getScheduler().runTaskTimer(main,Runnable -> {
        if (gameTime <= 0) {
          Runnable.cancel();
          player.sendTitle("**　鉱石掘りゲーム　**","** 終了しました。 **",
              0,50,10);
          //player.sendMessage("鉱石掘りゲーム終了しました。");
          for (PlayerInfo playerInfo : playerInfoList) {
            if (playerInfo.getPlayerName().equals(player.getName())) {
              player.sendMessage("獲得点数は" + playerInfo.getScore() + "。");
              playerScoreData.insert(new PlayerScore(player.getName()
                  ,playerInfo.getScore()));

              playerInfo.setScore(0);
              player.sendMessage("スコアを初期化しました。");
            }
          }
          return;
        }
        Material block = getBlock();
        player.sendMessage("鉱石は" + block + "に設定されました");
        world.getBlockAt(getBloackSetLocation(player, world)).setType(block);
        gameTime -=5;
      },0,5*20);
    }
    return false;
  }

  private void sendPlayerScoreList(Player player) {
    List<PlayerScore> playerScoreList = playerScoreData.selectList();
    for (PlayerScore playerScore : playerScoreList) {
      player.sendMessage(playerScore.getId()+ " | "
          + playerScore.getPlayerName()     + " | "
          + playerScore.getScore()          + " | "
          + playerScore.getRegisteredDt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }
  }

  private void addNewPlayer(Player player) {
    PlayerInfo newPlayer = new PlayerInfo();
    newPlayer.setPlayerName(player.getName());
    playerInfoList.add(newPlayer);
  }

  @EventHandler
  public void onBlockBreak(BlockBreakEvent event) {
    Player player = event.getPlayer();
    Block block = event.getBlock();

    if(Objects.isNull(player) || playerInfoList.isEmpty()){
      return;
    }
    for(PlayerInfo playerInfo : playerInfoList){
      if(playerInfo.getPlayerName().equals(player.getName())){
        if(gameTime > 0) {
          switch (block.getType()) {
            case COAL_ORE -> {
              if(coal_cnt > 3){
                playerInfo.setScore(playerInfo.getScore() + 10+30);
                player.sendMessage(
                  "石炭コンボ発生！　現在のスコアは" + playerInfo.getScore() + "点！");
              }else{
                playerInfo.setScore(playerInfo.getScore() + 10);
                player.sendMessage(
                    "石炭を壊しました！　現在のスコアは" + playerInfo.getScore() + "点！");
              }
              coal_cnt++;
              copper_cnt = 0;
              lapis_cnt = 0;
              iron_cnt = 0;
              gold_cnt = 0;
              red_cnt = 0;
              dia_cnt = 0;
              eme_cnt = 0;
              quartz_cnt = 0;
            }
            case COPPER_ORE -> {
              playerInfo.setScore(playerInfo.getScore() + 15);
              player.sendMessage(
                  "銅鉱石を壊しました！　現在のスコアは" + playerInfo.getScore() + "点！");
              coal_cnt =0;
            }
            case LAPIS_ORE -> {
              playerInfo.setScore(playerInfo.getScore() + 40);
              player.sendMessage(
                  "ラピスラズリを壊しました！　現在のスコアは" + playerInfo.getScore() + "点！");
              coal_cnt =0;
            }
            case IRON_ORE -> {
              playerInfo.setScore(playerInfo.getScore() + 15);
              player.sendMessage(
                  "鉄鉱石を壊しました！　現在のスコアは" + playerInfo.getScore() + "点！");
              coal_cnt =0;
            }
            case GOLD_ORE -> {
              playerInfo.setScore(playerInfo.getScore() + 80);
              player.sendMessage(
                  "金鉱石を壊しました！　現在のスコアは" + playerInfo.getScore() + "点！");
              coal_cnt =0;
            }
            case REDSTONE_ORE -> {
              playerInfo.setScore(playerInfo.getScore() + 40);
              player.sendMessage(
                  "レッドストーン鉱石を壊しました！　現在のスコアは" + playerInfo.getScore() + "点！");
              coal_cnt =0;
            }
            case DIAMOND_ORE -> {
              playerInfo.setScore(playerInfo.getScore() + 100);
              player.sendMessage(
                  "ダイヤモンド鉱石を壊しました！　現在のスコアは" + playerInfo.getScore() + "点！");
              coal_cnt =0;
            }
            case EMERALD_ORE -> {
              playerInfo.setScore(playerInfo.getScore() + 80);
              player.sendMessage(
                  "エメラルド鉱石を壊しました！　現在のスコアは" + playerInfo.getScore() + "点！");
              coal_cnt =0;
            }
            case NETHER_QUARTZ_ORE -> {
              playerInfo.setScore(playerInfo.getScore() + 100);
              player.sendMessage(
                  "ネザークォーツ鉱石を壊しました！　現在のスコアは" + playerInfo.getScore() + "点！");
              coal_cnt =0;
            }
          }
        }
      }
    }

  }

  private static void initPlayerStatus(Player player) {
    player.setHealth(20);
    player.setFoodLevel(20);
    PlayerInventory inventory = player.getInventory();
    inventory.setHelmet(new ItemStack(Material.NETHERITE_HELMET));
    inventory.setChestplate(new ItemStack(Material.NETHERITE_CHESTPLATE));
    inventory.setLeggings(new ItemStack(Material.NETHERITE_LEGGINGS));
    inventory.setBoots(new ItemStack(Material.NETHERITE_BOOTS));
    inventory.setItemInMainHand(new ItemStack(Material.NETHERITE_PICKAXE));
  }


  private Material getBlock() {
    List<Material> blockList =
        List.of(Material.COAL_ORE, Material.COPPER_ORE,Material.LAPIS_ORE,Material.IRON_ORE,
            Material.GOLD_ORE,Material.REDSTONE_ORE,Material.DIAMOND_ORE,Material.EMERALD_ORE);
        return blockList.get(new SplittableRandom().nextInt(blockList.size()));
  }

  private Location getBloackSetLocation(Player player, World world) {
    Location playerLocation = player.getLocation();
    int randomX = new SplittableRandom().nextInt(10) -10;
    int randomZ = new SplittableRandom().nextInt(10) -10;
    double x = playerLocation.getX()+randomX;
    double y = playerLocation.getY();
    double z = playerLocation.getZ()+randomZ;

    if(x == y ){
      x = x +2;
      y = y +3;
    }

    return new Location(world,x,y,z);
  }

}
