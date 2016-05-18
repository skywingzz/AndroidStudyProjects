package com.example.android.wearable.recipeassistant;

import android.content.Context;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

/**
 * Created by javan_000 on 2015-07-15.
 */

////////////////////jes4 ///////////////////////
public class RecipeFromOpenApi implements MyParser {
    @Override
    ////////////////////////// jes3////////////////////////////////////
    public List<? extends Object> parse(Object...o) throws Exception {
        InputStream in= (InputStream) o[0];
        Context mContext= (Context) o[1];
        DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
        DocumentBuilder builder=factory.newDocumentBuilder();
        Document document=builder.parse(in);
        Element root=  document.getDocumentElement();
        XPath xPath =  XPathFactory.newInstance().newXPath();

        // String expression = "/Envelope/Body/getRecipeListResponse/RecipeListResponse/Items";
        String expression = "/Envelope/Body/getRecipeListResponse/RecipeListResponse/items";

        NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(document, XPathConstants.NODESET);

        Log.i("jes", "node갯수" + nodeList.getLength());
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
}
