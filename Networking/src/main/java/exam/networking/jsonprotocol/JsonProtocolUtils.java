package exam.networking.jsonprotocol;

import exam.model.Game;
import exam.model.GameDTO;
import exam.model.Question;
import exam.model.User;

import java.util.List;


public class JsonProtocolUtils {
    public static Response createNewGameResponse(Game game){
        Response resp=new Response();
        resp.setType(ResponseType.NEW_GAME);
        resp.setGame(game);
        return resp;
    }

    public static Response createOkResponse(){
        Response resp=new Response();
        resp.setType(ResponseType.OK);
        return resp;
    }

    public static Response createErrorResponse(String errorMessage){
        Response resp=new Response();
        resp.setType(ResponseType.ERROR);
        resp.setErrorMessage(errorMessage);
        return resp;
    }

    public static Response createGetGamesResponse(List<GameDTO> games){
        Response resp=new Response();
        resp.setType(ResponseType.GET_GAMES);
        resp.setGames(games);
        return resp;
    }

    public static Response createGetQuestionsResponse(List<Question> questions){
        Response resp=new Response();
        resp.setType(ResponseType.GET_QUESTIONS);
        resp.setQuestions(questions);
        return resp;
    }


    public static Request createLoginRequest(User user){
        Request req=new Request();
        req.setType(RequestType.LOGIN);
        req.setUser(user);
        return req;
    }

    public static Request createSaveGameRequest(Game game){
        Request req=new Request();
        req.setType(RequestType.GAME_FINISH);
        req.setGame(game);
        return req;
    }

    public static Request createLogoutRequest(User user){
        Request req=new Request();
        req.setType(RequestType.LOGOUT);
        req.setUser(user);
        return req;
    }

    public static Request createGetGamesRequest(){
        Request req=new Request();
        req.setType(RequestType.GET_GAMES);
        return req;
    }
    public static Request createGetQuestionsRequest(int difficulty){
        Request req=new Request();
        req.setType(RequestType.GET_QUESTIONS);
        req.setDifficulty(difficulty);
        return req;
    }
}
