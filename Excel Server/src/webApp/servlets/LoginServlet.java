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
    // urls that starts with forward slash '/' are considered absolute
    // urls that doesn't start with forward slash '/' are considered relative to the place where this servlet request comes from
    // you can use absolute paths, but then you need to build them from scratch, starting from the context path
    // ( can be fetched from request.getContextPath() ) and then the 'absolute' path from it.
    // Each method with it's pros and cons...
    private final String CHAT_ROOM_URL = "../chatroom/chatroom.html";
    private final String SIGN_UP_URL = "../signup/signup.html";
    private final String LOGIN_ERROR_URL = "/pages/loginerror/login_attempt_after_error.jsp";  // must start with '/' since will be used in request dispatcher...
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
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

                        System.out.println("On login, request URI is: " + request.getRequestURI()); //for testing
                        //response.sendRedirect(HOME_PATH);
                    }
                }
            }
        }
        else
            changeResponseStatusAndMessage(response, 403, "Already logged in.");
    }

// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
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
