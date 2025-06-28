package exam.persistence;



import exam.model.Game;
import exam.model.GameDTO;

import java.util.List;

public interface GameRepository extends Repository<Long, Game>{
    List<GameDTO> findByAlias(String alias);
}
