package kh.farrukh.progee_api.global.security.utils.request_wrapper;

import lombok.SneakyThrows;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class CachedBodyServletInputStream extends ServletInputStream {

    private InputStream cachedBodyInputStream;

    public CachedBodyServletInputStream(byte[] cachedBody) {
        this.cachedBodyInputStream = new ByteArrayInputStream(cachedBody);
    }

    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public int read() throws IOException {
        return cachedBodyInputStream.read();
    }

    @SneakyThrows
    @Override
    public boolean isFinished() {
        return cachedBodyInputStream.available() == 0;
    }

    @Override
    public void setReadListener(ReadListener listener) {
    }
}
