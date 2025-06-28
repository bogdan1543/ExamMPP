package exam.networking.jsonprotocol;


import exam.model.Game;
import exam.model.GameDTO;
import exam.model.Question;
import exam.model.User;

public class Request {
    private RequestType type;
    private User user;
    private GameDTO gameDTO;
    private Game game;
    private int difficulty;

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public Request(){}
    public RequestType getType() {
        return type;
    }

    public void setType(RequestType type) {
        this.type = type;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public GameDTO getGameDTO() {
        return gameDTO;
    }

    public void setGameDTO(GameDTO gameDTO) {
        this.gameDTO = gameDTO;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }


}
