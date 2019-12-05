package com.iser.isdotgame;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface FileService {
    @Multipart
    @POST("UploadSingleFile")
    Call<FileInfo> upload(@Part MultipartBody.Part file);
}
