package plugin.oreDigging;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import plugin.oreDigging.Command.oreDiggingDone;
import plugin.oreDigging.Command.set_block_command;

public final class Main extends JavaPlugin  implements Listener {

  @Override
  public void onEnable() {
    Bukkit.getPluginManager().registerEvents(this, this);
    getCommand("setblock").setExecutor(new set_block_command());

    oreDiggingDone orediggingdone = new oreDiggingDone(this);
    Bukkit.getPluginManager().registerEvents(orediggingdone,this);
    getCommand("oredig").setExecutor(orediggingdone);
  }

}
