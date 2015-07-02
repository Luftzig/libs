package me.libs.server.persistence

/**
 * @author Noam Y. Tenne`
 */
interface PersistenceService {

    String sayHello()

    boolean usernameAvailable(String username)

    void signUp(String username, String email, String hashedPassword)

    String getApiKey(String username)

    void setApiKey(String username, String apiKey)

    boolean loginApiKey(String username, String apiKey)

    boolean login(String username, String hashedPassword)
}
