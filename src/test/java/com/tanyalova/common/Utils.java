package com.tanyalova.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.time.Instant;

public class Utils {
    public static final Logger LOGGER = LoggerFactory.getLogger(Utils.class);

    public static long calculateTime(Instant startTime) {
        LOGGER.info("Calculate time. Start: " + startTime);
        return Instant.now().toEpochMilli() - startTime.toEpochMilli();
    }


    public static void sleep(long sec) {
        LOGGER.info("Sleep for " + sec + " seconds");
        try {
            Thread.sleep(1000 * sec);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] readFileToBytes(@Nonnull File file) {
        try {
            return readInputStreamToBytes(Files.newInputStream(file.toPath()));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return null;
    }

    public static byte[] readInputStreamToBytes(@Nonnull InputStream is) throws IOException {
        return readInputStreamToBytes(is, 80);
    }

    public static byte[] readInputStreamToBytes(@Nonnull InputStream is, int bufferSize) throws IOException {
        ReadableByteChannel channel = Channels.newChannel(is);
        ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        int read;
        while ((read = channel.read(buffer)) > 0) {
            baos.write(buffer.array(), 0, read);
            buffer.clear();
        }

        return baos.toByteArray();
    }
}
