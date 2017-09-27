package condi.kr.ac.swu.condidemo.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import condi.kr.ac.swu.condidemo.R;
import condi.kr.ac.swu.condidemo.data.NetworkAction;
import condi.kr.ac.swu.condidemo.view.CircularNetworkImageView;
import condi.kr.ac.swu.condidemo.view.RoundImage;


public class JoinActivity extends RootActivity {
    private CircularNetworkImageView imgJoinProfile;
    private ImageView img_join_local;
    private ImageButton btnJoinProfile;
    private Button btnJoin, btn_join_no;
    private EditText editJoinPhone, editJoinName, editJoinHeight, editJoinPassword1, editJoinPassword2;
    private String phone, name, height, password1, password2;
    private String profile = null;
    private RoundImage roundedImage;
    /*
    * 파일 전송 관련 변수
    * */
    private String currentFilePath = "";
    private String filePath = null;
    private final String IMG_FILE_PREFIX = "IMG_";
    private final String IMG_FILE_SUFFIX = ".jpg";
    private final String ALBUM_NAME = "WalkingTogether";
    private final String SERVER = "http://condi.swu.ac.kr:80/condi2/profile/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
        initActionBar("회원가입");
        initView();
    }

    private void initView() {
        imgJoinProfile = (CircularNetworkImageView) findViewById(R.id.imgJoinProfile);
        img_join_local = (ImageView)findViewById(R.id.img_join_local);
        btnJoinProfile = (ImageButton) findViewById(R.id.btnJoinProfile);
        btnJoin = (Button) findViewById(R.id.btnJoin);
        editJoinPhone = (EditText) findViewById(R.id.editJoinPhone);
        editJoinName = (EditText) findViewById(R.id.editJoinName);
        editJoinHeight = (EditText) findViewById(R.id.editJoinHeight);
        editJoinPassword1 = (EditText) findViewById(R.id.editJoinPassword1);
        editJoinPassword2 = (EditText) findViewById(R.id.editJoinPassword2);
        btn_join_no = (Button)findViewById(R.id.btn_join_no);

        btnJoinProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickPictureIntent();
            }
        });
        btn_join_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectLoginActivity();
            }
        });

        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                * 다 작성했는지 검사
                *   o : 패스워드 검사
                *       o : 이미지파일 있는지 검사
                *           o : 인서트시키고 파일 업로드
                *           x : 인서트만 시킴.
                * */

                phone = editJoinPhone.getText().toString();
                name = editJoinName.getText().toString();
                height = editJoinHeight.getText().toString();
                password1 = editJoinPassword1.getText().toString();
                password2 = editJoinPassword2.getText().toString();

                if(checkEmpty() && checkPassword())
                    new Join().execute();
            }
        });

        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.thumb_story);
        roundedImage = new RoundImage(bm);
        img_join_local.setImageDrawable(roundedImage);
    }



    /*
    * 전송을 위한 내부 클래스
    * */
    private class Join extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            Properties p = new Properties();

            p.setProperty("phone", phone);
            p.setProperty("name", name);
            p.setProperty("height", height);
            p.setProperty("password", password1);
            p.setProperty("profile", profile + IMG_FILE_SUFFIX);
            return NetworkAction.sendDataToServer("join.php", p);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s.equals("success")) {
                if(chechProfile())
                    new UploadFile().execute();
                else
                    redirectLoginActivity();
            } else
                System.out.println("회원가입 실패");
        }
    }

    private class UploadFile extends AsyncTask<Void, Void, Integer> {
        @Override
        protected Integer doInBackground(Void... params) {
            return uploadFile(filePath);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if(integer==200)
                redirectLoginActivity();
            else
                Toast.makeText(getApplicationContext(),"파일 업로드를 실패했습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    /*
    * 체크 메소드
    * */
    private boolean checkEmpty() {
        if(TextUtils.isEmpty(phone)) {
            editJoinPhone.setError("휴대폰 번호를 입력해주세요.");
            return false;
        }

        if(TextUtils.isEmpty(name)) {
            editJoinName.setError("이름를 입력해주세요.");
            return false;
        }

        if(TextUtils.isEmpty(height)) {
            editJoinHeight.setError("키를 입력해주세요.");
            return false;
        }

        if(TextUtils.isEmpty(password1)) {
            editJoinPassword1.setError("비밀번호를 입력해주세요.");
            return false;
        }

        if(TextUtils.isEmpty(password1)) {
            editJoinPassword2.setError("비밀번호 확인을 입력해주세요.");
            return false;
        }

        return true;
    }

    private boolean checkPassword() {
        if(!password1.equals(password2)) {
            editJoinPassword2.setError("비밀번호를 다시 확인해주세요.");
            return false;
        }

        return true;
    }

    private boolean chechProfile() {
        return (filePath!=null ? true : false);
    }

    /*
    * 리다이렉트 메소드
    * */
    private void redirectLoginActivity() {
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
    }

    /*
    * ============================================파일 관련 메소드==================================================
    * */

    /* 임시 파일 관리 */
    private String getAlbumName() {
        return ALBUM_NAME;
    }

    private File getAlbumDir() {
        File storageDir = null;

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            storageDir = new File (
                    Environment.getExternalStorageDirectory()
                            + "/dcim/"
                            + getAlbumName()
            );

            if (storageDir != null) {
                if (! storageDir.mkdirs()) {
                    if (! storageDir.exists()){
                        Log.d("CameraSample", "디렉토리를 만들지 못했습니다.");
                        return null;
                    }
                }
            }

        } else {
            Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
        }

        return storageDir;
    }

    private File createTempFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File albumF = getAlbumDir();
        File mediaF = null;

        profile = IMG_FILE_PREFIX + timeStamp+"_";
        mediaF = File.createTempFile(profile, IMG_FILE_SUFFIX, albumF);

        return mediaF;
    }

    private File setUpTempFile() throws IOException {

        File f =  createTempFile();
        currentFilePath = f.getAbsolutePath();
        filePath = currentFilePath;

        return f;
    }
    /* 임시 파일 관리 */

    /*
    * 갤러리에서 사진 가져오기
    * */
    private void pickPictureIntent() {

        Intent galleryForImage = new Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        File f = null;
        try {
            f = setUpTempFile();
            currentFilePath = f.getAbsolutePath();

            galleryForImage.setType("image/*");              // 모든 이미지
            galleryForImage.putExtra("crop", "true");        // Crop기능 활성화
            galleryForImage.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
            galleryForImage.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        startActivityForResult(galleryForImage, 1001);
    }

    private void setImage() {
        //imgJoinProfile.setVisibility(View.VISIBLE);

        if (currentFilePath != null) {
            Bitmap bm = BitmapFactory.decodeFile(currentFilePath);
            roundedImage = new RoundImage(bm);
            img_join_local.setImageDrawable(roundedImage);
        } else {
            Toast.makeText(getApplicationContext(), "이미지 미리보기가 불가능합니다.", Toast.LENGTH_LONG).show();
        }

    }

    /*
    * 결과값 처리
    * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1001 && resultCode == RESULT_OK ) {
            setImage();
            currentFilePath = null;
        } else {
            Toast.makeText(getApplicationContext(), "이미지를 불러오지 못했습니다.", Toast.LENGTH_LONG).show();
        }
    }

    /*
    * 파일 전송 메소드
    * */
    public int uploadFile(String sourceFileUri) {

        String fileName = sourceFileUri;

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        int serverResponseCode = 0;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(fileName);

        if (!sourceFile.isFile()) {
            Toast.makeText(getApplicationContext(), "파일이 없습니다.", Toast.LENGTH_LONG).show();
            Log.e("uploadFile", "Source File not exist :" +fileName);
            return 0;
        }
        else
        {
            try {

                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL("http://condi.swu.ac.kr:80/condi2/condi/"+"upload_file.php");

                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();

                conn.setReadTimeout(10000);
                conn.setConnectTimeout(35000);
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

                conn.setRequestProperty("uploaded_file", profile + IMG_FILE_SUFFIX);


                dos = new DataOutputStream(conn.getOutputStream());
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=" + profile + IMG_FILE_SUFFIX + "" + lineEnd);


                //dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename="+ imageFileName + JPEG_FILE_SUFFIX + "" + lineEnd);
                dos.writeBytes(lineEnd);

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                // 더이상 읽을 것이 없어질 때 까지 돌려
                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);   // 이 시점부터 파일이 전송

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.i("uploadFile", "HTTP Response is : "+ serverResponseMessage + ": " + serverResponseCode);

                if(serverResponseCode == 200){

                    runOnUiThread(new Runnable() {
                        public void run() {
                            //isFileUploadOk = true;
                            // 리다이렉트이동
                            //redirectLoginActivity();
                        }
                    });
                }

                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {

                //Toast.makeText(getApplicationContext(), "MalformedURLException 에러", Toast.LENGTH_LONG).show();
                ex.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "MalformedURLException", Toast.LENGTH_SHORT).show();
                    }
                });

                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {

                //dialog.dismiss();
                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {

                        Toast.makeText(JoinActivity.this, "Got Exception : see logcat ",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                Log.e("Upload file Err", e.getMessage());
            }
            //dialog.dismiss();
            return serverResponseCode;

        } // End else block
    }

    /*
    * ===========================================/.파일 관련 메소드==================================================
    * */

}
