# ImageMapAndroid 

Credit to [Konstantin Burov](https://github.com/aectann)

Its an extension to what he has created github project [socratica-android](https://github.com/aectann/socratica-android)

Add following gradle dependency to your project's `build.gradle`
```
implementation 'com.github.BhupeshSahu.ImageMapAndroid:imagemap:v1.1'
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
