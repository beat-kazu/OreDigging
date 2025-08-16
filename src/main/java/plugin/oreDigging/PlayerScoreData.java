package plugin.oreDigging;

import java.io.InputStream;
import java.util.List;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import plugin.oreDigging.mapper.PlayerScoreMapper;
import plugin.oreDigging.mapper.data.PlayerRank;
import plugin.oreDigging.mapper.data.PlayerScore;

/**
 * DB接続やそれに付随する登録や更新処理を行うクラスです。
 */
public class PlayerScoreData {
  private SqlSessionFactory sqlSessionFactory;
  private PlayerScoreMapper mapper;

  public PlayerScoreData(){
    try {
      InputStream inputStream = Resources.getResourceAsStream("mybatis-config.xml");
      this.sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
      SqlSession session = sqlSessionFactory.openSession(true);
      this.mapper = session.getMapper(PlayerScoreMapper.class);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   *  プレイヤースコアテーブルから一覧でスコア情報を取得する。
   * @return　スコア情報の一覧
   */
  public List<PlayerScore> selectList(){
    return mapper.selectList();
  }

  /**
   *  プレイヤースコアテーブルから順位を取得する。
   * @return　順位情報
   */
  public int selectRank(){
      return mapper.selectRank();
  }

  /**
   * プレイヤースコアテーブルを除去する。
   */
  public void deleteAll(){
    mapper.deleteAll();
  }

  /**
   * プレイヤースコアテーブルにスコア情報を登録する。
   * @param playerScore　プレイヤースコア
   */
  public void insert(PlayerScore playerScore){
    mapper.insert(playerScore);
  }
}
