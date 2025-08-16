package plugin.oreDigging.Data;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

/**
 * 鉱石発掘ゲームのプレーヤー情報を扱う
 */
@Getter
@Setter
public class PlayerInfo {

  private String playerName;
  private int score;
  private int gameTime;

  public PlayerInfo(String playerName) {
    this.playerName = playerName;
  }

}
