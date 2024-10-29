package webApp.servlets;

import engine.api.Engine;
import engine.spreadsheet.api.ReadOnlySheet;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import webApp.managers.fileManager.FileManager;
import webApp.utils.ServletUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static utils.Constants.*;

//Beta Simp Cuck--> Daniel <--Cuck:
//DELETE sheets/deleteSheet
//PUT sheets/uploadSheet
//GET sheets/getSheet

//Chad Itamar:
//DELETE sheets
//PUT sheets
//GET sheets

@WebServlet(SHEETS_PATH + "/*")
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
            case VIEW_SHEET:
                handleGetSheet(engine, response);
                break;
            case VIEW_BY_VERSION:
                handleGetByVersion(engine, request, response);
                break;
            case VIEW_SORTED_SHEET:
                handleGetSorted(engine, request, response);
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
            case UPDATE_CELL:
                handleUpdateCell(engine, request, response);
                break;
            case ADD_RANGE:
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

        switch (requestData.action.toLowerCase()) {
            case DELETE_SHEET:
                handleDeleteSheet(requestData.fileName, response);
                break;
            case DELETE_RANGE:
                handleDeleteRange(requestData.engine, request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown action for DELETE: " + requestData.action);
        }
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

    // Handler methods

    private void handleGetSheet(Engine engine, HttpServletResponse response) throws IOException {
        synchronized (engine) {
            ReadOnlySheet sheet = engine.getSheet();
            //response.getWriter().write(GSON_INSTANCE.toJson(sheet));
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

        synchronized (engine) {
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

        RequestData(String fileName, String action, Engine engine) {
            this.fileName = fileName;
            this.action = action;
            this.engine = engine;
        }
    }

    private void addSheetToResponse(ReadOnlySheet sheet, HttpServletResponse response) throws IOException {
        response.getWriter().write(GSON_INSTANCE.toJson(sheet));
    }
}

