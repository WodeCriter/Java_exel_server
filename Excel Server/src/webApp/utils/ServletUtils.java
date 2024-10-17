package webApp.utils;

//todo: replace with engine imports
//import engine.chat.ChatManager;

import webApp.managers.engineManager.SheetManager;
import webApp.managers.userManager.UserManager;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;

import static webApp.utils.Constants.INT_PARAMETER_ERROR;

public class ServletUtils {

    private static final String USER_MANAGER_ATTRIBUTE_NAME = "userManager";
    private static final String SHEET_MANAGER_ATTRIBUTE_NAME = "engineManager";

    /*
    Note how the synchronization is done only on the question and\or creation of the relevant managers and once they exists -
    the actual fetch of them is remained un-synchronized for performance POV
     */
    private static final Object userManagerLock = new Object();
    private static final Object engineManagerLock = new Object();

    public static UserManager getUserManager(ServletContext servletContext) {

        synchronized (userManagerLock) {
            if (servletContext.getAttribute(USER_MANAGER_ATTRIBUTE_NAME) == null) {
                servletContext.setAttribute(USER_MANAGER_ATTRIBUTE_NAME, new UserManager());
            }
        }
        return (UserManager) servletContext.getAttribute(USER_MANAGER_ATTRIBUTE_NAME);
    }

    public static SheetManager getSheetManager(ServletContext servletContext) {
        synchronized (engineManagerLock) {
            if (servletContext.getAttribute(SHEET_MANAGER_ATTRIBUTE_NAME) == null) {
                servletContext.setAttribute(SHEET_MANAGER_ATTRIBUTE_NAME, new SheetManager());
            }
        }
        return (SheetManager) servletContext.getAttribute(SHEET_MANAGER_ATTRIBUTE_NAME);
    }

    public static int getIntParameter(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException numberFormatException) {
            }
        }
        return INT_PARAMETER_ERROR;
    }
}
