package me.libs.server.security

/**
 * @author Noam Y. Tenne
 */
class UsernameUnavailableException extends RuntimeException {

    UsernameUnavailableException(String username) {
        super("'$username' is already taken")
    }
}
