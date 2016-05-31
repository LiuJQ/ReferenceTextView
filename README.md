# ReferenceTextView
This is a custom TextView allow developers to realize a reply content with referenced User infomation, which is supported collapse and expand function.

## How does it look like ?
![](https://github.com/LiuJQ/ReferenceTextView/blob/master/screenshot.png)

## How to use ?
Checkout this repository or download the zip(extract it and open with [Android Studio](https://developer.android.com/studio/index.html)),
reference the [ReferenceTextView](https://github.com/LiuJQ/ReferenceTextView) library, then you can use it in your modules like below:
```xml
<com.android.jackin.library.ReferenceTextView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="@string/test_long_content"
    app:colorClickableText="@color/accent"/>

<com.android.jackin.library.ReferenceTextView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:layout_marginTop="20dp"
    android:text="@string/test_long_content"
    app:colorClickableText="@color/accent"
    app:referenceColor="@color/referenceColor"/>
```

## Thanks
* [ReadMoreTextView](https://github.com/borjabravo10/ReadMoreTextView)
* [Android Studio](https://developer.android.com/studio/index.html)

## Others
Please feel free to improve this library, or submit your feedback on Github.
