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
import top.jfunc.common.http.exe.BaseClientHttpResponse;
import top.jfunc.common.http.response.ClientHttpResponse;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.http.util.NativeUtil;
import top.jfunc.common.utils.IoUtil;

import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * {@link ClientHttpResponse} implementation that uses standard JDK facilities.
 *
 * @author xiongshiyan
 */
public class JdkClientHttpResponse extends BaseClientHttpResponse<HttpURLConnection> {


	public JdkClientHttpResponse(HttpURLConnection connection, HttpRequest httpRequest, StreamExtractor<HttpURLConnection> streamExtractor, HeaderExtractor<HttpURLConnection> headerExtractor) {
		super(connection, httpRequest, streamExtractor, headerExtractor);
	}


	@Override
	public int getStatusCode() throws IOException {
		return response.getResponseCode();
	}

	@Override
	public String getStatusText() throws IOException {
		return response.getResponseMessage();
	}

	@Override
	public void close() {
        NativeUtil.closeQuietly(response);
        IoUtil.close(responseStream);
	}
}
