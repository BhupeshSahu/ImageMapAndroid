# ImageMapAndroid 

Credit to [Konstantin Burov](https://github.com/aectann)

Its an extension to what he has created as github project [socratica-android](https://github.com/aectann/socratica-android)

Add following gradle dependency in your project's `build.gradle`
```
implementation 'com.github.BhupeshSahu.ImageMapAndroid:imagemap:v1.1'
```  

Add following code in your `layout.xml` where you want to put `ImageMap`
```xml
    <com.android.imagemap.ImageMap
        android:id="@+id/map"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:src="@drawable/floormap"
        app:map="@xml/floor"
        app:selectionColor="#f00"
        app:selectionPadding="50dp"
        app:selectionStrokeWidth="2"
        app:selectionType="stroke" />
```
Where `src` is the source image which will be map and `app:map` is an html `imagemap` file you can learn to create it [here](https://www.w3schools.com/tags/tag_map.asp)

To get event callback for area click, add following code in your `Activity` file
```java
        ImageMap map = findViewById(R.id.map);
        map.setImageMapListener(new ImageMapListener() {
            @Override
            public void onAreaClicked(int i) {
                // Call this function to highlight selected area in map
                map.showArea(i);
                // i is the referenceId which you have provided in href in map file, make sure you provide only integer as href
                Toast.makeText(ImageMapActivity.this, "Area clicked " + i, Toast.LENGTH_SHORT).show();
            }
        });
```

**Changes**

1. Added double tap zoom instead of single tap
2. Can reset image to initial zoom level by calling `map.resetToOverviewMode()`. It returns true if reset is actually performed, false otherwise.
Here's how we can do that :
```kotlin
    override fun onBackPressed() {
        if (!map.resetToOverviewMode())
            super.onBackPressed()
    }
```
