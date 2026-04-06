package com.albee.albeepoint.api.util;

import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;

@Log4j2
public class StrUtil {

    public static String byteSubString(String str, int sPoint, int length){
        String result = "";
        String EncodingLang = "euc-kr";
        try {
            byte[] bytes = str.getBytes(EncodingLang);

            byte[] value = new byte[length];

            if (bytes.length < sPoint + length) {
                throw new Exception("Length of bytes is less. length : " + bytes.length + " sPoint : " + sPoint + " length : " + length);
            }

            for (int i = 0; i < length; i++) {
                value[i] = bytes[sPoint + i];
            }
            result = new String(value, EncodingLang);
        }catch(Exception e){
            e.printStackTrace();
        }

        return result;
    }

    public static String leftTrim(String val){
        String result = "";
        boolean endYn = false;
        char[] chars = val.toCharArray();
        for(int i = 0; i < chars.length; i++){
            if(chars[i] == ' ' && !endYn){
                continue;
            }else{
                endYn = true;
            }

            result = result + chars[i];
        }

        return result;
    }


    public static String rightTrim(String val){
        String result = "";
        boolean endYn = false;
        char[] chars = val.toCharArray();
        for(int i = chars.length - 1; i >= 0; i--){
            if(chars[i] == ' ' && !endYn){
                continue;
            }else{
                endYn = true;
            }

            result = result + chars[i];
        }

        return result;
    }


    public static String toCamel(String val){
        String camelStr = "";

        if(val == null || val.trim() == "" || val.contains(" ") || val.contains("__")){
            return val;
        }

        char prvChar = ' ';
        char[] chars = val.toCharArray();

        for(int i = 0; i < chars.length; i++){
            if(prvChar == ' '){
                camelStr = camelStr + String.valueOf(chars[i]).toLowerCase();
            }else if(prvChar == '_'){
                camelStr = camelStr + String.valueOf(chars[i]).toUpperCase();
            }else if(prvChar != '_'){
                camelStr = camelStr + String.valueOf(chars[i]).toLowerCase();
            }

            prvChar = chars[i];
        }

        return camelStr;
    }

    public static String toSnake(String val){
        String snakeStr = "";

        if(val == null || val.trim() == "" || val.contains(" ") || val.contains("__")){
            return val;
        }

        char prvChar = ' ';
        char[] chars = val.toCharArray();

        for(int i = 0; i < chars.length; i++){
            if(prvChar == ' '){
                snakeStr = snakeStr + String.valueOf(chars[i]).toUpperCase();
            }else if(chars[i] >= 'A' && chars[i] <= 'Z'){
                snakeStr = snakeStr + "_" + String.valueOf(chars[i]).toUpperCase();
            }else{
                snakeStr = snakeStr + String.valueOf(chars[i]).toUpperCase();
            }

            prvChar = chars[i];
        }

        return snakeStr;
    }


    public static String leftPad(String val, int len, String r){
        if(val == null || val.trim().equals("") || len <= 0 || r == null){
            return val;
        }

        String val1 = val.trim();
        int gap = len - val.length();
        if(gap <= 0){
            return val;
        }

        String padStr = "";
        char[] chars = val1.toCharArray();
        for(int i = 0; i < len ; i++){
            if(i < gap){
                padStr = padStr + r;
            }else{
                padStr = padStr + chars[i - gap];
            }
        }

        return padStr;
    }


    public static String rightPad(String val, int len, String r){
        if(val == null || val.trim().equals("") || len <= 0 || r == null){
            return val;
        }

        String val1 = val.trim();
        int gap = len - val.length();
        if(gap <= 0){
            return val;
        }

        String padStr = "";
        char[] chars = val1.toCharArray();
        for(int i = 0; i < len ; i++){
            if(i >= val1.length()){
                padStr = padStr + r;
            }else{
                padStr = padStr + chars[i];
            }
        }

        return padStr;
    }



    public static String toComma(String val){
        String commaStr = "";
        if(val == null || val.length() <= 3){
            return val;
        }

        val = val.trim().replaceAll("-", "").replaceAll(",", "");
        String s = val.charAt(0) == '-' ? "-" : "";
        String prvNumStr = "";
        String afrNumStr = "";
        int a = 1;
        char[] valChar = val.toCharArray();
        for(int i = 0; i < valChar.length; i++){
            if(valChar[i] == '.'){
                a = 2;
            }

            if(a == 1){
                prvNumStr = prvNumStr + valChar[i];
            }else{
                afrNumStr = afrNumStr + valChar[i];
            }
        }

        char[] prvNumChar = prvNumStr.toCharArray();
        int j = 1;
        for(int i = prvNumChar.length - 1; i >= 0; i--){
            if(i % 3 == 0 && i > 1 && i > 0){
                commaStr = "," + commaStr;
            }
            commaStr = prvNumChar[i] + commaStr;
            j++;
        }

        return s + commaStr + afrNumStr;
    }

    public static String enumToString(Enum e){
        if(e == null){
            return null;
        }

        return e.toString();
    }

    public static Boolean enumEquals(Object a, Object b){
        String str1 = !a.getClass().isEnum() ? String.valueOf(a).toString() : a.toString();
        String str2 = !b.getClass().isEnum() ? String.valueOf(b).toString() : b.toString();

        return str1.equals(str2);
    }


    public static List<String> parseJunmun(String src, Integer[] layoutAry) {
        int startIdx = 0;
        int endIdx = 0;
        List<String> result = new ArrayList<>();

        for (int i = 0; 1 < layoutAry.length; i++) {
            endIdx = layoutAry[i];
            result.add(byteSubString(src, startIdx, endIdx));
            startIdx = startIdx + layoutAry[i];
        }

        return result;
    }

    public static <T> void println(List<T> val){
        if(val == null) return;
        for(T obj : val) log.info(obj.toString());
    }

    public static <T> void println(List<T> val, String comment){
        log.info(comment);
        if(val == null) return;
        for(T obj : val) log.info(obj.toString());
    }

    // json 문자열에서 주석(/*   */) 부분 제거한 순수한 json 문자열 추출
    private static String getJsonRemoveComment(String orgJsonString){

        String resultJson = "";
        int startIndex = 0;
        int endIndex = 0;

        while(true) {
            if(orgJsonString.indexOf("/*", startIndex) < 0) {
                resultJson = resultJson + orgJsonString.substring(startIndex);
                break;
            }else {
                endIndex = orgJsonString.indexOf("/*", startIndex);
            }

            resultJson = resultJson + orgJsonString.substring(startIndex, endIndex);
            startIndex = orgJsonString.indexOf ( "*/", endIndex) + 2;
        }

        return resultJson;
    }

}
