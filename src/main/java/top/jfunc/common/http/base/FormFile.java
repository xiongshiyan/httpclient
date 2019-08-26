package top.jfunc.common.http.base;

import top.jfunc.common.http.MediaType;

import java.io.*;

/**
 * 上传文件表单数据
 * @author xiongshiyan
 */
public class FormFile {
    /**上传文件的数据 */
    private InputStream inStream;
    /**文件长度*/
    private long        fileLen;
    /**文件名称 */
    private String      filName;
    /**请求参数名称 */
    private String      parameterName;
    /**内容类型 */
    private String      contentType = MediaType.APPLICATIPON_OCTET_STREAM.toString();

    public static FormFile of(String filName, byte[] data, String parameterName, String contentType){
        return new FormFile(filName , data , parameterName , contentType);
    }
    public static FormFile of(File file, String parameterName, String contentType){
        return new FormFile(file , parameterName , contentType);
    }
    public static FormFile of(String fileName, InputStream inStream, long fileLen, String parameterName, String contentType){
        return new FormFile(fileName , inStream , fileLen , parameterName , contentType);
    }
    public static FormFile of(String fileName, InputStream inStream, String parameterName, String contentType){
        int available;
        try {
            available = inStream.available();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new FormFile(fileName , inStream , available , parameterName , contentType);
    }
    public FormFile(String filName, byte[] data, String parameterName, String contentType){
        this.inStream = new ByteArrayInputStream(data);
        init(filName, data.length , parameterName, contentType);
    }



    public FormFile(File file, String parameterName, String contentType) {
        try {
            this.inStream = new FileInputStream(file);
            init(file.getName() , file.length() , parameterName , contentType);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public FormFile(String fileName, InputStream inStream, long fileLen, String parameterName, String contentType){
        this.inStream = inStream;
        init(fileName , fileLen , parameterName , contentType);
    }

    private void init(String filName, long fileLen , String parameterName, String contentType) {
        this.filName = filName;
        this.parameterName = parameterName;
        this.fileLen = fileLen;
        if(contentType != null){
            this.contentType = contentType;
        }
    }

    public InputStream getInStream(){
        return inStream;
    }

    public long getFileLen(){
        return fileLen;
    }

    public String getFilName(){
        return filName;
    }

    public void setFilName(String filName){
        this.filName = filName;
    }

    public String getParameterName(){
        return parameterName;
    }

    public void setParameterName(String parameterName){
        this.parameterName = parameterName;
    }

    public String getContentType(){
        return contentType;
    }

    public void setContentType(String contentType){
        this.contentType = contentType;
    }

    @Override
    public String toString() {
        return parameterName + " : " + filName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FormFile formFile = (FormFile) o;

        if (fileLen != formFile.fileLen) return false;
        if (inStream != null ? !inStream.equals(formFile.inStream) : formFile.inStream != null) return false;
        if (filName != null ? !filName.equals(formFile.filName) : formFile.filName != null) return false;
        if (parameterName != null ? !parameterName.equals(formFile.parameterName) : formFile.parameterName != null)
            return false;
        return contentType != null ? contentType.equals(formFile.contentType) : formFile.contentType == null;
    }

    @Override
    public int hashCode() {
        int result = inStream != null ? inStream.hashCode() : 0;
        result = 31 * result + (int) (fileLen ^ (fileLen >>> 32));
        result = 31 * result + (filName != null ? filName.hashCode() : 0);
        result = 31 * result + (parameterName != null ? parameterName.hashCode() : 0);
        result = 31 * result + (contentType != null ? contentType.hashCode() : 0);
        return result;
    }
}
