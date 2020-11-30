package top.jfunc.common.http.exe.apache;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.CloseableHttpClient;
import top.jfunc.common.http.component.HeaderExtractor;
import top.jfunc.common.http.component.StreamExtractor;
import top.jfunc.common.http.exe.BaseClientHttpResponse;
import top.jfunc.common.http.response.ClientHttpResponse;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.http.util.ApacheUtil;
import top.jfunc.common.utils.IoUtil;

import java.io.IOException;

/**
 * {@link ClientHttpResponse} implementation that uses Apache facilities.
 *
 * @author xiongshiyan
 */
public class ApacheClientHttpResponse extends BaseClientHttpResponse<HttpResponse> {

    private HttpClient httpClient;

	public ApacheClientHttpResponse(HttpClient httpClient, HttpResponse httpResponse, HttpRequest httpRequest, StreamExtractor<HttpResponse> streamExtractor, HeaderExtractor<HttpResponse> headerExtractor) {
		super(httpResponse, httpRequest, streamExtractor, headerExtractor);
		this.httpClient = httpClient;
	}


	@Override
	public int getStatusCode() throws IOException {
		return response.getStatusLine().getStatusCode() ;
	}

	@Override
	public String getStatusText() throws IOException {
		return response.getStatusLine().getReasonPhrase() ;
	}

	@Override
	public void close() {
		IoUtil.close(responseStream);
		ApacheUtil.closeQuietly(response);
        closeHttpClient(httpClient);
	}

    protected void closeHttpClient(HttpClient httpClient) {
        if(httpClient instanceof CloseableHttpClient){
            IoUtil.close((CloseableHttpClient) httpClient);
        }
    }
}
