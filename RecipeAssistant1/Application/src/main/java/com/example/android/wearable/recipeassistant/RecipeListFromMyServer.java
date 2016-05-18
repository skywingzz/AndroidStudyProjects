package com.example.android.wearable.recipeassistant;

import android.graphics.BitmapFactory;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedInputStream;
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

///////////////////////// jes8 //////////////////////////////
public class RecipeListFromMyServer implements MyParser {

    @Override
    public List<Item> parse(Object...o) throws Exception {
        InputStream in = (InputStream) o[0];
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(in);

        XPath xPath = XPathFactory.newInstance().newXPath();

        String expression = "/recipeItems/item";

        NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(document, XPathConstants.NODESET);

        Log.i("jes", "item갯수" + nodeList.getLength());

        Log.i("jes===","ITEM 파싱 시작");
        ArrayList<Item> list = new ArrayList<Item>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element ele = (Element) nodeList.item(i);
            Element idxE = (Element) ele.getElementsByTagName("idx").item(0);
            Element titleE = (Element) ele.getElementsByTagName("Title").item(0);
            Element imageE = (Element) ele.getElementsByTagName("Image").item(0);
            Element summaryE = (Element) ele.getElementsByTagName("summary").item(0);
            Element ingredientsTextE = (Element) ele.getElementsByTagName("ingredientsText").item(0);

            Item item = new Item();

            if (idxE != null) {
                Node firstChild = idxE.getFirstChild();
                if (firstChild != null) {
                    item.name = firstChild.getNodeValue();
                    Log.i("jes", "item " + i + " name=" + item.name);
                }
            }
            if (titleE != null) {
                Node firstChild = titleE.getFirstChild();
                if (firstChild != null) {
                    item.title = firstChild.getNodeValue();
                    Log.i("jes", "item " + i + " title=" + item.title);
                }
            }
            if (summaryE != null) {
                Node firstChild = summaryE.getFirstChild();
                if (firstChild != null) {
                    item.summary = firstChild.getNodeValue();
                    Log.i("jes", "item " + i + " summary=" + item.summary);
                }
            }
            if (ingredientsTextE != null) {
                Node firstChild = ingredientsTextE.getFirstChild();
                if (firstChild != null) {
                    item.ingredientsText = firstChild.getNodeValue();
                    Log.i("jes", "item " + i + " ingredientsText=" + item.ingredientsText);
                }
            }
            if (imageE != null) {
                Node firstChild = imageE.getFirstChild();
                if (firstChild != null) {
                    String imgUrl = firstChild.getNodeValue();
                    Log.i("jes", "item" + i + "imgUrl=" + imgUrl);

                    imgUrl=NetworkUtil.ServerURL+"/img/" + imgUrl;

                    BufferedInputStream bis=NetworkUtil.sendRequestGetImg(imgUrl);

                    item.image = BitmapFactory.decodeStream(bis);
                    //String base64String=firstChild.getNodeValue();
                    //byte[] imageAsBytes = Base64.decode(base64String.getBytes(), Base64.DEFAULT);
                    //item.image=BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);

                }
            }
            if (item.name == null) item.name = "NAME";
            if (item.summary == null) item.summary = "SUMMARY";

            list.add(item);
            }
            return list;


    }
////////////////////////////////////////////////////////////////////////////////



}
