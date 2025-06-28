package exam.client.controller;

import exam.model.User;
import exam.services.AppException;
import exam.services.IAppServices;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    public TextField aliasTextField;
    private MenuController menuController;
    Parent mainParent;

    private IAppServices server;

    public void setServer(IAppServices s){
        server=s;
    }

    public void setParent(Parent p){ mainParent = p; }

    public void setMenuController(MenuController menuController) {
        this.menuController = menuController;
    }

    public void showUserEditDialog(User user, ActionEvent actionEvent) {
        Stage stage=new Stage();
        stage.setTitle("Main menu");
        stage.setScene(new Scene(mainParent));
        stage.show();

        menuController.initModel();
        menuController.setUser(user);
        ((Node)(actionEvent.getSource())).getScene().getWindow().hide();
    }

    public void handleLogin(ActionEvent actionEvent) {
        User user = new User(aliasTextField.getText());
        try{
            System.out.println(user);
            server.login(user, menuController);
            showUserEditDialog(user, actionEvent);
        }catch (AppException e){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Agency");
            alert.setHeaderText("Authentication failure");
            alert.setContentText("Wrong username");
            alert.showAndWait();
        }
    }
}
