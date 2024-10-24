package webApp.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import webApp.managers.fileManager.FileManager;
import webApp.utils.ServletUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static utils.Constants.FILES_PATH;

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
                fileManager.addFile(fileName, inputStream);
                HomeServlet.increaseRequestNumber();

                // Respond with success message
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("File uploaded successfully.");
            }
            else
            {
                // If the file is not XML, return a 415 Unsupported Media Type
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
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        response.setContentType("text/html;charset=UTF-8");
        FileManager fileManager = ServletUtils.getFileManager(getServletContext());
        String fileName = request.getParameter("fileName");

        if (fileName != null && !fileName.isEmpty())
        {
            if (fileManager.isFileExists(fileName))
            {
                InputStream fileContent = fileManager.getFileContent(fileName);
                response.setStatus(HttpServletResponse.SC_OK);
                writeInputStreamToResponse(response, fileContent);
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

    private void writeInputStreamToResponse(HttpServletResponse response, InputStream inputStream) throws IOException{
        OutputStream out = response.getOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;

        // Copy the contents of the InputStream to the OutputStream
        while ((bytesRead = inputStream.read(buffer)) != -1)
            out.write(buffer, 0, bytesRead);

        // Ensure all data is written out
        out.flush();
    }

}
