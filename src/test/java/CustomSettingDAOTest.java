import com.example.javafxreadingdemo.CustomSetting;
import com.example.javafxreadingdemo.CustomSettingDAO;
import com.example.javafxreadingdemo.DatabaseConnection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CustomSettingDAOTest {
    private CustomSettingDAO customSettingDAO;
    private Connection connection;

    @BeforeEach
    void setUp() {
        customSettingDAO = new CustomSettingDAO();
        customSettingDAO.createCustomSettingTable();
        connection = DatabaseConnection.getInstance();
    }

    @Test
    void testCreateCustomSettingTable() {
        assertDoesNotThrow(() -> customSettingDAO.createCustomSettingTable());
    }

    @Test
    void testSaveCustomSetting() {
        int userId = 1;
        String themeColor = "Default";
        String soundAlert = "Chime";
        int breakTime = 10;
        int workTime = 25;

        assertDoesNotThrow(() -> customSettingDAO.saveCustomSetting(userId, themeColor, soundAlert, breakTime, workTime));

        List<CustomSetting> settings = customSettingDAO.getCustomSetting(userId);
        assertNotNull(settings);
        assertFalse(settings.isEmpty());

        CustomSetting setting = settings.get(0);
        assertEquals(themeColor, setting.getThemeColor());
        assertEquals(soundAlert, setting.getSoundAlert());
        assertEquals(breakTime, setting.getBreakTime());
        assertEquals(workTime, setting.getWorkTime());
    }

    @Test
    void testUpdateCustomSetting() {
        int userId = 1;
        String initialThemeColor = "Default";
        String initialSoundAlert = "Chime";
        int initialBreakTime = 10;
        int initialWorkTime = 25;

        customSettingDAO.saveCustomSetting(userId, initialThemeColor, initialSoundAlert, initialBreakTime, initialWorkTime);

        List<CustomSetting> initialSettings = customSettingDAO.getCustomSetting(userId);
        assertNotNull(initialSettings);
        assertFalse(initialSettings.isEmpty());

        CustomSetting initialSetting = initialSettings.get(0);
        assertEquals(initialThemeColor, initialSetting.getThemeColor());
        assertEquals(initialSoundAlert, initialSetting.getSoundAlert());
        assertEquals(initialBreakTime, initialSetting.getBreakTime());
        assertEquals(initialWorkTime, initialSetting.getWorkTime());

        // Now delete the existing settings for the user to prevent duplicates
        deleteCustomSetting(userId);

        // Update the settings
        String newThemeColor = "Summer";
        String newSoundAlert = "Bell";
        int newBreakTime = 15;
        int newWorkTime = 30;

        // Save the updated settings
        assertDoesNotThrow(() -> customSettingDAO.saveCustomSetting(userId, newThemeColor, newSoundAlert, newBreakTime, newWorkTime));

        List<CustomSetting> updatedSettings = customSettingDAO.getCustomSetting(userId);
        assertNotNull(updatedSettings);
        assertFalse(updatedSettings.isEmpty());

        CustomSetting updatedSetting = updatedSettings.get(0);
        assertEquals(newThemeColor, updatedSetting.getThemeColor());
        assertEquals(newSoundAlert, updatedSetting.getSoundAlert());
        assertEquals(newBreakTime, updatedSetting.getBreakTime());
        assertEquals(newWorkTime, updatedSetting.getWorkTime());
    }

    // Helper method to delete the custom setting for a user
    private void deleteCustomSetting(int userId) {
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM customSetting WHERE user_id = ?")) {
            statement.setInt(1, userId);
            statement.executeUpdate();
        } catch (SQLException ex) {
            System.err.println("Error deleting custom setting: " + ex.getMessage());
        }
    }
}
