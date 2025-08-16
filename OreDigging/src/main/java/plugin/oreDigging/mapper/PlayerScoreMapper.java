package plugin.oreDigging.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import plugin.oreDigging.mapper.data.PlayerRank;
import plugin.oreDigging.mapper.data.PlayerScore;

public interface PlayerScoreMapper {

  //@Select("select * from player_score")
  @Select("select * from player_score order by score desc limit 5")
  List<PlayerScore> selectList();

  @Select("select rank_by_score from (select player_name,score,registered_dt,rank() over (order by score desc) as rank_by_score from player_score ) ranked where registered_dt = (select max(registered_dt) from player_score )")
  int selectRank();

  @Delete("delete from player_score")
  void deleteAll();

  @Insert("insert player_score(player_name,score,registered_dt) values (#{playerName},#{score},now())")
  int insert(PlayerScore playerScore);
}
