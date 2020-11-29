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

package top.jfunc.common.http.exe.jdk;

import top.jfunc.common.http.component.HeaderExtractor;
import top.jfunc.common.http.component.StreamExtractor;
import top.jfunc.common.http.exe.ClientHttpResponse;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.http.util.NativeUtil;
import top.jfunc.common.utils.IoUtil;
import top.jfunc.common.utils.MultiValueMap;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

/**
 * {@link ClientHttpResponse} implementation that uses standard JDK facilities.
 *
 * @author Arjen Poutsma
 * @author Brian Clozel
 * @since 3.0
 */
public class JdkClientHttpResponse implements ClientHttpResponse {
    private StreamExtractor<HttpURLConnection> httpURLConnectionStreamExtractor;
    private HeaderExtractor<HttpURLConnection> httpURLConnectionHeaderExtractor;

	private final HttpURLConnection connection;

	private HttpRequest httpRequest;

	private MultiValueMap<String, String> headers;

	private InputStream responseStream;


	protected JdkClientHttpResponse(HttpURLConnection connection, HttpRequest httpRequest, StreamExtractor<HttpURLConnection> streamExtractor, HeaderExtractor<HttpURLConnection> headerExtractor) {
		this.connection = connection;
		this.httpRequest = httpRequest;
        setHttpURLConnectionStreamExtractor(streamExtractor);
        setHttpURLConnectionHeaderExtractor(headerExtractor);
	}


	@Override
	public int getStatusCode() throws IOException {
		return this.connection.getResponseCode();
	}

	@Override
	public String getStatusText() throws IOException {
		return this.connection.getResponseMessage();
	}

	@Override
	public MultiValueMap<String, String> getHeaders() throws IOException {
	    if(null == this.headers){
	       this.headers = getHttpURLConnectionHeaderExtractor().extract(connection, httpRequest);
        }
		//6.返回header,包括Cookie处理
		return this.headers;
	}

	@Override
	public InputStream getBody() throws IOException {
		/*InputStream errorStream = this.connection.getErrorStream();
		this.responseStream = (errorStream != null ? errorStream : this.connection.getInputStream());
		return this.responseStream;*/
		return this.responseStream =  getHttpURLConnectionStreamExtractor().extract(this.connection, this.httpRequest);
    }

	@Override
	public void close() {
        NativeUtil.closeQuietly(connection);
        IoUtil.close(this.responseStream);
	}

    public StreamExtractor<HttpURLConnection> getHttpURLConnectionStreamExtractor() {
        return httpURLConnectionStreamExtractor;
    }

    public void setHttpURLConnectionStreamExtractor(StreamExtractor<HttpURLConnection> httpURLConnectionStreamExtractor) {
        this.httpURLConnectionStreamExtractor = httpURLConnectionStreamExtractor;
    }

    public HeaderExtractor<HttpURLConnection> getHttpURLConnectionHeaderExtractor() {
		return httpURLConnectionHeaderExtractor;
	}

	public void setHttpURLConnectionHeaderExtractor(HeaderExtractor<HttpURLConnection> httpURLConnectionHeaderExtractor) {
		this.httpURLConnectionHeaderExtractor = httpURLConnectionHeaderExtractor;
	}
}
