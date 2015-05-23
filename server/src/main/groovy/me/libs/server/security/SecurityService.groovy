package me.libs.server.security
/**
 * @author Noam Y. Tenne
 */
interface SecurityService {

    Subject login(String username, String password)

    Subject signUp(String username, String email, String password)

    String getOrCreateApiKey(Subject subject)

    boolean loginApiKey(String username, String apiKey)
}