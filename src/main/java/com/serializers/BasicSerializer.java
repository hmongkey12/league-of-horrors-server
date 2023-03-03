package com.serializers;

import java.io.*;

public class BasicSerializer implements Serializer{
    @Override
    public Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        Object object = objectInputStream.readObject();
        objectInputStream.close();
        byteArrayInputStream.close();
        return object;
    }

    @Override
    public byte[] serialize(Object object) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(object);
        objectOutputStream.close();
        byteArrayOutputStream.close();
        byte[] data = byteArrayOutputStream.toByteArray();
        return data;
    }
}
