package plugin.oreDigging;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import plugin.oreDigging.Command.OreDiggingDone;
import plugin.oreDigging.Command.SetBlockCommand;

public final class Main extends JavaPlugin  implements Listener {

  @Override
  public void onEnable() {
    Bukkit.getPluginManager().registerEvents(this, this);
    getCommand("setblock").setExecutor(new SetBlockCommand());

    OreDiggingDone orediggingdone = new OreDiggingDone(this);
    Bukkit.getPluginManager().registerEvents(orediggingdone,this);
    getCommand("oredig").setExecutor(orediggingdone);
  }

}
