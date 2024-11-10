package utils;

import com.google.gson.Gson;

public class Constants {
    public static final String MAIN_TITLE = "Exel";

    public static final String USERNAME = "username";
    public static final String USER_NAME_ERROR = "username_error";

    public static final String CHAT_PARAMETER = "userstring";
    public static final String CHAT_VERSION_PARAMETER = "chatversion";

    public static final String HOME_PATH = "/home";
    public static final String LOGIN_PATH = "/login";
    public static final String FILES_PATH = "/files";
    public static final String SHEETS_PATH = "/sheets";
    public static final String CHAT_PATH = "/chat";

    public static final String VIEW_SHEET = "viewsheet";
    public static final String DELETE_SHEET = "deletesheet";
    public static final String UPDATE_CELL = "updatecell";
    public static final String ADD_RANGE = "addrange";
    public static final String DELETE_RANGE = "deleterange";
    public static final String VIEW_BY_VERSION = "getbyversion";
    public static final String VIEW_SORTED_SHEET = "viewsorted";
    public static final String VIEW_FILTERED_SHEET = "viewfiltered";
    public static final String SET_CELL_WIDTH = "setcellwidth";
    public static final String SET_CELL_HEIGHT = "setcellheight";
    public static final String PUT_CELL_FOR_ANALYSIS = "putcellanalysis";
    public static final String UPDATE_CELL_ANALYSIS = "updatecellanalysis";
    public static final String STOP_CELL_ANALYSIS = "stopcellanalysis";

    public static final int INT_PARAMETER_ERROR = Integer.MIN_VALUE;

    // global constants
    public static final String DATA_UPDATE_HEADER = "X-Data-Update-Available";

    // Server resources locations
    public final static String BASE_DOMAIN = "localhost";
    private final static String BASE_URL = "http://" + BASE_DOMAIN + ":8080";
    private final static String CONTEXT_PATH = "/Excel-Server";
    public final static String FULL_SERVER_PATH = BASE_URL + CONTEXT_PATH;

    public static String VIEW_SHEET_PAGE(String fileName){
        return getSheetsPathWithName(fileName) + VIEW_SHEET;
    }

    public static String VIEW_SHEET_BY_VERSION_REQUEST_PATH(String fileName){
        return getSheetsPathWithName(fileName) + VIEW_BY_VERSION;
    }

    public static String DELETE_SHEET_PAGE(String fileName){
        return getSheetsPathWithName(fileName) + DELETE_SHEET;
    }

    public static String UPDATE_CELL_REQUEST_PATH(String fileName){
        return getSheetsPathWithName(fileName) + UPDATE_CELL;
    }

    public static String SET_CELL_WIDTH_REQUEST_PATH(String fileName){
        return getSheetsPathWithName(fileName) + SET_CELL_WIDTH;
    }

    public static String SET_CELL_HEIGHT_REQUEST_PATH(String fileName){
        return getSheetsPathWithName(fileName) + SET_CELL_HEIGHT;
    }

    public static String PUT_CELL_FOR_ANALYSIS_REQUEST_PATH(String fileName){
        return getSheetsPathWithName(fileName) + PUT_CELL_FOR_ANALYSIS;
    }

    public static String UPDATE_CELL_ANALYSIS_REQUEST_PATH(String fileName){
        return getSheetsPathWithName(fileName) + UPDATE_CELL_ANALYSIS;
    }

    public static String STOP_CELL_ANALYSIS_REQUEST_PATH(String fileName){
        return getSheetsPathWithName(fileName) + STOP_CELL_ANALYSIS;
    }

    public static String ADD_RANGE_REQUEST_PATH(String fileName){
        return getSheetsPathWithName(fileName) + ADD_RANGE;
    }

    public static String DELETE_RANGE_REQUEST_PATH(String fileName){
        return getSheetsPathWithName(fileName) + DELETE_RANGE;
    }

    public static String VIEW_SORTED_SHEET_REQUEST_PATH(String fileName){
        return getSheetsPathWithName(fileName) + VIEW_SORTED_SHEET;
    }

    public static String VIEW_FILTERED_SHEET_REQUEST_PATH(String fileName){
        return getSheetsPathWithName(fileName) + VIEW_FILTERED_SHEET;
    }



    private static String getSheetsPathWithName(String fileName){
        return FULL_SERVER_PATH + SHEETS_PATH + '/' + fileName + '/';
    }


    public final static String LOGIN_PAGE = FULL_SERVER_PATH + LOGIN_PATH;
    public final static String HOME_PAGE = FULL_SERVER_PATH + HOME_PATH;
    public final static String FILES_PAGE = FULL_SERVER_PATH + FILES_PATH;
    public final static String CHAT_PAGE = FULL_SERVER_PATH + CHAT_PATH;

    // GSON instance
    public final static Gson GSON_INSTANCE = new Gson();

    // Http Status Code
    public final static int SC_UNPROCESSABLE_CONTENT = 422;
}