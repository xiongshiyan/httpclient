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
package top.jfunc.common.http.base;

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
    public static final String TXT_PLAIN_STRING                     = "text/plain";
    public static final String TXT_XML_STRING                       = "text/xml";
    public static final String TXT_HTML_STRING                      = "text/html";
    public static final String TXT_JAVASCRIPT_STRING                = "text/javascript";
    public static final String TXT_CSS_STRING                       = "text/css";
    public static final String TXT_CSV_STRING                       = "text/csv";
    /**常见的MediaType-IMAGE*/
    public static final String IMAGE_BMP_STRING                     = "image/bmp";
    public static final String IMAGE_GIF_STRING                     = "image/gif";
    public static final String IMAGE_JPEG_STRING                    = "image/jpeg";
    public static final String IMAGE_PNG_STRING                     = "image/png";
    public static final String IMAGE_TIFF_STRING                    = "image/tiff";
    public static final String IMAGE_WEBP_STRING                    = "image/webp";
    public static final String IMAGE_ICO_STRING                     = "image/vnd.microsoft.icon";
    /**常见的MediaType-AUDIO*/
    public static final String AUDIO_MP4_STRING                     = "audio/mp4";
    public static final String AUDIO_MPEG_STRING                    = "audio/mpeg";
    public static final String AUDIO_OGG_STRING                     = "audio/ogg";
    public static final String AUDIO_WEBM_STRING                    = "audio/webm";
    public static final String AUDIO_WMA_STRING                     = "audio/x-ms-wma";
    /**常见的MediaType-VIDEO*/
    public static final String VIDEO_MP4_STRING                     = "video/mp4";
    public static final String VIDEO_MPEG_STRING                    = "video/mpeg";
    public static final String VIDEO_OGG_STRING                     = "video/ogg";
    public static final String VIDEO_QUICKTIME_STRING               = "video/quicktime";
    public static final String VIDEO_WEBM_STRING                    = "video/webm";
    public static final String VIDEO_WMA_STRING                     = "video/x-ms-wmv";
    public static final String VIDEO_FLV_STRING                     = "video/x-flv";
    public static final String VIDEO_3GPP_STRING                    = "video/3gpp";
    /**常见的MediaType-APPLICATION*/
    public static final String APPLICATION_XML_STRING               = "application/xml";
    public static final String APPLICATION_ATOM_STRING              = "application/atom+xml";
    public static final String APPLICATION_FORM_DATA_STRING         = "application/x-www-form-urlencoded";
    public static final String APPLICATION_JSON_STRING              = "application/json";
    public static final String APPLICATION_JAVASCRIPT_STRING        = "application/javascript";
    public static final String APPLICATION_MICROSOFT_WORD_STRING    = "application/msword";
    public static final String APPLICATION_MICROSOFT_EXCEL_STRING   = "application/vnd.ms-excel";
    public static final String APPLICATION_OCTET_STREAM_STRING      = "application/octet-stream";
    public static final String APPLICATION_PDF_STRING               = "application/pdf";
    public static final String APPLICATION_RTF_STRING               = "application/rtf";
    public static final String APPLICATION_RDF_STRING               = "application/rdf+xml";
    public static final String APPLICATION_SOAP_XML_STRING          = "application/soap+xml";
    public static final String APPLICATION_TAR_STRING               = "application/x-tar";
    public static final String APPLICATION_WOFF_STRING              = "application/font-woff";
    public static final String APPLICATION_WOFF2_STRING             = "application/font-woff2";
    public static final String APPLICATION_XHTML_STRING             = "application/xhtml+xml";
    public static final String APPLICATION_ZIP_STRING               = "application/zip";
    /**常见的MediaType-MULTIPART*/
    public static final String MULTIPART_FORM_DATA_STRING           = "multipart/form-data";



    /**常见的MediaType-TEXT*/
    public static final MediaType TXT_PLAIN                     = parse(TXT_PLAIN_STRING);
    public static final MediaType TXT_XML                       = parse(TXT_XML_STRING);
    public static final MediaType TXT_HTML                      = parse(TXT_HTML_STRING);
    public static final MediaType TXT_JAVASCRIPT                = parse(TXT_JAVASCRIPT_STRING);
    public static final MediaType TXT_CSS                       = parse(TXT_CSS_STRING);
    public static final MediaType TXT_CSV                       = parse(TXT_CSV_STRING);
    /**常见的MediaType-IMAGE*/
    public static final MediaType IMAGE_BMP                     = parse(IMAGE_BMP_STRING);
    public static final MediaType IMAGE_GIF                     = parse(IMAGE_GIF_STRING);
    public static final MediaType IMAGE_JPEG                    = parse(IMAGE_JPEG_STRING);
    public static final MediaType IMAGE_PNG                     = parse(IMAGE_PNG_STRING);
    public static final MediaType IMAGE_TIFF                    = parse(IMAGE_TIFF_STRING);
    public static final MediaType IMAGE_WEBP                    = parse(IMAGE_WEBP_STRING);
    public static final MediaType IMAGE_ICO                     = parse(IMAGE_ICO_STRING);
    /**常见的MediaType-AUDIO*/
    public static final MediaType AUDIO_MP4                     = parse(AUDIO_MP4_STRING);
    public static final MediaType AUDIO_MPEG                    = parse(AUDIO_MPEG_STRING);
    public static final MediaType AUDIO_OGG                     = parse(AUDIO_OGG_STRING);
    public static final MediaType AUDIO_WEBM                    = parse(AUDIO_WEBM_STRING);
    public static final MediaType AUDIO_WMA                     = parse(AUDIO_WMA_STRING);
    /**常见的MediaType-VIDEO*/
    public static final MediaType VIDEO_MP4                     = parse(VIDEO_MP4_STRING);
    public static final MediaType VIDEO_MPEG                    = parse(VIDEO_MPEG_STRING);
    public static final MediaType VIDEO_OGG                     = parse(VIDEO_OGG_STRING);
    public static final MediaType VIDEO_QUICKTIME               = parse(VIDEO_QUICKTIME_STRING);
    public static final MediaType VIDEO_WEBM                    = parse(VIDEO_WEBM_STRING);
    public static final MediaType VIDEO_WMA                     = parse(VIDEO_WMA_STRING);
    public static final MediaType VIDEO_FLV                     = parse(VIDEO_FLV_STRING);
    public static final MediaType VIDEO_3GPP                    = parse(VIDEO_3GPP_STRING);
    /**常见的MediaType-APPLICATION*/
    public static final MediaType APPLICATION_XML               = parse(APPLICATION_XML_STRING);
    public static final MediaType APPLICATION_ATOM              = parse(APPLICATION_ATOM_STRING);
    public static final MediaType APPLICATION_FORM_DATA         = parse(APPLICATION_FORM_DATA_STRING);
    public static final MediaType APPLICATION_JSON              = parse(APPLICATION_JSON_STRING);
    public static final MediaType APPLICATION_JAVASCRIPT        = parse(APPLICATION_JAVASCRIPT_STRING);
    public static final MediaType APPLICATION_MICROSOFT_WORD    = parse(APPLICATION_MICROSOFT_WORD_STRING);
    public static final MediaType APPLICATION_MICROSOFT_EXCEL   = parse(APPLICATION_MICROSOFT_EXCEL_STRING);
    public static final MediaType APPLICATION_OCTET_STREAM      = parse(APPLICATION_OCTET_STREAM_STRING);
    public static final MediaType APPLICATION_PDF               = parse(APPLICATION_PDF_STRING);
    public static final MediaType APPLICATION_RTF               = parse(APPLICATION_RTF_STRING);
    public static final MediaType APPLICATION_RDF               = parse(APPLICATION_RDF_STRING);
    public static final MediaType APPLICATION_SOAP_XML          = parse(APPLICATION_SOAP_XML_STRING);
    public static final MediaType APPLICATION_TAR               = parse(APPLICATION_TAR_STRING);
    public static final MediaType APPLICATION_WOFF              = parse(APPLICATION_WOFF_STRING);
    public static final MediaType APPLICATION_WOFF2             = parse(APPLICATION_WOFF2_STRING);
    public static final MediaType APPLICATION_XHTML             = parse(APPLICATION_XHTML_STRING);
    public static final MediaType APPLICATION_ZIP               = parse(APPLICATION_ZIP_STRING);
    /**常见的MediaType-MULTIPART*/
    public static final MediaType MULTIPART_FORM_DATA           = parse(MULTIPART_FORM_DATA_STRING);





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
