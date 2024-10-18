package webApp.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

import webApp.managers.engineManager.FileManager;
import webApp.managers.userManager.UserManager;
import webApp.utils.ServletUtils;
//import com.google.gson.Gson;
import com.google.gson.*;

import java.io.IOException;

import static webApp.utils.Constants.*;

@WebServlet(HOME_PATH)
public class HomeServlet extends HttpServlet
{
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        UserManager userManager = ServletUtils.getUserManager(getServletContext());
        FileManager fileManager = ServletUtils.getSheetManager(getServletContext());
        Gson gson = new Gson();
        PrintWriter writer = response.getWriter();

        writer.println(gson.toJson(userManager));
        writer.println(gson.toJson(fileManager));
    }
}
