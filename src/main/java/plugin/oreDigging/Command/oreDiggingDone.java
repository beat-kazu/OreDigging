package plugin.oreDigging.Command;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SplittableRandom;
import java.util.function.Predicate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
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

/**
 * 　制限時間内に鉱石を掘って、スコアを獲得するゲームを起動するコマンド。
 * 　スコアは鉱石によって変わり、掘った鉱石の合計によってスコアが変動します。
 * 　結果はプレーヤー名、点数、日時などで保存されます。
 */
public class oreDiggingDone extends BaseCommand implements  Listener {

  public static final String LIST = "list";
  public static final String DELL = "delete";
  public static final String TELE1 = "tele1";
  public static final String TELE2 = "tele2";
  public static final String TELE3 = "tele3";
  public static final String RETURN = "return";
  public static final String DEBUG = "debug";
  public static final String HELP = "help";

  private final int GAME_TIME = 20;

  private final Main main;
  private final PlayerScoreData playerScoreData = new PlayerScoreData();
  private final List<PlayerInfo> playerInfoList = new ArrayList<>();


  enum Ore_Type {
    Coal_Type,
    Copper_Type,
    Lapis_Type,
    Iron_Type,
    Gold_Type,
    Red_Type,
    Dia_Type,
    Eme_Type,
    Quartz_Type
  }

  private final Map<Ore_Type, Integer> OreBreakCount =  new EnumMap<>(Ore_Type.class);

  public oreDiggingDone(Main main) {
    this.main = main;
  }

  @Override
  public boolean onExecutePlayerCommand(Player player, @NotNull Command command,
      @NotNull String label, @NotNull String[] args) {

    //サブコマンド実行
    if(args.length ==1 && HELP.equals(args[0]) ) {
      player.sendMessage(ChatColor.RED + "list:上記5名のランキングを表示します。");
      player.sendMessage(ChatColor.RED + "dell:DBデータを削除します。");
      player.sendMessage(ChatColor.RED + "debug:鉱石をランダムに出現させ鉱石掘りを実行できるデバッグ用オプションです。");
      player.sendMessage(ChatColor.RED + "return:拠点に戻るコマンドです。");
      player.sendMessage(ChatColor.RED + "tele1、tele2、tele3:ゲーム開始用洞窟に移動するコマンドです。(使用は任意)");
      player.sendMessage(ChatColor.RED + "上記以外の引数は通常通り鉱石掘りゲームが始まりますが、引数を入れず実行してください。");
      return false;
    }else if(args.length ==1 && LIST.equals(args[0]) ) {
      sendPlayerScoreList(player);
      return false;
    } else if(args.length ==1 && DELL.equals(args[0]) ){
      playerScoreData.deleteAll();
      // debug用表示
      player.sendMessage("DB削除しました");
      return  false;

    } else if (args.length == 1 && RETURN.equals(args[0])) {
      Bukkit.getScheduler().runTaskLater(main, () -> {
      player.teleport(new Location(player.getWorld(), -469,63,-727));
      }, 10L);
      return false;
    } else if (args.length == 1 && TELE1.equals(args[0])) {
      Bukkit.getScheduler().runTaskLater(main, () -> {
        player.teleport(new Location(player.getWorld(), -735,-1,-301));
      }, 10L);
      return false;
    } else if (args.length == 1 && TELE2.equals(args[0])) {
      Bukkit.getScheduler().runTaskLater(main, () -> {
      player.teleport(new Location(player.getWorld(), -474,-38,-681));
      }, 10L);
      return false;
    } else if (args.length == 1 && TELE3.equals(args[0])) {
        Bukkit.getScheduler().runTaskLater(main, () -> {
      player.teleport(new Location(player.getWorld(), -280,31,-775));
        }, 10L);
      return false;
    }

      //実行プレイヤー情報取得
      PlayerInfo nowPlayer = getPlayerInfo(player);

      // コンボ判定関数valueを初期化
      for (Ore_Type type : Ore_Type.values()) {
        OreBreakCount.put(type, 0);
      }

      initPlayerStatus(player);
      // 1=1tick ,1sec = 20 tick
      player.sendTitle("**　Game start　**","** 5秒間の挑戦です！ **",0,20*2,20/2);

      gamePlay(player, args, nowPlayer);
      return false;
   }
  /**
   * ゲームを実行します。規定の時間内に鉱石を壊すとスコアが加算されます。合計スコアを時間経過後に表示します。
   * @param player コマンドを実行したプレイヤー
   * @param nowPlayer　プレーヤースコア情報
   */
  private void gamePlay(Player player, String [] args, PlayerInfo nowPlayer) {
    Bukkit.getScheduler().runTaskTimer(main,Runnable -> {
      if (nowPlayer.getGameTime() <= 0) {
        Runnable.cancel();

              // debug用表示
              player.sendMessage("獲得点数は" + nowPlayer.getScore() + "。");

              // 登録
              playerScoreData.insert(new PlayerScore(player.getName()
                  ,nowPlayer.getScore()));

              sendPlayerRank(player,nowPlayer);

              nowPlayer.setScore(0);
              // debug用表示
              player.sendMessage("スコアを初期化しました。");

        return;
      }
      // debug 用にランダムに鉱石を出現させるコマンドを実装
      if(args.length ==1 && DEBUG.equals(args[0]) ) {
        Material block = getBlock();
        // debug用表示
        player.sendMessage("鉱石は" + block + "に設定されました");
        World world  = player.getWorld();
        world.getBlockAt(getBloackSetLocation(player)).setType(block);
      }
      nowPlayer.setGameTime(nowPlayer.getGameTime()-5);
    },0,5*20);
  }

  @Override
  public boolean onExecuteNPCCommand(CommandSender sender, @NotNull Command command,
      @NotNull String label, @NotNull String[] args) {
    return false;
  }

  /**
   * 現在登録されているスコアの一覧をメッセージに送る。
   * @param player　プレーヤー
   */
  private void sendPlayerScoreList(Player player) {
    List<PlayerScore> playerScoreList = playerScoreData.selectList();
    for (PlayerScore playerScore : playerScoreList) {
      player.sendMessage(playerScore.getId()+ " | "
          + playerScore.getPlayerName()     + " | "
          + playerScore.getScore()          + " | "
          + playerScore.getRegisteredDt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }
  }

  /**
   * DBから実行したプレイヤーのスコア順位を取得し表示する
   * @param player　 コマンドを実行したプレイヤー
   * @param info　　  DB情報
   */
  private void sendPlayerRank(Player player ,PlayerInfo info) {
    int playerRank = playerScoreData.selectRank();
      player.sendTitle(
          "お疲れさまでした、鉱石掘りゲームは終了しました。",
          "あなたの得点は" + info.getScore()+"点で、順位は" + playerRank + "位でした。" ,
          0,20*2,20/2);
  }

/**
 * 現在実行しているプレーヤーのスコア情報を取得する
 * @param player　コマンドを実行したプレーヤー
 * @return　現在実行しているプレーヤーのスコア情報
 */
private PlayerInfo getPlayerInfo(Player player) {
  PlayerInfo playerInfo = new PlayerInfo(player.getName());
  if(playerInfoList.isEmpty()) {
    playerInfo = addNewPlayer(player);
  }else{
    playerInfo = playerInfoList.stream()
        .findFirst()
        .map(ps -> ps.getPlayerName().equals(player.getName())
            ? ps
            : addNewPlayer(player)).orElse(playerInfo);
  }
  playerInfo.setGameTime(GAME_TIME);
  playerInfo.setScore(0);
  return playerInfo;
}
  /**
   * 新規のプレーヤー情報をリストに追加します。
   * @param player コマンドを実行したプレーヤー
   * @return 新規プレーヤー
   */
private PlayerInfo addNewPlayer(Player player) {
  PlayerInfo newPlayer = new PlayerInfo(player.getName());
  playerInfoList.add(newPlayer);
  return newPlayer;
}

  @EventHandler
  public void onBlockBreak(BlockBreakEvent event) {
    Player player = event.getPlayer();
    Block block = event.getBlock();

    Map<oreDiggingDone.Ore_Type, Predicate<Block>> conditionCheckers = getPredicateMap(
        block);

    if(Objects.isNull(player) || playerInfoList.isEmpty()){
      return;
    }
    playerInfoList.stream()
        .filter(p -> p.getPlayerName().equals(player.getName()))
        .findFirst()
        .ifPresent(p -> {
          if (p.getGameTime() > 0) {

            int point = switch (block.getType()) {
              case COAL_ORE,DEEPSLATE_COAL_ORE -> 10;
              case COPPER_ORE, DEEPSLATE_COPPER_ORE, IRON_ORE,DEEPSLATE_IRON_ORE -> 15;
              case LAPIS_ORE, DEEPSLATE_LAPIS_ORE,REDSTONE_ORE,DEEPSLATE_REDSTONE_ORE -> 40;
              case GOLD_ORE,DEEPSLATE_GOLD_ORE ,EMERALD_ORE ,DEEPSLATE_EMERALD_ORE-> 80;
              case DIAMOND_ORE,DEEPSLATE_DIAMOND_ORE ,NETHER_QUARTZ_ORE -> 100;
              default -> 150;
            };

            for (Map.Entry<oreDiggingDone.Ore_Type, Predicate<Block>> entry : conditionCheckers.entrySet()) {
              oreDiggingDone.Ore_Type type = entry.getKey();         // 条件の種類
              Predicate<Block> checker = entry.getValue(); // 判定関数

              // 直前の鉱石と同じか判定
              if (checker.test(block)) { // 条件を満たすか判定
                int currentCount = OreBreakCount.get(type);
                OreBreakCount.put(type, currentCount + 1); // カウントアップ
                if(OreBreakCount.get(type) > 3){
                  p.setScore(p.getScore() + point*3);
                  // debug用表示
                  player.sendMessage(
                      "コンボ発生！　現在のスコアは" + p.getScore() + "点！");
                }else{
                  p.setScore(p.getScore() + point);
                  // debug用表示
                  player.sendMessage(
                      "鉱石を壊しました！　現在のスコアは" + p.getScore() + "点！");
                }
                // debug用表示
                player.sendMessage("コンボカウントアップ！！");
                player.sendMessage("コンボ数は" + OreBreakCount.get(type) + "！！");
                //player.sendMessage("掘った鉱石は" + block.getType() + "だよ。　内部情報だよ");
                // 直前と違う鉱石の場合、コンボカウンタリセット
              } else {
                OreBreakCount.put(type, 0); // その他を初期化
              }
            }
          }
        });
  }

  /**
   * 前に掘った鉱石と一致しているか確認するための条件付きインターフェース
   * @param block
   * @return　条件式
   */
  @NotNull
  private Map<oreDiggingDone.Ore_Type, Predicate<Block>> getPredicateMap(Block block) {
    Map<oreDiggingDone.Ore_Type, Predicate<Block>> conditionCheckers = Map.of(
        Ore_Type.Coal_Type,   p -> ((block.getType() == getBlock().COAL_ORE)     || (block.getType() == getBlock().DEEPSLATE_COAL_ORE)),
        Ore_Type.Copper_Type, p -> ((block.getType() == getBlock().COPPER_ORE)   || (block.getType() == getBlock().DEEPSLATE_COPPER_ORE)),
        Ore_Type.Lapis_Type,  p -> ((block.getType() == getBlock().LAPIS_ORE)    || (block.getType() == getBlock().DEEPSLATE_LAPIS_ORE)),
        Ore_Type.Iron_Type,   p -> ((block.getType() == getBlock().IRON_ORE)     || (block.getType() == getBlock().DEEPSLATE_IRON_ORE)),
        Ore_Type.Gold_Type,   p -> ((block.getType() == getBlock().GOLD_ORE)     || (block.getType() == getBlock().DEEPSLATE_GOLD_ORE)),
        Ore_Type.Red_Type,    p -> ((block.getType() == getBlock().REDSTONE_ORE) || (block.getType() == getBlock().DEEPSLATE_REDSTONE_ORE)),
        Ore_Type.Dia_Type,    p -> ((block.getType() == getBlock().DIAMOND_ORE)  || (block.getType() == getBlock().DEEPSLATE_DIAMOND_ORE)),
        Ore_Type.Eme_Type,    p -> ((block.getType() == getBlock().EMERALD_ORE)  || (block.getType() == getBlock().DEEPSLATE_EMERALD_ORE)),
        Ore_Type.Quartz_Type, p -> (block.getType() == getBlock().NETHER_QUARTZ_ORE)
    );
    return conditionCheckers;
  }

  /**
   *  ゲームを始める前にプレーヤーの状態を設定する。
   *  体力と空腹度を最大にして、装備はネザライト一式になる。
   * @param player　コマンドを実行したプレーヤー
   */
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

  /**
   * ランダムで鉱石を選択して、その結果を出力します。
   * @return 選択された鉱石
   */
  private Material getBlock() {
    List<Material> blockList =
        List.of(Material.COAL_ORE, Material.COPPER_ORE,Material.LAPIS_ORE,Material.IRON_ORE,
            Material.GOLD_ORE,Material.REDSTONE_ORE,Material.DIAMOND_ORE,Material.EMERALD_ORE);
        return blockList.get(new SplittableRandom().nextInt(blockList.size()));
  }

  /**
   * デバッグ用メソッドです。
   * 鉱石の出現場所を取得します。
   * 出現エリアはX軸とZ軸は自分の位置からプラス、ランダムで-10~9の値が設定されます。
   * Y軸はプレイヤーと同じ位置になります。
   * @param player
   * @return
   */
  private Location getBloackSetLocation(Player player) {
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

    return new Location(player.getWorld(),x,y,z);
  }

}
