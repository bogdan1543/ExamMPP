package exam.networking.jsonprotocol;




import exam.model.Game;
import exam.model.GameDTO;
import exam.model.Question;
import exam.model.User;

import java.io.Serializable;
import java.util.List;


public class Response implements Serializable {
    private ResponseType type;
    private String errorMessage;
    private User user;
    private List<GameDTO> games;
    private Game game;
    private List<Question> questions;

    public Response() {
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public ResponseType getType() {
        return type;
    }

    public void setType(ResponseType type) {
        this.type = type;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<GameDTO> getGames() {
        return games;
    }

    public void setGames(List<GameDTO> games) {
        this.games = games;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }
}
