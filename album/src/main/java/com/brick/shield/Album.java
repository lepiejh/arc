/*
 * Copyright 2016 Yan Zhenjie.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.brick.shield;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.support.annotation.IntDef;
import android.util.Log;

import com.brick.shield.api.ImageMultipleWrapper;
import com.brick.shield.api.AlbumMultipleWrapper;
import com.brick.shield.api.AlbumSingleWrapper;
import com.brick.shield.api.BasicGalleryWrapper;
import com.brick.shield.api.GalleryAlbumWrapper;
import com.brick.shield.api.GalleryWrapper;
import com.brick.shield.api.ImageCameraWrapper;
import com.brick.shield.api.ImageSingleWrapper;
import com.brick.shield.api.VideoCameraWrapper;
import com.brick.shield.api.VideoMultipleWrapper;
import com.brick.shield.api.VideoSingleWrapper;
import com.brick.shield.api.camera.AlbumCamera;
import com.brick.shield.api.camera.Camera;
import com.brick.shield.api.choice.AlbumChoice;
import com.brick.shield.api.choice.Choice;
import com.brick.shield.api.choice.ImageChoice;
import com.brick.shield.api.choice.VideoChoice;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * <p>Entrance.</p>
 * Created by Yan Zhenjie on 2016/10/23.
 */
public final class Album {
    public static final String KEY_INPUT_WIDGET_2 = "KEY_INPUT_WIDGET_2";
    public static final String KEY_INPUT_WIDGET = "KEY_INPUT_WIDGET";
    public static final String KEY_INPUT_CHECKED_LIST = "KEY_INPUT_CHECKED_LIST";

    public static final String KEY_INPUT_FUNCTION = "KEY_INPUT_FUNCTION";
    public static final int FUNCTION_CHOICE_IMAGE = 0;
    public static final int FUNCTION_CHOICE_VIDEO = 1;
    public static final int FUNCTION_CHOICE_ALBUM = 2;

    public static final int FUNCTION_CAMERA_IMAGE = 0;
    public static final int FUNCTION_CAMERA_VIDEO = 1;

    public static final String KEY_INPUT_CHOICE_MODE = "KEY_INPUT_CHOICE_MODE";
    public static final int MODE_MULTIPLE = 1;
    public static final int MODE_SINGLE = 2;
    public static final String KEY_INPUT_COLUMN_COUNT = "KEY_INPUT_COLUMN_COUNT";
    public static final String KEY_INPUT_ALLOW_CAMERA = "KEY_INPUT_ALLOW_CAMERA";
    public static final String KEY_INPUT_LIMIT_COUNT = "KEY_INPUT_LIMIT_COUNT";

    // Gallery.
    public static final String KEY_INPUT_CURRENT_POSITION = "KEY_INPUT_CURRENT_POSITION";
    public static final String KEY_INPUT_GALLERY_CHECKABLE = "KEY_INPUT_GALLERY_CHECKABLE";

    // Camera.
    public static final String KEY_INPUT_FILE_PATH = "KEY_INPUT_FILE_PATH";
    public static final String KEY_INPUT_CAMERA_QUALITY = "KEY_INPUT_CAMERA_QUALITY";
    public static final String KEY_INPUT_CAMERA_DURATION = "KEY_INPUT_CAMERA_DURATION";
    public static final String KEY_INPUT_CAMERA_BYTES = "KEY_INPUT_CAMERA_BYTES";

    // Filter.
    public static final String KEY_INPUT_FILTER_VISIBILITY = "KEY_INPUT_FILTER_VISIBILITY";

    @IntDef({FUNCTION_CHOICE_IMAGE, FUNCTION_CHOICE_VIDEO, FUNCTION_CHOICE_ALBUM})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ChoiceFunction {
    }

    @IntDef({FUNCTION_CAMERA_IMAGE, FUNCTION_CAMERA_VIDEO})
    @Retention(RetentionPolicy.SOURCE)
    public @interface CameraFunction {
    }

    @IntDef({MODE_MULTIPLE, MODE_SINGLE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ChoiceMode {
    }

    private static AlbumConfig sAlbumConfig;

    /**
     * Initialize Album.
     *
     * @param albumConfig {@link AlbumConfig}.
     */
    public static void initialize(AlbumConfig albumConfig) {
        if (sAlbumConfig == null) sAlbumConfig = albumConfig;
        else Log.w("Album", new IllegalStateException("Illegal operation, only allowed to configure once."));
    }

    /**
     * Get the album configuration.
     */
    public static AlbumConfig getAlbumConfig() {
        if (sAlbumConfig == null) {
            sAlbumConfig = AlbumConfig.newBuilder(null).build();
        }
        return sAlbumConfig;
    }

    /**
     * Open the camera from the activity.
     */
    public static Camera<ImageCameraWrapper, VideoCameraWrapper> camera(Context context) {
        return new AlbumCamera(context);
    }

    /**
     * Select images.
     */
    public static Choice<ImageMultipleWrapper, ImageSingleWrapper> image(Context context) {
        return new ImageChoice(context);
    }

    /**
     * Select videos.
     */
    public static Choice<VideoMultipleWrapper, VideoSingleWrapper> video(Context context) {
        return new VideoChoice(context);
    }

    /**
     * Select images and videos.
     */
    public static Choice<AlbumMultipleWrapper, AlbumSingleWrapper> album(Context context) {
        return new AlbumChoice(context);
    }

    /**
     * Preview picture.
     */
    public static GalleryWrapper gallery(Context context) {
        return new GalleryWrapper(context);
    }

    /**
     * Preview Album.
     */
    public static GalleryAlbumWrapper galleryAlbum(Context context) {
        return new GalleryAlbumWrapper(context);
    }

    /**
     * Open the camera from the activity.
     */
    public static Camera<ImageCameraWrapper, VideoCameraWrapper> camera(Activity activity) {
        return new AlbumCamera(activity);
    }

    /**
     * Select images.
     */
    public static Choice<ImageMultipleWrapper, ImageSingleWrapper> image(Activity activity) {
        return new ImageChoice(activity);
    }

    /**
     * Select videos.
     */
    public static Choice<VideoMultipleWrapper, VideoSingleWrapper> video(Activity activity) {
        return new VideoChoice(activity);
    }

    /**
     * Select images and videos.
     */
    public static Choice<AlbumMultipleWrapper, AlbumSingleWrapper> album(Activity activity) {
        return new AlbumChoice(activity);
    }

    /**
     * Preview picture.
     */
    public static BasicGalleryWrapper<GalleryWrapper, String, String, String> gallery(Activity activity) {
        return new GalleryWrapper(activity);
    }

    /**
     * Preview Album.
     */
    public static BasicGalleryWrapper<GalleryAlbumWrapper, AlbumFile, String, AlbumFile> galleryAlbum(Activity activity) {
        return new GalleryAlbumWrapper(activity);
    }

    /**
     * Open the camera from the activity.
     */
    public static Camera<ImageCameraWrapper, VideoCameraWrapper> camera(Fragment fragment) {
        return new AlbumCamera(fragment.getActivity());
    }

    /**
     * Select images.
     */
    public static Choice<ImageMultipleWrapper, ImageSingleWrapper> image(Fragment fragment) {
        return new ImageChoice(fragment.getActivity());
    }

    /**
     * Select videos.
     */
    public static Choice<VideoMultipleWrapper, VideoSingleWrapper> video(Fragment fragment) {
        return new VideoChoice(fragment.getActivity());
    }

    /**
     * Select images and videos.
     */
    public static Choice<AlbumMultipleWrapper, AlbumSingleWrapper> album(Fragment fragment) {
        return new AlbumChoice(fragment.getActivity());
    }

    /**
     * Preview picture.
     */
    public static BasicGalleryWrapper<GalleryWrapper, String, String, String> gallery(Fragment fragment) {
        return new GalleryWrapper(fragment.getActivity());
    }

    /**
     * Preview Album.
     */
    public static BasicGalleryWrapper<GalleryAlbumWrapper, AlbumFile, String, AlbumFile> galleryAlbum(Fragment fragment) {
        return new GalleryAlbumWrapper(fragment.getActivity());
    }

    /**
     * Open the camera from the activity.
     */
    public static Camera<ImageCameraWrapper, VideoCameraWrapper> camera(android.support.v4.app.Fragment fragment) {
        return new AlbumCamera(fragment.getContext());
    }

    /**
     * Select images.
     */
    public static Choice<ImageMultipleWrapper, ImageSingleWrapper> image(android.support.v4.app.Fragment fragment) {
        return new ImageChoice(fragment.getContext());
    }

    /**
     * Select videos.
     */
    public static Choice<VideoMultipleWrapper, VideoSingleWrapper> video(android.support.v4.app.Fragment fragment) {
        return new VideoChoice(fragment.getContext());
    }

    /**
     * Select images and videos.
     */
    public static Choice<AlbumMultipleWrapper, AlbumSingleWrapper> album(android.support.v4.app.Fragment fragment) {
        return new AlbumChoice(fragment.getContext());
    }

    /**
     * Preview picture.
     */
    public static BasicGalleryWrapper<GalleryWrapper, String, String, String> gallery(android.support.v4.app.Fragment fragment) {
        return new GalleryWrapper(fragment.getContext());
    }

    /**
     * Preview Album.
     */
    public static BasicGalleryWrapper<GalleryAlbumWrapper, AlbumFile, String, AlbumFile> galleryAlbum(android.support.v4.app.Fragment fragment) {
        return new GalleryAlbumWrapper(fragment.getContext());
    }
}