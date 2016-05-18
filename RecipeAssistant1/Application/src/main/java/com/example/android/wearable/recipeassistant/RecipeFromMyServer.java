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
public class RecipeFromMyServer implements MyParser {

    @Override
    public List<Recipe.RecipeStep> parse(Object...o) throws Exception {
        InputStream in = (InputStream) o[0];
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(in);

        XPath xPath = XPathFactory.newInstance().newXPath();

        String expression="/recipe/step";

        NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(document, XPathConstants.NODESET);

        Log.i("jes", "item갯수" + nodeList.getLength());

        Log.i("jes===","Recipe 파싱 시작");
        ArrayList<Recipe.RecipeStep> list = new ArrayList<Recipe.RecipeStep>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element ele = (Element) nodeList.item(i);
            Element stepTextE = (Element) ele.getElementsByTagName("stepText").item(0);
            Element stepImageE = (Element) ele.getElementsByTagName("stepImage").item(0);

            Recipe.RecipeStep recipeStep = new Recipe.RecipeStep();


            if (stepTextE != null) {
                Node firstChild = stepTextE.getFirstChild();
                if (firstChild != null) {
                    recipeStep.stepText = firstChild.getNodeValue();
                    Log.i("jes", "item " + i + " recipeStep.stepText=" + recipeStep.stepText);
                }
            }
            if (stepImageE != null) {
                Node firstChild = stepImageE.getFirstChild();
                if (firstChild != null) {
                    recipeStep.stepImage = firstChild.getNodeValue();
                    Log.i("jes", "item " + i + " recipeStep.stepImage=" + recipeStep.stepImage);
                }
            }


            list.add(recipeStep);
        }
        return list;


    }
////////////////////////////////////////////////////////////////////////////////



}
