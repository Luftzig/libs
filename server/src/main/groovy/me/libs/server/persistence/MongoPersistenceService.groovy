package me.libs.server.persistence

/**
 * @author Noam Y. Tenne
 */
class MongoPersistenceService implements PersistenceService {

    MongoPersistenceService() {
        println 'its initialized !!! '
    }

    @Override
    String sayHello() {
        'Hello Libs :)'
    }

    @Override
    boolean usernameAvailable(String username) {
        return false
    }

    @Override
    void signUp(String username, String email, String hashedPassword) {

    }

    @Override
    String getApiKey(String username) {
        return null
    }

    @Override
    void setApiKey(String username, String apiKey) {

    }

    @Override
    boolean loginApiKey(String username, String password) {
        return false
    }

    @Override
    boolean login(String username, String password) {
        return false
    }
}
