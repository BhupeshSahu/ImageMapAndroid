/*
 * Copyright (C) 2014 Socratica LLC.
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
package com.android.imagemap;

import android.content.Context;
import android.graphics.Path;
import android.util.SparseArray;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

/**
 * TODO
 *
 * @author aectann@gmail.com (Konstantin Burov)
 */
public class SimpleResourceCache implements ImageMapResourcesCache {

    private Object dataIds;
    private Object paths;
    private MapParser mapParser;
    private SparseArray<ArrayList<Integer>> areaGroups;

    SimpleResourceCache(MapParser mapParser) {
        dataIds = null;
        paths = null;
        areaGroups = null;
        this.mapParser = mapParser;
    }

    @Override
    public synchronized Path[] getAreaPaths(Context context, MapResource mapResource) {
        if (dataIds != null) {
            return (Path[]) paths;
        }
        try {
            init(context, mapResource);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException("Failed to init image map areas", e);
        }
        notify();
        return (Path[]) paths;
    }

    public synchronized void init(Context context, MapResource mapResource)
            throws XmlPullParserException, IOException {
        if (dataIds == null) {
            ArrayList<MapParser.Area> areas = mapParser.parseAreas(context, mapResource);
            int size = areas.size();
            int[][] areaIds = new int[size][2];
            Path[] areaPaths = new Path[size];
            SparseArray<ArrayList<Integer>> groupsByData = new SparseArray<ArrayList<Integer>>();
            int i = 0;
            for (MapParser.Area a : areas) {
                areaIds[i][0] = a.id;
                areaIds[i][1] = a.target;
                areaPaths[i] = a.path;
                if (groupsByData.indexOfKey(a.id) < 0) {
                    groupsByData.put(a.id, new ArrayList<Integer>());
                }
                groupsByData.get(a.id).add(a.target);
                i++;
            }
            dataIds = areaIds;
            paths = areaPaths;
            areaGroups = groupsByData;
        }
    }

    @Override
    public int getDataId(Context context, MapResource resource, Integer pathIndex) {
        if (dataIds == null) {
            try {
                init(context, resource);
            } catch (Exception e) {
                throw new IllegalStateException("Failed to init image map areas", e);
            }
        }
        return ((int[][]) dataIds)[pathIndex][0];
    }


    @Override
    public int getAreaId(Context context, MapResource mapResource, Integer dataId,
                         Integer target) {
        if (dataIds == null) {
            try {
                init(context, mapResource);
            } catch (Exception e) {
                throw new IllegalStateException("Failed to init image map areas", e);
            }
        }
        int[][] ids = (int[][]) this.dataIds;
        for (int i = 0; i < ids.length; i++) {
            if (dataId == ids[i][0] && (target == -1 || target == ids[i][1])) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int getAreaId(Context context, MapResource mapResource, Integer id) {
        return getAreaId(context, mapResource, id, -1);
    }

    @Override
    public ArrayList<Integer> getAreaGroups(Context context, MapResource mapResource, Integer dataId) {
        if (areaGroups == null) {
            try {
                init(context, mapResource);
            } catch (Exception e) {
                throw new IllegalStateException("Failed to init image map areas", e);
            }
        }
        return areaGroups.get(dataId);
    }
}
