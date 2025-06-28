package exam.networking.jsonprotocol;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import exam.model.Game;
import exam.model.GameDTO;
import exam.model.Question;
import exam.model.User;
import exam.networking.utils.EnumTypeAdapter;
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
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class AppServicesJsonProxy implements IAppServices {
    private String host;
    private int port;

    private IAppObserver client;

    private BufferedReader input;
    private PrintWriter output;
    private Gson gsonFormatter;
    private Socket connection;

    private BlockingQueue<Response> qresponses;
    private volatile boolean finished;

    private static Logger logger = LogManager.getLogger(AppServicesJsonProxy.class);

    public AppServicesJsonProxy(String host, int port) {
        this.host = host;
        this.port = port;
        qresponses=new LinkedBlockingQueue<Response>();
    }



    private void closeConnection() {
        finished=true;
        try {
            input.close();
            output.close();
            connection.close();
            client=null;
        } catch (IOException e) {
            logger.error(e);
            logger.error(e.getStackTrace());
        }

    }

    private void sendRequest(Request request)throws AppException {
        String reqLine=gsonFormatter.toJson(request);
        try {
            output.println(reqLine);
            output.flush();
        } catch (Exception e) {
            throw new AppException("Error sending object "+e);
        }

    }

    private Response readResponse() throws AppException {
       Response response=null;
        try{

            response=qresponses.take();

        } catch (InterruptedException e) {
            logger.error(e);
            logger.error(e.getStackTrace());
        }
        return response;
    }
    private void initializeConnection() throws AppException {
        try {
            gsonFormatter= new GsonBuilder()
                    .registerTypeAdapter(java.time.LocalDateTime.class, new LocalDateTimeAdapter())
                    .registerTypeAdapter(RequestType.class, new EnumTypeAdapter<RequestType>())
                    .create();
            connection=new Socket(host,port);
            output=new PrintWriter(connection.getOutputStream());
            output.flush();
            input=new BufferedReader(new InputStreamReader(connection.getInputStream()));
            finished=false;
            startReader();
        } catch (IOException e) {
            logger.error(e);
            logger.error(e.getStackTrace());
        }
    }
    private void startReader(){
        Thread tw=new Thread(new ReaderThread());
        tw.start();
    }


    private void handleUpdate(Response response){
        if (response.getType()== ResponseType.NEW_GAME){
            Game game = response.getGame();
            logger.debug("New game {}", game);
            try {
                client.gameFinish(game);
            } catch (AppException e) {
                logger.error(e);
                logger.error(e.getStackTrace());
            }
        }
    }

    private boolean isUpdate(Response response){
        return response.getType()== ResponseType.NEW_GAME;
    }

    @Override
    public List<GameDTO> getAllGames() throws AppException {
        Request req= JsonProtocolUtils.createGetGamesRequest();
        sendRequest(req);
        Response response=readResponse();
        if (response.getType()== ResponseType.ERROR){
            String err=response.getErrorMessage();
            throw new AppException(err);
        }
        List<GameDTO> games = response.getGames();
        return games;
    }

    @Override
    public List<Question> getAllQuestions(int difficulty) {
        Request req= JsonProtocolUtils.createGetQuestionsRequest(difficulty);
        sendRequest(req);
        Response response=readResponse();
        if (response.getType()== ResponseType.ERROR){
            String err=response.getErrorMessage();
            throw new AppException(err);
        }
        List<Question> questions = response.getQuestions();
        return questions;
    }

    @Override
    public void saveGame(Game game) throws AppException {
        Request req=JsonProtocolUtils.createSaveGameRequest(game);
        logger.debug("Request type: " +  req.getType());
        sendRequest(req);
        Response response=readResponse();
        if (response.getType()== ResponseType.ERROR){
            String err=response.getErrorMessage();
            throw new AppException(err);
        }
    }

    @Override
    public void login(User user, IAppObserver client) throws AppException {
        initializeConnection();
        Request req= JsonProtocolUtils.createLoginRequest(user);
        sendRequest(req);
        Response response=readResponse();
        if (response.getType()== ResponseType.OK){
            this.client=client;
            return;
        }
        if (response.getType()== ResponseType.ERROR){
            String err=response.getErrorMessage();;
            closeConnection();
            throw new AppException(err);
        }
    }

    @Override
    public void logout(User user, IAppObserver client) throws AppException {
        Request req=JsonProtocolUtils.createLogoutRequest(user);
        sendRequest(req);
        Response response=readResponse();
        closeConnection();
        if (response.getType()== ResponseType.ERROR){
            String err=response.getErrorMessage();
            throw new AppException(err);
        }
    }

    private class ReaderThread implements Runnable{
        public void run() {
            while(!finished){
                try {
                    String responseLine=input.readLine();
                    logger.debug("response received {}",responseLine);
                    Response response=gsonFormatter.fromJson(responseLine, Response.class);
                    if (isUpdate(response)){
                        handleUpdate(response);
                    }else{

                        try {
                            qresponses.put(response);
                        } catch (InterruptedException e) {
                            logger.error(e);
                            logger.error(e.getStackTrace());
                        }
                    }
                } catch (IOException e) {
                    logger.error("Reading error "+e);
                }
            }
        }
    }
}
