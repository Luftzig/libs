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
}
