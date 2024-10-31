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
import webApp.utils.SessionUtils;

import java.io.IOException;

import static java.lang.Integer.MAX_VALUE;
import static utils.Constants.*;

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
            addAllHomeDataToResponse(request, response);
        else
            response.setHeader(DATA_UPDATE_HEADER, "false");

        response.setStatus(HttpServletResponse.SC_OK);
    }

    private void addAllHomeDataToResponse(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");

        UserManager userManager = ServletUtils.getUserManager(getServletContext());
        FileManager fileManager = ServletUtils.getFileManager(getServletContext());
        String sender = SessionUtils.getUsername(request);
        JsonObject jsonObject = new JsonObject();

        jsonObject.add("userNames", GSON_INSTANCE.toJsonTree(userManager.getUserNames()));
        jsonObject.add("fileNames", GSON_INSTANCE.toJsonTree(fileManager.getListOfFilesNames()));
        jsonObject.add("PermissionRequests", GSON_INSTANCE.toJsonTree(fileManager.getAllUserPendingRequests(sender)));
        response.getWriter().println(GSON_INSTANCE.toJson(jsonObject));

        response.setHeader(DATA_UPDATE_HEADER, "true");
        response.setHeader("X-Latest-Number", String.valueOf(latestRequestNumber));
    }

    static void increaseRequestNumber(){
        latestRequestNumber = (latestRequestNumber + 1) % MAX_VALUE;
    }
}
