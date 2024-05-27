package fintech.mock;

import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

public class MockResponseBody  extends ResponseBody {

    private final Buffer buffer;
    private final String contentType;

    public MockResponseBody(String content, String contentType) {
        this.buffer = new Buffer().writeUtf8(content);
        this.contentType = contentType;
    }

    @Override
    public okhttp3.MediaType contentType() {
        return contentType == null ? null : okhttp3.MediaType.parse(contentType);
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
