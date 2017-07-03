package com.github.tinkerti.rxjavasample;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.github.tinkerti.rxjavasample.utils.FileUtils;

import java.io.File;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    Context context;
    LinearLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        container = (LinearLayout) findViewById(R.id.ac_ll_image_view_container);

        String dirPath = FileUtils.getAttachDownloadDir(this);
        File dirFile = new File(dirPath);
        File[] fileArray = new File[]{dirFile};

        Observable.fromArray(fileArray)
                .flatMap(new Function<File, ObservableSource<File>>() {
                    @Override
                    public ObservableSource<File> apply(@NonNull File file) throws Exception {
                        return Observable.fromArray(file.listFiles()); //记得在manifest中添加对应的权限，否则这个方法会返回空值；
                    }
                })
                .filter(new Predicate<File>() {
                    @Override
                    public boolean test(@NonNull File file) throws Exception {
                        return file.getName().endsWith(".jpg");
                    }
                })
                .map(new Function<File, Bitmap>() {
                    @Override
                    public Bitmap apply(@NonNull File file) throws Exception {
                        return FileUtils.decodeBitmapFromFile(file.getPath(), 60, 60);
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Bitmap>() {
                    @Override
                    public void accept(@NonNull Bitmap bitmap) throws Exception {
                        ImageView imageView = new ImageView(context);
                        imageView.setImageBitmap(bitmap);
                        container.addView(imageView);
                    }
                });
    }
}
