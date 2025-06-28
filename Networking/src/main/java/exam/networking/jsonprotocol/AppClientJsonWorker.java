package exam.networking.jsonprotocol;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import exam.model.Game;
import exam.model.GameDTO;
import exam.model.Question;
import exam.model.User;
import exam.networking.utils.LocalDateTimeAdapter;
import exam.services.AppException;
import exam.services.IAppObserver;
import exam.services.IAppServices;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

public class AppClientJsonWorker implements Runnable, IAppObserver {
    private IAppServices server;
    private Socket connection;

    private BufferedReader input;
    private PrintWriter output;
    private Gson gsonFormatter;
    private volatile boolean connected;

    private static Logger logger = LogManager.getLogger(AppClientJsonWorker.class);

    public AppClientJsonWorker(IAppServices server, Socket connection) {
        this.server = server;
        this.connection = connection;
        gsonFormatter = new GsonBuilder()
                .registerTypeAdapter(java.time.LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
        try{
            output=new PrintWriter(connection.getOutputStream());
            input=new BufferedReader(new InputStreamReader(connection.getInputStream()));
            connected=true;
        } catch (IOException e) {
            logger.error(e);
            logger.error(e.getStackTrace());
        }
    }

    public void run() {
        while(connected){
            try {
                String requestLine=input.readLine();
                Request request=gsonFormatter.fromJson(requestLine, Request.class);
                Response response=handleRequest(request);
                if (response!=null){
                    sendResponse(response);
                }
            } catch (IOException e) {
                logger.error(e);
                logger.error(e.getStackTrace());
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                logger.error(e);
                logger.error(e.getStackTrace());
            }
        }
        try {
            input.close();
            output.close();
            connection.close();
        } catch (IOException e) {
            logger.error("Error "+e);
        }
    }

    private static Response okResponse=JsonProtocolUtils.createOkResponse();

    private Response handleRequest(Request request){
       Response response=null;
        if (request.getType()== RequestType.LOGIN){
            logger.debug("Login request ...{}"+request.getUser());
            User user=request.getUser();
            try {
                server.login(user, this);
                return okResponse;
            } catch (AppException e) {
                connected=false;
                return JsonProtocolUtils.createErrorResponse(e.getMessage());
            }
        }
        if (request.getType()== RequestType.LOGOUT){
            logger.debug("Logout request {}",request.getUser());
            User user=request.getUser();
            try {
                server.logout(user, this);
                connected=false;
                return okResponse;

            } catch (AppException e) {
                return JsonProtocolUtils.createErrorResponse(e.getMessage());
            }
        }
        if (request.getType()== RequestType.GAME_FINISH){

            Game game=request.getGame();
            logger.debug("GameFinishRequest ...{} ",game);
            try {
                server.saveGame(game);
                return okResponse;
            } catch (AppException e) {
                return JsonProtocolUtils.createErrorResponse(e.getMessage());
            }
        }

        if (request.getType()== RequestType.GET_GAMES){
            logger.debug("GetGames Request ...user= {}",request.getUser());
            try {
                List<GameDTO> excursions = server.getAllGames();

                return JsonProtocolUtils.createGetGamesResponse(excursions);
            } catch (AppException e) {
                return JsonProtocolUtils.createErrorResponse(e.getMessage());
            }
        }
        if (request.getType()== RequestType.GET_QUESTIONS){
            logger.debug("GetQuestions Request ...user= {}",request.getUser());
            try {
                List<Question> questions = server.getAllQuestions(request.getDifficulty());

                return JsonProtocolUtils.createGetQuestionsResponse(questions);
            } catch (AppException e) {
                return JsonProtocolUtils.createErrorResponse(e.getMessage());
            }
        }

        return response;
    }

    private void sendResponse(Response response) throws IOException{
        String responseLine=gsonFormatter.toJson(response);
        logger.debug("sending response "+responseLine);
        synchronized (output) {
            output.println(responseLine);
            output.flush();
        }
    }

    @Override
    public void gameFinish(Game game) throws AppException {
        Response resp= JsonProtocolUtils.createNewGameResponse(game);
        logger.debug("New Game  "+game);
        try {
            sendResponse(resp);
        } catch (IOException e) {
            throw new AppException("Sending error: "+e);
        }
    }
}
