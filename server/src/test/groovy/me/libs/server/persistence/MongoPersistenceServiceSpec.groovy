package me.libs.server.persistence

import com.mongodb.BasicDBObject
import com.mongodb.client.MongoDatabase
import de.flapdoodle.embed.mongo.MongodExecutable
import de.flapdoodle.embed.mongo.MongodStarter
import de.flapdoodle.embed.mongo.config.IMongodConfig
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder
import de.flapdoodle.embed.mongo.config.Net
import de.flapdoodle.embed.mongo.distribution.Version
import de.flapdoodle.embed.process.runtime.Network
import spock.lang.Shared
import spock.lang.Specification

/**
 * @author Noam Y. Tenne
 */
class MongoPersistenceServiceSpec extends Specification {

    @Shared
    private MongodExecutable mongodExecutable

    @Shared
    private MongoPersistenceService mongoPersistenceService

    def setupSpec() {
        MongodStarter starter = MongodStarter.getDefaultInstance()

        int port = 12345
        IMongodConfig mongodConfig = new MongodConfigBuilder()
                .version(Version.Main.V3_1)
                .net(new Net(port, Network.localhostIsIPv6()))
                .build()

        mongodExecutable = starter.prepare(mongodConfig)
        mongodExecutable.start()

        def mongoClient = new MongoClients().get('mongodb://localhost:12345/libs')
        mongoPersistenceService = new MongoPersistenceService(mongoClient: mongoClient)
    }

    def cleanupSpec() {
        mongodExecutable.stop()
    }

    def 'Say hello'() {
        expect:
        mongoPersistenceService.sayHello().contains('3.1.0')
    }

    def 'Ensure collections exist'() {
        given:
        def db = libsDb()

        expect:
        db.getCollection('subject')
        db.getCollection('apiKey')
    }

    def 'Sign up'() {
        when:
        mongoPersistenceService.signUp('jim', 'jim@jim.com', 'hashedPass')

        then:
        def database = libsDb()
        def subjects = database.getCollection('subject')
        def results = subjects.find(new BasicDBObject('username', 'jim'))
        def jim = results.first()
        jim.getString('email') == 'jim@jim.com'
        jim.getString('password') == 'hashedPass'
    }

    def 'Check if a username is available'() {
        setup:
        mongoPersistenceService.signUp('unavailable', 'unavailable@unavailable.com', 'hashedPass')

        expect:
        !mongoPersistenceService.usernameAvailable('unavailable')
        mongoPersistenceService.usernameAvailable('available')
    }

    def 'Log in'() {
        setup:
        mongoPersistenceService.signUp('bob', 'bob@bob.com', 'hashedPass')

        expect:
        !mongoPersistenceService.login('bob', 'wrongpass')
        mongoPersistenceService.login('bob', 'hashedPass')
    }

    def 'Set API key'() {
        setup:
        mongoPersistenceService.setApiKey('joe', 'apiKey')

        expect:
        def database = libsDb()
        def apiKeys = database.getCollection('apiKey')
        def results = apiKeys.find(new BasicDBObject('username', 'joe'))
        def jim = results.first()
        jim.getString('apiKey') == 'apiKey'
    }

    def 'Get an API key'() {
        setup:
        mongoPersistenceService.setApiKey('jimbob', 'apiKey')

        expect:
        mongoPersistenceService.getApiKey('jimbob') == 'apiKey'
    }

    def 'Get a non-existing API key'() {
        expect:
        !mongoPersistenceService.getApiKey('janeboob')
    }

    def 'Log in with an API key'() {
        setup:
        mongoPersistenceService.setApiKey('joebob', 'apiKey')

        expect:
        mongoPersistenceService.loginApiKey('joebob', 'apiKey')
    }

    private MongoDatabase libsDb() {
        mongoPersistenceService.mongoClient.getDatabase('libs')
    }
}
