package top.jfunc.common.http.response;

import top.jfunc.common.http.base.ContentCallback;
import top.jfunc.common.http.base.HttpInputMessage;
import top.jfunc.common.http.exe.HttpRequestExecutor;
import top.jfunc.common.http.request.HttpRequest;

import java.io.Closeable;
import java.io.IOException;

/**
 * Represents a client-side HTTP response.
 * Obtained via an calling of the {@link HttpRequestExecutor#execute(HttpRequest, ContentCallback)}.
 *
 * <p>A {@code ClientHttpResponse} must be {@linkplain #close() closed},
 * typically in a {@code finally} block.
 *
 * @author Arjen Poutsma
 * @since 3.0
 */
public interface ClientHttpResponse extends HttpInputMessage, Closeable {

	/**
	 * Return the HTTP status code of the response.
	 * @return the HTTP status as an HttpStatus enum value
	 * @throws IOException in case of I/O errors
	 */
	int getStatusCode() throws IOException;

	/**
	 * Return the HTTP status text of the response.
	 * @return the HTTP status text
	 * @throws IOException in case of I/O errors
	 */
	String getStatusText() throws IOException;

	/**
	 * Close this response, freeing any resources created.
	 */
	@Override
	void close() throws IOException;

}
