package com.fishhawk.comicimageview.listener;

public interface OnDragListener {

    /**
     * Callback for when the image is experiencing a drag event. This cannot be invoked when the
     * user is scaling.
     *
     * @param dx The change of the coordinates in the x-direction
     * @param dy The change of the coordinates in the y-direction
     */
    void onDrag(float dx, float dy);
}
