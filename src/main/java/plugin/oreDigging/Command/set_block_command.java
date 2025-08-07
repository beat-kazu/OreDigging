package plugin.oreDigging.Command;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class set_block_command implements CommandExecutor {

  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
      @NotNull String s, @NotNull String[] args) {
    if(sender instanceof Player player){
      World world = player.getWorld();
      Location playerLocation = player.getLocation();
      switch (args[0]) {
        case "SET_COAL" -> world.getBlockAt(
            new Location(world,
                playerLocation.getX() + 2,
                playerLocation.getY(),
                playerLocation.getZ())).setType(Material.COAL_ORE);
        case "SET_COPPER" -> world.getBlockAt(
            new Location(world,
                playerLocation.getX() + 2,
                playerLocation.getY(),
                playerLocation.getZ())).setType(Material.COPPER_ORE);
        case "SET_LAPIS" -> world.getBlockAt(
            new Location(world,
                playerLocation.getX() + 2,
                playerLocation.getY(),
                playerLocation.getZ())).setType(Material.LAPIS_ORE);
        case "SET_IRON" -> world.getBlockAt(
            new Location(world,
                playerLocation.getX() + 2,
                playerLocation.getY(),
                playerLocation.getZ())).setType(Material.IRON_ORE);
        case "SET_GOLD" -> world.getBlockAt(
            new Location(world,
                playerLocation.getX() + 2,
                playerLocation.getY(),
                playerLocation.getZ())).setType(Material.GOLD_ORE);
        case "SET_RED" -> world.getBlockAt(
            new Location(world,
                playerLocation.getX() + 2,
                playerLocation.getY(),
                playerLocation.getZ())).setType(Material.REDSTONE_ORE);
        case "SET_DIA" -> world.getBlockAt(
            new Location(world,
                playerLocation.getX() + 2,
                playerLocation.getY(),
                playerLocation.getZ())).setType(Material.DIAMOND_ORE);
        default -> player.sendMessage("引数にSET**を設定してください");
      }
    }
    return false;
  }
}
