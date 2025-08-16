package plugin.oreDigging.mapper.data;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * プレイヤースコアの順位情報を扱うオブジェクト
 * DBに存在するテーブルと連動する
 */
@Getter
@Setter
@NoArgsConstructor
public class PlayerRank {

  private String playerName;
  private int score;
  private LocalDateTime registeredDt;
  private int Rankbyscore;

}
