package cn.zytx.common.http.base.ssl;

import cn.zytx.common.utils.ArrayUtil;
import cn.zytx.common.utils.StrUtil;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Objects;

/**
 * SSLSocketFactory构建器
 * @author Looly
 * @author xiongshiyan 增加证书相关方法
 */
public class SSLSocketFactoryBuilder{

    /** Supports some version of SSL; may support other versions */
	public static final String SSL = "SSL";
	/** Supports SSL version 2 or later; may support other versions */
	public static final String SSLv2 = "SSLv2";
	/** Supports SSL version 3; may support other versions */
	public static final String SSLv3 = "SSLv3";
	
	/** Supports some version of TLS; may support other versions */
	public static final String TLS = "TLS";
	/** Supports RFC 2246: TLS version 1.0 ; may support other versions */
	public static final String TLSv1 = "TLSv1";
	/** Supports RFC 4346: TLS version 1.1 ; may support other versions */
	public static final String TLSv11 = "TLSv1.1";
	/** Supports RFC 5246: TLS version 1.2 ; may support other versions */
	public static final String TLSv12 = "TLSv1.2";

	private String protocol = TLS;
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
	public SSLSocketFactory buildWithClassPathCert(String certPath, String certPass){
		return getClassPathSSLContext(certPath , certPass).getSocketFactory();
	}
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
	 * @param certPath 证书路径
	 * @param certPass 证书密码
	 */
	public SSLContext getSSLContext(String certPath, String certPass){
		try {
			InputStream inputStream = new FileInputStream(certPath);
			return getSSLContext(inputStream , certPass);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 * @param certPath classpath 证书路径
	 * @param certPass 证书密码
	 */
	public SSLContext getClassPathSSLContext(String certPath, String certPass){
		try {
			InputStream inputStream = SSLSocketFactoryBuilder.class.getResourceAsStream(certPath);
			return getSSLContext(inputStream , certPass);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @param certStream 证书流，本方法会自动关闭
	 * @param certPass 密码
	 */
	public SSLContext getSSLContext(InputStream certStream, String certPass){
		InputStream inputStream = Objects.requireNonNull(certStream);

		try {
			KeyStore clientStore = KeyStore.getInstance("PKCS12");
			char[] passArray = certPass.toCharArray();
			clientStore.load(inputStream, passArray);

			KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			kmf.init(clientStore, passArray);
			KeyManager[] kms = kmf.getKeyManagers();
			//"TLSv1"
			SSLContext sslContext = SSLContext.getInstance(protocol);

			sslContext.init(kms, this.trustManagers, this.secureRandom);

			inputStream.close();

			return sslContext;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}