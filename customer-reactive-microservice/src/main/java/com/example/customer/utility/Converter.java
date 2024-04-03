package com.example.customer.utility;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class Converter {

    public static ByteBuffer convertStringToByteBuffer(String inputString) {
        DefaultDataBufferFactory dataBufferFactory = new DefaultDataBufferFactory();
        byte[] bytes = inputString.getBytes(StandardCharsets.UTF_8); // Convert String to byte array
        DataBuffer dataBuffer = dataBufferFactory.wrap(bytes); // Wrap byte array in a DataBuffer
        return dataBuffer.asByteBuffer();
    }

    public static String convertByteBufferToString(ByteBuffer buffer) {
        byte[] bytes = buffer.array(); // Get the byte array from the ByteBuffer
        return new String(bytes, StandardCharsets.UTF_8);
    }

}
