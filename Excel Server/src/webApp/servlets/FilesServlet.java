package webApp.servlets;

import com.google.gson.Gson;
import engine.api.Engine;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import jakarta.xml.bind.JAXBException;
import utils.perms.Permission;
import utils.perms.PermissionRequest;
import webApp.managers.fileManager.FileManager;
import webApp.utils.ServletUtils;
import webApp.utils.SessionUtils;

import java.io.IOException;
import java.io.InputStream;

import static utils.Constants.FILES_PATH;
import static utils.Constants.SC_UNPROCESSABLE_CONTENT;

@WebServlet(FILES_PATH)
@MultipartConfig
public class FilesServlet extends HttpServlet
{
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        Part filePart = request.getPart("file");
        FileManager fileManager = ServletUtils.getFileManager(getServletContext());

        if (filePart != null)
        {
            // Get the file name and ensure it's XML
            String fileName = filePart.getSubmittedFileName();
            String contentType = filePart.getContentType();

            if ("application/xml".equals(contentType))
            {
                // Save the file's content in the file manager.
                InputStream inputStream = filePart.getInputStream();
                try
                {
                    fileManager.addFile(fileNameWithoutXML(fileName), inputStream, SessionUtils.getUsername(request));
                    HomeServlet.increaseRequestNumber();
                    response.setStatus(HttpServletResponse.SC_OK);
                }
                catch (JAXBException e) //When given file is not a sheet file
                {
                    response.setStatus(SC_UNPROCESSABLE_CONTENT);
                    response.getWriter().write("UNPROCESSABLE File.");
                }
                catch (RuntimeException e) //When file already exist (probably)
                {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write(e.getMessage());
                    System.out.println(e.getMessage());
                }
                catch (Exception e) //Else...
                {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.getWriter().write(e.getMessage());
                    System.out.println(e.getMessage());
                }

            }
            else
            {
                response.setStatus(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
                response.getWriter().write("Only XML files are allowed.");
            }
        }
        else
        {
            // Respond with error if no file part is found
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("No file provided.");
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        String fileName = request.getParameter("fileName");
        String requestSender = SessionUtils.getUsername(request);
        Permission requestedPermission = getPermissionFromRequest(request, response);

        if (requestedPermission != null)
        {
            ServletUtils.getRequestManager(getServletContext()).addRequest(requestSender, requestedPermission, fileName);
            HomeServlet.increaseRequestNumber();
        }
    }

    private static Permission getPermissionFromRequest(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try
        {
            return Permission.valueOf(request.getParameter("permission"));
        }
        catch (IllegalArgumentException e)
        {
            addErrorToResponse(HttpServletResponse.SC_BAD_REQUEST, "Invalid permission was received", response);
            return null;
        }
    }

    private String fileNameWithoutXML(String fileName){
        return fileName.substring(0, fileName.length() - 4);
    }
    private static void addErrorToResponse(int statusCode, String errorMessage, HttpServletResponse response) throws IOException {
        response.getWriter().write(errorMessage);
        response.setStatus(statusCode);
    }
}
