package Ex03.Manager;

import Ex03.Utils.Error;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

// Manages the errors in the system
public class ErrorManager {
    private final Map<HttpSession, Error> m_SessionToError;

    public ErrorManager() {
        m_SessionToError = new HashMap<>();
    }

    public void AddError(HttpSession i_UserSession, Error i_UserError) {
        m_SessionToError.put(i_UserSession, i_UserError);
    }

    public Error GetErrorBySession(HttpSession i_UserSession) {
        return m_SessionToError.get(i_UserSession);
    }
}
