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

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

/**
 * TODO
 * 
 * @author aectann@gmail.com (Konstantin Burov)
 * 
 */
public interface ImageMapResourcesCache {

  Path[] getAreaPaths(Context context, MapResource resource) throws IOException, XmlPullParserException;

  int getDataId(Context context, MapResource resource, Integer pathIndex);

  int getAreaId(Context context, MapResource resource, Integer dataId,
                Integer target);

  int getAreaId(Context context, MapResource resource, Integer dataId);

  ArrayList<Integer> getAreaGroups(Context context, MapResource resource,
                                   Integer dataId);
}
