package exam.server;

import exam.model.Game;
import exam.model.GameDTO;
import exam.model.Question;
import exam.persistence.GameRepository;
import exam.persistence.QuestionRepository;
import exam.persistence.UserRepository;
import exam.services.AppException;
import exam.services.IAppObserver;
import exam.services.IAppServices;
import exam.model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppServiceImplementation implements IAppServices {
    private UserRepository userRepository;
    private GameRepository gameRepository;
    private QuestionRepository questionRepository;

    private static Logger logger = LogManager.getLogger(AppServiceImplementation.class);

    private Map<String, IAppObserver> loggedClients;

    public AppServiceImplementation(UserRepository userRepository, GameRepository gameRepository, QuestionRepository questionRepository) {
        this.userRepository = userRepository;
        this.gameRepository = gameRepository;
        this.questionRepository = questionRepository;
        loggedClients = new ConcurrentHashMap<>();
    }


    public void login(User user, IAppObserver client) throws AppException {
        User user1 = userRepository.findOne(user.getId());
        if (user1!=null){
            loggedClients.put(user.getId(), client);
        }else
            throw new AppException("Authentication failed.");
    }

    public void logout(User user, IAppObserver client) throws AppException {
        IAppObserver localClient=loggedClients.remove(user.getId());
        if (localClient==null)
            throw new AppException("User "+user.getId()+" is not logged in.");
    }

    @Override
    public List<Question> getAllQuestions(int difficulty){
        List<Question> allQuestions = questionRepository.findAll();

        List<Question> result = new ArrayList<>();

        for(Question question: allQuestions){
            if(question.getDifficulty() == difficulty)
                result.add(question);
        }
        return result;
    }

    @Override
    public List<GameDTO> getAllGames() throws AppException {
        try {
            List<Game> games = gameRepository.findAll();
            List<GameDTO> dtos = new ArrayList<>();

            for (Game game : games) {
                float time = Duration.between(game.getStartTime(), game.getEndTime()).toMillis() / 1000.0f;
                dtos.add(new GameDTO(game.getAlias(), game.getScore(), time));
            }


            dtos.sort(Comparator
                    .comparingInt(GameDTO::getScore).reversed()
                    .thenComparing(GameDTO::getTime));

            return dtos;
        } catch (Exception e) {
            throw new AppException("Failed to retrieve games", e);
        }
    }


    private final int defaultThreadsNo=5;
    @Override
    public void saveGame(Game game) throws AppException {
        gameRepository.save(game);
        ExecutorService executor= Executors.newFixedThreadPool(defaultThreadsNo);

        for( IAppObserver client : loggedClients.values()){
            executor.execute(() -> {
                try {
                    logger.debug("Notifying...");
                    client.gameFinish(game);
                } catch (AppException e) {
                    logger.error("Error notifying!");
                }
            });
        }

        executor.shutdown();
    }
}
