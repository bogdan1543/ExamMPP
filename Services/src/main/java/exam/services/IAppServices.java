package exam.services;

//import exam.model.Game;
//import exam.model.GameDTO;

import exam.model.Game;
import exam.model.GameDTO;
import exam.model.Question;
import exam.model.User;

import java.util.List;

public interface IAppServices {
    List<GameDTO> getAllGames() throws AppException;
    List<Question> getAllQuestions(int difficulty);

    void saveGame(Game game) throws AppException;

    void login(User user, IAppObserver client) throws AppException;
    void logout(User user, IAppObserver client) throws AppException;
}
