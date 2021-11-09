package com.example.caloriestracker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.caloriestracker.databinding.ActivityMainBinding;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private ActivityMainBinding binding;
    private FloatingActionButton camera;
    String selectedImagePath;
    ArrayList<Food> foodDetails = new ArrayList<>();
    AlertDialog.Builder builderSingle ;
    ImageView image ;
    private ProgressBar main_loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();//hide toolbar

        BottomNavigationView navView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home,  R.id.navigation_me)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        camera = binding.floatingButton;
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isStoragePermissionGranted()){
                    ImagePicker.with(MainActivity.this).start();
                }
            }
        });
        main_loading = binding.mainLoading;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            selectedImagePath = getPath(getApplicationContext(), uri);
            connectServer();
            image = new ImageView(this);
            image.setImageURI(uri);
        }

    }

    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void connectServer() {

        String postUrl= "http://"+"192.168.68.103"+":"+"5000"+"/";

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        // Read BitMap by file path
        Bitmap bitmap = BitmapFactory.decodeFile(selectedImagePath, options);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        RequestBody postBodyImage = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", selectedImagePath, RequestBody.create(MediaType.parse("image/*jpg"), byteArray))
                .build();
        main_loading.setVisibility(View.VISIBLE);

        postRequest(postUrl, postBodyImage);
    }

    void postRequest(String postUrl, RequestBody postBody) {

        OkHttpClient client = new OkHttpClient().newBuilder() .connectTimeout(50, TimeUnit.SECONDS) .readTimeout(50, TimeUnit.SECONDS) .writeTimeout(50, TimeUnit.SECONDS) .build();

        Request request = new Request.Builder()
                .url(postUrl)
                .post(postBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Cancel the post on failure.
                call.cancel();

                // In order to access the TextView inside the UI thread, the code is executed inside runOnUiThread()
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        main_loading.setVisibility(View.INVISIBLE);
                        Toast.makeText(MainActivity.this,"Failed to Connect to Server: "+e,Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                // In order to access the TextView inside the UI thread, the code is executed inside runOnUiThread()
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            String output = response.body().string().replace("(", "").replace(")","");
                            String[] elements = output.split(",");
                            foodDetails=new ArrayList<>();

                            for (int i = 0; i<elements.length;i++){
                                Food food = new Food();
                                switch (elements[i].trim()){
                                    case "1":
                                        food.setFoodName("Bah Kut Teh");
                                        break;
                                    case "2":
                                        food.setFoodName("Cendol");
                                        break;
                                    case "3":
                                        food.setFoodName("Char Kway Teow");
                                        break;
                                    case "4":
                                        food.setFoodName("Curry Puff");
                                        break;
                                    case "5":
                                        food.setFoodName("Fried Rice");
                                        break;
                                    case "6":
                                        food.setFoodName("Laksa");
                                        break;
                                    case "7":
                                        food.setFoodName("Otak Otak");
                                        break;
                                    case "8":
                                        food.setFoodName("Roti Canai");
                                        break;
                                    case "9":
                                        food.setFoodName("Roti Tisu");
                                        break;
                                    case "10":
                                        food.setFoodName("Chicken Satay");
                                        break;
                                    default:
                                        break;
                                }
                                food.setQty(1);
                                foodDetails.add(food);
                            }

                            ArrayList<Food> tempList = foodDetails;
                            for (int i = 0; i < tempList.size(); i++) {
                                for (int j = i+1; j < tempList.size(); j++) {
                                    // compare list.get(i) and list.get(j)
                                    if(tempList.get(i).getFoodName()==tempList.get(j).getFoodName()){
                                        foodDetails.get(i).setQty(foodDetails.get(i).getQty()+1);
                                        foodDetails.remove(j);
                                    }
                                }
                            }

                            main_loading.setVisibility(View.INVISIBLE);
                            builderSingle = new AlertDialog.Builder(MainActivity.this);
                            builderSingle.setTitle("Confirm Food");
                            builderSingle.setView(image);

                            String[] arr = new String[foodDetails.size()];
                            for(int i=0 ; i< foodDetails.size();i++){
                                arr[i] = foodDetails.get(i).getFoodName();
                            }
                            ArrayList<Food> foodsCheck = new ArrayList<>();
                            builderSingle.setMultiChoiceItems(arr, null, new DialogInterface.OnMultiChoiceClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                    if(isChecked){
                                        foodsCheck.add(foodDetails.get(which));
                                    }
                                    else {
                                        foodsCheck.remove(foodDetails.get(which));
                                    }
                                }
                            });

                            builderSingle.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if(!foodsCheck.isEmpty()){
                                        Intent intent = new Intent(MainActivity.this, Quantity.class);
                                        Bundle args = new Bundle();
                                        args.putSerializable("ARRAYLIST",(Serializable)foodsCheck);
                                        intent.putExtra("BUNDLE",args);
                                        startActivity(intent);
                                    }
                                    else{
                                        FoodNotFound();//display alert dialog to show list of food cover currently
                                    }

                                }
                            });

                            builderSingle.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    FoodNotFound();//display alert dialog to show list of food cover currently
                                }
                            });
                            AlertDialog dialog = builderSingle.create();
                            dialog.show();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("permissionD","Permission is granted");
                return true;
            } else {

                Log.v("permissionD","Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v("permissionD","Permission is granted auto");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            Log.v("permissionD","Permission: "+permissions[0]+ "was "+grantResults[0]);
            //resume tasks needing this permission
        }
    }

    public void FoodNotFound(){
        AlertDialog.Builder FoodNotFound = new AlertDialog.Builder(MainActivity.this);
        FoodNotFound.setTitle("Food Not Found? ")
                .setMessage("\n\n"+"Here is the list of food cover in this app currently.\n\n" +
                        "1. Laksa\n\n2. Cendol\n\n3. Curry Puff\n\n4. Bah Kut Teh\n\n 5. Char Kway Teow" +
                        "\n\n6. Chicken Satay\n\n7. Fried Rice\n\n8. Otak-otak\n\n9. Roti Canai" +
                        "\n\n10. Roti Tisu\n\n")
                .setNeutralButton("OK",null)
                .show();
    }
}