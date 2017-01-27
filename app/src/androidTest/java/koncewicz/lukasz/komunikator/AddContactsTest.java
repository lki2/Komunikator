package koncewicz.lukasz.komunikator;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import net.sqlcipher.database.SQLiteDatabase;

import org.junit.Test;
import org.junit.runner.RunWith;

import koncewicz.lukasz.komunikator.database.ContactPOJO;
import koncewicz.lukasz.komunikator.database.DatabaseAdapter;
import koncewicz.lukasz.komunikator.database.MessagePOJO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class AddContactsTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        SQLiteDatabase.loadLibs(appContext);
        DatabaseAdapter dbAdapter = DatabaseAdapter.getInstance(appContext);
        dbAdapter.open("123");

        assertNotEquals(-1L, dbAdapter.addContact(new ContactPOJO("+11 790561100", "Contact1")));
        assertEquals(-1L, dbAdapter.addContact(new ContactPOJO("+11 790561100", "Contact1")));
        assertNotEquals(-1L, dbAdapter.addContact(new ContactPOJO("+11 790 561 101", "Contact2")));
        assertEquals(-1L, dbAdapter.addContact(new ContactPOJO("+11 790 561 101", "Contact2")));
        assertNotEquals(-1L, dbAdapter.addContact(new ContactPOJO("+11 790-561-102", "Contact3")));
        assertEquals(-1L, dbAdapter.addContact(new ContactPOJO("+11 790-561-102", "Contact3")));
        assertNotEquals(-1L, dbAdapter.addContact(new ContactPOJO("+11790561103", "Contact4")));
        assertEquals(-1L, dbAdapter.addContact(new ContactPOJO("+11790561103", "Contact4")));
        assertNotEquals(-1L, dbAdapter.addContact(new ContactPOJO("+11790561104", "Contact5")));
        assertNotEquals(-1L, dbAdapter.addContact(new ContactPOJO("+11790561105", "Contact6")));
        assertNotEquals(-1L, dbAdapter.addContact(new ContactPOJO("+11790561106", "Contact7")));
        assertNotEquals(-1L, dbAdapter.addContact(new ContactPOJO("+11790561107", "Contact8")));
        assertNotEquals(-1L, dbAdapter.addContact(new ContactPOJO("+11790561108", "Contact9")));
        assertNotEquals(-1L, dbAdapter.addContact(new ContactPOJO("+11790561109", "Contact10")));
        assertNotEquals(-1L, dbAdapter.addContact(new ContactPOJO("+11790561110", "Contact11")));
        assertNotEquals(-1L, dbAdapter.addContact(new ContactPOJO("+11790561111", "Contact12")));
        assertNotEquals(-1L, dbAdapter.addContact(new ContactPOJO("+11790561112", "Contact13")));
        assertNotEquals(-1L, dbAdapter.addContact(new ContactPOJO("+11790561113", "Contact14")));
        assertNotEquals(-1L, dbAdapter.addContact(new ContactPOJO("+11790561114", "Contact15")));

        dbAdapter.close();
    }
}
