package exam.client.controller;

import exam.model.Game;
import exam.model.GameDTO;
import exam.model.Question;
import exam.model.User;
import exam.services.AppException;
import exam.services.IAppObserver;
import exam.services.IAppServices;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MenuController implements IAppObserver {
    public TableView<GameDTO> tableView;
    public TableColumn<GameDTO, String> tableColumnAlias;
    public TableColumn<GameDTO, Integer> tableColumnScore;
    public TableColumn<GameDTO, Float> tableColumnTime;
    public TextArea answerTextArea;
    public Label questionLabel;
    private IAppServices server;

    private int score;
    private Question currentQuestion;
    private int level = 1;
    private int firstLevel = 2;
    private int wrongFirst = 0;
    private int secondLevel = 2;
    private int thirdLevel = 2;

    private boolean gameOver = false;
    private int idx = 0;

    private LocalDateTime startTime;
    private boolean gameStarted = false;

    private User currentUser;
    private ObservableList<GameDTO> model = FXCollections.observableArrayList();

    private static Logger logger = LogManager.getLogger(MenuController.class);

    public void setUser(User user) {
        currentUser = user;
    }

    public void setServer(IAppServices server) {
        this.server = server;
    }

    @FXML
    public void initialize() {
        tableColumnAlias.setCellValueFactory(new PropertyValueFactory<>("alias"));
        tableColumnScore.setCellValueFactory(new PropertyValueFactory<>("score"));
        tableColumnTime.setCellValueFactory(new PropertyValueFactory<>("time"));
    }

    public void initModel() {
        try {
            List<GameDTO> games = server.getAllGames();
            logger.debug("games:" + games);
            model = FXCollections.observableArrayList(games);
            tableView.setItems(model);

        } catch (AppException e) {
            logger.error(e);
            logger.error(e.getStackTrace());
        }
        newQuestion();
    }


    @Override
    public void gameFinish(Game game) throws AppException {
        Platform.runLater(()->{
            initModel();
            logger.debug("List updated");
        });
    }

    public void handleSubmit(ActionEvent actionEvent) {
        if(gameOver) return;
        if(!gameStarted){
            startTime = LocalDateTime.now();
            gameStarted = true;
        }

        if(Objects.equals(currentQuestion.getAnswer(), answerTextArea.getText())){
            int ok = 1;
            score += 4*level*level;

            if(firstLevel == 0 && secondLevel == 0){
                thirdLevel--;
            }
            if(thirdLevel == 0) {
                saveWin();
                gameOver = true;
                return;
            }

            if(firstLevel == 0 && secondLevel > 0){
                secondLevel--;
            }
            if(secondLevel == 0 && thirdLevel == 2){
                level = 3;
                wrongFirst = 0;
                idx = 0;
            }
            if(firstLevel > 0){
                firstLevel--;
            }
            if(firstLevel == 0 && secondLevel == 2){
                level = 2;
                wrongFirst = 0;
                idx = 0;
            }
        }else{
            wrongFirst++;
            score -= 2;
        }

        newQuestion();
    }

    private void newQuestion() {
        if(gameOver) return;
        if(wrongFirst == 2){
            saveWin();
            gameOver = true;
            return;
        }
        List<Question> same_level_questions = server.getAllQuestions(level);
        if(idx < same_level_questions.size()) {
            questionLabel.setText(same_level_questions.get(idx).getQuestion());
            currentQuestion = same_level_questions.get(idx);
        }
        idx++;
    }

    private void saveWin() {
        LocalDateTime endTime = LocalDateTime.now();
        Game game = new Game(currentUser.getId(), score, startTime, endTime);

        try {
            server.saveGame(game);

        } catch (AppException e) {
            logger.error("Eroare la salvarea jocului", e);
        }
    }
}
