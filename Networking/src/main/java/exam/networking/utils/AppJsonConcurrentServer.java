package exam.networking.utils;

import exam.networking.jsonprotocol.AppClientJsonWorker;
import exam.services.IAppServices;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.Socket;

public class AppJsonConcurrentServer extends AbsConcurrentServer{
    private IAppServices chatServer;
    private static Logger logger = LogManager.getLogger(AppJsonConcurrentServer.class);
    public AppJsonConcurrentServer(int port, IAppServices chatServer) {
        super(port);
        this.chatServer = chatServer;
        logger.info("ver");
    }

    @Override
    protected Thread createWorker(Socket client) {
        AppClientJsonWorker worker=new AppClientJsonWorker(chatServer, client);

        Thread tw=new Thread(worker);
        return tw;
    }
}
