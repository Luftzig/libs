package me.libs.server.persistence

import me.libs.server.domain.Book

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

    Book getBook(String id)

    Book createBook(Book book)

    void deleteBook(String id)

    void updateBook(Book book)
}
