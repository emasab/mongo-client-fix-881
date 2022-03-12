package mongodb.client.fix

import com.mongodb.MongoClient
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.utility.DockerImageName
import java.time.Duration
import kotlin.test.assertFalse
import kotlin.test.fail

class AppTest {

    private val mongoDBContainer: MongoDBContainer = MongoDBContainer(DockerImageName.parse("mongo:4.0.10"))

    @Before
    fun setUp() {
        mongoDBContainer.start()
        mongoDBContainer.waitingFor(
            Wait.forListeningPort()
            .withStartupTimeout(Duration.ofSeconds(180L)))
    }

    @Test
    fun `not have an active transaction after withTransaction with unchecked exception` () {
        val mongoClient = MongoClient("127.0.0.1", mongoDBContainer.firstMappedPort)
        val session = mongoClient.startSession()
        mongoClient.use {
            session.use {
                try {
                    session.withTransaction {
                        throwingRuntimeException()
                    }
                    fail("should not reach here")
                } catch (ex: Exception) {
                    assertFalse(session.hasActiveTransaction(), "should not have an active transaction")
                }
            }
        }
    }

    @Test
    fun `not have an active transaction after withTransaction with checked exception` () {
        val mongoClient = MongoClient("127.0.0.1", mongoDBContainer.firstMappedPort)
        val session = mongoClient.startSession()
        mongoClient.use {
            session.use {
                try {
                    session.withTransaction {
                        throwingException()
                    }
                    fail("should not reach here")
                } catch (ex: Exception) {
                    assertFalse(session.hasActiveTransaction(), "should not have an active transaction")
                }
            }
        }
    }

    @After
    fun tearDown() {
        mongoDBContainer.close()
    }
}
