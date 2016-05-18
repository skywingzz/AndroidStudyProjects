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

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends ListActivity {

    private static final String TAG = "RecipeAssistant";
    private RecipeListAdapter mAdapter;

    //////////////////// before jes11 ////////////////////////
    public static Item selectedItem;
    //////////////////////////////////////////////////////////

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG , "onListItemClick " + position);
        }
        String itemName = mAdapter.getItemName(position);
        Intent intent = new Intent(getApplicationContext(), RecipeActivity.class);
        intent.putExtra(Constants.RECIPE_NAME_TO_LOAD, itemName);

        selectedItem= (Item) mAdapter.getItem(position);//==>11

        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(android.R.layout.list_content);

        mAdapter = new RecipeListAdapter(this);
        setListAdapter(mAdapter);

    }


    ////////////////////////// jes 6 ///////////////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0,1,Menu.NONE,"레시피 카테고리 보기").setIcon(R.drawable.recipe_category);

        return true;
    }
    ///////////////////////////////////////////////////////



    ///////////////////  jes7 ///////////////////////
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Toast.makeText(this, NetworkUtil.getConnectivityStatusString(this), Toast.LENGTH_SHORT).show();
        if(NetworkUtil.getConnectivityStatus(this)==NetworkUtil.TYPE_WIFI){
            final Handler handler=new Handler();
            Thread xmlThread=new Thread(){
                @Override
                public void run() {
                    try {
                        MyParser myParser=new RecipeCategoryFromMyServer();//==>8
                        final List<String> items= (List<String>) NetworkUtil.sendRequestGet(NetworkUtil.ServerURL+"/recipeCategory", myParser);//==>9
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                appendItemsToList(items);//==>10
                            }
                        });
                    } catch (Exception e) {
                        Log.e("jes",NetworkUtil.ServerURL+"로 레시피 가져오기 실패");
                        e.printStackTrace();
                    }
                }
            };
            xmlThread.start();

        }
        return super.onOptionsItemSelected(item);
    }



    //////////////////// jes10 /////////////////////////////
    private void appendItemsToList(List<String> items){

        AlertDialog.Builder alertBuilder=new AlertDialog.Builder(this);
        alertBuilder.setIcon(R.drawable.recipe_category);
        alertBuilder.setTitle("항목중에 하나를 선택하세요");

        final ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.select_dialog_singlechoice);
        for(String item:items){
            adapter.add(item);
        }

        alertBuilder.setAdapter(adapter,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, final int categoryNo) {
                Toast.makeText(getApplicationContext(),categoryNo+"",Toast.LENGTH_SHORT).show();
                final Handler handler=new Handler();
                Thread xmlThread=new Thread(){
                    @Override
                    public void run() {
                        try {
                            MyParser myParser=new RecipeListFromMyServer();
                            final List<Item> items= (List<Item>) NetworkUtil.sendRequestGet(NetworkUtil.ServerURL+"/recipe/list/"+categoryNo, myParser);
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    mAdapter.appendItemsToListAfterClear(items);
                                }
                            });
                        } catch (Exception e) {
                            Log.e("jes",NetworkUtil.ServerURL+"로 레시피 가져오기 실패");
                            e.printStackTrace();
                        }
                    }
                };
                xmlThread.start();
            }
        });

        alertBuilder.setNegativeButton("취소",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });


        alertBuilder.show();
    }

}


