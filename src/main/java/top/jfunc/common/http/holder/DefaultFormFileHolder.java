package top.jfunc.common.http.holder;

import top.jfunc.common.http.base.FormFile;

import java.util.*;

/**
 * @author xiongshiyan at 2019/6/2 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class DefaultFormFileHolder implements FormFileHolder{
    /**
     * 2018-06-18为了文件上传增加的
     */
    private List<FormFile> formFiles = new ArrayList<>(2);

    @Override
    public FormFile[] getFormFiles() {
        return this.formFiles.toArray(new FormFile[this.formFiles.size()]);
    }

    @Override
    public FormFileHolder setFormFiles(Iterable<FormFile> formFiles) {
        Objects.requireNonNull(formFiles);
        formFiles.iterator().forEachRemaining(this.formFiles::add);
        return this;
    }

    @Override
    public FormFileHolder addFormFile(FormFile... formFiles) {
        if(null != formFiles && formFiles.length > 0){
            this.formFiles.addAll(Arrays.asList(formFiles));
        }
        return this;
    }
}
