接口及实现类整体鸟瞰：

![接口及实现类整体鸟瞰](https://images.gitee.com/uploads/images/2020/0323/162815_151f96c9_1507575.png "all.png") "all.png")

用户接口设计：

![用户接口设计](https://images.gitee.com/uploads/images/2020/0323/162954_2b32dd37_1507575.png "Facade-SmartHttpClient.png")
![用户接口设计](https://images.gitee.com/uploads/images/2020/0323/163009_d826c9c7_1507575.png "Facade-SimpleHttpClient-HttpRequestHttpClient-SmartHttpClient.png")

OkHttp3实现主线：

![OkHttp3主线](https://images.gitee.com/uploads/images/2020/0323/163029_2e921e57_1507575.png "OkHttp3SmartHttpClient.png")

ApacheHttpClient实现主线：

![ApacheHttpClient实现主线](https://images.gitee.com/uploads/images/2020/0323/163044_90d59122_1507575.png "ApacheSmartHttpClient.png")

HttpURLConnection实现主线：

![HttpURLConnection实现主线](https://images.gitee.com/uploads/images/2020/0323/163105_61b5f1c7_1507575.png "NativeSmartHttpClient.png")

JoddHttp实现主线：

![JoddHttp实现主线](https://images.gitee.com/uploads/images/2020/0323/163123_712e90ed_1507575.png "JoddSmartHttpClient.png")

代表所有参数的Request类：

![Request](https://images.gitee.com/uploads/images/2020/0323/163153_cba0147d_1507575.png "Request.png")

HttpRequest接口体系，使用这些请求类意义更明确：

![HttpRequest接口体系，使用这些请求类意义更明确](https://images.gitee.com/uploads/images/2020/0323/163216_f6a410dd_1507575.png "HttpRequest体系.png")

HttpRequest-HolderHttpRequest接口体系，基于Holder的HttpRequest：

![HttpRequest-HolderHttpRequest接口体系，基于Holder的HttpRequest](https://images.gitee.com/uploads/images/2020/0323/163233_73e00878_1507575.png "HttpRequest-HolderHttpRequest体系.png")

提供众多的holder简化参数处理

![提供众多的holder简化参数处理](https://images.gitee.com/uploads/images/2020/0323/163254_93aea0ae_1507575.png "holder.png")

