package me.libs.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ratpack.server.RatpackServer;
import ratpack.server.ServerConfig;

/**
 * @author Noam Y. Tenne
 */
public class LibsServer {

    private static Logger logger = LoggerFactory.getLogger(LibsServer.class);

    public static void main(String[] args) {
        try {
            RatpackServer.of(b -> b
                    .serverConfig(ServerConfig.findBaseDirProps())
                    .handlers(chain -> chain
                            .handler("hello", context -> context.render("Hello Ratpack :)")) // Map to /foo
                            .handler(context -> context.render("root handler!")))).start();
        } catch (Exception e) {
            logger.error("Error occurred while starting Libs server", e);
        }
    }
}
