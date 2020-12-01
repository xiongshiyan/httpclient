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

package top.jfunc.common.http.component.jodd;

import jodd.http.HttpResponse;
import top.jfunc.common.http.component.HeaderExtractor;
import top.jfunc.common.http.component.StreamExtractor;
import top.jfunc.common.http.response.BaseClientHttpResponse;
import top.jfunc.common.http.response.ClientHttpResponse;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.http.util.JoddUtil;
import top.jfunc.common.utils.IoUtil;

import java.io.IOException;

/**
 * {@link ClientHttpResponse} implementation that uses Jodd facilities.
 *
 * @author xiongshiyan
 */
public class JoddClientHttpResponse extends BaseClientHttpResponse<HttpResponse> {

	public JoddClientHttpResponse(HttpResponse httpResponse, HttpRequest httpRequest, StreamExtractor<HttpResponse> streamExtractor, HeaderExtractor<HttpResponse> headerExtractor) {
		super(httpResponse, httpRequest, streamExtractor, headerExtractor);
	}


	@Override
	public int getStatusCode() throws IOException {
		return response.statusCode();
	}

	@Override
	public String getStatusText() throws IOException {
		return response.statusPhrase();
	}

	@Override
	public void close() {
		IoUtil.close(responseStream);
		JoddUtil.closeQuietly(response);
	}
}
