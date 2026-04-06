package com.albee.albeepoint.api.util;



import com.albeepoint.core.biz.org.models.dto.OrgSearch;
import com.albeepoint.core.biz.org.models.entity.OrgMstEntity;
import com.albeepoint.core.biz.org.service.OrgMstService;
import com.albeepoint.core.common.enc.models.dto.EncInfoSearch;
import com.albeepoint.core.common.enc.models.entity.EncInfoEntity;
import com.albeepoint.core.common.enc.service.EncInfoService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.util.Base64;

import static com.albeepoint.core.common.models.ErrorCode.BIZ_ERR_001033;

/*
    MariaDb 기본 암호화 모드 : block_encryption_mode :	aes-128-ecb (AES, 암호키길이 16(128비트), ECB)
 */
@Component
public class EncUtil {

    private static EncInfoService encInfoService = null;
    private static OrgMstService orgMstService = null;

    public static final Logger log = LogManager.getLogger(EncUtil.class);

    public EncUtil(EncInfoService encInfoService, OrgMstService orgMstService) {
        this.encInfoService = encInfoService;
        this.orgMstService = orgMstService;
    }

    public static String getSalt() {

        //1. Random, byte 객체 생성
        SecureRandom r = new SecureRandom ();
        byte[] salt = new byte[20];

        //2. 난수 생성
        r.nextBytes(salt);

        //3. byte To String (10진수의 문자열로 변경)
        StringBuffer sb = new StringBuffer();
        for(byte b : salt) {
            sb.append(String.format("%02x", b));
        };

        return sb.toString().substring(0, 16);
    }

    public static String encryptSha256(String pwd, String salt) {

        String result = "";
        try {
            //1. SHA256 알고리즘 객체 생성
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            //2. pwd와 salt 합친 문자열에 SHA 256 적용
            System.out.println("pwd + salt 적용 전 : " + pwd+salt);
            md.update((pwd+salt).getBytes());
            byte[] pwdsalt = md.digest();

            //3. byte To String (10진수의 문자열로 변경)
            StringBuffer sb = new StringBuffer();
            for (byte b : pwdsalt) {
                sb.append(String.format("%02x", b));
            }

            result=sb.toString();
            System.out.println("pwd + salt 적용 후 : " + result);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static String encryptAes256ByOrgNo(String text, Long orgNo){
        EncInfoDto encInfo = encInfoService.getEncInfo(new EncInfoSearchDto("ORG", orgNo.toString()));
        VdUtil.emptyEc(encInfo, BIZ_ERR_001033, "암호화 정보 없음");
        return encryptAes256(text, encInfo.getEncKey());
    }

    public static String encryptAes256ByOrgCd(String text, String orgCd){
        OrgMstEntity org = orgMstService.getOrgMstWec(new OrgSearch(orgCd));
        EncInfoDto encInfo = encInfoService.getEncInfo(new EncInfoSearchDto("ORG", org.getOrgNo().toString()));
        VdUtil.emptyEc(encInfo, BIZ_ERR_001033, "암호화 정보 없음");
        return encryptAes256(text, encInfo.getEncKey());
    }

    /*
        EncUtil.encryptAes256("평문", ENC_INFO.ENC_KEY));
     */
    public static String encryptAes256(String text, String encKey){
        final String iv = encKey.substring(0, 16);
        Cipher cipher = null;
        byte[] encrypted = null;
        String encStr = "";
        try {
            cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            SecretKeySpec keySpec = new SecretKeySpec(iv.getBytes("UTF-8"), "AES");
            // IvParameterSpec ivParamSpec = new IvParameterSpec(iv.getBytes());
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);

            encrypted = cipher.doFinal(text.getBytes("UTF-8"));
            encStr =  Base64.getEncoder().encodeToString(encrypted);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
       // } catch (InvalidAlgorithmParameterException e) {
       //     throw new RuntimeException(e);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        }

        return encStr;
    }

    public static String decryptAes256ByOrgNo(String cipherText, Long orgNo) {
        EncInfoDto encInfo = encInfoService.getEncInfo(new EncInfoSearchDto("ORG", orgNo.toString()));
        VdUtil.emptyEc(encInfo, BIZ_ERR_001033, "암호화 정보 없음");
        return decryptAes256(cipherText, encInfo.getEncKey());
    }

    public static String decryptAes256ByOrgCd(String cipherText, String orgCd) {
        OrgMstEntity org = orgMstService.getOrgMstWec(new OrgSearch(orgCd));
        EncInfoDto encInfo = encInfoService.getEncInfo(new EncInfoSearchDto("ORG", org.getOrgNo().toString()));
        VdUtil.emptyEc(encInfo, BIZ_ERR_001033, "암호화 정보 없음");
        return decryptAes256(cipherText, encInfo.getEncKey());
    }

    /*

        EncUtil.decryptAes256("암호화된스트링", ENC_INFO.ENC_KEY));
     */
    public static String decryptAes256(String cipherText, String encKey) {
        final String iv = encKey.substring(0, 16);
        Cipher cipher = null;
        byte[] decrypted = null;
        String decStr = "";
        try {
            cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            SecretKeySpec keySpec = new SecretKeySpec(iv.getBytes(), "AES");
            // IvParameterSpec ivParamSpec = new IvParameterSpec(iv.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, keySpec);

            byte[] decodedBytes = Base64.getDecoder().decode(cipherText);
            decrypted = cipher.doFinal(decodedBytes);
            decStr = new String(decrypted, "UTF-8");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        //} catch (InvalidAlgorithmParameterException e) {
        //    throw new RuntimeException(e);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return decStr;
    }
}
