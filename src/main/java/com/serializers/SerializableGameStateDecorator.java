package com.serializers;

import java.io.IOException;

public class SerializableGameStateDecorator implements Serializer {
    Serializer serializer;

    public SerializableGameStateDecorator(Serializer serializer) {
       this.serializer = serializer;
    }

    @Override
    public Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
        Object desializedObject = serializer.deserialize(data);
        if (desializedObject instanceof SerializableGameState) {
            return desializedObject;
        }
        return null;
    }

    @Override
    public byte[] serialize(Object object) throws IOException {
        return serializer.serialize(object);
    }
}
