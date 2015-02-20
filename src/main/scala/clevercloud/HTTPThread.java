package clevercloud;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelException;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Marc-Antoine Perennou<Marc-Antoine@Perennou.com>
 */

public class HTTPThread extends Thread {
    int port;

    public HTTPThread(int port){
        this.port = port;
    }

    @Override
    public void run() {
        ServerBootstrap bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));
        bootstrap.setPipelineFactory(new HTTPPipelineFactory());
        try {
            bootstrap.bind(new InetSocketAddress(this.port));
        } catch (ChannelException e) {
            Logger.getLogger(HTTPThread.class.getName()).log(Level.SEVERE, null, e);
            System.exit(1);
        }
    }
}
