package me.libs.server;

import me.libs.server.api.handler.LogInHandler;
import me.libs.server.api.handler.SignUpHandler;
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
                                    .handler("hello", context -> context.render("Hello Libs :)")) // Map to /foo
                                    .prefix("api/v1", nested -> {
                                                nested.prefix("auth", nestedAuth -> {
                                                            nestedAuth.handler("signup", new SignUpHandler());
                                                            nestedAuth.handler("login", new LogInHandler());
                                                        }
                                                );
                                            }
                                    )
                    )).start();
        } catch (Exception e) {
            logger.error("Error occurred while starting Libs server", e);
        }
    }
}
