package webApp.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

import webApp.managers.fileManager.FileManager;
import webApp.managers.userManager.UserManager;
import webApp.utils.ServletUtils;
//import com.google.gson.Gson;
import com.google.gson.*;

import java.io.IOException;

import static java.lang.Integer.MAX_VALUE;
import static webApp.utils.Constants.*;

@WebServlet(HOME_PATH)
public class HomeServlet extends HttpServlet
{
    private static final String DATA_UPDATE_HEADER = "X-Data-Update-Available";
    private static int latestRequestNumber = 0;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int requestNumber = Integer.parseInt(request.getParameter("requestNumber"));

        if (requestNumber != latestRequestNumber)
        {
            response.setContentType("text/html;charset=UTF-8");

            UserManager userManager = ServletUtils.getUserManager(getServletContext());
            FileManager fileManager = ServletUtils.getFileManager(getServletContext());
            Gson gson = new Gson();
            PrintWriter writer = response.getWriter();

            writer.println(gson.toJson(userManager));
            writer.println(gson.toJson(fileManager));

            response.setHeader(DATA_UPDATE_HEADER, "true");
            response.setHeader("X-Latest-Number", String.valueOf(latestRequestNumber));
        }
        else
            response.setHeader(DATA_UPDATE_HEADER, "false");

        response.setStatus(HttpServletResponse.SC_OK);
    }

    static void increaseRequestNumber(){
        latestRequestNumber = (latestRequestNumber + 1) % MAX_VALUE;
    }
}
