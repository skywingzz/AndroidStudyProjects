package com.example.android.wearable.recipeassistant;

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

///////////////////////// jes8 //////////////////////////////
public class RecipeCategoryFromMyServer implements MyParser {

    @Override
    public List<String> parse(Object...o) throws Exception {
        InputStream in = (InputStream) o[0];
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(in);
        Element root = document.getDocumentElement();
        XPath xPath = XPathFactory.newInstance().newXPath();

        String expression = "/categoryItems/item/categoryName";

        NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(document, XPathConstants.NODESET);

        Log.i("jes", "item갯수" + nodeList.getLength());


            ArrayList<String> list = new ArrayList<String>();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Element ele = (Element) nodeList.item(i);

                if (ele != null) {
                    Node firstChild = ele.getFirstChild();
                    if (firstChild != null) {
                        String categoryName = firstChild.getNodeValue();
                        list.add(categoryName);
                        Log.i("jes", "item" + i + "title=" + categoryName);
                    }
                }
            }

            Log.i("jes", "list.size()=" + list.size());
            return list;


    }
////////////////////////////////////////////////////////////////////////////////



}
