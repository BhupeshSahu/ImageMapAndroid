package com.android.imagemap;

import android.content.Context;
import android.view.View;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.StringReader;

/**
 * Created by Android on 22-10-2018.
 */

public class MapResource {

    private ResourceType resourceType = null;
    private int resourceId = View.NO_ID;
    private String url = "";
    private String rawString = "";


    public MapResource(int resourceId) {
        this.resourceType = ResourceType.INTERNAL_RESOURCE;
        this.resourceId = resourceId;
    }

    public MapResource(ResourceType resourceType, String resValue) {
        this.resourceType = resourceType;
        if (resourceType == ResourceType.LOCAL_FILE)
            this.url = resValue;
        else if (resourceType == ResourceType.RAW_STRING)
            this.rawString = resValue;
    }

    public MapResource(String rawString) {
        resourceType = ResourceType.RAW_STRING;
        this.rawString = rawString;
    }


    XmlPullParser getParser(Context context) throws XmlPullParserException, FileNotFoundException {
        switch (resourceType) {
            case INTERNAL_RESOURCE:
                if (resourceId == 0)
                    throw new IllegalStateException("please supply valid resource for map");
                return context.getResources().getXml(resourceId);
            case LOCAL_FILE:
                if (url == null || url.trim().isEmpty())
                    throw new IllegalStateException("please supply valid resource for map");
                File file = new File(url);
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput(new FileInputStream(file), "UTF-8");
                return xpp;
            case RAW_STRING:
                if (rawString == null || rawString.trim().isEmpty())
                    throw new IllegalStateException("please supply valid resource for map");
                factory = XmlPullParserFactory.newInstance();
                xpp = factory.newPullParser();
                xpp.setInput(new StringReader(rawString));
                return xpp;
        }
        return null;
    }
}
