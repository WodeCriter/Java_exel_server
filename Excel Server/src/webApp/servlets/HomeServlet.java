package webApp.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import webApp.userManager.UserManager;
import webApp.utils.ServletUtils;
import com.google.gson.Gson;

import java.io.IOException;

import static webApp.utils.Constants.*;

@WebServlet(HOME_URL)
public class HomeServlet extends HttpServlet
{
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        UserManager userManager = ServletUtils.getUserManager(getServletContext());
        Gson gson = new Gson();
        PrintWriter writer = response.getWriter();

        String responseBody = gson.toJson(userManager);
        writer.println(responseBody);
    }

//    @Override
//    protected void doPost(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//        //Will later be used to save sheets to the server.
//    }
}
