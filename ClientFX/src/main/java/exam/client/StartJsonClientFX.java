package exam.client;
import exam.client.controller.LoginController;
import exam.client.controller.MenuController;
import exam.networking.jsonprotocol.AppServicesJsonProxy;
import exam.services.IAppServices;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

public class StartJsonClientFX extends Application {
    private Stage primaryStage;

    private static int defaultChatPort = 55556;
    private static String defaultServer = "localhost";

    private static Logger logger = LogManager.getLogger(StartJsonClientFX.class);

    public void start(Stage primaryStage) throws Exception {
        logger.debug("In start");
        Properties clientProps = new Properties();
        try {
            clientProps.load(StartJsonClientFX.class.getResourceAsStream("/appclient.properties"));
            logger.info("Client properties set {} ",clientProps);
            clientProps.list(System.out);
        } catch (IOException e) {
            logger.error("Cannot find appclient.properties " + e);
            logger.debug("Looking for appclient.properties in folder {}",(new File(".")).getAbsolutePath());
            return;
        }
        String serverIP = clientProps.getProperty("app.server.host", defaultServer);
        int serverPort = defaultChatPort;

        try {
            serverPort = Integer.parseInt(clientProps.getProperty("app.server.port"));
        } catch (NumberFormatException ex) {
            logger.error("Wrong port number " + ex.getMessage());
            logger.debug("Using default port: " + defaultChatPort);
        }
        logger.info("Using server IP " + serverIP);
        logger.info("Using server port " + serverPort);

        IAppServices server = new AppServicesJsonProxy(serverIP, serverPort);

        FXMLLoader loader = new FXMLLoader(
                getClass().getClassLoader().getResource("login.fxml"));
        Parent root=loader.load();
        LoginController ctrl =
                loader.<LoginController>getController();
        ctrl.setServer(server);

        FXMLLoader cloader = new FXMLLoader(
                getClass().getClassLoader().getResource("menu.fxml"));
        Parent croot=cloader.load();
        MenuController menuController =
                cloader.<MenuController>getController();
        menuController.setServer(server);

        ctrl.setMenuController(menuController);
        ctrl.setParent(croot);

        primaryStage.setTitle("App");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }


}
