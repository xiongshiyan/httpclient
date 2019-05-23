/*
 * Copyright (C) 2013 Square, Inc.
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
package top.jfunc.common.http;

import java.nio.charset.Charset;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An <a href="http://tools.ietf.org/html/rfc2045">RFC 2045</a> Media Type, appropriate to describe
 * the content type of an HTTP request or response body.
 */
public final class MediaType {
    private static final String TOKEN = "([a-zA-Z0-9-!#$%&'*+.^_`{|}~]+)";
    private static final String QUOTED = "\"([^\"]*)\"";
    private static final Pattern TYPE_SUBTYPE = Pattern.compile(TOKEN + "/" + TOKEN);
    private static final Pattern PARAMETER = Pattern.compile(
    ";\\s*(?:" + TOKEN + "=(?:" + TOKEN + "|" + QUOTED + "))?");

    private final String type;
    private final String subtype;
    private String charset;

    public MediaType(String type, String subtype, String charset) {
        this.type = type;
        this.subtype = subtype;
        this.charset = charset;
    }

    /**常见的MediaType-TEXT*/
    public static final MediaType TXT_PLAIN                     = parse("text/plain");
    public static final MediaType TXT_XML                       = parse("text/xml");
    public static final MediaType TXT_HTML                      = parse("text/html");
    public static final MediaType TXT_JAVASCRIPT                = parse("text/javascript");
    public static final MediaType TXT_CSS                       = parse("text/css");
    public static final MediaType TXT_CSV                       = parse("text/csv");
    /**常见的MediaType-IMAGE*/
    public static final MediaType IMAGE_BMP                     = parse("image/bmp");
    public static final MediaType IMAGE_GIF                     = parse("image/gif");
    public static final MediaType IMAGE_JPEG                    = parse("image/jpeg");
    public static final MediaType IMAGE_PNG                     = parse("image/png");
    public static final MediaType IMAGE_TIFF                    = parse("image/tiff");
    public static final MediaType IMAGE_WEBP                    = parse("image/webp");
    public static final MediaType IMAGE_ICO                     = parse("image/vnd.microsoft.icon");
    /**常见的MediaType-AUDIO*/
    public static final MediaType AUDIO_MP4 = parse("audio/mp4");
    public static final MediaType AUDIO_MPEG                    = parse("audio/mpeg");
    public static final MediaType AUDIO_OGG                     = parse("audio/ogg");
    public static final MediaType AUDIO_WEBM                    = parse("audio/webm");
    public static final MediaType AUDIO_WMA                     = parse("audio/x-ms-wma");
    /**常见的MediaType-VIDEO*/
    public static final MediaType VIDEO_MP4                     = parse("video/mp4");
    public static final MediaType VIDEO_MPEG                    = parse("video/mpeg");
    public static final MediaType VIDEO_OGG                     = parse("video/ogg");
    public static final MediaType VIDEO_QUICKTIME               = parse("video/quicktime");
    public static final MediaType VIDEO_WEBM                    = parse("video/webm");
    public static final MediaType VIDEO_WMA                     = parse("video/x-ms-wmv");
    public static final MediaType VIDEO_FLV                     = parse("video/x-flv");
    public static final MediaType VIDEO_3GPP                    = parse("video/3gpp");
    /**常见的MediaType-APPLICATION*/
    public static final MediaType APPLICATIPON_XML_             = parse("application/xml");
    public static final MediaType APPLICATIPON_ATOM             = parse("application/atom+xml");
    public static final MediaType APPLICATIPON_FORM_DATA        = parse("application/x-www-form-urlencoded");
    public static final MediaType APPLICATIPON_JSON             = parse("application/json");
    public static final MediaType APPLICATIPON_JAVASCRIPT       = parse("application/javascript");
    public static final MediaType APPLICATIPON_MICROSOFT_WORD   = parse("application/msword");
    public static final MediaType APPLICATIPON_MICROSOFT_EXCEL  = parse("application/vnd.ms-excel");
    public static final MediaType APPLICATIPON_OCTET_STREAM     = parse("application/octet-stream");
    public static final MediaType APPLICATIPON_PDF              = parse("application/pdf");
    public static final MediaType APPLICATIPON_RTF              = parse("application/rtf");
    public static final MediaType APPLICATIPON_RDF              = parse("application/rdf+xml");
    public static final MediaType APPLICATIPON_SOAP_XML         = parse("application/soap+xml");
    public static final MediaType APPLICATIPON_TAR              = parse("application/x-tar");
    public static final MediaType APPLICATIPON_WOFF             = parse("application/font-woff");
    public static final MediaType APPLICATIPON_WOFF2            = parse("application/font-woff2");
    public static final MediaType APPLICATIPON_XHTML            = parse("application/xhtml+xml");
    public static final MediaType APPLICATIPON_ZIP              = parse("application/zip");
    /**常见的MediaType-MULTIPART*/
    public static final MediaType MULTIPART_FORM_DATA           = parse("multipart/form-data");





    /**
    * Returns a media type for {@code string}.
    *
    * @throws IllegalArgumentException if {@code string} is not a well-formed media type.
    */
    public static MediaType get(String string) {
    Matcher typeSubtype = TYPE_SUBTYPE.matcher(string);
    if (!typeSubtype.lookingAt()) {
      throw new IllegalArgumentException("No subtype found for: \"" + string + '"');
    }
    String type = typeSubtype.group(1).toLowerCase(Locale.US);
    String subtype = typeSubtype.group(2).toLowerCase(Locale.US);

    String charset = null;
    Matcher parameter = PARAMETER.matcher(string);
    for (int s = typeSubtype.end(); s < string.length(); s = parameter.end()) {
      parameter.region(s, string.length());
      if (!parameter.lookingAt()) {
        throw new IllegalArgumentException("Parameter is not formatted correctly: \""
            + string.substring(s)
            + "\" for: \""
            + string
            + '"');
      }

      String name = parameter.group(1);
      if (name == null || !"charset".equalsIgnoreCase(name)) {
          continue;
      }
      String charsetParameter;
      String token = parameter.group(2);
      if (token != null) {
        // If the token is 'single-quoted' it's invalid! But we're lenient and strip the quotes.
        charsetParameter = (token.startsWith("'") && token.endsWith("'") && token.length() > 2)
            ? token.substring(1, token.length() - 1)
            : token;
      } else {
        // Value is "double-quoted". That's valid and our regex group already strips the quotes.
        charsetParameter = parameter.group(3);
      }
      if (charset != null && !charsetParameter.equalsIgnoreCase(charset)) {
        throw new IllegalArgumentException("Multiple charsets defined: \""
            + charset
            + "\" and: \""
            + charsetParameter
            + "\" for: \""
            + string
            + '"');
      }
      charset = charsetParameter;
    }

    return new MediaType(type, subtype, charset);
    }

    /**
    * Returns a media type for {@code string}, or null if {@code string} is not a well-formed media
    * type.
    */
    public static MediaType parse(String string) {
        try {
          return get(string);
        } catch (IllegalArgumentException ignored) {
          return null;
        }
    }

    /**
    * Returns the high-level media type, such as "text", "image", "audio", "video", or
    * "application".
    */
    public String type() {
    return type;
    }

    /**
    * Returns a specific media subtype, such as "plain" or "png", "mpeg", "mp4" or "xml".
    */
    public String subtype() {
    return subtype;
    }

    /**
    * Returns the charset of this media type, or null if this media type doesn't specify a charset.
    */
    public Charset charset() {
    return charset(null);
    }

    /**
    * Returns the charset of this media type, or {@code defaultValue} if either this media type
    * doesn't specify a charset, of it its charset is unsupported by the current runtime.
    */
    public Charset charset(Charset defaultValue) {
        try {
          return charset != null ? Charset.forName(charset) : defaultValue;
        } catch (IllegalArgumentException e) {
          // This charset is invalid or unsupported. Give up.
          return defaultValue;
        }
    }

    /**设置字符编码*/
    public MediaType withCharset(String charset){
        this.charset = charset;
        return this;
    }
    /**设置字符编码*/
    public MediaType withCharset(Charset charset){
        this.charset = charset.name();
        return this;
    }

    /**
    * Returns the encoded media type, like "text/plain; charset=utf-8", appropriate for use in a
    * Content-Type header.
    */
    @Override public String toString() {
        String base = type + "/" + subtype;
        return null == charset ? base : (base + ";charset=" + charset.toLowerCase());
    }

    @Override public boolean equals(Object other) {
        if(!(other instanceof MediaType)){
            return false;
        }
        //type/subtype;charset完全一致
        MediaType o = (MediaType)other;
        if(null == charset){
            return type.equalsIgnoreCase(o.type) &&
                    subtype.equalsIgnoreCase(o.subtype);
        }else {
            return type.equalsIgnoreCase(o.type) &&
                    subtype.equalsIgnoreCase(o.subtype) &&
                    charset.equalsIgnoreCase(o.charset);
        }
    }

    @Override public int hashCode() {
    return toString().hashCode();
  }
}
