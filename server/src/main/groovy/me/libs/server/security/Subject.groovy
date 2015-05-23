package me.libs.server.security

/**
 * @author Noam Y. Tenne
 */
class Subject {

    public static Subject ANYONYMOUS = new Subject(username: 'anonymous')

    String username
    String email
}