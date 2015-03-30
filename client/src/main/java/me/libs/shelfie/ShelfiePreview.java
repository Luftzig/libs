package me.libs.shelfie;

import android.content.Context;
import android.hardware.Camera;
import android.view.*;

import java.io.IOException;
import java.util.List;

/**
 * TODO: document your custom view class.
 */
public class ShelfiePreview extends SurfaceView implements SurfaceHolder.Callback {
    private final View cameraView;
    private final Context context;
    private SurfaceHolder surfaceHolder;
    private Camera camera;
    private List<Camera.Size> supportedPreviewSizes;
    private List<String> supportedFlashModes;
    private Camera.Size previewSize;

    ShelfiePreview(Context context, Camera camera, View cameraView) {
        super(context);

        // Capture the context
        this.cameraView = cameraView;
        this.context = context;
        setCamera(camera);

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setKeepScreenOn(true);
        // deprecated setting, but required on Android versions prior to 3.0
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    /**
     * Begin the preview of the camera input.
     */
    public void startCameraPreview()
    {
        try{
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Extract supported preview and flash modes from the camera.
     * @param camera
     */
    public void setCamera(Camera camera)
    {
        // Source: http://stackoverflow.com/questions/7942378/android-camera-will-not-work-startpreview-fails
        this.camera = camera;
        if (this.camera == null) {
            return;
        }
        supportedPreviewSizes = this.camera.getParameters().getSupportedPreviewSizes();
        supportedFlashModes = this.camera.getParameters().getSupportedFlashModes();

        // Set the camera to Auto Flash mode.
        if (supportedFlashModes != null && supportedFlashModes.contains(Camera.Parameters.FLASH_MODE_AUTO)){
            Camera.Parameters parameters = camera.getParameters();
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
            camera.setParameters(parameters);
        }

        requestLayout();
    }

    /**
     * The Surface has been created, now tell the camera where to draw the preview.
     * @param holder
     */
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            camera.setPreviewDisplay(holder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Dispose of the camera preview.
     * @param holder
     */
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (camera != null){
            camera.stopPreview();
        }
    }

    /**
     * React to surface changed events
     * @param holder
     * @param format
     * @param w
     * @param h
     */
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (surfaceHolder.getSurface() == null){
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            Camera.Parameters parameters = camera.getParameters();

            // Set the auto-focus mode to "continuous"
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);

            // Preview size must exist.
            if(this.previewSize != null) {
                Camera.Size previewSize = this.previewSize;
                parameters.setPreviewSize(previewSize.width, previewSize.height);
            }

            camera.setParameters(parameters);
            camera.startPreview();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Calculate the measurements of the layout
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        // Source: http://stackoverflow.com/questions/7942378/android-camera-will-not-work-startpreview-fails
        final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        setMeasuredDimension(width, height);

        if (supportedPreviewSizes != null){
            previewSize = getOptimalPreviewSize(supportedPreviewSizes, width, height);
        }
    }

    /**
     * Update the layout based on rotation and orientation changes.
     * @param changed
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom)
    {
        // Source: http://stackoverflow.com/questions/7942378/android-camera-will-not-work-startpreview-fails
        if (changed) {
            final int width = right - left;
            final int height = bottom - top;

            int previewWidth = width;
            int previewHeight = height;

            if (previewSize != null){
                Display display = ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

                switch (display.getRotation())
                {
                    case Surface.ROTATION_0:
                        previewWidth = previewSize.height;
                        previewHeight = previewSize.width;
                        camera.setDisplayOrientation(90);
                        break;
                    case Surface.ROTATION_90:
                        previewWidth = previewSize.width;
                        previewHeight = previewSize.height;
                        break;
                    case Surface.ROTATION_180:
                        previewWidth = previewSize.height;
                        previewHeight = previewSize.width;
                        break;
                    case Surface.ROTATION_270:
                        previewWidth = previewSize.width;
                        previewHeight = previewSize.height;
                        camera.setDisplayOrientation(180);
                        break;
                }
            }

            final int scaledChildHeight = previewHeight * width / previewWidth;
            cameraView.layout(0, height - scaledChildHeight, width, height);
        }
    }

    /**
     *
     * @param sizes
     * @param width
     * @param height
     * @return
     */
    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int width, int height)
    {
        // Source: http://stackoverflow.com/questions/7942378/android-camera-will-not-work-startpreview-fails
        Camera.Size optimalSize = null;

        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) height / width;

        // Try to find a size match which suits the whole screen minus the menu on the left.
        for (Camera.Size size : sizes){

            if (size.height != width) continue;
            double ratio = (double) size.width / size.height;
            if (ratio <= targetRatio + ASPECT_TOLERANCE && ratio >= targetRatio - ASPECT_TOLERANCE){
                optimalSize = size;
            }
        }

        // If we cannot find the one that matches the aspect ratio, ignore the requirement.
        if (optimalSize == null) {
            // TODO : Backup in case we don't get a size.
        }

        return optimalSize;
    }
}
