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
import java.util.*;

import static jakarta.servlet.http.HttpServletResponse.*;
import static utils.Constants.*;

@WebServlet(SHEETS_PATH + "/*")
public class SheetsServlet extends HttpServlet
{
    private FileManager fileManager;
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
                    handleGetSheet(engine, sender, response);
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
                    addErrorToResponse(SC_BAD_REQUEST,
                            "Unknown action for GET: " + requestData.action, response);
            }
        else
            addErrorToResponse(HttpServletResponse.SC_FORBIDDEN,
                    "User \"" + sender + "\" is not allowed to " + requestData.action, response);
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestData requestData = parseRequest(request, response);
        if (requestData == null) return;

        Engine engine = requestData.engine;
        String sender = requestData.sender;
        String fileName = requestData.fileName;

        if (engine.getUserPermission(sender).compareTo(Permission.WRITER) >= 0)
        {
            switch (requestData.action.toLowerCase()) {
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
                case PUT_CELL_FOR_ANALYSIS:
                    handlePutCellAnalysis(engine, request, response);
                    break;
                case UPDATE_CELL_ANALYSIS:
                    handleUpdateCellAnalysis(engine, request, response);
                    break;
                case STOP_CELL_ANALYSIS:
                    handleStopCellAnalysis(engine, request, response);
                    break;
                default:
                    addErrorToResponse(SC_BAD_REQUEST,
                            "Unknown action for PUT: " + requestData.action, response);
            }
            IndexServlet.increaseRequestNumber(sender, fileName);
        }
        else
        {
            addErrorToResponse(HttpServletResponse.SC_FORBIDDEN,
                    "User \"" + sender + "\" is not allowed to " + requestData.action, response);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestData requestData = parseRequest(request, response);
        if (requestData == null) return;

        Engine engine = requestData.engine;
        String sender = requestData.sender;
        String fileName = requestData.fileName;

        if (engine == null)
        {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        switch (requestData.action.toLowerCase())
        {
            case DELETE_SHEET:
                if (engine.getUserPermission(sender).compareTo(Permission.OWNER) >= 0)
                    handleDeleteSheet(requestData.fileName, response);
                else
                    response.sendError(SC_FORBIDDEN, "\"" + sender + "\" is not allowed to " + requestData.action);
                break;
            case DELETE_RANGE:
                if (engine.getUserPermission(sender).compareTo(Permission.WRITER) >= 0)
                {
                    handleDeleteRange(requestData.engine, request, response);
                    IndexServlet.increaseRequestNumber(sender, fileName);
                }
                else
                    addErrorToResponse(SC_FORBIDDEN,
                            "\"" + sender + "\" is not allowed to " + requestData.action, response);
                break;
            default:
                addErrorToResponse(SC_BAD_REQUEST,
                        "Unknown action for DELETE: " + requestData.action, response);
        }
    }

    // Separate parse method

    private RequestData parseRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo(); // e.g., /FileName/action
        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendError(SC_BAD_REQUEST, "FileName and action are required in the path.");
            return null;
        }

        String[] pathParts = pathInfo.substring(1).split("/");
        if (pathParts.length < 2) {
            response.sendError(SC_BAD_REQUEST, "Invalid request path.");
            return null;
        }

        String fileName = pathParts[0];
        String action = pathParts[1];
        String sender = SessionUtils.getUsername(request);

        Engine engine = fileManager.getEngine(fileName);
        return new RequestData(fileName, action, engine, sender);
    }

    // Handler methods
    private void handleGetSheet(Engine engine, String sender, HttpServletResponse response) throws IOException {
        synchronized (engine) {
            ReadOnlySheet sheet = engine.getSheet();
            addSheetToResponse(sheet, response);
            response.setHeader("permission", engine.getUserPermission(sender).toString());
        }
    }

    private void handleGetByVersion(Engine engine,HttpServletRequest request, HttpServletResponse response) throws IOException {
        String version = request.getParameter("version");
        synchronized (engine) {
            try {
                ReadOnlySheet versionSheet = engine.getSheetOfVersion(Integer.parseInt(version));
                response.setStatus(SC_OK);
                addSheetToResponse(versionSheet, response);
            }
            catch (Exception e) {
                addErrorToResponse(SC_UNPROCESSABLE_CONTENT, e.getMessage(), response);
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
                addErrorToResponse(SC_UNPROCESSABLE_CONTENT, e.getMessage(), response);
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
                addErrorToResponse(SC_UNPROCESSABLE_CONTENT, e.getMessage(), response);
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

    private void handlePutCellAnalysis(Engine engine, HttpServletRequest request, HttpServletResponse response) {
        synchronized (engine) {
            Set<String> coordinates = new HashSet<>(Arrays.asList(request.getParameterValues("coordinate")));
            engine.pickCellForDynamicAnalysis(coordinates);
            response.setStatus(SC_OK);
        }
    }

    private void handleUpdateCellAnalysis(Engine engine, HttpServletRequest request, HttpServletResponse response) throws IOException {
        synchronized (engine) {
            ReadOnlySheet sheet = engine.changeCellValueForDynamicAnalysis(request.getParameter("coordinate"),
                    request.getParameter("newValue"));
            addSheetToResponse(sheet, response);
            response.setStatus(SC_OK);
        }
    }

    private void handleStopCellAnalysis(Engine engine, HttpServletRequest request, HttpServletResponse response) throws IOException {
        boolean toSave = Boolean.parseBoolean(request.getParameter("toSave"));
        String sender = SessionUtils.getUsername(request);
        ReadOnlySheet sheet;

        synchronized (engine) {
            if (toSave)
                sheet = engine.saveSheetAfterDynamicAnalysis(sender);
            else
                sheet = engine.returnSheetBackAfterDynamicAnalysis(); //todo: delete returnBackToNormal

            addSheetToResponse(sheet, response);
            response.setStatus(SC_OK);
        }
    }

    private void handleUpdateCell(Engine engine, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String sender = SessionUtils.getUsername(request);
        synchronized (engine) {
            // Read data from request to update the sheet
            try
            {
                engine.updateCellContents(request.getParameter("coordinate"), request.getParameter("newValue"), sender);
                response.setStatus(SC_OK);
                addSheetToResponse(engine.getSheet(), response);
            }
            catch (Exception e)
            {
                addErrorToResponse(SC_UNPROCESSABLE_CONTENT, e.getMessage(), response);
                //response.setStatus(SC_UNPROCESSABLE_CONTENT); //when maham fails
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
                addErrorToResponse(SC_BAD_REQUEST, e.getMessage(), response);
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
                addErrorToResponse(SC_BAD_REQUEST, e.getMessage(), response);
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
                addErrorToResponse(SC_BAD_REQUEST, e.getMessage(), response);
            }
            catch (RuntimeException e)
            {
                addErrorToResponse(SC_FORBIDDEN, e.getMessage(), response);
            }
        }
    }

    private void handleDeleteSheet(String fileName, HttpServletResponse response) throws IOException {
        synchronized (fileManager) {
            if (fileManager.removeFile(fileName))
            {
                response.setStatus(SC_OK);
                HomeServlet.increaseRequestNumber();
            }
            else
                response.setStatus(SC_NOT_FOUND);
        }
    }

    private void handleDeleteRange(Engine engine, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String rangeName = request.getParameter("rangeName");
        synchronized (engine) {
            try {
                engine.deleteRange(rangeName);
                response.setStatus(SC_OK);
                addSheetToResponse(engine.getSheet(), response);
            } catch (IllegalArgumentException e) {
                addErrorToResponse(SC_BAD_REQUEST, e.getMessage(), response);
            } catch (RuntimeException e) {
                addErrorToResponse(SC_INTERNAL_SERVER_ERROR, e.getMessage(), response);
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
        if (sheet != null)
            response.getWriter().write(GSON_INSTANCE.toJson(sheet));
    }

    private static void addErrorToResponse(int statusCode, String errorMessage, HttpServletResponse response) throws IOException {
        response.getWriter().write(errorMessage);
        response.setStatus(statusCode);
    }
}

