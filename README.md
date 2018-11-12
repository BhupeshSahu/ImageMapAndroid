# ImageMapAndroid

Credit to [Konstantin Burov](https://github.com/aectann)

Its an extension to what he has created as github project [socratica-android](https://github.com/aectann/socratica-android)

The main purpose to make further modification in this project is to add ability to add image source and map xml resource dynamically. This will benifit those who are looking to download image and map from remote server then show it in this view. And to control zooming behaviour of image.

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
Where 
`src` is the source image which will be map

`app:map` is an html imagemap file. You can learn to create it [here](https://www.w3schools.com/tags/tag_map.asp)

`selectionPadding` is padding applied for filling the color in area

`seletionType` can be `stoke/fill`

`selectionColor` is color for highlighting the selected area

`selectionStrokeWidth` is width of selection if `selectionType` is `stroke`

There are plenty of applications to create imagemap xml file by selecting regions in image. An open source application is [GIMP](https://www.gimp.org/downloads/)

**Usage**

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

1. Added double tap zoom instead of single tap
2. To reset image to initial zoom level by calling `map.resetToOverviewMode()`. It returns true if reset is actually performed, false otherwise.
Here's how we can do that :
```java
    @Override
    public void onBackPressed() {
        if (!map.resetToOverviewMode())
            super.onBackPressed();
    }
```

***Release Notes***

**v1.2**

Add images dynamically
Note : This view only support image with actual resolution. Down sampled images not supported for now. Although it will take care of memory utilisation of bitmap.

```java
map.setImageFile(urlMap, bmpDrawable);
```
Picasso can be used to get image in required resolution using following code:

```java
    private Target target = new Target() {

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            Drawable bmpDrawable = new BitmapDrawable(getResources(), bitmap);
            map.setImageFile(urlMap, bmpDrawable);
        }

        @Override
        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
            
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };
    
    Picasso.get().load(urlImage).into(target);
```

Where `urlImage` is remote url image
