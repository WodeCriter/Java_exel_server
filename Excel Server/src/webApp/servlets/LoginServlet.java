package webApp.servlets;

import jakarta.servlet.annotation.WebServlet;
import static utils.Constants.*;

import webApp.managers.userManager.UserManager;
import webApp.utils.SessionUtils;
import webApp.utils.ServletUtils;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

//import static utils.Constants.USERNAME;

@WebServlet(LOGIN_PATH)
public class LoginServlet extends HttpServlet {
    private final String CHAT_ROOM_URL = "../chatroom/chatroom.html";
    private final String SIGN_UP_URL = "../signup/signup.html";
    private final String LOGIN_ERROR_URL = "/pages/loginerror/login_attempt_after_error.jsp";  // must start with '/' since will be used in request dispatcher...

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        String usernameFromSession = SessionUtils.getUsername(request);
        UserManager userManager = ServletUtils.getUserManager(getServletContext());
        if (usernameFromSession == null)
        {
            //user is not logged in yet
            String usernameFromParameter = request.getParameter(USERNAME);
            if (usernameFromParameter == null || usernameFromParameter.isEmpty())
            {
                changeResponseStatusAndMessage(response, 400, "No username provided");
            }
            else
            {
                //normalize the username value
                usernameFromParameter = usernameFromParameter.trim();

                synchronized (this)
                {
                    if (userManager.isUserExists(usernameFromParameter))
                    {
                        String errorMessage = "Username \"" + usernameFromParameter + "\" already exists. Please enter a different username.";
                        changeResponseStatusAndMessage(response, 403, errorMessage);
                        //getServletContext().getRequestDispatcher(LOGIN_ERROR_URL).forward(request, response);
                    }
                    else
                    {
                        userManager.addUser(usernameFromParameter);
                        HomeServlet.increaseRequestNumber();
                        request.getSession(true).setAttribute(USERNAME, usernameFromParameter);
                        //request.getSession(true).removeAttribute(USERNAME);

                        System.out.println("On login, request URI is: " + request.getRequestURI()); //for testing
                        //response.sendRedirect(HOME_PATH);
                    }
                }
            }
        }
        else
            changeResponseStatusAndMessage(response, 403, "Already logged in.");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException{
        UserManager userManager = ServletUtils.getUserManager(getServletContext());
        userManager.removeUser(SessionUtils.getUsername(request));
        request.getSession(true).removeAttribute(USERNAME);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    private void changeResponseStatusAndMessage(HttpServletResponse response, int statusCode, String message)
            throws IOException
    {
        response.setStatus(statusCode);
        response.getWriter().println(message);
    }
}
