/*
 * Copyright (C) 2014 The Android Open Source Project
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

package com.example.android.wearable.recipeassistant;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.util.ArrayList;

public class RecipeActivity extends Activity {
    private static final String TAG = "RecipeAssistant";
    private String mRecipeName;
    private Recipe mRecipe;
    private ImageView mImageView;
    private TextView mTitleTextView;
    private TextView mSummaryTextView;
    private TextView mIngredientsTextView;
    private LinearLayout mStepsLayout;

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();
        mRecipeName = intent.getStringExtra(Constants.RECIPE_NAME_TO_LOAD);
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "Intent: " + intent.toString() + " " + mRecipeName);
        }

        loadRecipe();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe);
        mTitleTextView = (TextView) findViewById(R.id.recipeTextTitle);
        mSummaryTextView = (TextView) findViewById(R.id.recipeTextSummary);
        mImageView = (ImageView) findViewById(R.id.recipeImageView);
        mIngredientsTextView = (TextView) findViewById(R.id.textIngredients);
        mStepsLayout = (LinearLayout) findViewById(R.id.layoutSteps);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_cook:
                startCooking();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    ///////////////// after jes11 ////////////////////
    private void loadRecipe(ArrayList<Recipe.RecipeStep> list){
        Recipe recipe=new Recipe();
        recipe.titleText=MainActivity.selectedItem.title;
        recipe.recipeImageBitmap=MainActivity.selectedItem.image;//==>jes12
        recipe.recipeImage="imageBitmap";
        recipe.summaryText=MainActivity.selectedItem.summary;
        recipe.ingredientsText=MainActivity.selectedItem.ingredientsText;
        System.out.println(recipe.ingredientsText);
        recipe.recipeSteps=list;
        mRecipe=recipe;
        displayRecipe(recipe);//==>13
    }
    ///////////////////////////////////////////////

    private void loadRecipe() {
        ////////////////////////////// jes11 /////////////////////////////////////
        try{
            Integer.parseInt(mRecipeName);
            final String url=NetworkUtil.ServerURL+"/recipe/"+mRecipeName;
            Toast.makeText(this, NetworkUtil.getConnectivityStatusString(this), Toast.LENGTH_SHORT).show();
            if(NetworkUtil.getConnectivityStatus(this)==NetworkUtil.TYPE_WIFI){
                final Handler handler=new Handler();
                Thread xmlThread=new Thread(){
                    @Override
                    public void run() {
                        try {
                            MyParser myParser=new RecipeFromMyServer();
                            final  ArrayList<Recipe.RecipeStep> list= (ArrayList<Recipe.RecipeStep>) NetworkUtil.sendRequestGet(url,myParser);
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    loadRecipe(list);//==>after 11
                                }
                            });
                        } catch (Exception e) {
                            Log.e("jes","http로 레시피 가져오기 실패");
                            e.printStackTrace();
                        }
                    }
                };
                xmlThread.start();

            }
        }catch(NumberFormatException e){
            Log.i("jes///","mRecipeName is not number : "+mRecipeName);
            JSONObject jsonObject = AssetUtils.loadJSONAsset(this, mRecipeName);
            if (jsonObject != null) {
                mRecipe = Recipe.fromJson(this, jsonObject);
                if (mRecipe != null) {
                    displayRecipe(mRecipe);
                }
            }
        }
        //////////////////////////////////////////////////////////////////////
    }


    private void displayRecipe(Recipe recipe) {
        Animation fadeIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        mTitleTextView.setAnimation(fadeIn);
        mTitleTextView.setText(recipe.titleText);
        mSummaryTextView.setText(recipe.summaryText);
        if (recipe.recipeImage != null) {
            mImageView.setAnimation(fadeIn);

            ////////////////// jes13 //////////////////
            Bitmap recipeImage=null;
            if(recipe.recipeImage.equals("imageBitmap")){
                recipeImage=recipe.recipeImageBitmap;
            }else {
                recipeImage = AssetUtils.loadBitmapAsset(this, recipe.recipeImage);
            }
            /////////////////////////////////////////////

            mImageView.setImageBitmap(recipeImage);
        }
        mIngredientsTextView.setText(recipe.ingredientsText);

        findViewById(R.id.ingredientsHeader).setAnimation(fadeIn);
        findViewById(R.id.ingredientsHeader).setVisibility(View.VISIBLE);
        findViewById(R.id.stepsHeader).setAnimation(fadeIn);

        findViewById(R.id.stepsHeader).setVisibility(View.VISIBLE);

        LayoutInflater inf = LayoutInflater.from(this);
        mStepsLayout.removeAllViews();
        int stepNumber = 1;
        for (Recipe.RecipeStep step : recipe.recipeSteps) {
            View view = inf.inflate(R.layout.step_item, null);
            final ImageView iv = (ImageView) view.findViewById(R.id.stepImageView);
            if (step.stepImage == null) {
                System.out.println("stepImage is null");
                iv.setVisibility(View.GONE);
            } else {

                ///////////////////// jes14 ///////////////////////////////////

                if(recipe.recipeImage.equals("imageBitmap")){
                    final String stepImgStr=step.stepImage;
                    final Handler handler=new Handler();
                    Thread netThread=new Thread(){
                        @Override
                        public void run() {
                            try {
                                String stepImageUrl=NetworkUtil.ServerURL+"/img/"+stepImgStr;

                                BufferedInputStream bis = NetworkUtil.sendRequestGetImg(stepImageUrl);
                                final Bitmap bitmap = BitmapFactory.decodeStream(bis);
                                System.out.println("stepImage is network request ok!!!");
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {

                                        setImageBitmap(iv, bitmap);

                                    }
                                });
                            } catch (Exception e) {
                                Log.e("jes","[fe80::358c:dde6:6c27:49ce]로 레시피 가져오기 실패");
                                e.printStackTrace();
                                try {
                                    String stepImageUrl=NetworkUtil.ServerURL+"/img/"+stepImgStr;

                                    BufferedInputStream bis = NetworkUtil.sendRequestGetImg(stepImageUrl);
                                    final Bitmap bitmap = BitmapFactory.decodeStream(bis);
                                    System.out.println("stepImage is network request ok!!!");
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {

                                            setImageBitmap(iv, bitmap);

                                        }
                                    });
                                } catch (Exception ex) {
                                    Log.e("jes","192.168.0.41로 레시피 가져오기 실패");
                                    ex.printStackTrace();
                                }
                            }
                        }
                    };
                    netThread.start();

                }else {
                    Bitmap bitmap = AssetUtils.loadBitmapAsset(this, step.stepImage);
                    iv.setImageBitmap(bitmap);
                }

                ///////////////////////////////////////////////////////////////////
            }

            ((TextView) view.findViewById(R.id.textStep)).setText(
                    (stepNumber++) + ". " + step.stepText);
            mStepsLayout.addView(view);
        }
    }

    private void setImageBitmap(ImageView iv,Bitmap bitmap){
        System.out.println("setImageBitmap ok!!!");
        iv.setImageBitmap(bitmap);
    }

    private void startCooking() {
        Intent intent = new Intent(this, RecipeService.class);
        intent.setAction(Constants.ACTION_START_COOKING);
        intent.putExtra(Constants.EXTRA_RECIPE, mRecipe.toBundle());
        startService(intent);
    }
}
