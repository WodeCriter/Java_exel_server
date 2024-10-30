package webApp.servlets;

import engine.api.Engine;
import engine.spreadsheet.api.ReadOnlySheet;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.perms.Permission;
import webApp.managers.fileManager.FileManager;
import webApp.utils.ServletUtils;
import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;
import webApp.utils.SessionUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import static utils.Constants.*;

@WebServlet(SHEETS_PATH + "/*")
public class SheetsServlet extends HttpServlet {
    FileManager fileManager;
    //update sheet - PUT (done)
    //get Sheet - GET (done)
    //delete sheet - DELETE (done)
    //add Range - PUT (done)
    //get Version - GET (done)
    //set width/height - PUT

    public void init() {
        fileManager = ServletUtils.getFileManager(getServletContext()); //todo: make sure it gives the same file manager
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGetAndPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGetAndPost(request, response);
    }

    private void doGetAndPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        RequestData requestData = parseRequest(request, response);
        if (requestData == null) return;

        Engine engine = requestData.engine;
        String sender = requestData.sender;

        if (engine.getUserPermission(sender).compareTo(Permission.READER) >= 0)
            switch (requestData.action.toLowerCase())
            {
                case VIEW_SHEET:
                    handleGetSheet(engine, response);
                    break;
                case VIEW_BY_VERSION:
                    handleGetByVersion(engine, request, response);
                    break;
                case VIEW_SORTED_SHEET:
                    handleGetSorted(engine, request, response);
                    break;
                case VIEW_FILTERED_SHEET:
                    handleGetFiltered(engine, request, response);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown action for GET: " + requestData.action);
            }
        else
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "\"" + sender + "\" is not allowed to " + requestData.action);
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestData requestData = parseRequest(request, response);
        if (requestData == null) return;

        Engine engine = requestData.engine;
        String sender = requestData.sender;

        if (engine.getUserPermission(sender).compareTo(Permission.WRITER) >= 0)
            switch (requestData.action.toLowerCase())
            {
                case UPDATE_CELL:
                    handleUpdateCell(engine, request, response);
                    break;
                case ADD_RANGE:
                    handleAddRange(engine, request, response);
                    break;
                case SET_CELL_WIDTH:
                    handleSetCellWidth(engine, request, response);
                    break;
                case SET_CELL_HEIGHT:
                    handleSetCellHeight(engine, request, response);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown action for PUT: " + requestData.action);
            }
        else
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "\"" + sender + "\" is not allowed to " + requestData.action);
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestData requestData = parseRequest(request, response);
        if (requestData == null) return;

        String sender = requestData.sender;

        if (requestData.engine.getUserPermission(sender).compareTo(Permission.WRITER) >= 0)
            switch (requestData.action.toLowerCase())
            {
                case DELETE_SHEET:
                    handleDeleteSheet(requestData.fileName, response);
                    break;
                case DELETE_RANGE:
                    handleDeleteRange(requestData.engine, request, response);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown action for DELETE: " + requestData.action);
            }
        else
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "\"" + sender + "\" is not allowed to " + requestData.action);
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
        String sender = SessionUtils.getUsername(request);

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

        return new RequestData(fileName, action, engine, sender);
    }

    // Handler methods

    private void handleGetSheet(Engine engine, HttpServletResponse response) throws IOException {
        synchronized (engine) {
            ReadOnlySheet sheet = engine.getSheet();
            addSheetToResponse(sheet, response);
        }
    }
    private void handleGetByVersion(Engine engine,HttpServletRequest request, HttpServletResponse response) throws IOException {
        String version = request.getParameter("version");
        synchronized (engine) {
            try {
                ReadOnlySheet versionSheet = engine.getSheetOfVersion(Integer.parseInt(version));
                response.setStatus(HttpServletResponse.SC_OK);
                addSheetToResponse(versionSheet, response);
            }
            catch (Exception e) {
                response.setStatus(SC_UNPROCESSABLE_CONTENT); //when get version fails
                response.getWriter().write(e.getMessage());
            }

        }
    }

    private void handleGetSorted(Engine engine, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String cord1 = request.getParameter("cord1");
        String cord2 = request.getParameter("cord2");
        List<String> columnsToSortBy = List.of(request.getParameterValues("columns"));

        synchronized (engine)
        {
            try
            {
                ReadOnlySheet sortedSheet = engine.createSortedSheetFromCords(cord1, cord2, columnsToSortBy);
                addSheetToResponse(sortedSheet, response);
            }
            catch (Exception e)
            {
                response.setStatus(SC_UNPROCESSABLE_CONTENT);
                response.getWriter().write(e.getMessage());
            }
        }
    }

    private void handleGetFiltered(Engine engine, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String cord1 = request.getParameter("cord1");
        String cord2 = request.getParameter("cord2");
        Map<String, List<String>> data = getFilterData(request);

        synchronized (engine)
        {
            try
            {
                ReadOnlySheet sortedSheet = engine.createFilteredSheetFromCords(cord1, cord2, data);
                addSheetToResponse(sortedSheet, response);
            }
            catch (Exception e)
            {
                response.setStatus(SC_UNPROCESSABLE_CONTENT);
                response.getWriter().write(e.getMessage());
            }
        }
    }

    private static Map<String, List<String>> getFilterData(HttpServletRequest request) throws IOException {
        // Read the JSON from the request body
        StringBuilder jsonBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream())))
        {
            String line;
            while ((line = reader.readLine()) != null)
                jsonBuilder.append(line);
        }

        String jsonString = jsonBuilder.toString();
        Type mapType = new TypeToken<Map<String, List<String>>>() {}.getType();

        // Parse JSON into a Java object (replace DataObject with your actual class)
        return GSON_INSTANCE.fromJson(jsonString, mapType);
    }

    private void handleUpdateCell(Engine engine, HttpServletRequest request, HttpServletResponse response) throws IOException {
        synchronized (engine) {
            // Read data from request to update the sheet
            try
            {
                engine.updateCellContents(request.getParameter("coordinate"), request.getParameter("newValue"));
                response.setStatus(HttpServletResponse.SC_OK);
                addSheetToResponse(engine.getSheet(), response);
            }
            catch (Exception e)
            {
                response.setStatus(SC_UNPROCESSABLE_CONTENT); //when maham fails
            }
        }
    }

    private void handleSetCellWidth(Engine engine, HttpServletRequest request, HttpServletResponse response) throws IOException {
        synchronized (engine) {
            String width = request.getParameter("width");
            try
            {
                engine.changeCellWidth(Integer.parseInt(width));
                addSheetToResponse(engine.getSheet(), response);
            }
            catch (Exception e)
            {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(e.getMessage());
            }

        }
    }
    private void handleSetCellHeight(Engine engine, HttpServletRequest request, HttpServletResponse response) throws IOException{
        synchronized (engine) {
            String height = request.getParameter("height");
            try
            {
                engine.changeCellHeight(Integer.parseInt(height));
                addSheetToResponse(engine.getSheet(), response);
            }
            catch (Exception e)
            {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(e.getMessage());
            }
        }
    }

    private void handleAddRange(Engine engine, HttpServletRequest request, HttpServletResponse response) throws IOException {
        synchronized (engine) {
            String rangeName = request.getParameter("rangeName");
            String topLeftCord = request.getParameter("topLeftCord");
            String bottomRightCord = request.getParameter("bottomRightCord");

            try
            {
                engine.addNewRange(rangeName, topLeftCord, bottomRightCord);
                addSheetToResponse(engine.getSheet(), response);
            }
            catch (IllegalArgumentException e)
            {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(e.getMessage());
            }
            catch (RuntimeException e)
            {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write(e.getMessage());
            }
        }
    }

    private void handleDeleteSheet(String fileName, HttpServletResponse response) throws IOException {
        synchronized (fileManager) {
            if (fileManager.removeFile(fileName))
            {
                response.setStatus(HttpServletResponse.SC_OK);
                HomeServlet.increaseRequestNumber();
            }
            else
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);;
        }
    }

    private void handleDeleteRange(Engine engine, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String rangeName = request.getParameter("rangeName");
        synchronized (engine) {
            try {
                engine.deleteRange(rangeName);
                response.setStatus(HttpServletResponse.SC_OK);
                addSheetToResponse(engine.getSheet(), response);
            } catch (IllegalArgumentException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(e.getMessage());
            } catch (RuntimeException e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write(e.getMessage());
            }
        }
    }

    private class RequestData {
        String fileName;
        String action;
        Engine engine;
        String sender;

        RequestData(String fileName, String action, Engine engine, String sender) {
            this.fileName = fileName;
            this.action = action;
            this.engine = engine;
            this.sender = sender;
        }
    }

    private void addSheetToResponse(ReadOnlySheet sheet, HttpServletResponse response) throws IOException {
        response.getWriter().write(GSON_INSTANCE.toJson(sheet));
    }
}

