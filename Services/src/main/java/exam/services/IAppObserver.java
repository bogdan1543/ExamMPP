package exam.services;

//import exam.model.Game;

import exam.model.Game;

public interface IAppObserver {
    void gameFinish(Game game) throws AppException;
}
