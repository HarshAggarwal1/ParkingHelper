package com.project.parking_helper;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;

public class CameraPage extends AppCompatActivity implements SurfaceHolder.Callback{

    private SurfaceView cameraPreview;
    private ImageView flashLightButton;
    private ImageView shutterButton;
    private CameraDevice cameraDevice;
    private CameraCaptureSession cameraCaptureSession;
    private CaptureRequest.Builder previewRequestBuilder;
    private Handler backgroundHandler;
    private boolean isFlashOn = false;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(CameraPage.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    Log.d("MainActivity", "Camera Permission Accepted");
                }
            } else {
                Log.d("MainActivity", "Camera Permission Denied");
            }
        }
        if (requestCode == 2) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(CameraPage.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    Log.d("MainActivity", "Storage Permission Accepted");
                }
            } else {
                Log.d("MainActivity", "Storage Permission Denied");
            }
        }
        if (requestCode == 3) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(CameraPage.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    Log.d("MainActivity", "Storage Permission Accepted");
                }
            } else {
                Log.d("MainActivity", "Storage Permission Denied");
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        // ---------- Camera Permission ------------
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA},1);
        }
        else {
            Log.d("CameraPage", "Camera Permission Already Granted");
        }

        // ---------- Storage Permission ------------
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            System.out.println("Storage Permission Not Granted");
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},2);
        }
        else {
            Log.d("CameraPage", "Storage Permission Already Granted");
        }

        // ---------- Storage Permission ------------
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            System.out.println("Storage Permission Not Granted");
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},3);
        }
        else {
            Log.d("CameraPage", "Storage Permission Already Granted");
        }

        cameraPreview = findViewById(R.id.camera_preview);
        if (cameraPreview != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                Log.d("MainActivity", "Camera Permission Denied");
                turnOffFlashLight();
                finish();
            }

            // set surface holder
            cameraPreview.getHolder().setFixedSize(480, 480);

            cameraPreview.getHolder().addCallback(this);

        }

        // ---------- Flashlight Button ------------
        flashLightButton = findViewById(R.id.camera_flash_button);
        flashLightButton.setImageResource(R.drawable.flash_off);
        flashLightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFlashOn) {
                    turnOffFlashLight();
                    flashLightButton.setImageResource(R.drawable.flash_off);
                    isFlashOn = false;
                } else {
                    turnOnFlashLight();
                    flashLightButton.setImageResource(R.drawable.flash_on);
                    isFlashOn = true;
                }
            }
        });

        // ---------- Back Button ------------
        ImageView backButton = findViewById(R.id.camera_back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                turnOffFlashLight();
                finish();
            }
        });

        // ---------- Shutter Button ------------
        shutterButton = findViewById(R.id.camera_shutter);
        shutterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shutterButton.setImageResource(R.drawable.yellow_camera_shutter);
                shutterButton.setBackgroundResource(R.drawable.circle_bg_yellow_shutter_clicked);
                captureImage();

            }
        });

    }

    private void captureImage() {
        try {
            CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
            CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraManager.getCameraIdList()[0]);
            Size[] jpegSizes = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(ImageFormat.JPEG);
            int width = 720;
            int height = 720;
            if (jpegSizes != null && jpegSizes.length > 0) {
                width = jpegSizes[0].getWidth();
                height = jpegSizes[0].getHeight();
            }
            ImageReader imageReader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1);
            List<Surface> outputSurface = Collections.singletonList(imageReader.getSurface());

            CaptureRequest.Builder captureBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(imageReader.getSurface());
            captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
            int sensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            int jpegOrientation = (sensorOrientation + rotation * 90 + 270) % 360;
            previewRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, jpegOrientation);


            CameraCaptureSession.CaptureCallback captureCallback = new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);
                    try  {
                        Image image = imageReader.acquireLatestImage();
                        System.out.println(image.getWidth() + " " + image.getHeight());
                        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                        byte[] bytes = new byte[buffer.capacity()];
                        buffer.get(bytes);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        bitmap = rotateImage(bitmap);
                        System.out.println(bitmap.getWidth() + " " + bitmap.getHeight());
                        image.close();
                        // reduce size of bitmap
                        bitmap = Bitmap.createScaledBitmap(bitmap, 720, 720, false);
                        saveImage(bitmap);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    shutterButton.setImageResource(R.drawable.camera_shutter);
                    shutterButton.setBackgroundResource(R.drawable.circle_bg_translucent_black);
                }
            };

            cameraDevice.createCaptureSession(outputSurface, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    try {
                        session.capture(captureBuilder.build(), captureCallback, null);
                    }
                    catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {

                }
            }, backgroundHandler);
        }
        catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void saveImage(Bitmap bitmap) {
        String fileName = "PH" + System.currentTimeMillis() + ".jpg";
        String path = Environment.getExternalStorageDirectory().toString() + "/DCIM/Camera/" + fileName;
        File file = new File(Environment.getExternalStorageDirectory().toString() + "/DCIM/Camera/" + "PH" + System.currentTimeMillis() + ".jpg");
        System.out.println(file.getAbsolutePath());
        try {
            OutputStream outputStream = Files.newOutputStream(file.toPath());
            // reduce quality and size of image
            System.out.println(bitmap.getWidth() + " " + bitmap.getHeight());
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
            System.out.println(bitmap.getWidth() + " " + bitmap.getHeight());
            outputStream.flush();
            outputStream.close();
            Toast.makeText(CameraPage.this, "Image Saved", Toast.LENGTH_SHORT).show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        cameraCaptureSession.close();
        createCameraPreviewSession();
    }

    private Bitmap rotateImage(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            String cameraId = cameraManager.getCameraIdList()[0];
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                Log.d("MainActivity", "Camera Permission Denied");
            }
            cameraManager.openCamera(cameraId, new CameraDevice.StateCallback() {
                @Override
                public void onOpened(@NonNull CameraDevice camera) {
                    cameraDevice = camera;
                    createCameraPreviewSession();
                }

                @Override
                public void onDisconnected(@NonNull CameraDevice camera) {
                    camera.close();
                    cameraDevice = null;
                }

                @Override
                public void onError(@NonNull CameraDevice camera, int error) {
                    camera.close();
                    cameraDevice = null;
                }
            }, null);
        } catch (Exception e) {
            Log.d("CameraPage", "Camera Error");
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder,int format,int width,int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    private void createCameraPreviewSession() {
        try {
            Surface surface = cameraPreview.getHolder().getSurface();
            previewRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            previewRequestBuilder.addTarget(surface);

            cameraDevice.createCaptureSession(Collections.singletonList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    cameraCaptureSession = session;
                    try {
                        cameraCaptureSession.setRepeatingRequest(previewRequestBuilder.build(), null, backgroundHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                    Toast.makeText(CameraPage.this, "Unable to create camera preview session", Toast.LENGTH_SHORT).show();
                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void turnOnFlashLight() {
        try {
            if (cameraDevice != null) {
                previewRequestBuilder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_TORCH);
                cameraCaptureSession.setRepeatingRequest(previewRequestBuilder.build(), null, backgroundHandler);
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void turnOffFlashLight() {
        try {
            if (cameraDevice != null) {
                previewRequestBuilder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_OFF);
                cameraCaptureSession.setRepeatingRequest(previewRequestBuilder.build(), null, backgroundHandler);
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        turnOffFlashLight();
        finish();
    }
}
