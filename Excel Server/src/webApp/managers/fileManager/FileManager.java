package webApp.managers.fileManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class FileManager
{
    private transient Map<String, InputStream> nameToFileContentMap = new HashMap<>();
    private List<String> fileNames = new LinkedList<>();

    public void addFile(String fileName, InputStream content)
    {
        if (isFileExists(fileName))
            throw new RuntimeException("File already exists");
        nameToFileContentMap.put(fileName, content);
        fileNames.add(fileName);
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

    public InputStream getFileContent(String fileName){
        if (!isFileExists(fileName))
            throw new RuntimeException("File does not exist");
        return nameToFileContentMap.get(fileName);
    }

    public List<String> getListOfFilesNames(){
        return fileNames;
    }
}
