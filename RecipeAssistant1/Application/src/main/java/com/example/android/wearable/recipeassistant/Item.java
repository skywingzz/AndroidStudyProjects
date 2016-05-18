package com.example.android.wearable.recipeassistant;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by javan_000 on 2015-07-15.
 */
public class Item implements Serializable{
    String title;
    String name;
    String summary;
    Bitmap image;
    String ingredientsText;
}
