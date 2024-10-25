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
import webApp.managers.fileManager.FileManager;
import webApp.utils.ServletUtils;

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
                    fileManager.addFile(fileName, inputStream);
                    HomeServlet.increaseRequestNumber();
                    response.setStatus(HttpServletResponse.SC_OK);
                }
                catch (JAXBException e)
                {
                    response.setStatus(SC_UNPROCESSABLE_CONTENT);
                    response.getWriter().write("File is Broken."); //todo: write better message
                }
                catch (RuntimeException e)
                {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write(e.getMessage());
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

    //Will move to sheet servlet later
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        response.setContentType("text/html;charset=UTF-8");
        FileManager fileManager = ServletUtils.getFileManager(getServletContext());
        String fileName = request.getParameter("fileName");

        if (fileName != null && !fileName.isEmpty())
        {
            if (fileManager.isFileExists(fileName))
            {
                Engine fileContent = fileManager.getEngine(fileName);
                response.getWriter().write((new Gson()).toJson(fileContent.getReadOnlySheet()));
                response.setStatus(HttpServletResponse.SC_OK);
            }
            else
            {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("File not found.");
            }
        }
        else
        {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("No file provided.");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException{

    }
}
