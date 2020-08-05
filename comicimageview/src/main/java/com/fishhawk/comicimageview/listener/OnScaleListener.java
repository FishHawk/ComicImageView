package com.fishhawk.comicimageview.listener;

public interface OnScaleListener {

    /**
     * Callback for when the scale changes
     *
     * @param scaleFactor the scale factor (less than 1 for zoom out, greater than 1 for zoom in)
     * @param focusX      focal point X position
     * @param focusY      focal point Y position
     */
    void onScale(float scaleFactor, float focusX, float focusY);
}
