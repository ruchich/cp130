package edu.uw.ruc.order;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;



import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.uw.ext.framework.order.ClientOrder;
import edu.uw.ext.framework.order.ClientOrderCodec;

public class ClientOrderCodecImpl implements ClientOrderCodec {
	
	/**KeyStore type*/
	private static final String JCEKS = "JCEKS";
	
	/**AES cipher algo*/
	private static final String AES_ALGORITHM = "AES";
	
	/** key size in bits*/
	private static final int AES_KEY_SIZE = 128;
	
	/** key size in bytes*/
	
	private static final int AES_KEY_LEN = 16;
	
	/**Root Path order String*/
	private static final String ROOT_PATH = "/";
	
	/**Signing Algo*/
	private static final String SIGN_ALGORITHM = "MOSmithRSA";
	
	/**Structure to hold the componenets to be represented in file*/
	
	private static class CodecTriple{
		
		/** the shared encryption key, enciphered with the recipients private key*/
		public byte []encipheredSharedKey;
		
		/** The enciphered order data*/
		
		public byte [] ciphertext;
		
		/** Signature resulting from signing of the plain text order with the senders private key*/
		
		public byte [] signature;
		
	}
	
	/** constructor*/
	
	public ClientOrderCodecImpl(){
		
	}
	/**
	 * 
	 * @param storeName name for keystore
	 * @param storePasswd - Password for keystore
	 * @return
	 * @throws KeyStoreException
	 * @throws NoSuchAlgorithmException
	 * @throws CertificateException
	 * @throws IOException
	 */
	
	private static KeyStore loadKeyStore(final String storeName,final char[] storePasswd) throws
	KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException 
	{
		try (InputStream stream = ClientOrderCodec.class.getResourceAsStream(ROOT_PATH)) { 
			if (stream ==null){
				throw new KeyStoreException("Unable to locate keyStore resource" + storeName);
			}
			
			KeyStore keyStore = KeyStore.getInstance(JCEKS);
			keyStore.load(stream, storePasswd);
			return keyStore;
		}
	}

	
	public void encipher(final List<ClientOrder> orders, final File orderFile,
			final String senderKeystoreName,  final char[]senderKeyStorePasswd,
			final String senderKeyName,  final char[]senderKeyPasswd,
			final String senderTruststoreName,  final char[]senderTrustStorePasswd,
			final String receipientCertName)throws GeneralSecurityException, IOException{
		
		ObjectMapper mapper = new ObjectMapper();
		byte[] data = mapper.writeValueAsBytes(orders);
		CodecTriple triple = new CodecTriple();
		
		SecretKey sharedSecretKey = generateAESSecretKey();
		byte [] sharedSecretKeyBytes = sharedSecretKey.getEncoded();
		
		
		KeyStore senderTrustStore = loadKeyStore(senderTruststoreName, senderTrustStorePasswd);
		PublicKey key = senderTrustStore.getCertificate(receipientCertName).getPublicKey();
		triple.encipheredSharedKey = encipher(key, sharedSecretKeyBytes);
		triple.ciphertext = encipher(sharedSecretKey, data);
		
		triple.signature = sign(data,senderKeystoreName, senderKeyStorePasswd,
			senderKeyName, senderKeyPasswd);
		writeFile(orderFile, triple);
	}
	
	
/** generate AES Secret key*/
	
	private static SecretKey generateAESSecretKey()throws
	NoSuchAlgorithmException{
		KeyGenerator generator = KeyGenerator.getInstance(AES_ALGORITHM);
		generator.init(AES_KEY_SIZE);
		SecretKey key = generator.generateKey();
		return key;
		
	}
	
	/** encipher the provided dATA with provided key*/
	private static byte [] encipher(final Key cipherKey, final byte[]plaintext)throws
	  GeneralSecurityException, IOException{
		try{
			Cipher cipher = Cipher.getInstance(cipherKey.getAlgorithm());
			cipher.init(Cipher.ENCRYPT_MODE, cipherKey);
			byte [] ciphertext = cipher.doFinal(plaintext);
			return ciphertext;
		}catch (InvalidKeyException |NoSuchAlgorithmException |NoSuchPaddingException e){
			throw new GeneralSecurityException("Error encrypting data", e);
		}
		}
	
	/** encrypt the signature*/
	
	private static byte []sign( final byte [] data, final String signerKeyStoreName,
			final char [] signerStorePasswd,
			final String signerName, final char [] signerPsswd)
			throws GeneralSecurityException, IOException{
		byte [] signature;
		try{
			KeyStore clientKeyStore  = loadKeyStore(signerKeyStoreName,signerStorePasswd );
			PrivateKey privateKey = (PrivateKey)clientKeyStore.getKey(signerName, signerPsswd);
			if(privateKey ==null){
				throw new GeneralSecurityException("No key eaists named" + signerName);
			}
			
			Signature signer = Signature.getInstance(SIGN_ALGORITHM);
			signer.initSign(privateKey);
			signer.update(data);
			signature = signer.sign();
			return signature;
		}catch(KeyStoreException| UnrecoverableKeyException |InvalidKeyException |NoSuchAlgorithmException |CertificateException | SignatureException e){
			throw new GeneralSecurityException("Error signing order data", e);
	}
	}


/** writes byte array*/
private static void writeByteArray(final DataOutputStream out, final byte[]b)throws IOException{
	
	final int len = (b == null) ? -1 : b.length;
	out.writeInt(len);
	if(len>0){
		out.write(b);
		
	}
}

/** convenience method for reading byte array*/

private static byte[] readByteArray(final DataInputStream in)throws IOException{
	
	byte[] bytes = null;
	final int len = in.readInt();
	
	if(len>=0){
		bytes = new byte[len];
		in.readFully(bytes);
	}
	return bytes;
	
}

/** read an encrypted order list and signature from file and verify the order list data*/
public List<ClientOrder> decipher(
 final File orderFile,
final String receipientKeystoreName,  final char[]receipientKeyStorePasswd,
final String receipientKeyName,  final char[]receipientKeyPasswd,
final String trustStoreName,  final char[]trustStorePasswd,
final String signerCertName)throws GeneralSecurityException, IOException{


CodecTriple triple = readFile(orderFile);

KeyStore keyStore = loadKeyStore(receipientKeystoreName, receipientKeyStorePasswd);
Key skey = keyStore.getKey(receipientKeyName, receipientKeyPasswd);
byte [ ] encipheredSharedKeyBytes = decipher(skey, triple.encipheredSharedKey);

SecretKey sharedSecretKey = keyBytesToAesSecretKey(encipheredSharedKeyBytes);
byte []orderData = decipher(sharedSecretKey, triple.ciphertext);
boolean verified = verifySignature(orderData, triple.signature,trustStoreName, trustStorePasswd,
												signerCertName);

List<ClientOrder> orders = null;

if(verified){
	
	ObjectMapper mapper = new ObjectMapper();
	JavaType type = mapper.getTypeFactory().constructCollectionType(List.class, ClientOrder.class);
	try{
		orders = mapper.readValue(orderData, type);
	}catch (IOException e){
		throw new IOException("Error parsing order data, e");
	}
}else{
	throw new GeneralSecurityException("Signature verification failed");
}

return orders;

}

/** Read the enciphered key, data and the signature*/

private static CodecTriple readFile( final File OrderFile)throws IOException{
	try{
		FileInputStream inStrm = new FileInputStream(OrderFile);
		DataInputStream dataIn = new DataInputStream(inStrm);
		CodecTriple triple = new CodecTriple();
		triple.encipheredSharedKey = readByteArray(dataIn);
		triple.ciphertext = readByteArray(dataIn);
		triple.signature = readByteArray(dataIn);
		return triple;
	}catch (IOException e){
		throw new IOException("Error reading input file", e);
	}
}

/** Decipher the provoided data with the provided key*/

private static byte [] decipher(final Key cipherKey, final byte[]ciphertext)throws
GeneralSecurityException, IOException{
	try{
		Cipher cipher = Cipher.getInstance(cipherKey.getAlgorithm());
		cipher.init(Cipher.DECRYPT_MODE, cipherKey);
		byte [] plaintext = cipher.doFinal(ciphertext);
		return  plaintext;
	}catch (InvalidKeyException |NoSuchAlgorithmException |NoSuchPaddingException e){
		throw new GeneralSecurityException("Error encrypting data", e);
	}
	}

/** reconstitutes an AES secret key to bytes*/

private static SecretKey keyBytesToAesSecretKey(final byte [] keyBytes)throws
NoSuchAlgorithmException{
	KeyGenerator generator = KeyGenerator.getInstance(AES_ALGORITHM);
	generator.init(AES_KEY_SIZE);
	SecretKey secKey = new SecretKeySpec(keyBytes, 0, AES_KEY_LEN, AES_ALGORITHM);
	return secKey;
	
}

/** Verify Signature*/
private static boolean verifySignature( final byte[] data, final byte []signature,final String trustStoreName,
						final char[] trustStorePasswd, final String signerPubKeyName)throws
						GeneralSecurityException, IOException{
	try{
		KeyStore clientTrustStore = loadKeyStore(trustStoreName, trustStorePasswd);
		Signature  verifier = Signature.getInstance(SIGN_ALGORITHM);
		Certificate cert = clientTrustStore.getCertificate(signerPubKeyName);
		PublicKey publicKey = cert.getPublicKey();
		verifier.initVerify(publicKey);
		verifier.update(data);
		return verifier.verify(signature);
		
	}catch (KeyStoreException| IOException | CertificateException e){
		throw new GeneralSecurityException("unable to retreive signing key", e);
	}catch (InvalidKeyException |NoSuchAlgorithmException |SignatureException e){
		throw new GeneralSecurityException("Invalid signing key", e);
}
}
	private static void writeFile(final File orderFile, final CodecTriple triple)throws IOException{
		try(
			FileOutputStream fout = new FileOutputStream(orderFile);
			DataOutputStream dout = new DataOutputStream(fout)){
			writeByteArray(dout, triple.ciphertext);
			writeByteArray(dout, triple.encipheredSharedKey);
			writeByteArray(dout, triple.signature);
			dout.flush();
		}catch(IOException e){
			throw new IOException("Error attempting write order file", e);
		}
			
			
		}
	}

