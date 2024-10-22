package webApp.managers.fileManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class FileManager
{
    private transient Map<String, InputStream> nameToFileContentMap = new HashMap<>();
    private List<String> fileNames = new ArrayList<>();

    public void addFile(String fileName, InputStream content)
    {
        if (isFileExists(fileName))
            throw new RuntimeException("File already exists");
        nameToFileContentMap.put(fileName, content);
        addNameSorted(fileName);
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

    private void addNameSorted(String fileName) {
        int index = binarySearchInsertionPoint(fileName);
        fileNames.add(index, fileName);
    }

    private int binarySearchInsertionPoint(String fileName) {
        int low = 0;
        int high = fileNames.size() - 1;

        while (low <= high)
        {
            int mid = low + (high - low) / 2;
            int comparison = fileNames.get(mid).compareTo(fileName);

            if (comparison < 0)
                low = mid + 1;
            else
                high = mid - 1;
        }

        return low; // Correct insertion point
    }
}
