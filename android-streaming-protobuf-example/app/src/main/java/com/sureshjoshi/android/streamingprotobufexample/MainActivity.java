package com.sureshjoshi.android.streamingprotobufexample;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.squareup.wire.Message;
import com.sureshjoshi.android.pb.Sample;
import com.sureshjoshi.android.streamingprotobufexample.utils.WireUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends Activity {

    int mRunningX = 0;
    static final String FILE_NAME = "samples.pbld";
    static final String FILE_NAME_COBS = "samples.pbcobs1";

    @Bind(R.id.chart)
    LineChart mChart;

    @OnClick(R.id.button_add_one)
    void addOneSample() {
        // Create one random entry
        try {
            Sample sample = new Sample.Builder()
                    .x(++mRunningX)
                    .y((int) (Math.random() * 1000))
                    .build();

            OutputStream outputStream = new BufferedOutputStream(openFileOutput(FILE_NAME, MODE_APPEND));
            WireUtils.writeDelimitedTo(outputStream, sample);
            outputStream.close();

            OutputStream outputStreamCobs = new BufferedOutputStream(openFileOutput(FILE_NAME_COBS, MODE_APPEND));
            WireUtils.writeCobsEncodedTo(outputStreamCobs, sample);
            outputStreamCobs.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        refreshChart();
    }

    @OnClick(R.id.button_add_ten)
    void addTenSamples() {
        // Create 10 random entries
        List<Message> samples = new ArrayList<>();
        for (int i = 0; i < 10; ++i) {
            samples.add(new Sample.Builder()
                    .x(++mRunningX)
                    .y((int) (Math.random() * 1000))
                    .build());
        }

        try {
            OutputStream outputStream = new BufferedOutputStream(openFileOutput(FILE_NAME, MODE_APPEND));
            WireUtils.writeDelimitedTo(outputStream, samples);
            outputStream.close();

//            OutputStream outputStreamCobs = new BufferedOutputStream(openFileOutput(FILE_NAME_COBS, MODE_APPEND));
//            WireUtils.writeCobsEncodedTo(outputStreamCobs, samples);
//            outputStreamCobs.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        refreshChart();
    }


    @OnClick(R.id.button_refresh)
    void refreshChart() {
        // Read in from the file, clear and remake chart
        List<Sample> samples = new ArrayList<>();
        try {
            InputStream inputStream = new BufferedInputStream(openFileInput(FILE_NAME));
            samples = WireUtils.readDelimitedFrom(inputStream, Sample.ADAPTER);
            inputStream.close();

//            InputStream inputStreamCobs = new BufferedInputStream(openFileInput(FILE_NAME_COBS));
//            samples = WireUtils.readCobsEncodedFrom(inputStreamCobs, Sample.ADAPTER);
//            inputStreamCobs.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Everything below here is specifically for MPAndroid

        // Setup samples to work with MPAndroid charting
        ArrayList<String> xVals = new ArrayList<>();
        ArrayList<Entry> yVals = new ArrayList<>();
        for (Sample sample : samples) {
            xVals.add((sample.x) + "");
            yVals.add(new Entry(sample.y, sample.x));
        }

        // Create a dataset and give it a type
        LineDataSet dataSet = new LineDataSet(yVals, "DataSet 1");
        dataSet.setLineWidth(1f);
        dataSet.setDrawCircles(false);
        dataSet.setValueTextSize(0f);
        dataSet.setColor(Color.RED);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataSet); // add the datasets

        // Create a data object with the datasets
        LineData data = new LineData(xVals, dataSets);

        // Set data
        mChart.setData(data);
        mChart.invalidate();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // Delete the file, if any, between onCreate calls.
        deleteFile(FILE_NAME);
        deleteFile(FILE_NAME_COBS);
    }
}