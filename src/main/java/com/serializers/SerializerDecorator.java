package com.serializers;

import java.io.IOException;

public class SerializerDecorator implements Serializer {
    Serializer serializer;
    public SerializerDecorator(Serializer serializer) {
        this.serializer = serializer;
    }

    @Override
    public Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
        return serializer.deserialize(data);
    }

    @Override
    public byte[] serialize(Object object) throws IOException {
        return serializer.serialize(object);
    }
}
