// Copyright (c) 2003-present, Jodd Team (http://jodd.org)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

package top.jfunc.common.http.cookie;

import top.jfunc.common.http.base.HttpHeaders;
import top.jfunc.common.utils.StrUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * from jodd
 * Cookie object. Simple cookie data holder, cookie header parser and generator.
 * @author xiongshiyan
 */
public class Cookie {
	public static final String MAX_AGE   = "Max-Age";
	public static final String COMMENT   = "Comment";
	public static final String DOMAIN    = "Domain";
	public static final String PATH      = "Path";
	public static final String SECURE    = "Secure";
	public static final String VERSION   = "Version";
	public static final String HTTP_ONLY = "HttpOnly";
	public static final String EXPIRES   = "Expires";

    /**
     * cookie key
     */
	private String name;
    /**
     * cookie value
     * name=value
     */
	private String value;

	/// attributes encoded in the header's cookie fields

    /**
     * ;Comment=VALUE ... describes cookie's use ; Discard ... implied by maxAge < 0
     */
    private String comment;
    /**
     * ;Domain=VALUE ... domain that sees cookie
     */
    private String domain;
    /**
     * ;Max-Age=VALUE ... cookies auto-expire
     */
    private Integer maxAge;
    /**
     * ;Path=VALUE ... URLs that see the cookie
     */
    private String path;
    /**
     * ;Expires= ... expires values
     */
    private String expires;
    /**
     * ;Secure ... e.g. use SSL
     */
    private boolean secure;
    /**
     * ;Version=1 ... means RFC 2109++ style
     */
    private Integer version;
    /**
     * ;HttpOnly
     */
    private boolean httpOnly;


	/**
	 * Creates cookie with specified name and value.
	 * <p>
	 * The name must conform to RFC 2109. That means it can contain
	 * only ASCII alphanumeric characters and cannot contain commas,
	 * semicolons, or white space or begin with a $ character.
	 * <p>
	 * The value can be anything the server chooses to send.
	 */
	public Cookie(final String name, final String value) {
		setName(name);
		setValue(value);
	}

	/**
     * 形如：oschina_new_user=false; path=/; expires=Fri, 02 Nov 2040 09:11:02 -0000
	 * Parses cookie data from given user-agent string.
	 */
	public Cookie(String cookieString) {
		int from = 0;
		int ndx = 0;

		cookieString = cookieString.trim();

		while (ndx < cookieString.length()) {
			ndx = cookieString.indexOf(StrUtil.SEMICOLON, from);

			if (ndx == -1) {
				// last chunk
				ndx = cookieString.length();
			}
			int ndx2 = cookieString.indexOf(StrUtil.EQUALS, from);

			String name;
			String value;
			if (ndx2 != -1 && ndx2 < ndx) {
				name = cookieString.substring(from, ndx2).trim();
				value = cookieString.substring(ndx2 + 1, ndx).trim();
			} else {
				if (from == ndx) {
					ndx++;
					continue;
				}
				name = cookieString.substring(from, ndx).trim();
				value = null;
			}

			if (value != null && MAX_AGE.equalsIgnoreCase(name)) {
				setMaxAge(Integer.parseInt(value));
			} else if (COMMENT.equalsIgnoreCase(name)) {
				setComment(value);
			} else if (DOMAIN.equalsIgnoreCase(name)) {
				setDomain(value);
			} else if (PATH.equalsIgnoreCase(name)) {
				setPath(value);
			} else if (SECURE.equalsIgnoreCase(name)) {
				setSecure(true);
			} else if (value != null && VERSION.equalsIgnoreCase(name)) {
				setVersion(Integer.parseInt(value));
			} else if (HTTP_ONLY.equalsIgnoreCase(name)) {
				setHttpOnly(true);
			} else if (EXPIRES.equalsIgnoreCase(name)) {
				setExpires(value);
			} else if (this.name == null && !StrUtil.isBlank(name)) {
				setName(name);
				setValue(value);
			}

			// continue
			from = ndx + 1;
		}
	}

	/**
	 * Sets the cookie name and checks for validity.
	 */
	private void setName(final String name) {
		if (name.contains(StrUtil.SEMICOLON) || name.contains(StrUtil.COMMA) || name.startsWith("$")) {
			throw new IllegalArgumentException("Invalid cookie name:" + name);
		}

		for (int n = 0; n < name.length(); n++) {
			char c = name.charAt(n);
			if (c <= 0x20 || c >= 0x7f) {
				throw new IllegalArgumentException("Invalid cookie name:" + name);
			}
		}
		this.name = name;
	}

	/**
	 * Returns the comment describing the purpose of this cookie, or
	 * <code>null</code> if the cookie has no comment.
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * Specifies a comment that describes a cookie's purpose.
	 * The comment is useful if the browser presents the cookie
	 * to the user.
	 */
	public Cookie setComment(final String purpose) {
		comment = purpose;
		return this;
	}

	/**
	 * Returns the domain name set for this cookie. The form of
	 * the domain name is set by RFC 2109.
	 */

	public String getDomain() {
		return domain;
	}

	/**
	 * Specifies the domain within which this cookie should be presented.
	 * <p>
	 * The form of the domain name is specified by RFC 2109. A domain
	 * name begins with a dot (<code>.foo.com</code>) and means that
	 * the cookie is visible to servers in a specified Domain Name System
	 * (DNS) zone (for example, <code>www.foo.com</code>, but not
	 * <code>a.b.foo.com</code>). By default, cookies are only returned
	 * to the server that sent them.
	 */

	public Cookie setDomain(final String pattern) {
        // IE allegedly needs this
        domain = pattern.toLowerCase();
		return this;
	}

	/**
	 * Returns the maximum age of the cookie, specified in seconds,
	 * By default, <code>-1</code> indicating the cookie will persist
	 * until browser shutdown.
	 */

	public Integer getMaxAge() {
		return maxAge;
	}

	/**
	 * Sets the maximum age of the cookie in seconds.
	 * <p>
	 * A positive value indicates that the cookie will expire
	 * after that many seconds have passed. Note that the value is
	 * the <i>maximum</i> age when the cookie will expire, not the cookie's
	 * current age.
	 * <p>
	 * A negative value means
	 * that the cookie is not stored persistently and will be deleted
	 * when the Web browser exits. A zero value causes the cookie
	 * to be deleted.
	 */

	public Cookie setMaxAge(final int expiry) {
		maxAge = expiry;
		return this;
	}

	/**
	 * Returns the path on the server
	 * to which the browser returns this cookie. The
	 * cookie is visible to all subpaths on the server.
	 */
	public String getPath() {
		return path;
	}

	/**
	 * Specifies a path for the cookie
	 * to which the client should return the cookie.
	 * <p>
	 * The cookie is visible to all the pages in the directory
	 * you specify, and all the pages in that directory's subdirectories.
	 * A cookie's path must include the servlet that set the cookie,
	 * for example, <i>/catalog</i>, which makes the cookie
	 * visible to all directories on the server under <i>/catalog</i>.
	 *
	 * <p>Consult RFC 2109 (available on the Internet) for more
	 * information on setting path names for cookies.
	 */
	public Cookie setPath(final String uri) {
		path = uri;
		return this;
	}

	/**
	 * Returns <code>true</code> if the browser is sending cookies
	 * only over a secure protocol, or <code>false</code> if the
	 * browser can send cookies using any protocol.
	 */
	public boolean isSecure() {
		return secure;
	}

	/**
	 * Indicates to the browser whether the cookie should only be sent
	 * using a secure protocol, such as HTTPS or SSL.
	 */
	public Cookie setSecure(final boolean flag) {
		secure = flag;
		return this;
	}

	/**
	 * Returns the name of the cookie. The name cannot be changed after
	 * creation.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the value of the cookie.
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Assigns a new value to a cookie after the cookie is created.
	 * If you use a binary value, you may want to use BASE64 encoding.
	 */
	public Cookie setValue(final String newValue) {
		value = newValue;
		return this;
	}

	/**
	 * Returns the version of the protocol this cookie complies
	 * with. Version 1 complies with RFC 2109,
	 * and version 0 complies with the original
	 * cookie specification drafted by Netscape. Cookies provided
	 * by a browser use and identify the browser's cookie version.
	 */
	public Integer getVersion() {
		return version;
	}

	/**
	 * Sets the version of the cookie protocol this cookie complies
	 * with. Version 0 complies with the original Netscape cookie
	 * specification. Version 1 complies with RFC 2109.
	 */
	public Cookie setVersion(final int version) {
		this.version = version;
		return this;
	}

	public boolean isHttpOnly() {
		return httpOnly;
	}

	public Cookie setHttpOnly(final boolean httpOnly) {
		this.httpOnly = httpOnly;
		return this;
	}

	public String getExpires() {
		return expires;
	}

	public Cookie setExpires(final String expires) {
		this.expires = expires;
		return this;
	}

	@Override
	public String toString() {
		StringBuilder cookie = new StringBuilder();

		cookie.append(name).append('=').append(value);

		if (maxAge != null) {
			cookie.append("; Max-Age=").append(maxAge);
		}
		if (expires != null) {
			cookie.append("; Expires=").append(expires);
		}
		if (comment != null) {
			cookie.append("; Comment=").append(comment);
		}
		if (domain != null) {
			cookie.append("; Domain=").append(domain);
		}
		if (path != null) {
			cookie.append("; Path=").append(path);
		}
		if (secure) {
			cookie.append("; Secure");
		}
		if (version != null) {
			cookie.append("; Version=").append(version);
		}
		if (httpOnly) {
			cookie.append("; HttpOnly");
		}

		return cookie.toString();
	}


    /**
     * 形成key1=value1;key2=value2用于请求Cookie:key1=value1;key2=value2
     * @param cookies 多个cookie
     * @return cookie字符串
     */
    public static String buildCookieString(List<Cookie> cookies){
        StringBuilder cookieString = new StringBuilder();

        boolean first = true;

        for (Cookie cookie : cookies) {
            Integer maxAge = cookie.getMaxAge();
            if (maxAge != null && maxAge == 0) {
                continue;
            }

            if (!first) {
                cookieString.append("; ");
            }

            first = false;
            cookieString.append(cookie.getName());
            cookieString.append(StrUtil.EQUALS);
            cookieString.append(cookie.getValue());
        }
        return cookieString.toString();
    }

    /**
     * 从响应中的{@link HttpHeaders#SET_COOKIE}|{@link HttpHeaders#SET_COOKIE2}字段获取cookie解析成一个个的cookie
     * @param newCookies 多个{@link HttpHeaders#SET_COOKIE}|{@link HttpHeaders#SET_COOKIE2}的值
     * @return 多个cookie
     */
    public static List<Cookie> parseCookies(List<String> newCookies){
        List<Cookie> cookieList = new ArrayList<>(newCookies.size());

        for (String cookieValue : newCookies) {
            try {
                cookieList.add(new Cookie(cookieValue));
            }catch (Exception e) {
                // ignore
            }
        }
        return cookieList;
    }

}
