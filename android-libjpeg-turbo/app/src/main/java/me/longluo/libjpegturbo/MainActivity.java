package me.longluo.libjpegturbo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.libjpegturbo.turbojpeg.TJ;
import org.libjpegturbo.turbojpeg.TJCompressor;

import java.io.InputStream;
import java.nio.ByteBuffer;


public class MainActivity extends AppCompatActivity {

    private TextView mTvText;

    private ImageView mIvImage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mTvText = findViewById(R.id.sample_text);
        mIvImage = findViewById(R.id.sample_img);

        init();
    }

    private void init() {
        try {
            InputStream inputStream = getAssets().open("negative-space-sandpiper_rock_nature_beach.jpg");
            byte[] byteFile = new byte[inputStream.available()];
            inputStream.read(byteFile);
            inputStream.close();

            Bitmap bmp = BitmapFactory.decodeByteArray(byteFile, 0, byteFile.length);

            int size = bmp.getRowBytes() * bmp.getHeight();
            byte[] bytes = new byte[size];
            ByteBuffer buffer = ByteBuffer.wrap(bytes);
            bmp.copyPixelsToBuffer(buffer);
            bmp.recycle();

            TJCompressor tjc = new TJCompressor();
            tjc.setSourceImage(bytes, 0, 0, bmp.getWidth(), 0, bmp.getHeight(), TJ.PF_RGBX);
            tjc.setSubsamp(TJ.SAMP_444);
            tjc.setJPEGQuality(100);
            byte[] outputBytes = tjc.compress(0);

            Bitmap output = BitmapFactory.decodeByteArray(outputBytes, 0, tjc.getCompressedSize());
            tjc.close();

            mIvImage.setImageBitmap(output);
            mTvText.setText(String.format("%s %s %s", output.getWidth(), output.getHeight(), output.getRowBytes()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

