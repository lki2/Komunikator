package koncewicz.lukasz.komunikator;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import net.sqlcipher.database.SQLiteDatabase;

import org.junit.Test;
import org.junit.runner.RunWith;

import koncewicz.lukasz.komunikator.database.DatabaseAdapter;

import static org.junit.Assert.assertEquals;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class DeleteContactsTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        SQLiteDatabase.loadLibs(appContext);
        DatabaseAdapter dbAdapter = DatabaseAdapter.getInstance(appContext);
        dbAdapter.open("123");

        assertEquals(true, dbAdapter.deleteContact(dbAdapter.getContact("+11 790561100")));
        assertEquals(false, dbAdapter.deleteContact(dbAdapter.getContact("+11 790561100")));
        assertEquals(true, dbAdapter.deleteContact(dbAdapter.getContact("+11 790 561 101")));
        assertEquals(false, dbAdapter.deleteContact(dbAdapter.getContact("+11 790 561 101")));
        assertEquals(true, dbAdapter.deleteContact(dbAdapter.getContact("+11 790-561-102")));
        assertEquals(false, dbAdapter.deleteContact(dbAdapter.getContact("+11 790-561-102")));
        assertEquals(true, dbAdapter.deleteContact(dbAdapter.getContact("+11790561103")));
        assertEquals(true, dbAdapter.deleteContact(dbAdapter.getContact("+11790561104")));
        assertEquals(true, dbAdapter.deleteContact(dbAdapter.getContact("+11790561105")));
        assertEquals(true, dbAdapter.deleteContact(dbAdapter.getContact("+11790561106")));
        assertEquals(true, dbAdapter.deleteContact(dbAdapter.getContact("+11790561107")));
        assertEquals(true, dbAdapter.deleteContact(dbAdapter.getContact("+11790561108")));
        assertEquals(true, dbAdapter.deleteContact(dbAdapter.getContact("+11790561109")));
        assertEquals(true, dbAdapter.deleteContact(dbAdapter.getContact("+11790561110")));
        assertEquals(true, dbAdapter.deleteContact(dbAdapter.getContact("+11790561111")));
        assertEquals(true, dbAdapter.deleteContact(dbAdapter.getContact("+11790561112")));
        assertEquals(true, dbAdapter.deleteContact(dbAdapter.getContact("+11790561113")));
        assertEquals(true, dbAdapter.deleteContact(dbAdapter.getContact("+11790561114")));

        dbAdapter.close();

    }
}