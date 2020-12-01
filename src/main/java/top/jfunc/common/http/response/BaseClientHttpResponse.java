package top.jfunc.common.http.response;

import top.jfunc.common.http.component.HeaderExtractor;
import top.jfunc.common.http.component.StreamExtractor;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.utils.MultiValueMap;

import java.io.IOException;
import java.io.InputStream;

/**
 * RR 代表能从中获取header、InputStream的响应
 * @author xiongshiyan
 */
public abstract class BaseClientHttpResponse<RR> implements ClientHttpResponse {
    private StreamExtractor<RR> responseStreamExtractor;
    private HeaderExtractor<RR> responseHeaderExtractor;

    protected RR response;
	protected HttpRequest httpRequest;

	protected MultiValueMap<String, String> headers;

	protected InputStream responseStream;


	public BaseClientHttpResponse(RR response, HttpRequest httpRequest, StreamExtractor<RR> streamExtractor, HeaderExtractor<RR> headerExtractor) {
		this.response = response;
		this.httpRequest = httpRequest;
        setResponseStreamExtractor(streamExtractor);
        setResponseHeaderExtractor(headerExtractor);
	}

	@Override
	public MultiValueMap<String, String> getHeaders() throws IOException {
	    if(null == this.headers){
	       this.headers = getResponseHeaderExtractor().extract(this.response, this.httpRequest);
        }
		return this.headers;
	}

	@Override
	public InputStream getBody() throws IOException {
		return this.responseStream = getResponseStreamExtractor().extract(this.response, this.httpRequest);
    }

	public StreamExtractor<RR> getResponseStreamExtractor() {
		return responseStreamExtractor;
	}

	public void setResponseStreamExtractor(StreamExtractor<RR> responseStreamExtractor) {
		this.responseStreamExtractor = responseStreamExtractor;
	}

	public HeaderExtractor<RR> getResponseHeaderExtractor() {
		return responseHeaderExtractor;
	}

	public void setResponseHeaderExtractor(HeaderExtractor<RR> responseHeaderExtractor) {
		this.responseHeaderExtractor = responseHeaderExtractor;
	}
}
