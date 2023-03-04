package com.example.udpserver.shared;

import org.springframework.beans.factory.annotation.Value;

import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class DatagramCompressor {

    @Value("${DATAGRAMPACKET_SIZE}")
    private static int DATAGRAM_PACKET_SIZE;
    public static byte[] compress(byte[] data) {
        byte[] output = new byte[DATAGRAM_PACKET_SIZE];
        Deflater compresser = new Deflater();
        compresser.setInput(data);
        compresser.finish();
        compresser.deflate(output);
        return output;
    }

    public static byte[] decompress(byte[] data) throws DataFormatException {
        byte[] output = new byte[DATAGRAM_PACKET_SIZE];
        Inflater decompresser = new Inflater();
        decompresser.setInput(data, 0, data.length);
        decompresser.inflate(output);
        decompresser.end();
        return output;
    }
}
