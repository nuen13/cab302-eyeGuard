import com.example.javafxreadingdemo.AnalyticsDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;


// Test methods for creating tables, inserting, updating, and retrieving focus session data.
class AnalyticsDAOTest {
    private AnalyticsDAO analyticsDAO;

    @BeforeEach
    void setUp() {
        analyticsDAO = new AnalyticsDAO();
    }

    @Test
    void testCreateFocusSessionTable() {
        assertDoesNotThrow(() -> analyticsDAO.createFocusSessionTable());
    }

    @Test
    void testInsertFocusSession() {
        int userId = 1;
        LocalDate sessionDate = LocalDate.now();
        int workTime = 3600;
        int breakTime = 600;

        assertDoesNotThrow(() -> analyticsDAO.insertFocusSession(userId, sessionDate, workTime, breakTime));
    }

    @Test
    void testUpdateFocusSession() {
        int userId = 1;
        LocalDate sessionDate = LocalDate.now();
        int duration = 1200;
        boolean isBreakTime = false;

        analyticsDAO.insertFocusSession(userId, sessionDate, 0, 0);
        assertDoesNotThrow(() -> analyticsDAO.updateFocusSession(userId, sessionDate, duration, isBreakTime));
    }

    @Test
    void testGetFocusSessionsByUserId() {
        int userId = 1;
        assertNotNull(analyticsDAO.getFocusSessionsByUserId(userId));
    }

    @Test
    void testGetDayStreak() {
        int userId = 1;
        assertNotNull(analyticsDAO.getDayStreak(userId));
    }

    @Test
    void testGetTotalWorkTime() {
        int userId = 1;
        assertTrue(analyticsDAO.getTotalWorkTime(userId) >= 0);
    }

    @Test
    void testGetTotalBreakTime() {
        int userId = 1;
        assertTrue(analyticsDAO.getTotalBreakTime(userId) >= 0);
    }

    @Test
    void testGetDaysAccessed() {
        int userId = 1;
        assertNotNull(analyticsDAO.getDaysAccessed(userId));
    }
}
