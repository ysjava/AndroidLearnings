package com.learndm.androidlearnings.pp.middle;

import android.util.Log;

import com.tamsiree.rxpay.alipay.Base64;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;

public class ee {
    public static String decryptFromBase64(String data, String key) {
        try {
            byte[] originalData = Base64.decode(data);
            byte[] valueByte = decrypt(originalData, key.getBytes("UTF-8"));
            return new String(valueByte, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("decrypt fail!", e);
        }
    }

    public static byte[] decrypt(byte[] data, byte[] key) {
        if (key.length != 16) {
            throw new RuntimeException("16161616");
        } else {
            try {
                SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
                byte[] enCodeFormat = secretKeySpec.getEncoded();
                SecretKeySpec seckey = new SecretKeySpec(enCodeFormat, "AES");
                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                IvParameterSpec iv = new IvParameterSpec(key);
                cipher.init(2, seckey, iv);
                byte[] result = cipher.doFinal(data);
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("decrypt fail!", e);
            }
        }
    }

    public static SSLSocketFactory getSSLSocketFactory()
    {
        try
        {
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, getTrustManager(), new SecureRandom());
            return sslContext.getSocketFactory();
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    //获取TrustManager
    private static TrustManager[] getTrustManager()
    {
        //就是把下面这个X509TrustManager更换一下
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager()
        {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType)
            {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType)
            {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers()
            {
                return new X509Certificate[]{};
            }
        }};
        return trustAllCerts;
    }



    public static OkHttpClient.Builder ignoreSSL (OkHttpClient.Builder builder) {
        builder.sslSocketFactory(createSSLSocketFactory(),new TrustAllManager())
                .hostnameVerifier((s, sslSession) -> true);
        return builder;
    }

    private static SSLSocketFactory createSSLSocketFactory () {

        SSLSocketFactory sSLSocketFactory = null;

        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new TrustAllManager()}, new SecureRandom());
            sSLSocketFactory = sc.getSocketFactory();
        } catch (Exception e) {
//            LOGGER.info(e.getMessage(), e);
        }

        return sSLSocketFactory;
    }

    private static class TrustAllManager implements X509TrustManager {

        @Override
        public void checkClientTrusted (java.security.cert.X509Certificate[] x509Certificates,
                                        String s) throws java.security.cert.CertificateException {

        }

        @Override
        public void checkServerTrusted (java.security.cert.X509Certificate[] x509Certificates,
                                        String s) throws java.security.cert.CertificateException {

        }


        @Override
        public java.security.cert.X509Certificate[] getAcceptedIssuers () {
            return new X509Certificate[0];
        }
    }

    public static String getMD5String(String str) {
        try {
            // 生成一个MD5加密计算摘要
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 计算md5函数
            md.update(str.getBytes());
            // digest()最后确定返回md5 hash值，返回值为**8位字符串**。因为md5 hash值是**16位的hex值**，实际上就是**8位的字符**
            // BigInteger函数则**将8位的字符串转换成16位hex值**，用**字符串**来表示；**得到字符串形式的hash值**
            //一个byte是八位二进制，也就是2位十六进制字符（2的8次方等于16的2次方）
            return new BigInteger(1, md.digest()).toString(16);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
