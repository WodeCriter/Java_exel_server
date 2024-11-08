package webApp.servlets;

import com.google.gson.JsonObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import webApp.managers.fileManager.FileManager;
import webApp.managers.requestManager.RequestManager;
import webApp.managers.userManager.UserManager;
import webApp.utils.ServletUtils;
import webApp.utils.SessionUtils;

import java.io.IOException;

import static java.lang.Integer.MAX_VALUE;
import static utils.Constants.GSON_INSTANCE;

@WebServlet("/index") //todo: add constant
public class IndexServlet extends HttpServlet
{
    private static final String DATA_UPDATE_HEADER = "X-Data-Update-Available";
    private static int latestRequestNumber = 0;
    private static String userResponsible;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int requestNumber = Integer.parseInt(request.getParameter("requestNumber"));

        if (requestNumber != latestRequestNumber)
            addAllIndexDataToResponse(request, response);
        else
            response.setHeader(DATA_UPDATE_HEADER, "false");

        response.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response){
        String coordinate = request.getParameter("coordinate");
    }

    private void addAllIndexDataToResponse(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //response.setContentType("text/html;charset=UTF-8");

        FileManager fileManager = ServletUtils.getFileManager(getServletContext());
        String fileName = request.getParameter("fileName");
        String sender = SessionUtils.getUsername(request);

        if (!userResponsible.equals(sender))
            response.getWriter().println(GSON_INSTANCE.toJson(fileManager.getEngine(fileName).getSheet()));

        response.setHeader(DATA_UPDATE_HEADER, "true");
        response.setHeader("X-Latest-Number", String.valueOf(latestRequestNumber));
    }

    static void increaseRequestNumber(String userResponsibleForUpdate){
        latestRequestNumber = (latestRequestNumber + 1) % MAX_VALUE;
        userResponsible = userResponsibleForUpdate;
    }
}
