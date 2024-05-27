package fintech.api;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

public final class TestResponseBody extends ResponseBody {
    private final Buffer buffer;
    private final String contentType;

    public TestResponseBody(String content, String contentType) {
        this.buffer = new Buffer().writeUtf8(content);
        this.contentType = contentType;
    }

    @Override
    public MediaType contentType() {
        return contentType == null ? null : MediaType.parse(contentType);
    }

    @Override
    public long contentLength() {
        return buffer.size();
    }

    @Override
    public BufferedSource source() {
        return buffer.clone();
    }
}
