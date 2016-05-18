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

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

public class RecipeListAdapter2 implements ListAdapter {
    private String TAG = "RecipeListAdapter";

    private class Item {
        String title;
        String name;
        String summary;
        Bitmap image;
    }

    private List<Item> mItems = new ArrayList<Item>();
    private Context mContext;
    private DataSetObserver mObserver;

    public RecipeListAdapter2(Context context) {
        mContext = context;
        loadRecipeList();
    }


    Handler handler;

    private void loadRecipeList() {
        JSONObject jsonObject = AssetUtils.loadJSONAsset(mContext, Constants.RECIPE_LIST_FILE);
        if (jsonObject != null) {
            List<Item> items = parseJson(jsonObject);
            appendItemsToList(items);
        }

        ////////////////////////// jes 1 ///////////////////////

        Toast.makeText(mContext,NetworkUtil.getConnectivityStatusString(mContext),Toast.LENGTH_SHORT).show();
        if(NetworkUtil.getConnectivityStatus(mContext)==NetworkUtil.TYPE_WIFI){
            handler=new Handler();
            XmlThread xmlThread=new XmlThread();
            xmlThread.start();

        }
        ///////////////////////////////////////////////////////
    }

    /////////////////////////////////jes2 //////////////////////
    class XmlThread extends Thread{
        @Override
        public void run() {
            try {
                List<Item> items=loadRecipeListFromHttp();
                startHandler(items);

            } catch (Exception e) {
                Log.e("jes","http로 레시피 가져오기 실패");
                e.printStackTrace();

            }
        }
    }
    //////////////////////////////////////////////////////////////

    /////////////////////jes4 ///////////////////
    private void startHandler(final List<Item> items){
        handler.post(new Runnable() {
            @Override
            public void run() {
                appendItemsToList(items);
            }
        });
    }
    ///////////////////////////////////////////


    //////////////////// jes3 /////////////////////////////////
    private List<Item> loadRecipeListFromHttp() throws Exception{
        FileInputStream fin=null;
        ByteArrayOutputStream bout=null;
        OutputStream out=null;
        InputStream in=null;

        try {
            String SOAPUrl = "http://openapi.foodsafety.go.kr/webservice/soap/RecipeService";
            String xmlFile2Send = "soapRequest.xml";
            String SOAPAction = "";

            // Create the connection where we're going to send the file.
            URL url = new URL(SOAPUrl);
            URLConnection connection = url.openConnection();
            HttpURLConnection httpConn = (HttpURLConnection) connection;

 /*         에셋을 사용하므로 수정함...
            fin = new FileInputStream(xmlFile2Send);
            bout = new ByteArrayOutputStream();

            // Copy the SOAP file to the open connection.
            copy(ais, bout);

            byte[] b = bout.toByteArray();
*/
            byte[] b =AssetUtils.loadAsset(mContext,xmlFile2Send);

            // Set the appropriate HTTP parameters.
            httpConn.setRequestProperty("Content-Length", String.valueOf(b.length));
            httpConn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
            httpConn.setRequestProperty("SOAPAction", SOAPAction);
            httpConn.setRequestMethod("POST");
            httpConn.setDoOutput(true);
            httpConn.setDoInput(true);

            // Everything's set up; send the XML that was read in to b.
            out = httpConn.getOutputStream();
            out.write(b);


            // Read the response and write it to standard out.
            int resCode = httpConn.getResponseCode();
            Log.d("jes", "웹응답:" + resCode);
            if (resCode == HttpURLConnection.HTTP_OK) {
                in = httpConn.getInputStream();
                return parseXML(in);

            }
            return null;
        }finally {
            if(fin!=null) fin.close();
            if(bout!=null) bout.close();
            if(out !=null) out.close();
            if(in !=null) in.close();
        }
    }

    private  void copy(InputStream in, OutputStream out) throws IOException {
        synchronized (in) {
            synchronized (out) {
                byte[] buffer = new byte[256];
                while (true) {
                    int bytesRead = in.read(buffer);
                    if (bytesRead == -1)
                        break;
                    out.write(buffer, 0, bytesRead);
                }
            }
        }
    }

    private List<Item> parseXML(InputStream in) throws Exception {
        DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
        DocumentBuilder builder=factory.newDocumentBuilder();
        Document document=builder.parse(in);
        Element root=  document.getDocumentElement();
        XPath xPath =  XPathFactory.newInstance().newXPath();

       // String expression = "/Envelope/Body/getRecipeListResponse/RecipeListResponse/Items";
        String expression = "/Envelope/Body/getRecipeListResponse/RecipeListResponse/items";

        NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(document, XPathConstants.NODESET);

      //  Log.i("jes", "root:" + root.getTagName());
     //   NodeList nodeList=root.getElementsByTagName("Items");
        Log.i("jes","root:sub갯수"+nodeList.getLength());
        ArrayList<Item> list=new ArrayList<Item>();


        for(int i=0;i<nodeList.getLength();i++){
            Element ele= (Element) nodeList.item(i);
            Element titleE = (Element) ele.getElementsByTagName("Title").item(0);
            Element imageE = (Element) ele.getElementsByTagName("Image").item(0);

            Item item=new Item();

            if(titleE !=null){
                Node firstChild=titleE.getFirstChild();
                if(firstChild !=null){
                    item.title=firstChild.getNodeValue();
                    Log.i("jes","item"+i+"title="+item.title);
                }
            }
            if(imageE !=null){
                Node firstChild=imageE.getFirstChild();
                if(firstChild !=null){
                    String imgUrl=firstChild.getNodeValue();
                    Log.i("jes","item"+i+"imgUrl="+imgUrl);
                    String imageFile="";
                    if(imgUrl.contains("/UserFiles/searching/recipe/016100.jpg")) {
                         imageFile="galbitang.jpg";

                    }else if(imgUrl.contains("/UserFiles/searching/recipe/013700.jpg")) {
                         imageFile="gamjatang.jpg";

                    }else if(imgUrl.contains("/UserFiles/searching/recipe/015500.jpg")) {
                         imageFile="gomtang.jpg";

                    }
                    item.image = AssetUtils.loadBitmapAsset(mContext, imageFile);
                    //String base64String=firstChild.getNodeValue();
                    //byte[] imageAsBytes = Base64.decode(base64String.getBytes(), Base64.DEFAULT);
                    //item.image=BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);

                }
            }
            if(item.name==null) item.name="NAME";
            if(item.summary==null) item.summary="SUMMARY";

            list.add(item);
        }
        Log.i("jes","list.size()="+list.size());
        return list;
    }
////////////////////////////////////////////////////////////////////////////////

    private List<Item> parseJson(JSONObject json) {
        List<Item> result = new ArrayList<Item>();
        try {
            JSONArray items = json.getJSONArray(Constants.RECIPE_FIELD_LIST);
            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);
                Item parsed = new Item();
                parsed.name = item.getString(Constants.RECIPE_FIELD_NAME);
                parsed.title = item.getString(Constants.RECIPE_FIELD_TITLE);
                if (item.has(Constants.RECIPE_FIELD_IMAGE)) {
                    String imageFile = item.getString(Constants.RECIPE_FIELD_IMAGE);
                    parsed.image = AssetUtils.loadBitmapAsset(mContext, imageFile);
                }
                parsed.summary = item.getString(Constants.RECIPE_FIELD_SUMMARY);
                result.add(parsed);
            }
        } catch (JSONException e) {
            Log.e(TAG, "Failed to parse recipe list: " + e);
        }
        return result;
    }

    private void appendItemsToList(List<Item> items) {
        mItems.addAll(items);
        if (mObserver != null) {
            mObserver.onChanged();
        }
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inf = LayoutInflater.from(mContext);
            view = inf.inflate(R.layout.list_item, null);
        }
        Item item = (Item) getItem(position);
        TextView titleView = (TextView) view.findViewById(R.id.textTitle);
        TextView summaryView = (TextView) view.findViewById(R.id.textSummary);
        ImageView iv = (ImageView) view.findViewById(R.id.imageView);

        titleView.setText(item.title);
        summaryView.setText(item.summary);
        if (item.image != null) {
            iv.setImageBitmap(item.image);
        } else {
            iv.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_noimage));
        }
        return view;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return mItems.isEmpty();
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        mObserver = observer;
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        mObserver = null;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    public String getItemName(int position) {
        return mItems.get(position).name;
    }
}
