package webApp.managers.fileManager;

import engine.api.Engine;
import engine.imp.EngineImp;
import jakarta.xml.bind.JAXBException;
import utils.perms.Permission;
import utils.perms.PermissionRequest;

import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class FileManager
{
    private Map<String, Engine> fileNameToEngineMap = new ConcurrentHashMap<>();
    private List<String> fileNames = new ArrayList<>();
    //private Map<String, Set<Engine>> usernameToUserEnginesMap = new ConcurrentHashMap<>();

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
        fileNames.remove(fileName);
        Engine engineToRemove = fileNameToEngineMap.remove(fileName);
//        if (engineToRemove != null)
//        {
//            String engineOwner = engineToRemove.getOwnerName();
//            usernameToUserEnginesMap.get(engineOwner).remove(engineToRemove);
//        }

        return engineToRemove != null;
    }

    public boolean isFileExists(String fileName)
    {
        return fileNameToEngineMap.containsKey(fileName);
    }

    public Engine getEngine(String fileName){
        if (!isFileExists(fileName))
            throw new RuntimeException("File does not exist");
        return fileNameToEngineMap.get(fileName);
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
