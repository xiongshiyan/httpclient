/*
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package top.jfunc.common.http.exe.jodd;

import jodd.http.HttpResponse;
import top.jfunc.common.http.component.HeaderExtractor;
import top.jfunc.common.http.component.StreamExtractor;
import top.jfunc.common.http.exe.ClientHttpResponse;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.http.util.JoddUtil;
import top.jfunc.common.utils.IoUtil;
import top.jfunc.common.utils.MultiValueMap;

import java.io.IOException;
import java.io.InputStream;

/**
 * {@link ClientHttpResponse} implementation that uses standard JDK facilities.
 *
 * @author Arjen Poutsma
 * @author Brian Clozel
 * @since 3.0
 */
public class JoddClientHttpResponse implements ClientHttpResponse {
    private StreamExtractor<HttpResponse> httpResponseStreamExtractor;
    private HeaderExtractor<HttpResponse> httpResponseHeaderExtractor;

	private final HttpResponse httpResponse;

	private HttpRequest httpRequest;

	private MultiValueMap<String, String> headers;

	private InputStream responseStream;


	protected JoddClientHttpResponse(HttpResponse httpResponse, HttpRequest httpRequest, StreamExtractor<HttpResponse> streamExtractor, HeaderExtractor<HttpResponse> headerExtractor) {
		this.httpResponse = httpResponse;
		this.httpRequest = httpRequest;
        setHttpResponseStreamExtractor(streamExtractor);
        setHttpResponseHeaderExtractor(headerExtractor);
	}


	@Override
	public int getStatusCode() throws IOException {
		return this.httpResponse.statusCode();
	}

	@Override
	public String getStatusText() throws IOException {
		return this.httpResponse.statusPhrase();
	}

	@Override
	public MultiValueMap<String, String> getHeaders() throws IOException {
	    if(null == this.headers){
	       this.headers = getHttpResponseHeaderExtractor().extract(httpResponse, httpRequest);
        }
		//6.返回header,包括Cookie处理
		return this.headers;
	}

	@Override
	public InputStream getBody() throws IOException {
		/*InputStream errorStream = this.connection.getErrorStream();
		this.responseStream = (errorStream != null ? errorStream : this.connection.getInputStream());
		return this.responseStream;*/
		return this.responseStream =  getHttpResponseStreamExtractor().extract(this.httpResponse, this.httpRequest);
    }

	@Override
	public void close() {
		JoddUtil.closeQuietly(httpResponse);
        IoUtil.close(this.responseStream);
	}
	public StreamExtractor<HttpResponse> getHttpResponseStreamExtractor() {
		return httpResponseStreamExtractor;
	}

	public void setHttpResponseStreamExtractor(StreamExtractor<HttpResponse> httpResponseStreamExtractor) {
		this.httpResponseStreamExtractor = httpResponseStreamExtractor;
	}

	public HeaderExtractor<HttpResponse> getHttpResponseHeaderExtractor() {
		return httpResponseHeaderExtractor;
	}

	public void setHttpResponseHeaderExtractor(HeaderExtractor<HttpResponse> httpResponseHeaderExtractor) {
		this.httpResponseHeaderExtractor = httpResponseHeaderExtractor;
	}
}
