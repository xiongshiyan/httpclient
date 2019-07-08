接口及实现类整体鸟瞰：

![接口及实现类整体鸟瞰](https://gitee.com/uploads/images/2019/0507/133622_d2ad2ba2_1507575.png "all.png")

功能接口设计：

![功能接口设计](https://gitee.com/uploads/images/2019/0428/143237_36d81cf6_1507575.png "expound-http.png")

用户接口设计：

![用户接口设计](https://gitee.com/uploads/images/2019/0428/143300_9d754644_1507575.png "facade-(HttpClient-SmartHttpClient).png")

OkHttp3实现主线：

![OkHttp3主线](https://gitee.com/uploads/images/2019/0505/180835_8b621fdb_1507575.png "OkHttp3SmartHttpClient.png")

ApacheHttpClient实现主线：

![ApacheHttpClient实现主线](https://gitee.com/uploads/images/2019/0505/180859_f2b9b142_1507575.png "ApacheSmartHttpClient.png")

HttpURLConnection实现主线：

![HttpURLConnection实现主线](https://gitee.com/uploads/images/2019/0505/180936_6e33efcb_1507575.png "NativeSmartHttpClient.png")

JoddHttp实现主线：

![JoddHttp实现主线](https://gitee.com/uploads/images/2019/0507/133655_65f3d31a_1507575.png "JoddSmartHttpClient.png")

代表所有参数的Request类：

![Request](https://images.gitee.com/uploads/images/2019/0627/124024_5dd9b66a_1507575.png "Request.png")

HttpRequest接口体系，使用这些请求类意义更明确：

![HttpRequest接口体系，使用这些请求类意义更明确](https://images.gitee.com/uploads/images/2019/0611/152249_cb6beefa_1507575.png "HttpRequest体系.png")

HttpRequest-HolderHttpRequest接口体系，基于Holder的HttpRequest：

![HttpRequest-HolderHttpRequest接口体系，基于Holder的HttpRequest](https://images.gitee.com/uploads/images/2019/0708/200719_fef33858_1507575.png "HttpRequest-HolderHttpRequest体系.png")

提供众多的holder简化参数处理

![提供众多的holder简化参数处理](https://images.gitee.com/uploads/images/2019/0611/152341_7aff634a_1507575.png "holder.png")

