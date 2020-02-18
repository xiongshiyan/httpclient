package top.jfunc.common.http.ssl;

import top.jfunc.common.utils.ArrayUtil;
import top.jfunc.common.utils.StrUtil;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.Objects;

/**
 * SSLSocketFactory构建器
 * @author Looly
 * @author xiongshiyan 增加证书相关方法
 */
public class SSLSocketFactoryBuilder {

    /** Supports some version of SSL; may support other versions */
	public static final String SSL    = "SSL";
	/** Supports SSL version 2 or later; may support other versions */
	public static final String SSLv2  = "SSLv2";
	/** Supports SSL version 3; may support other versions */
	public static final String SSLv3  = "SSLv3";
	
	/** Supports some version of TLS; may support other versions */
	public static final String TLS    = "TLS";
	/** Supports RFC 2246: TLS version 1.0 ; may support other versions */
	public static final String TLSv1  = "TLSv1";
	/** Supports RFC 4346: TLS version 1.1 ; may support other versions */
	public static final String TLSv11 = "TLSv1.1";
	/** Supports RFC 5246: TLS version 1.2 ; may support other versions */
	public static final String TLSv12 = "TLSv1.2";


	/**
	 * 证书类型
	 */
	public static final String JKS = "JKS";
	public static final String PKCS12 = "PKCS12";

	private String protocol = TLS;
	private String certType = PKCS12;
	private KeyManager[] keyManagers;
	private TrustManager[] trustManagers = {new DefaultTrustManager()};
	private SecureRandom secureRandom  = new SecureRandom();
	
	
	/**
	 * 创建 SSLSocketFactoryBuilder
	 * @return SSLSocketFactoryBuilder
	 */
	public static SSLSocketFactoryBuilder create(){
		return new SSLSocketFactoryBuilder();
	}
	
	/**
	 * 设置协议
	 * @param protocol 协议
	 * @return 自身
	 */
	public SSLSocketFactoryBuilder setProtocol(String protocol){
		if(StrUtil.isNotBlank(protocol)){
			this.protocol = protocol;
		}
		return this;
	}

	/**
	 * 设置证书类型
	 * @param certType 证书类型
	 */
	public SSLSocketFactoryBuilder setCertType(String certType) {
		if(StrUtil.isNotBlank(certType)){
			this.certType = certType;
		}
		return this;
	}

	/**
	 * 设置信任信息
	 * @param trustManagers TrustManager列表
	 * @return 自身
	 */
	public SSLSocketFactoryBuilder setTrustManagers(TrustManager... trustManagers) {
		if (ArrayUtil.isNotEmpty(trustManagers)) {
			this.trustManagers = trustManagers;
		}
		return this;
	}

	public TrustManager[] getTrustManagers() {
		return trustManagers;
	}

	/**
	 * 设置 JSSE key managers
	 * @param keyManagers JSSE key managers
	 * @return 自身
	 */
	public SSLSocketFactoryBuilder setKeyManagers(KeyManager... keyManagers) {
		if (ArrayUtil.isNotEmpty(keyManagers)) {
			this.keyManagers = keyManagers;
		}
		return this;
	}
	
	/**
	 * 设置 SecureRandom
	 * @param secureRandom SecureRandom
	 * @return 自己
	 */
	public SSLSocketFactoryBuilder setSecureRandom(SecureRandom secureRandom){
		if(null != secureRandom){
			this.secureRandom = secureRandom;
		}
		return this;
	}
	
	/**
	 * 构建SSLSocketFactory
	 * @return SSLSocketFactory
	 */
	public SSLSocketFactory build() {
		return getSSLContext().getSocketFactory();
	}

	/**
	 * 用于ssl双向认证，例如微信退款应用中
	 * 用证书和密码构建SSLSocketFactory
	 * @param certPath 证书路径
	 * @param certPass 证书密码
	 */
	public SSLSocketFactory build(String certPath, String certPass){
		return getSSLContext(certPath , certPass).getSocketFactory();
	}
	public SSLSocketFactory buildWithClassPathCert(Class<?> clazz , String certPath, String certPass){
		return getClassPathSSLContext(clazz , certPath , certPass).getSocketFactory();
	}

    /**
     * @param inputStream 需要自行关闭流
     * @param certPass 密码
     */
	public SSLSocketFactory build(InputStream inputStream, String certPass){
		return getSSLContext(inputStream , certPass).getSocketFactory();
	}


	public SSLContext getSSLContext(){
		try {
			SSLContext sslContext = SSLContext.getInstance(protocol);
			sslContext.init(this.keyManagers, this.trustManagers, this.secureRandom);
			return sslContext;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
     * 使用FileInputStream来加载资源
	 * @param certPath 证书路径
	 * @param certPass 证书密码
	 */
	public SSLContext getSSLContext(String certPath, String certPass){
	    try(InputStream inputStream = new FileInputStream(certPath)){
	        return getSSLContext(inputStream , certPass);}
	    catch (Exception e){
            throw new RuntimeException(e);
        }
	}
	/**
     * 使用class来加载资源
	 * @param clazz 用于加载资源 /开头的话就从classpath目录下，否则从class包下
	 *              @see Class#getResourceAsStream(String)
	 * @param certPath classpath 证书路径
	 * @param certPass 证书密码
	 */
	public SSLContext getClassPathSSLContext(Class<?> clazz , String certPath, String certPass){
        try(InputStream inputStream = clazz.getResourceAsStream(certPath)){
            return getSSLContext(inputStream , certPass);}
        catch (Exception e){
            throw new RuntimeException(e);
        }
	}

	/**
	 * @param certStream 证书流，本方法不会关闭流
	 * @param certPass 密码
	 */
	public SSLContext getSSLContext(InputStream certStream, String certPass){
		InputStream inputStream = Objects.requireNonNull(certStream);

		try {
			KeyStore clientStore = KeyStore.getInstance(certType);
			char[] passArray = certPass.toCharArray();
			clientStore.load(inputStream, passArray);

			KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			kmf.init(clientStore, passArray);
			KeyManager[] kms = kmf.getKeyManagers();
			//"TLSv1"
			SSLContext sslContext = SSLContext.getInstance(protocol);

			sslContext.init(kms, this.trustManagers, this.secureRandom);

			/// 谁打开谁关闭
			//inputStream.close();

			return sslContext;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}