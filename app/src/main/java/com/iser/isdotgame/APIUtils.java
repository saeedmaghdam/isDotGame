package com.iser.isdotgame;

public class APIUtils {
    private APIUtils(){}

    public static final String API_URL = Helper.getUploadUrl();
    public static FileService getFileService(){
        return RetrofitClient.getClient(API_URL).create(FileService.class);
    }
}
