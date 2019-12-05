package com.iser.isdotgame;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.loader.content.CursorLoader;

import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionState;
//import com.squareup.okhttp.MediaType;
//import com.squareup.okhttp.OkHttpClient;
//import com.squareup.okhttp.Request;
//import com.squareup.okhttp.RequestBody;
//import com.squareup.picasso.Picasso;
import com.zarinpal.ewallets.purchase.OnCallbackRequestPaymentListener;
import com.zarinpal.ewallets.purchase.OnCallbackVerificationPaymentListener;
import com.zarinpal.ewallets.purchase.PaymentRequest;
import com.zarinpal.ewallets.purchase.ZarinPal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Callable;

//import retrofit.RetrofitError;
//import retrofit.client.Response;
//import retrofit.mime.TypedFile;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.view.View.GONE;

public class ChargeActivity extends BaseActivity {
    Helper helper;
    private static int coins;
    private static long amount;
    private HubConnection hubConnection;
    SignalRService service;
    protected Handler handler;

    private String name;
    private String phone;
    private String avatar;

    private String imagePath="";
    private File savedFileDestination;
    private static final int PICK_CAMERA_IMAGE = 1;

    private Button btnPhoto, btnUpload, btnUploadZip;
    ImageView imgPhoto;
//    private RestClient restClient;
//    ProgressDialog mProgress;
    FileService fileService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charge);

        helper = new Helper(this);
        fileService = APIUtils.getFileService();
        super.setUpdateMethod(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                CheckFields();
                return null;
            }
        });
        service = new SignalRService(this, this.handler);

//        hubConnection = HubConnectionBuilder.create(this.helper.getHubUrl()).build();
        hubConnection = service.getHubConnection();

        final AlertDialog.Builder alert = new AlertDialog.Builder(ChargeActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.custom_dialog,null);
        final EditText txt_inputText = (EditText)mView.findViewById(R.id.txt_input);
        Button btn_cancel = (Button)mView.findViewById(R.id.btn_cancel);
        Button btn_okay = (Button)mView.findViewById(R.id.btn_okay);
        alert.setView(mView);
        final AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(false);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        btn_okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hubConnection.getConnectionState() == HubConnectionState.DISCONNECTED) {
                    try {
                        hubConnection.start().blockingAwait();
                    } catch (Exception ex) {
                        helper.showMessage("در حال حاضر ارتباط با سرور قطع می باشد!");
                    }
                }
                if (hubConnection.getConnectionState() == HubConnectionState.CONNECTED) {
                    try {
                        hubConnection.send("VerifyCode", helper.getUserUniqueId(), helper.getUsername(), txt_inputText.getText().toString());
                    } catch (Exception ex) {
                        helper.showMessage("در حال حاضر ارتباط با سرور قطع می باشد!");
                    }
                }

                alertDialog.dismiss();
            }
        });

//        alertDialog.show();

        hubConnection.on("TextMessageSent", () -> {

        });

        hubConnection.on("Test", () -> {
//            Message message = new Message();
//            message.what = 1;
//            message.arg1 = 999;
//            mHandlerThread.sendMessage(message);
//            Toast.makeText(this, "Hello Dear World!", Toast.LENGTH_LONG).show();
        });

        hubConnection.on("PhoneVerified", () -> {
////            EditText et = ChargeActivity.this.findViewById(R.id.txtMobile);
////            et.setText("Saeed");
//            LinearLayout ll = findViewById(R.id.linearLayout2);
//
//            EditText et = findViewById(R.id.txtMobile);
//            et.setVisibility(View.GONE);
//
//            ll.invalidate();
            EditText et = ChargeActivity.this.findViewById(R.id.txtMobile);
            this.phone = et.getText().toString();
            this.setPhone(this.phone);

            Message message = new Message();
            message.what = 2;
            this.handler.sendMessage(message);
        });

        hubConnection.on("NameUpdated", () -> {
            EditText et = ChargeActivity.this.findViewById(R.id.txtName);
            this.name = et.getText().toString();
            this.setName(this.name);

            Message message = new Message();
            message.what = 3;
            this.handler.sendMessage(message);
        });

        hubConnection.on("ErrorOccured", () -> {
            Message message = new Message();
            message.what = 1;
            this.handler.sendMessage(message);
        });

        if (hubConnection.getConnectionState() == HubConnectionState.DISCONNECTED) {
            try {
                hubConnection.start().blockingAwait();
            } catch (Exception ex) {
                helper.showMessage("در حال حاضر ارتباط با سرور قطع می باشد!");
            }
        }

        paymentResult();

        Button ol2000 = findViewById(R.id.ol2000);
        ol2000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pay(100);
            }
        });

        Button ol3000 = findViewById(R.id.ol3000);
        ol3000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pay(210);
            }
        });

        Button ol4000 = findViewById(R.id.ol4000);
        ol4000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pay(320);
            }
        });

        Button ol5000 = findViewById(R.id.ol5000);
        ol5000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pay(430);
            }
        });

        Button btnSetName = findViewById(R.id.btnSetName);
        btnSetName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText txtName = findViewById(R.id.txtName);

                String name = txtName.getText().toString();
                if (!TextUtils.isEmpty(name)){
                    if (hubConnection.getConnectionState() == HubConnectionState.DISCONNECTED) {
                        try {
                            hubConnection.start().blockingAwait();
                        } catch (Exception ex) {
                            helper.showMessage("در حال حاضر ارتباط با سرور قطع می باشد!");
                        }
                    }
                    if (hubConnection.getConnectionState() == HubConnectionState.CONNECTED) {
                        try {
                            hubConnection.send("UpdateName", helper.getUserUniqueId(), helper.getUsername(), name);
                        } catch (Exception ex) {
                            helper.showMessage("در حال حاضر ارتباط با سرور قطع می باشد!");
                        }
                    }
                }
            }
        });

        Button btnSetMobile = findViewById(R.id.btnSetMobile);
        btnSetMobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hubConnection.getConnectionState() == HubConnectionState.DISCONNECTED) {
                    try {
                        hubConnection.start().blockingAwait();
                    } catch (Exception ex) {
                        helper.showMessage("در حال حاضر ارتباط با سرور قطع می باشد!");
                    }
                }
                if (hubConnection.getConnectionState() == HubConnectionState.CONNECTED) {
                    try {
                        hubConnection.send("Test");
                    } catch (Exception ex) {
                        helper.showMessage("در حال حاضر ارتباط با سرور قطع می باشد!");
                    }
                }

                EditText txtMobile = findViewById(R.id.txtMobile);

                String phone = txtMobile.getText().toString();
                if (!TextUtils.isEmpty(phone)){
                    if (hubConnection.getConnectionState() == HubConnectionState.DISCONNECTED) {
                        try {
                            hubConnection.start().blockingAwait();
                        } catch (Exception ex) {
                            helper.showMessage("در حال حاضر ارتباط با سرور قطع می باشد!");
                        }
                    }
                    if (hubConnection.getConnectionState() == HubConnectionState.CONNECTED) {
                        try {
                            hubConnection.send("UpdatePhone", helper.getUserUniqueId(), helper.getUsername(), phone);
                            alertDialog.show();
                        } catch (Exception ex) {
                            helper.showMessage("در حال حاضر ارتباط با سرور قطع می باشد!");
                        }
                    }
                }
            }
        });

        Button btnSetImage = findViewById(R.id.btnSetImage);
        btnSetImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(Intent.ACTION_PICK);
//                intent.setType("image/jpeg");
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(Intent.createChooser(intent, "انتخاب عکس"), 0);
////                startActivityForResult(intent, 0);
                if (ActivityCompat.checkSelfPermission(ChargeActivity.this, READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK
//                            MediaStore.Images.Media.INTERNAL_CONTENT_URI
                    );
//                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
//                    intent.putExtra("crop", "true");
////                    intent.putExtra("scale", true);
//                    intent.putExtra("aspectX", 1);
//                    intent.putExtra("aspectY", 1);
//                    intent.putExtra("return-data", true);
                    startActivityForResult(intent, 0);
                }
            }
        });

//        restClient = new RestClient();
        imgPhoto = (ImageView) findViewById(R.id.imgPhoto);

        btnPhoto = (Button) findViewById(R.id.btnPhoto);
        btnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
                String name = dateFormat.format(new Date());
                savedFileDestination = new File(imagePath, name + ".jpg");

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(savedFileDestination));
                startActivityForResult(intent, PICK_CAMERA_IMAGE);
                //FileHelper.zip(imagePath, zipPath, "a.zip");

            }
        });
        //Remove this section if you don't want camera code (End)

        btnUploadZip = (Button) findViewById(R.id.btnUploadZip);
        btnUploadZip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                new UploadSync(ChargeActivity.this).execute(imagePath);
            }
        });

        //location where photo saved
        imagePath =  Environment.getExternalStorageDirectory().toString() + "/instinctcoder/uploadFiles/";
        new File(imagePath).mkdir();


        btnUpload= (Button) findViewById(R.id.btnUpload);
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        Button btnChoosePhoto = findViewById(R.id.btnChoosePhoto);
        btnChoosePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 0);
            }
        });

        Button btnUploadChosenPhoto = findViewById(R.id.btnUploadChosenPhoto);
        btnUploadChosenPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(imagePath);
                RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestBody);

                Call<com.iser.isdotgame.FileInfo> call = fileService.upload(body);
                call.enqueue(new Callback<com.iser.isdotgame.FileInfo>() {
                    @Override
                    public void onResponse(Call<com.iser.isdotgame.FileInfo> call, Response<com.iser.isdotgame.FileInfo> response) {
                        if (response.isSuccessful()){
                            Toast.makeText(ChargeActivity.this, "Image Uploaded Successfully!", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<com.iser.isdotgame.FileInfo> call, Throwable t) {
                        Toast.makeText(ChargeActivity.this, "ERROR: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    public String compressImage(String imageUri) {

        String filePath = getRealPathFromURI(imageUri);
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612

        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        String filename = getFilename();
        try {
            out = new FileOutputStream(filename);

//          write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return filename;

    }

    public String getFilename() {
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "MyFolder/Images");
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
        return uriSting;

    }

    private String getRealPathFromURI(String contentURI) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    private void doCrop(String picPath) {
        try {


            Intent cropIntent = new Intent("com.android.camera.action.CROP");

            File f = new File(picPath);
            Uri contentUri = Uri.fromFile(f);

            cropIntent.setDataAndType(contentUri, "image/*");

            cropIntent.putExtra("crop", "true");
            // indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            // indicate output X and Y
            cropIntent.putExtra("outputX", 280);
            cropIntent.putExtra("outputY", 280);

            // retrieve data on return
            cropIntent.putExtra("return-data", true);
            // start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, 0);
        }
        catch (ActivityNotFoundException anfe) {
            String errorMessage = "your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            if (data == null){
                Toast.makeText(ChargeActivity.this, "Unable to choose photo!", Toast.LENGTH_LONG).show();
                return;
            }

            Uri imageUri = data.getData();
            if (imageUri!=null) {
                Bitmap myBitmap = null;
                try {
//                    Bitmap largeImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
//                    int h = 256; // height in pixels
//                    int w = 256; // width in pixels
//                    myBitmap = Bitmap.createScaledBitmap(largeImage, h, w, true);
//                    Bundle extras = data.getExtras();
//                    Bitmap bitmap = extras.getParcelable("data");
//                    Bundle extras = data.getExtras();
//                    if (extras != null) {
//                        myBitmap = extras.getParcelable("data");
//
//                        //imgView.setImageBitmap(selectedBitmap);
//                    } else {
//                        myBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
//                    }

                    myBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                    int width = myBitmap.getWidth();
                    int height = myBitmap.getHeight();
                    int newWidth = 0;
                    int newHeight = 0;
                    if (width < height) {
                        newWidth = 256;
                        newHeight = (newWidth * height) / width;
                    }else {
                        newHeight = 256;
                        newWidth = (width * newHeight) / height;
                    }
                    Bitmap resized = ThumbnailUtils.extractThumbnail(myBitmap, newWidth, newHeight);
                    myBitmap = resized;
                }
                catch (Exception ex){

                }

//                try{
//                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
//                    Cursor cursor = this.getContentResolver().query(imageUri, filePathColumn, null, null, null);
//                    if (cursor != null){
//                        if (cursor.moveToFirst()){
//                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//                            String filePath = cursor.getString(columnIndex);
//                            cursor.close();
////                            File file = new File(filePath);
//
//                            doCrop(filePath);
//                        }
//                    }
//                }
//                catch (Exception ex){
//
//                }

                imagePath = getRealPathFromUri(imageUri);
                String p1 = compressImage(imagePath);
                File file = new File(imagePath);
                File destFile = null;

                if (file.exists()) {
                    deleteTempFolder("avatar");
//                BitmapFactory.Options options = new BitmapFactory.Options();
//
////      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
////      you try the use the bitmap here, you will get null.
//                options.inJustDecodeBounds = false;
//                myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
//                    try {
//                        myBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
//                    } catch (Exception e) {
//
//                    }
                    ImageView myImage = (ImageView) findViewById(R.id.imgPhoto);
                    myImage.setImageBitmap(myBitmap);


                    // Copy image    to internal storage
                    ContextWrapper cw = new ContextWrapper(getApplicationContext());
                    File destDirectory = cw.getDir("avatar", Context.MODE_PRIVATE);
                    String destFileName = getUserViewId() + ".jpg";
                    destFile = new File(destDirectory, destFileName);
//
                    FileOutputStream fos = null;
                    try {
                        fos = new FileOutputStream(destFile);
                        myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                        fos.flush();
                        fos.close();

                        //saveToInternalStorage(myBitmap, "avatar", destFileName);

                        if (hubConnection.getConnectionState() == HubConnectionState.DISCONNECTED) {
                            try {
                                hubConnection.start().blockingAwait();
                            } catch (Exception ex) {
                                helper.showMessage("در حال حاضر ارتباط با سرور قطع می باشد!");
                            }
                        }
                        if (hubConnection.getConnectionState() == HubConnectionState.CONNECTED) {
                            try {
                                hubConnection.send("UpdateAvatarFileName", helper.getUserUniqueId(), helper.getUsername(), destFileName);
                                setAvatar(destFileName);
                            } catch (Exception ex) {
                                helper.showMessage("در حال حاضر ارتباط با سرور قطع می باشد!");
                            }
                        }
//                } catch (java.io.IOException e) {
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), destFile);
                MultipartBody.Part body = MultipartBody.Part.createFormData("file", destFile.getName(), requestBody);

                Call<com.iser.isdotgame.FileInfo> call = fileService.upload(body);
                call.enqueue(new Callback<com.iser.isdotgame.FileInfo>() {
                    @Override
                    public void onResponse(Call<com.iser.isdotgame.FileInfo> call, Response<com.iser.isdotgame.FileInfo> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(ChargeActivity.this, "Image Uploaded Successfully!", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<com.iser.isdotgame.FileInfo> call, Throwable t) {
                        Toast.makeText(ChargeActivity.this, "ERROR: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
                }
            }
        }
    }

    private void deleteTempFolder(String dir) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir(dir, Context.MODE_PRIVATE);
        if (directory.isDirectory()) {
            String[] children = directory.list();
            for (int i = 0; i < children.length; i++) {
                new File(directory, children[i]).delete();
            }
        }
    }

    private String saveToInternalStorage(Bitmap bitmapImage, String dir, String fileName){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir(dir, Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,fileName);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

    private void loadImageFromStorage(String fileName)
    {
        try {
//            ContextWrapper cw = new ContextWrapper(getApplicationContext());
//            // path to /data/data/yourapp/app_data/imageDir
//            File directory = cw.getDir("avatar", Context.MODE_PRIVATE);
//
//            File f=new File(directory.toString(), fileName);
//            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
//            ImageView img=(ImageView)findViewById(R.id.imgPhoto);
//            img.setImageBitmap(b);

            ImageView imageView=(ImageView)findViewById(R.id.imgPhoto);
            ContextWrapper cw = new ContextWrapper(getApplicationContext());
            File directory = cw.getDir("avatar", Context.MODE_PRIVATE);
            File file = new File(directory.toString(), fileName);
            imageView.setImageDrawable(Drawable.createFromPath(file.toString()));
        }
//        catch (FileNotFoundException e)
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    private String getRealPathFromUri(Uri uri){
        String[] projection = { MediaStore.Images.Media.DATA };
        CursorLoader loader = new CursorLoader(getApplicationContext(), uri, projection, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_idx = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_idx);
        cursor.close();
        return result;
    }

    private void initiateProgressDialog(){
//        mProgress = new ProgressDialog(this);
//        mProgress.setMessage("Uploading files...");
//        mProgress.setCancelable(true);

        //setButton is depreciated, it's tell us is not good to cancel when something is running at the back. :). Think about it.
//When cancel button clicked, it will ignore the response from server
        //You could see this from video
//        mProgress.setButton("Cancel", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//                CancelableCallback.cancelAll();
//                return;
//            }
//        });
//        mProgress.show();
    }


    private void uploadImage(){
        if (savedFileDestination==null) {
            Toast.makeText(this,"Please take photo first", Toast.LENGTH_LONG).show();
            return;
        }

//        OkHttpClient client = new OkHttpClient();
//
//        MediaType mediaType = MediaType.parse("multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW");
//        RequestBody body = RequestBody.create(mediaType, "------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; name=\"file\"; filename=\"محمدزاده.rar\"\r\nContent-Type: application/x-rar-compressed\r\n\r\n\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW--");
//        Request request = new Request.Builder()
//                .url("http://localhost:54343/FileUpload/UploadSingleFile")
//                .post(body)
//                .addHeader("content-type", "multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW")
//                .addHeader("cache-control", "no-cache")
//                .addHeader("postman-token", "5ff7bae6-da87-3caf-b988-74e3d666cae5")
//                .build();
//
//        Response response = client.newCall(request).execute();

//        HttpResponse<String> response = Unirest.post("http://localhost:54343/FileUpload/UploadSingleFile")
//                .header("content-type", "multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW")
//                .header("cache-control", "no-cache")
//                .header("postman-token", "309259cb-2d68-d24c-8f65-182612889403")
//                .body("------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; name=\"file\"; filename=\"محمدزاده.rar\"\r\nContent-Type: application/x-rar-compressed\r\n\r\n\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW--")
//                .asString();


//        TypedFile typedFile = new TypedFile("multipart/form-data", savedFileDestination);
//        initiateProgressDialog();
//
//        restClient.getService().upload(typedFile, new CancelableCallback<Response>() {
//            @Override
//            public void onSuccess(Response response, Response response2) {
//                mProgress.dismiss();
//                Picasso.with(ChargeActivity.this)
//                        .load(savedFileDestination)
//                        .into(imgPhoto);
//
//
//                Toast.makeText(ChargeActivity.this,"Upload successfully",Toast.LENGTH_LONG).show();
//                Log.e("Upload", "success");
//            }
//
//            @Override
//            public void onFailure(RetrofitError error) {
//                mProgress.dismiss();
//                Toast.makeText(ChargeActivity.this,"Upload failed",Toast.LENGTH_LONG).show();
//                Log.e("Upload", error.getMessage().toString());
//            }
//        });


    }

//    @Override
//    public void onBackPressed(){
////        if (CancelableCallback.runningProcess()==false) finish();
//    }

    //Remove this section if you don't want camera code (Start)
    //Got new image taken from camera intent
//    @Override
//    protected void onActivityResult( int requestCode, int resultCode, Intent data)
//    {
//        super.onActivityResult(requestCode, resultCode, data);
//    }

//    @Override
//    public void onSaveInstanceState(Bundle savedInstanceState) {
//        //For the purpose of when the device orientation change
//        savedInstanceState.putString("destination", savedFileDestination.getName());
//        super.onSaveInstanceState(savedInstanceState);
//    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //For the purpose of when the device orientation change

        String sPath = savedInstanceState.getString("destination");
        savedFileDestination = new File(sPath);
    }
    //Remove this section if you don't want camera code (End)

    private void CheckFields(){
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                EditText txtName = findViewById(R.id.txtName);
                EditText txtPhone = findViewById(R.id.txtMobile);
                Button btnSetName = findViewById(R.id.btnSetName);
                Button btnSetPhone = findViewById(R.id.btnSetMobile);
                Button btnSetImage = findViewById(R.id.btnSetImage);

                if (!TextUtils.isEmpty(getName())) {
//            btnSetName.setVisibility(GONE);
//            txtName.setEnabled(false);
                    btnSetName.setText("ذخیره");
                    txtName.setText(getName());
                }
                if (!TextUtils.isEmpty(getPhone())) {
                    btnSetPhone.setVisibility(GONE);
                    txtPhone.setEnabled(false);
                    txtPhone.setText(getPhone());
                }
                if (!TextUtils.isEmpty(getAvatar())){
                    btnSetImage.setText("آپلود عکس جدید");
                    loadImageFromStorage(getAvatar());
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 0){

                } else if (msg.what == 1)
                    helper.showMessage("خطا در عملیات بروزرسانی");
                else if (msg.what == 2)
                    helper.showMessage("شماره موبایل شما با موفقیت ثبت و ذخیره شد");
                else if (msg.what == 3)
                    helper.showMessage("نام شما با موفقیت ثبت و ذخیره شد");

                CheckFields();
            }
        };
    }

    private void paymentResult(){
        Uri data = getIntent().getData();
        ZarinPal.getPurchase(this).verificationPayment(data, new OnCallbackVerificationPaymentListener() {
            @Override
            public void onCallbackResultVerificationPayment(boolean isPaymentSuccess, String refID, PaymentRequest paymentRequest) {
                if (hubConnection.getConnectionState() == HubConnectionState.DISCONNECTED) {
                    try {
                        hubConnection.start().blockingAwait();
                    } catch (Exception ex) {
                        helper.showMessage("در حال حاضر ارتباط با سرور قطع می باشد!");
                    }
                }
                if (hubConnection.getConnectionState() == HubConnectionState.CONNECTED) {
                    try {
                        if (isPaymentSuccess) {
                            hubConnection.send("Charge", helper.getUserUniqueId(), helper.getUsername(), refID, coins, amount);
                        }
                        else {
                            hubConnection.send("Charge", helper.getUserUniqueId(), helper.getUsername(), refID, "error", amount);
                        }
                    } catch (Exception ex) {
                        helper.showMessage("در حال حاضر ارتباط با سرور قطع می باشد!");
                    }
                }

                if (isPaymentSuccess) {
                    helper.showMessage("پرداخت با موفقیت انجام شد، " + coins + " سکه به موجودی شما اضافه شد.");
                }
                else{
                    helper.showMessage("پرداخت موفقیت آمیز نبود");
                }
            }
        });
    }

    private void pay(int coins){
        long amount = 1000;

        switch (coins){
            case 100:
//                amount = 2000;
                amount = 100;
                break;
            case 210:
                amount = 101;
                break;
            case 320:
                amount = 102;
                break;
            case 430:
                amount = 103;
                break;
        }
        this.coins = coins;
        this.amount = amount;

        ZarinPal purchase = ZarinPal.getPurchase(this);
        PaymentRequest payment = ZarinPal.getPaymentRequest();

        payment.setMerchantID("ccadd3e2-10d2-11ea-8171-000c295eb8fc");
        payment.setAmount(amount);
        payment.setDescription("خرید " + String.valueOf(coins) + " سکه");
        payment.setCallbackURL("app://app");
//        payment.setMobile("09355106005");
//        payment.setEmail("imannamix@gmail.com");


        ZarinPal.getPurchase(getApplicationContext()).startPayment(payment, new OnCallbackRequestPaymentListener() {
            @Override
            public void onCallbackResultPaymentRequest(int status, String authority, Uri paymentGatewayUri, Intent intent) {
                if (status == 100)
                    startActivity(intent);
                else
                    helper.showMessage("خطا در ایجاد درخواست پرداخت");
            }
        });
    }
}
