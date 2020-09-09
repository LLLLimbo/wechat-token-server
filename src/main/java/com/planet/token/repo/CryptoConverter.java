package com.planet.token.repo;

import cn.hutool.core.util.StrUtil;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.persistence.AttributeConverter;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@SuppressWarnings("unused")
public class CryptoConverter implements AttributeConverter<String, String> {

    private static final String AES = "AES";
    private static final String SECRET = "PLANET-KEY-12345";

    @Override
    public String convertToDatabaseColumn(String token) {
        if (StrUtil.isEmpty(token)){
            return token;
        }
        try {
            final Key key = new SecretKeySpec(SECRET.getBytes(), AES);
            final Cipher cipher = Cipher.getInstance(AES);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return Base64.getEncoder().encodeToString(cipher.doFinal(token.getBytes()));
        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if (StrUtil.isEmpty(dbData)) {
            return dbData;
        }
        try {
            final Key key = new SecretKeySpec(SECRET.getBytes(), AES);
            final Cipher cipher = Cipher.getInstance(AES);
            cipher.init(Cipher.DECRYPT_MODE, key);
            return new String(cipher.doFinal(Base64.getDecoder().decode(dbData)));
        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException e) {
            throw new IllegalStateException(e);
        }
    }
}
