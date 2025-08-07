package plugin.oreDigging;

import java.io.InputStream;
import java.util.List;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import plugin.oreDigging.mapper.PlayerScoreMapper;
import plugin.oreDigging.mapper.data.PlayerScore;

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

  public List<PlayerScore> selectList(){
    return mapper.selectList();
  }

  public void deleteAll(){
    mapper.deleteAll();
  }

  public void insert(PlayerScore playerScore){
    mapper.insert(playerScore);
  }
}
