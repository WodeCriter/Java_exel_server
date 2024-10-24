package webApp.servlets;

import engine.api.Engine;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import webApp.managers.fileManager.FileManager;
import webApp.utils.ServletUtils;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@WebServlet("/sheets/*")
public class SheetsServlet extends HttpServlet {
    FileManager fileManager;

    //update sheet - PUT
    //get Sheet - GET
    //delete sheet - DELETE
    //add Range - PUT
    //get Version - GET
    //get cell ?? (maybe update the ReadOnlySheet)



    public void init() {
        fileManager = ServletUtils.getFileManager(getServletContext()); //todo: make sure it gives the same file manager
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        RequestData requestData = parseRequest(request, response);
        if (requestData == null) return;

        Engine engine = requestData.engine;

        switch (requestData.action.toLowerCase()) {
            case "getsheet":
                handleGetSheet(engine, response);
                break;
            case "getversion":
                handleGetVersion(engine, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown action for GET: " + requestData.action);
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        RequestData requestData = parseRequest(request, response);
        if (requestData == null) return;

        Engine engine = requestData.engine;

        switch (requestData.action.toLowerCase()) {
            case "updatesheet":
                handleUpdateSheet(engine, request, response);
                break;
            case "addrange":
                handleAddRange(engine, request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown action for PUT: " + requestData.action);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        RequestData requestData = parseRequest(request, response);
        if (requestData == null) return;

        // For delete, we may not need the engine if we are deleting the sheet itself
        if (!"deletesheet".equalsIgnoreCase(requestData.action)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown action for DELETE: " + requestData.action);
            return;
        }

        handleDeleteSheet(requestData.fileName, response);
    }

    // Separate parse method
    private RequestData parseRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo(); // e.g., /FileName/action
        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "FileName and action are required in the path.");
            return null;
        }

        String[] pathParts = pathInfo.substring(1).split("/");
        if (pathParts.length < 2) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request path.");
            return null;
        }

        String fileName = pathParts[0];
        String action = pathParts[1];

        //todo: think
        // For DELETE, the engine may not exist
        Engine engine = null;
        if (!"DELETE".equalsIgnoreCase(request.getMethod()) || !"deletesheet".equalsIgnoreCase(action)) {
            engine = fileManager.getEngine(fileName);
            if (engine == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Engine not found for FileName: " + fileName);
                return null;
            }
        }

        return new RequestData(fileName, action, engine);
    }

    //t
    // Data class to hold parsed request data
    private static class RequestData {
        String fileName;
        String action;
        Engine engine;

        RequestData(String fileName, String action, Engine engine) {
            this.fileName = fileName;
            this.action = action;
            this.engine = engine;
        }
    }

    // Handler methods
    private void handleGetSheet(Engine engine, HttpServletResponse response) throws IOException {
        synchronized (engine) {
            String sheetData = engine.getSheet();
            response.setContentType("application/xml");
            response.getWriter().write(sheetData);
        }
    }

    private void handleGetVersion(Engine engine, HttpServletResponse response) throws IOException {
        synchronized (engine) {
            String version = engine.getVersion();
            response.getWriter().write("Version: " + version);
        }
    }

    private void handleUpdateSheet(Engine engine, HttpServletRequest request, HttpServletResponse response) throws IOException {
        synchronized (engine) {
            // Read data from request to update the sheet
            engine.updateSheet(request.getInputStream());
            response.getWriter().write("Sheet updated successfully for " + engine.getFileName());
        }
    }

    private void handleAddRange(Engine engine, HttpServletRequest request, HttpServletResponse response) throws IOException {
        synchronized (engine) {
            // Read data from request to add range
            engine.addRange(request.getInputStream());
            response.getWriter().write("Range added successfully to " + engine.getFileName());
        }
    }

    private void handleDeleteSheet(String fileName, HttpServletResponse response) throws IOException {
        synchronized (fileManager) {
            fileManager.removeFile(fileName);
            response.getWriter().write("Sheet deleted for " + fileName);
        }
    }
}
}