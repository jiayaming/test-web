package com.jiayaming.dubbo.web.util;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.alibaba.fastjson.JSONObject;

import org.apache.commons.codec.binary.Base64;


/**
 * 
 * aes加密
 * @version [版本号，默认V1.0.0]
 * @Credited 2016年5月17日 下午1:15:08
 * @see       [相关类/方法]
 * @since     [产品/模块版本]
 */
public class CryptAES {


	private static final String AESTYPE = "AES/ECB/PKCS5Padding";
	private static final String KEYSTR = "tkhhealthcare777";

	/**
	 * 
	 * 描述：加密
	 * @param keyStr
	 * @param plainText
	 * @return 
	 * @Credited 2016年5月17日 下午1:19:57
	 * @version [版本号，默认V1.0.0]
	 */
	public static String AES_Encrypt(String keyStr, String plainText) {
		byte[] encrypt = null;
		try {
			Key key = generateKey(keyStr);
			Cipher cipher = Cipher.getInstance(AESTYPE);
			cipher.init(Cipher.ENCRYPT_MODE, key);
			encrypt = cipher.doFinal(plainText.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new String(Base64.encodeBase64(encrypt));
	}
	
	
	public static String AES_Encrypt(String plainText) {
		byte[] encrypt = null;
		try {
			Key key = generateKey(KEYSTR);
			Cipher cipher = Cipher.getInstance(AESTYPE);
			cipher.init(Cipher.ENCRYPT_MODE, key);
			encrypt = cipher.doFinal(plainText.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new String(Base64.encodeBase64(encrypt));
	}

	/**
	 * 
	 * 描述：解密
	 * @param keyStr
	 * @param encryptData
	 * @return 
	 * @Credited 2016年5月17日 下午1:19:45
	 * @version [版本号，默认V1.0.0]
	 */
	public static String AES_Decrypt(String keyStr, String encryptData) {
		byte[] decrypt = null;
		try {
			Key key = generateKey(keyStr);
			Cipher cipher = Cipher.getInstance(AESTYPE);
			cipher.init(Cipher.DECRYPT_MODE, key);
			decrypt = cipher
					.doFinal(Base64.decodeBase64(encryptData.getBytes()));
		} catch (Exception e) {
			;
		}
		return new String(decrypt).trim();
	}
	
	public static String AES_Decrypt(String encryptData) {
		byte[] decrypt = null;
		try {
			Key key = generateKey(KEYSTR);
			Cipher cipher = Cipher.getInstance(AESTYPE);
			cipher.init(Cipher.DECRYPT_MODE, key);
			decrypt = cipher
					.doFinal(Base64.decodeBase64(encryptData.getBytes()));
		} catch (Exception e) {
			;
		}
		return new String(decrypt).trim();
	}

	/**
	 * 
	 * 描述：封装key值
	 * @param key
	 * @return
	 * @throws Exception 
	 * @Credited 2016年5月17日 下午1:19:23
	 * @version [版本号，默认V1.0.0]
	 */
	private static Key generateKey(String key) throws Exception {
		
		try {
			SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
			return keySpec;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

	}
	
	/**
	 * @Description:AES解密,可设置秘钥任意长度
	 * @time:2016年11月16日
	 * @return:String
	 */
	public static String decrypt(String content, String key){
		if(content.length() < 1){
			return null;
		}
		byte[] byteResult = new byte[content.length() /2];
		for(int i = 0 ; i < content.length() / 2 ; i++){
			int high = Integer.parseInt(content.substring(i * 2, i * 2 + 1), 16);
			int low = Integer.parseInt(content.substring(i * 2 + 1, i *2 + 2), 16);
			byteResult[i] = (byte)(high * 16 + low);
		}
		try{
			KeyGenerator kgen = KeyGenerator.getInstance("AES");
			SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG" ); 
			secureRandom.setSeed(key.getBytes());
			kgen.init(128, secureRandom);
			SecretKey secretKey = kgen.generateKey();
			byte[] enCodeFormat = secretKey.getEncoded();
			SecretKeySpec secretKeySpec = new SecretKeySpec(enCodeFormat, "AES");
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
			byte[] result = cipher.doFinal(byteResult);
			return new String(result);
		}catch(NoSuchAlgorithmException e){
			e.printStackTrace();
		}catch(NoSuchPaddingException e){
			e.printStackTrace();
		}catch(InvalidKeyException e){
			e.printStackTrace();
		}catch(IllegalBlockSizeException e){
			e.printStackTrace();
		}catch(BadPaddingException e){
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * @Description:AES加密,可设置秘钥任意长度
	 * @time:2016年11月16日
	 * @return:String
	 */
	public static String encrypt(String content, String key){
		try{
			KeyGenerator kgen = KeyGenerator.getInstance("AES");
			SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG" ); 
			secureRandom.setSeed(key.getBytes());
			kgen.init(128, secureRandom);
			SecretKey secretKey = kgen.generateKey();
			byte[] enCodeFormat = secretKey.getEncoded();
			SecretKeySpec secretKeySpec = new SecretKeySpec(enCodeFormat, "AES");
			Cipher cipher = Cipher.getInstance("AES");
			byte[] byteContent = content.getBytes("utf-8");
			cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
			byte[] byteResult = cipher.doFinal(byteContent);
			StringBuffer sb = new StringBuffer();
			for(int i = 0; i < byteResult.length; i++){
				String hex = Integer.toHexString(byteResult[i] & 0xFF);
				if(hex.length() == 1){
					hex = '0' + hex;
				}
				sb.append(hex.toUpperCase());
			}
			return sb.toString();
		}catch(NoSuchAlgorithmException e){
			e.printStackTrace();
		}catch(NoSuchPaddingException e){
			e.printStackTrace();
		}catch(InvalidKeyException e){
			e.printStackTrace();
		}catch(UnsupportedEncodingException e){
			e.printStackTrace();
		}catch(IllegalBlockSizeException e){
			e.printStackTrace();
		}catch(BadPaddingException e){
			e.printStackTrace();
		}
		return null;
	}
	
	
	public static void main(String[] args) throws Exception {
		/*String keyStr = "tkhhealthcare777";
		System.out.println(keyStr);
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("uid", "c1543e4d-41df-adf4-0a73-03ad9b50c338");
		String encText = AES_Encrypt(keyStr, jsonObj.toString());
		System.out.println(encText);
		String decText = AES_Decrypt(keyStr, encText);
		System.out.println(decText);*/
		String keyStr = "tkhyhyaeskey";
		System.out.println(keyStr);
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("requestid", "c1543e4d-41df-adf4-0a73-03ad9b50c338");
		jsonObj.put("username", "yuxin001");
		jsonObj.put("password", "123abc");
		jsonObj.put("orgid", 2);
		jsonObj.put("requestTimeA", "20160701121021");
		jsonObj.put("requestTimeB", "20161125201021");
		String encText = encrypt(jsonObj.toString(), keyStr);
		System.out.println(encText);
		String decText = decrypt(encText, keyStr);
		System.out.println(decText);
	}
}
