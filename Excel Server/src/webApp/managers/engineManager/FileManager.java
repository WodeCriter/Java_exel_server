package webApp.managers.engineManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class FileManager
{
    private Map<String, String> nameToFileContentMap = new HashMap<>();

    private void addFile(String sheetName, String content)
    {
        if (isFileExists(sheetName))
            throw new RuntimeException("Sheet already exists");
        nameToFileContentMap.put(sheetName, content);
    }

    public void addFile(String sheetName, InputStream inputStream) throws IOException {
        addFile(sheetName, convertStreamToString(inputStream));
    }

    private String convertStreamToString(InputStream inputStream) throws IOException
    {
        StringBuilder fileContentAsString = new StringBuilder();

        int ch;
        while ((ch = inputStream.read()) != -1)
        {
            fileContentAsString.append((char) ch);
        }

        return fileContentAsString.toString();
    }

    public boolean removeFile(String sheetName)
    {
        return nameToFileContentMap.remove(sheetName) != null;
    }

    public boolean isFileExists(String sheetName)
    {
        return nameToFileContentMap.containsKey(sheetName);
    }

    public String getFileContent(String fileName){
        if (!isFileExists(fileName))
            throw new RuntimeException("File does not exist");
        return nameToFileContentMap.get(fileName);
    }
}
