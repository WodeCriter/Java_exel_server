package webApp.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import webApp.utils.SessionUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static utils.Constants.*;

@WebServlet(CHAT_PATH)
public class ChatServlet extends HttpServlet
{
    private static List<String> messagesToPrintOnChat = new LinkedList<>();
    private static int latestRequestNumber = 0;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        String message = getMessageBody(request);
        String sender = SessionUtils.getUsername(request);

        if (!message.isEmpty())
        {
            messagesToPrintOnChat.add(sender + ": " + message);
            latestRequestNumber++;
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        int requestNumber = Integer.parseInt(request.getParameter("requestNumber"));

        if (requestNumber != latestRequestNumber)
            addAllChatDataToResponse(request, response);
        else
            response.setHeader(DATA_UPDATE_HEADER, "false");

        response.setStatus(HttpServletResponse.SC_OK);
    }

    private void addAllChatDataToResponse(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.getWriter().println(GSON_INSTANCE.toJson(messagesToPrintOnChat));

        response.setHeader(DATA_UPDATE_HEADER, "true");
        response.setHeader("X-Latest-Number", String.valueOf(latestRequestNumber));
    }

    private static String getMessageBody(HttpServletRequest request) throws IOException {
        // Read the text from the request body
        StringBuilder textBuilder = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                textBuilder.append(line);
            }
        }

        return textBuilder.toString();
    }
}
