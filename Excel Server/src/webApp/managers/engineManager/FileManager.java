package webApp.managers.engineManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class FileManager
{
    private transient Map<String, String> nameToFileContentMap = new HashMap<>();
    private List<String> fileNames = new LinkedList<>();

    private void addFile(String fileName, String content)
    {
        if (isFileExists(fileName))
            throw new RuntimeException("Sheet already exists");
        nameToFileContentMap.put(fileName, content);
        fileNames.add(fileName);
    }

    public void addFile(String fileName, InputStream inputStream) throws IOException {
        addFile(fileName, convertStreamToString(inputStream));
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

    public boolean removeFile(String fileName)
    {
        fileNames.remove(fileName);
        return nameToFileContentMap.remove(fileName) != null;
    }

    public boolean isFileExists(String fileName)
    {
        return nameToFileContentMap.containsKey(fileName);
    }

    public String getFileContent(String fileName){
        if (!isFileExists(fileName))
            throw new RuntimeException("File does not exist");
        return nameToFileContentMap.get(fileName);
    }

    public List<String> getListOfFilesNames(){
        return fileNames;
    }
}
