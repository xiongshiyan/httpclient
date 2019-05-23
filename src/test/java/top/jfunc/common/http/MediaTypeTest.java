package top.jfunc.common.http;

import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.Charset;

import static org.hamcrest.core.Is.is;

/**
 * @author xiongshiyan at 2019/5/22 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class MediaTypeTest {
    @Test
    public void testToString(){
        Assert.assertThat(MediaType.TXT_PLAIN.withCharset("utf-8").toString() , is("text/plain;charset=utf-8"));
        Assert.assertThat(MediaType.TXT_XML.withCharset("utf-8").toString() , is("text/xml;charset=utf-8"));
        Assert.assertThat(MediaType.TXT_HTML.withCharset("utf-8").toString() , is("text/html;charset=utf-8"));
        Assert.assertThat(MediaType.TXT_JAVASCRIPT.withCharset("utf-8").toString() , is("text/javascript;charset=utf-8"));
        Assert.assertThat(MediaType.TXT_CSS.withCharset("utf-8").toString() , is("text/css;charset=utf-8"));
        Assert.assertThat(MediaType.TXT_CSV.withCharset("utf-8").toString() , is("text/csv;charset=utf-8"));
        Assert.assertThat(MediaType.IMAGE_BMP.toString() , is("image/bmp"));
        Assert.assertThat(MediaType.IMAGE_GIF.toString() , is("image/gif"));
        Assert.assertThat(MediaType.IMAGE_JPEG.toString() , is("image/jpeg"));
        Assert.assertThat(MediaType.IMAGE_PNG.toString() , is("image/png"));
        Assert.assertThat(MediaType.IMAGE_TIFF.toString() , is("image/tiff"));
        Assert.assertThat(MediaType.IMAGE_WEBP.toString() , is("image/webp"));
        Assert.assertThat(MediaType.IMAGE_ICO.toString() , is("image/vnd.microsoft.icon"));
        Assert.assertThat(MediaType.AUDIO_MP4.toString() , is("audio/mp4"));
        Assert.assertThat(MediaType.AUDIO_MPEG.toString() , is("audio/mpeg"));
        Assert.assertThat(MediaType.AUDIO_OGG.toString() , is("audio/ogg"));
        Assert.assertThat(MediaType.AUDIO_WEBM.toString() , is("audio/webm"));
        Assert.assertThat(MediaType.AUDIO_WMA.toString() , is("audio/x-ms-wma"));
        Assert.assertThat(MediaType.VIDEO_MP4.toString() , is("video/mp4"));
        Assert.assertThat(MediaType.VIDEO_MPEG.toString() , is("video/mpeg"));
        Assert.assertThat(MediaType.VIDEO_OGG.toString() , is("video/ogg"));
        Assert.assertThat(MediaType.VIDEO_QUICKTIME.toString() , is("video/quicktime"));
        Assert.assertThat(MediaType.VIDEO_WEBM.toString() , is("video/webm"));
        Assert.assertThat(MediaType.VIDEO_WMA.toString() , is("video/x-ms-wmv"));
        Assert.assertThat(MediaType.VIDEO_FLV.toString() , is("video/x-flv"));
        Assert.assertThat(MediaType.VIDEO_3GPP.toString() , is("video/3gpp"));
        Assert.assertThat(MediaType.APPLICATIPON_XML_.toString() , is("application/xml"));
        Assert.assertThat(MediaType.APPLICATIPON_ATOM.toString() , is("application/atom+xml"));
        Assert.assertThat(MediaType.APPLICATIPON_FORM_DATA.toString() , is("application/x-www-form-urlencoded"));
        Assert.assertThat(MediaType.APPLICATIPON_JSON.toString() , is("application/json"));
        Assert.assertThat(MediaType.APPLICATIPON_JAVASCRIPT.toString() , is("application/javascript"));
        Assert.assertThat(MediaType.APPLICATIPON_MICROSOFT_WORD.toString() , is("application/msword"));
        Assert.assertThat(MediaType.APPLICATIPON_MICROSOFT_EXCEL.toString() , is("application/vnd.ms-excel"));
        Assert.assertThat(MediaType.APPLICATIPON_OCTET_STREAM.toString() , is("application/octet-stream"));
        Assert.assertThat(MediaType.APPLICATIPON_PDF.toString() , is("application/pdf"));
        Assert.assertThat(MediaType.APPLICATIPON_RTF.toString() , is("application/rtf"));
        Assert.assertThat(MediaType.APPLICATIPON_RDF.withCharset("utf-8").toString() , is("application/rdf+xml;charset=utf-8"));
        Assert.assertThat(MediaType.APPLICATIPON_SOAP_XML.withCharset("utf-8").toString() , is("application/soap+xml;charset=utf-8"));
        Assert.assertThat(MediaType.APPLICATIPON_TAR.toString() , is("application/x-tar"));
        Assert.assertThat(MediaType.APPLICATIPON_WOFF.toString() , is("application/font-woff"));
        Assert.assertThat(MediaType.APPLICATIPON_WOFF2.toString() , is("application/font-woff2"));
        Assert.assertThat(MediaType.APPLICATIPON_XHTML.toString() , is("application/xhtml+xml"));
        Assert.assertThat(MediaType.APPLICATIPON_ZIP.toString() , is("application/zip"));
        Assert.assertThat(MediaType.MULTIPART_FORM_DATA.toString() , is("multipart/form-data"));

        Assert.assertEquals("UTF-8" , Charset.forName("utf-8").name());

        System.out.println(MediaType.APPLICATIPON_FORM_DATA.withCharset("UTF-8").toString());
    }
}
