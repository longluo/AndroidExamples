package com.sureshjoshi.android.streamingprotobufexample.utils;

import com.squareup.wire.Message;
import com.squareup.wire.ProtoAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

public class WireUtils {

    public static void writeDelimitedTo(OutputStream outputStream, List<Message> messages) throws IOException {
        for (Message message : messages) {
            writeDelimitedTo(outputStream, message);
        }
    }

    public static void writeDelimitedTo(OutputStream outputStream, Message message) throws IOException {
        int size = message.adapter().encodedSize(message);
        BufferedSink sink = Okio.buffer(Okio.sink(outputStream));
        sink.writeIntLe(size);
        message.encode(sink);
        sink.emit();
    }

    public static <M extends Message> List<M> readDelimitedFrom(InputStream inputStream, ProtoAdapter<M> adapter) throws IOException {
        List<M> messages = new ArrayList<>();
        BufferedSource source = Okio.buffer(Okio.source(inputStream));
        while (!source.exhausted()) {
            int size = source.readIntLe();
            byte[] bytes = source.readByteArray(size);
            messages.add(adapter.decode(bytes));
        }
        return messages;
    }

    public static void writeCobsEncodedTo(OutputStream outputStream, List<Message> messages) throws IOException {
        for (Message message : messages) {
            writeCobsEncodedTo(outputStream, message);
        }
    }

    public static void writeCobsEncodedTo(OutputStream outputStream, Message message) throws IOException {
        BufferedSink sink = Okio.buffer(Okio.sink(outputStream));
        sink.write(CobsUtils.encode(message.encode()));
        sink.emit();
    }

    public static <M extends Message> List<M> readCobsEncodedFrom(InputStream inputStream, ProtoAdapter<M> adapter) throws IOException {
        List<M> messages = new ArrayList<>();
        BufferedSource source = Okio.buffer(Okio.source(inputStream));
        while (!source.exhausted()) {
            long length = source.indexOf((byte) 0);
            byte[] decodedBytes = CobsUtils.decode(source.readByteArray(length + 1));
            messages.add(adapter.decode(decodedBytes));
        }
        return messages;
    }

}
