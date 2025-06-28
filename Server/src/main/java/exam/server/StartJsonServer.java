package exam.server;

import exam.networking.utils.AbstractServer;
import exam.networking.utils.AppJsonConcurrentServer;
import exam.persistence.GameRepository;
import exam.persistence.QuestionRepository;
import exam.persistence.UserRepository;
import exam.persistence.hibernate.GameHibernateRepository;
import exam.persistence.hibernate.QuestionHibernateRepository;
import exam.persistence.hibernate.UserHibernateRepository;
import exam.services.IAppServices;
import exam.networking.utils.ServerException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

public class StartJsonServer {
    private static int defaultPort=55556;
    private static Logger logger = LogManager.getLogger(StartJsonServer.class);
    public static void main(String[] args) {
        Properties serverProps=new Properties();
        try {
            serverProps.load(StartJsonServer.class.getResourceAsStream("/appserver.properties"));
           logger.info("Server properties set. {} ", serverProps);
            //serverProps.list(System.out);
        } catch (IOException e) {
            logger.error("Cannot find appserver.properties "+e);
            logger.debug("Looking for file in "+(new File(".")).getAbsolutePath());
            return;
        }

        UserRepository userRepo= new UserHibernateRepository();
        GameRepository gameRepository=new GameHibernateRepository();
        QuestionRepository questionRepository = new QuestionHibernateRepository();

        IAppServices appServiceImpl = new AppServiceImplementation(userRepo,gameRepository,questionRepository);
        int appServerPort=defaultPort;
        try {
            appServerPort = Integer.parseInt(serverProps.getProperty("app.server.port"));
        }catch (NumberFormatException nef){
            logger.error("Wrong  Port Number"+nef.getMessage());
            logger.debug("Using default port "+defaultPort);
        }
        logger.debug("Starting server on port: "+appServerPort);
        AbstractServer server = new AppJsonConcurrentServer(appServerPort, appServiceImpl);
        try {
            server.start();
        } catch (ServerException e) {
            logger.error("Error starting the server" + e.getMessage());
        }
    }
}
