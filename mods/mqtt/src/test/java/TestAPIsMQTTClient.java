import org.fusesource.mqtt.client.*;
import org.junit.Assert;
import org.junit.Test;

import java.nio.Buffer;

import static org.fusesource.hawtbuf.UTF8Buffer.utf8;

public class TestAPIsMQTTClient {
    
    @Test
    public void testPublishAndSuscribeBlocking() throws Exception {
        MQTT mqtt = new MQTT();
        mqtt.setHost("tcp://test.mosquitto.org:1883");
        BlockingConnection connection = mqtt.blockingConnection();
        connection.connect();
        Topic[] topics = {new Topic(utf8("MiTopico"), QoS.AT_LEAST_ONCE)};
        byte[] qoses = connection.subscribe(topics);
        connection.publish("MiTopico", "Hola a todos".getBytes(), QoS.AT_LEAST_ONCE, false);
        Message message = connection.receive();
        Assert.assertEquals("Hola a todos", new String(message.getPayload())) ;
        message.ack();
        connection.disconnect();
    }

    @Test
    public void testPublishAndSuscribeBlockingFuture() throws Exception {
        MQTT mqtt = new MQTT();
        mqtt.setHost("tcp://test.mosquitto.org:1883");
        final Promise<Buffer> result = new Promise<Buffer>();
        FutureConnection connection = mqtt.futureConnection();
        Future<Void> f1 = connection.connect();
        f1.await();
        Future<byte[]> f2 = connection.subscribe(new Topic[]{new Topic(utf8("MiTopico"), QoS.AT_LEAST_ONCE)});
        byte[] qoses = f2.await();
        Future<Message> receive = connection.receive();
        connection.publish("MiTopico", "Hola de nuevo".getBytes(), QoS.AT_LEAST_ONCE, false);
        Message message = receive.await();
        Assert.assertEquals("Hola de nuevo", new String(message.getPayload()));
        message.ack();
        connection.disconnect().await();
    }
}