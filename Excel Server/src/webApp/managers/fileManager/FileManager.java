package webApp.managers.fileManager;

import engine.api.Engine;
import engine.imp.EngineImp;
import jakarta.xml.bind.JAXBException;
import utils.perms.Permission;
import engine.util.FileData;

import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class FileManager
{
    private Map<String, Engine> fileNameToEngineMap = new ConcurrentHashMap<>();
    //private List<String> fileNames = new ArrayList<>();
    private List<FileData> fileDataList = new ArrayList<>();

    public void addFile(String fileName, InputStream fileContent, String ownerName) throws JAXBException {
        if (isFileExists(fileName))
            throw new RuntimeException("File already exists");
        Engine newEngine = new EngineImp(fileName, ownerName, fileContent);
        fileNameToEngineMap.put(fileName, newEngine);
//        usernameToUserEnginesMap.computeIfAbsent(ownerName, k->new LinkedHashSet<>())
//                .add(newEngine);
        addNameSorted(fileName);
    }

    public boolean removeFile(String fileName)
    {
        ListIterator<FileData> iterator = fileDataList.listIterator();
        while (iterator.hasNext())
        {
            if (iterator.next().getFilename().equals(fileName))
            {
                iterator.remove();
                break;
            }
        }
        return fileNameToEngineMap.remove(fileName) != null;
    }

    public boolean isFileExists(String fileName)
    {
        return fileNameToEngineMap.containsKey(fileName);
    }

    public Engine getEngine(String fileName){
        if (!isFileExists(fileName))
            return null;
        return fileNameToEngineMap.get(fileName);
    }

    public List<FileData> getFileDataListWithPerms(String username){
        fileDataList.forEach(fileData ->
        {
            Engine engine = fileNameToEngineMap.get(fileData.getFilename());
            Permission userPermission = engine.getUserPermission(username);
            fileData.setUserPermission(userPermission);
        });

        return fileDataList;
    }

    private void addNameSorted(String fileName) {
        int index = binarySearchInsertionPoint(fileName);

        Engine engine = fileNameToEngineMap.get(fileName);
        int numOfCols = engine.getNumOfCols();
        int numOfRows = engine.getNumOfRows();
        String ownerName = engine.getOwnerName();

        fileDataList.add(index, new FileData(fileName, ownerName, numOfCols, numOfRows));
    }

    private int binarySearchInsertionPoint(String fileName) {
        int low = 0;
        int high = fileDataList.size() - 1;

        while (low <= high)
        {
            int mid = low + (high - low) / 2;
            int comparison = fileDataList.get(mid).compareTo(fileName);

            if (comparison < 0)
                low = mid + 1;
            else
                high = mid - 1;
        }

        return low; // Correct insertion point
    }
}
