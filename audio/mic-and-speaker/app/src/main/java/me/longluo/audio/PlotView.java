package me.longluo.audio;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;


public class PlotView extends View {

    private final Paint paint;
    private final Path path;
    private float[] buffer;
    private int channelCount;
    private int channel; // 0, 1, ...
    private int color;

    public PlotView(Context context) {
        super(context);
        setWillNotDraw(false);
        paint = new Paint();
        color = 0xFF000000; // default
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setAntiAlias(true);
        path = new Path();
    }

    public void setBuffer(float[] buffer) {
        this.buffer = buffer;
    }

    public void setNumChannels(int channelCount) {
        this.channelCount = channelCount;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setLineWidth(float width) {
        paint.setStrokeWidth(width);
    }

    @Override
    public void onDraw(Canvas cnv) {

        int w = getWidth();
        int h = getHeight();

        paintFill(true);
        paint.setTextSize(20);
        paint.setColor(color);

        if (buffer == null) {
            cnv.drawText("buffer == null", w / 2.0f, h / 2.0f, paint);
            return;
        }

        float x;
        float y;

        path.reset();
        paintFill(false);

        for (int i = 0; i < buffer.length; i += channelCount) {
            x = (((float) (i)) * w) / buffer.length;
            y = (buffer[i + channel] + 1) / 2.0f; // 0 .. 1
            y = h - y * h;
            if (x < 0) {
                x = 0;
            }
            if (x >= w) {
                x = w - 1;
            }
            if (y < 0) {
                y = 0;
            }
            if (y >= h) {
                y = h - 1;
            }

            if (i == 0) {
                path.moveTo(x, y);
            } else if (i == buffer.length - 1) {
                path.setLastPoint(x, y);
            } else {
                path.lineTo(x, y);
            }
        }

        cnv.drawPath(path, paint);
    }

    public void paintFill(boolean fill) {
        if (fill) {
            paint.setStyle(Paint.Style.FILL);
        } else {
            paint.setStyle(Paint.Style.STROKE);
        }
    }
}