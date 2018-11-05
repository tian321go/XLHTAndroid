package com.axeac.android.sdk.jhsp;

import android.content.Context;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.axeac.android.sdk.tools.BigInteger;
import com.axeac.android.sdk.tools.Property;
import com.axeac.android.sdk.tools.StringUtil;

public class Certificate {

	private String certId;
	private String version;
	private String company;
	private String country;
	private String funcion;
	private String publicKey;
	private String modulus;
	private String clientType;
	private String expired;
	private String specialCode;
	private String productId;
	private String verify;

	private boolean finished = false;

	public static Certificate getCertficateForData(Context ctx) {
		Certificate certificate = null;
		try {
			byte[] bs = StringUtil.load(ctx.getApplicationContext().getAssets().open("khmap5.cert"));
			BigInteger bi = new BigInteger(String.valueOf(bs.length));
			String content = StringUtil.encryptXOR(new String(bs), StringUtil.encodeMD5(bi.toString(13)));
			certificate = parse(content);
		} catch (Exception e) {
			Log.e("", e.getMessage(), e);
		}
		return certificate;
	}

	public String getCertId() {
		verify();
		return certId;
	}

	public void setCertId(String certId) {
		this.certId = certId;
	}

	public String getVersion() {
		verify();
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getCompany() {
		verify();
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getCountry() {
		verify();
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getFuncion() {
		verify();
		return funcion;
	}

	public void setFuncion(String funcion) {
		this.funcion = funcion;
	}

	public String getPublicKey() {
		verify();
		return publicKey;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

	public String getModulus() {
		verify();
		return modulus;
	}

	public void setModulus(String modulus) {
		this.modulus = modulus;
	}

	public String getClientType() {
		verify();
		return clientType;
	}

	public void setClientType(String clientType) {
		this.clientType = clientType;
	}

	public String getExpired() {
		verify();
		return expired;
	}

	public void setExpired(String expired) {
		this.expired = expired;
	}

	public String getSpecialCode() {
		verify();
		return specialCode;
	}

	public void setSpecialCode(String specialCode) {
		this.specialCode = specialCode;
	}

	public String getProductId() {
		verify();
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getVerify() {
		return verify;
	}

	public void setVerify(String verify) {
		this.verify = verify;
	}

	private void verify() {
		if (!finished)
			return;
		String s = StringUtil.encodeMD5(certId + version
				+ StringUtil.toUTF(company) + country
				+ funcion + publicKey
				+ modulus + clientType
				+ expired + specialCode
				+ productId);
		if(!s.equals(verify)){
			throw new RuntimeException("证书信息已被修改!");
		}
	}

	public void finished() {
		finished = true;
		verify();
	}

	public static String read(String path) {
		byte[] bs = null;
		try {
			bs = StringUtil.load(new FileInputStream(path));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		BigInteger bi = new BigInteger(String.valueOf(bs.length));
		String key = StringUtil.encodeMD5(bi.toString(13));
		String val = StringUtil.encryptXOR(new String(bs), key);
		return val;
	}

	public static Certificate parse(String certData) {
		Property map;
		if (certData.indexOf("\r\n") == -1) {
			map = new Property(certData, "\n");
		} else {
			map = new Property(certData);
		}
		Certificate cert = new Certificate();
		cert.setCertId(map.getProperty("certId"));
		cert.setVersion(map.getProperty("version"));
		cert.setCompany(map.getProperty("company"));
		cert.setCountry(map.getProperty("country"));
		cert.setFuncion(map.getProperty("funcion"));
		cert.setPublicKey(map.getProperty("publicKey"));
		cert.setModulus(map.getProperty("modulus"));
		cert.setClientType(map.getProperty("clientType"));
		cert.setExpired(map.getProperty("expired"));
		cert.setSpecialCode(map.getProperty("specialCode"));
		cert.setProductId(map.getProperty("productId"));
		cert.setVerify(map.getProperty("verify"));
		cert.finished();
		return cert;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("certId = " + this.getCertId() + "\r\n");
		sb.append("version = " + this.getVersion() + "\r\n");
		sb.append("company = " + StringUtil.toUTF(this.getCompany()) + "\r\n");
		sb.append("country = " + this.getCountry() + "\r\n");
		sb.append("funcion = " + this.getFuncion() + "\r\n");
		sb.append("publicKey = " + this.getPublicKey() + "\r\n");
		sb.append("modulus = " + this.getModulus() + "\r\n");
		sb.append("clientType = " + this.getClientType() + "\r\n");
		sb.append("expired = " + this.getExpired() + "\r\n");
		sb.append("specialCode = " + this.getSpecialCode() + "\r\n");
		sb.append("productId = " + this.getProductId() + "\r\n");
		return sb.toString();
	}
}