package com.serializers;

import java.io.IOException;

public interface Serializer {
   Object deserialize(byte[] data) throws IOException, ClassNotFoundException;
   byte[] serialize(Object object) throws IOException;
}
