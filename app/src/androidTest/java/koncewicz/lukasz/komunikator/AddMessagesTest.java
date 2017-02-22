package koncewicz.lukasz.komunikator;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import net.sqlcipher.database.SQLiteDatabase;

import org.junit.Test;
import org.junit.runner.RunWith;

import info.guardianproject.cacheword.CacheWordHandler;
import koncewicz.lukasz.komunikator.database.DatabaseAdapter;
import koncewicz.lukasz.komunikator.database.Message;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class AddMessagesTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        SQLiteDatabase.loadLibs(appContext);
        DatabaseAdapter dbAdapter = new DatabaseAdapter(appContext, new CacheWordHandler(appContext));

        long contactId = dbAdapter.getContactId("+11790561100");
        assertNotEquals(-1L, dbAdapter.addMsg(new Message(contactId, "Message RECEIVED", Message.Status.RECEIVED)));
        assertNotEquals(-1L, dbAdapter.addMsg(new Message(contactId, "Message SENT", Message.Status.SENT)));
        assertNotEquals(-1L, dbAdapter.addMsg(new Message(contactId, "Message FAILURE", Message.Status.FAILURE)));
        assertEquals(-1L, dbAdapter.addMsg(new Message(100, "Message FAILURE", Message.Status.FAILURE)));
        assertEquals(-1L, dbAdapter.addMsg(new Message(-23, "Message FAILURE", Message.Status.FAILURE)));
        dbAdapter.close();
    }
}
