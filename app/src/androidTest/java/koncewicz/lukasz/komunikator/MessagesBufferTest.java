package koncewicz.lukasz.komunikator;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import koncewicz.lukasz.komunikator.database.MessagePOJO;
import koncewicz.lukasz.komunikator.utils.MessagesBuffer;

import static org.junit.Assert.assertEquals;


/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class MessagesBufferTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        MessagesBuffer buffer = new MessagesBuffer(appContext);

        MessagePOJO msg0 = new MessagePOJO("phone1", "content1", "dateTime1");
        MessagePOJO msg1 = new MessagePOJO("phone2", "content2", "dateTime2");
        MessagePOJO msg2 = new MessagePOJO("phone3", "content3", "dateTime3");
        MessagePOJO msg3 = new MessagePOJO("phone4", "content4", "dateTime4");
        MessagePOJO msg4 = new MessagePOJO("phone5", "content5", "dateTime5");

        buffer.putMessage(msg0);
        buffer.putMessage(msg1);
        buffer.putMessage(msg2);
        buffer.putMessage(msg3);
        buffer.putMessage(msg4);

        MessagePOJO[] msgs = buffer.popMessages();

        assertEquals(msgs.length, 5);

        assertEquals(msgs[0].getSenderNumber(), msg0.getSenderNumber());
        assertEquals(msgs[0].getContent(), msg0.getContent());
        assertEquals(msgs[0].getDateTime(), msg0.getDateTime());

        assertEquals(msgs[4].getSenderNumber(), msg4.getSenderNumber());
        assertEquals(msgs[4].getContent(), msg4.getContent());
        assertEquals(msgs[4].getDateTime(), msg4.getDateTime());

        msgs = buffer.popMessages();
        assertEquals(msgs.length, 0);

        buffer.putMessage(msg2);
        msgs = buffer.popMessages();
        assertEquals(msgs.length, 1);

        assertEquals(msgs[0].getSenderNumber(), msg2.getSenderNumber());
        assertEquals(msgs[0].getContent(), msg2.getContent());
        assertEquals(msgs[0].getDateTime(), msg2.getDateTime());
    }
}