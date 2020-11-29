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

package top.jfunc.common.http.exe.okhttp3;

import okhttp3.Response;
import top.jfunc.common.http.component.HeaderExtractor;
import top.jfunc.common.http.component.StreamExtractor;
import top.jfunc.common.http.exe.ClientHttpResponse;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.http.util.OkHttp3Util;
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
public class OkHttp3ClientHttpResponse implements ClientHttpResponse {
    private StreamExtractor<Response> responseStreamExtractor;
    private HeaderExtractor<Response> responseHeaderExtractor;

	private final Response response;

	private HttpRequest httpRequest;

	private MultiValueMap<String, String> headers;

	private InputStream responseStream;


	protected OkHttp3ClientHttpResponse(Response response, HttpRequest httpRequest, StreamExtractor<Response> streamExtractor, HeaderExtractor<Response> headerExtractor) {
		this.response = response;
		this.httpRequest = httpRequest;
        setResponseStreamExtractor(streamExtractor);
        setResponseHeaderExtractor(headerExtractor);
	}


	@Override
	public int getStatusCode() throws IOException {
		return this.response.code();
	}

	@Override
	public String getStatusText() throws IOException {
		return this.response.message();
	}

	@Override
	public MultiValueMap<String, String> getHeaders() throws IOException {
	    if(null == this.headers){
	       this.headers = getResponseHeaderExtractor().extract(response, httpRequest);
        }
		//6.返回header,包括Cookie处理
		return this.headers;
	}

	@Override
	public InputStream getBody() throws IOException {
		/*InputStream errorStream = this.connection.getErrorStream();
		this.responseStream = (errorStream != null ? errorStream : this.connection.getInputStream());
		return this.responseStream;*/
		return this.responseStream =  getResponseStreamExtractor().extract(this.response, this.httpRequest);
    }

	@Override
	public void close() {
        IoUtil.close(this.responseStream);
		OkHttp3Util.closeQuietly(response);
	}

	public StreamExtractor<Response> getResponseStreamExtractor() {
		return responseStreamExtractor;
	}

	public void setResponseStreamExtractor(StreamExtractor<Response> responseStreamExtractor) {
		this.responseStreamExtractor = responseStreamExtractor;
	}

	public HeaderExtractor<Response> getResponseHeaderExtractor() {
		return responseHeaderExtractor;
	}

	public void setResponseHeaderExtractor(HeaderExtractor<Response> responseHeaderExtractor) {
		this.responseHeaderExtractor = responseHeaderExtractor;
	}
}
