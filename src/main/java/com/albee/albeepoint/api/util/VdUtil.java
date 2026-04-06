package com.albee.albeepoint.api.util;
 
import com.albee.albeepoint.api.common.constant.ErrorCode;
import com.albee.albeepoint.api.common.exception.AlbeepointException;

import lombok.extern.log4j.Log4j2;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Log4j2
public class VdUtil {

    public static void ec(Boolean errYn, ErrorCode errorCode, String message){

        if(errYn) {
            log.error(errorCode.getCode() + "[" + (isEmpty(message) ? errorCode.getMessage() : message) + "]");
            throw new AlbeepointException(errorCode, isEmpty(message) ? errorCode.getMessage() : message);
        }
    }

    // Empty 이면 throw exception
    public static Object emptyEc(Object obj, ErrorCode errorCode){
        if(isEmpty(obj)) {
            log.error(errorCode.getCode() + "[" + (isEmpty(errorCode.getMessage()) ? errorCode.getMessage() : errorCode.getMessage()) + "]");
            throw new AlbeepointException(errorCode, isEmpty(errorCode.getMessage()) ? errorCode.getMessage() : errorCode.getMessage());
        }

        return obj;
    }

    // Empty 이면 throw exception
    public static Object emptyEc(Object obj, ErrorCode errorCode, String message){

        if(isEmpty(obj)) {
            log.error(errorCode.getCode() + "[" + (isEmpty(message) ? errorCode.getMessage() : message) + "]");
            throw new AlbeepointException(errorCode, isEmpty(message) ? errorCode.getMessage() : message);
        }

        return obj;
    }


    // 첫번째, 두번째 파라미터의 값이가 불일치시 오류 발생
    public static void notEqualEc(Object obj1, Object obj2, ErrorCode errorCode, String message){
        if(!isEqual(obj1, obj2)) {
            log.error(errorCode.getCode() + "[" + (isEmpty(message) ? errorCode.getMessage() : message) + "]");
            throw new AlbeepointException(errorCode, message);
        }
    }

    // 첫번째, 두번째 파라미터의 값이가 불일치시 오류 발생
    public static void notEqualEc(Object obj1, Object obj2, ErrorCode errorCode){
        if(!isEqual(obj1, obj2)) {
            throw new AlbeepointException(errorCode, errorCode.getMessage());
        }
    }

    public static void ec(Boolean errYn, ErrorCode errorCode){

        if(errYn) {
            log.error(errorCode.getCode() + "[" + errorCode.getMessage() + "]");
            throw new AlbeepointException(errorCode, errorCode.getMessage());
        }
    }

    public static Boolean isEmpty(Integer val){
        return val == null || val == 0;
    }

    public static Boolean isNotEmpty(Integer val){
        return val != null && val > 0;
    }

    public static Boolean isEmpty(Long val){
        return val == null || val == 0L;
    }

    public static Boolean isNotEmpty(Long val){
        return val != null && val > 0L;
    }

    public static Boolean isEmpty(Object obj){
        if(obj == null){
            return true;
        }

        if(obj != null) {
            if (obj instanceof ArrayList) {
                if (((ArrayList) obj).size() <= 0) {
                    return true;
                }
            }

            if (obj instanceof String) {
                if (((String) obj).trim().length() == 0) {
                    return true;
                }
            }

            if (obj instanceof Integer) {
                if ((Integer) obj == 0) {
                    return true;
                }
            }

            if (obj instanceof Long) {
                if ((Long) obj == 0L) {
                    return true;
                }
            }

            if (obj instanceof Double) {
                if ((Double) obj == 0.0) {
                    return true;
                }
            }
        }

        return false;
    }

    public static Boolean isNotEmpty(Object obj){
        if(obj != null){
            return true;
        }

        return false;
    }

    public static <T> Boolean isEmpty(List<T> list){
        if((list == null) || (list.size() == 0)){
            return true;
        }

        return false;
    }

    public static <T> Boolean isNotEmpty(List<T> list){
        if((list != null) && (list.size() > 0)){
            return true;
        }

        return false;
    }

    public static Boolean isEmpty(Timestamp val){
        if(val == null){
            return true;
        }

        LocalDateTime dt = null;
        try{
            dt = val.toLocalDateTime();
        }catch(Exception e){
            return false;
        }

        return false;
    }

    public static Boolean isNotEmpty(Timestamp val){
        if(val == null){
            return false;
        }

        LocalDateTime dt = null;
        try{
            dt = val.toLocalDateTime();
        }catch(Exception e){
            return true;
        }

        return false;
    }

    public static Boolean isEmpty(String val){
        if(val == null){
            return true;
        }

        return val.trim().equals("");
    }

    public static Boolean isNotEmpty(String val){
        if(val == null){
            return false;
        }

        return !val.trim().equals("");
    }

    /*
       VdUtil.isInclude(dom.getUsePeriodTypeCd().toString(), EnumCont.UsePeriodType.class)

       VdUtil.errorCheck(VdUtil.isInclude(dom.getUsePeriodTypeCd().toString(), EnumCont.UsePeriodType.class), BIZ_ERR_001028, "[UsePeriod] 사용기간 계산 입력값 확인 필요");
     */
    public static <E extends Enum<E>> Boolean isInclude(String val, Class<E> e){
        for(E ee : e.getEnumConstants()){
            if(val.equals(ee.toString())){
                return true;
            }
        }

        return false;
    }

    public static Boolean isInclude(String val, String vals){
        if(vals == null || val == null){
            return null;
        }

        String[] values = vals.trim().split(",");
        for(int i = 0; i < values.length; i++){
            if(val.equals(values[i].trim())) {
                return true;
            }
        }

        return false;
    }


    public static Boolean isNotInclude(String val, String vals){
        if(vals == null || val == null){
            return null;
        }

        String[] values = vals.trim().split(",");
        for(int i = 0; i < values.length; i++){
            if(val.equals(values[i].trim())) {
                return false;
            }
        }

        return true;
    }


    public static boolean isNullOrEmpty(String str){
        boolean result;
        if(str == null || str.trim().length() == 0){
            result = true;
        }else{
            result = false;
        }
        return result;
    }

    public static boolean isNotEqual(Object obj1, Object obj2){
        return !isEqual(obj1, obj2);
    }

    public static boolean isEqual(Object obj1, Object obj2)
    {
        if(obj1 == null && obj2 == null){
            return true;
        }

        if(obj1 == null || obj2 == null){
            return false;
        }

        if (obj1.getClass().isEnum()) {
            if (obj1.toString().equals(obj2.toString())) {
                return true;
            }else{
                return false;
            }
        }

        if (obj1 instanceof Long || obj1 instanceof Integer) {
            if (!(obj2 instanceof Long || obj2 instanceof Integer)) {
                return false;
            }
        }

        if (obj1 instanceof Long || obj1 instanceof Integer) {
            if (Long.parseLong(obj1.toString()) == Long.parseLong(obj2.toString())) {
                return true;
            }else{
                return false;
            }
        }

        if (obj1 instanceof Double) {
            if (Double.parseDouble(obj1.toString()) == Double.parseDouble(obj2.toString())) {
                return true;
            }else{
                return false;
            }
        }

        if (obj1 instanceof String) {
            if (((String) obj1).toString().equals(((String) obj2).toString())) {
                return true;
            }else{
                return false;
            }
        }

        return false;
    }


//
//    public static boolean isEqual(Enum e1, Enum e2)
//    {
//        if(e1 == null && e2 == null){
//            return true;
//        }
//
//        if(e1 == null || e2 == null){
//            return false;
//        }
//
//        return e1.toString().equals(e2.toString());
//    }
//
//    public static boolean isNotEqual(Enum e1, Enum e2)
//    {
//        if(e1 == null && e2 == null){
//            return false;
//        }
//
//        if(e1 == null || e2 == null){
//            return true;
//        }
//
//        return !e1.toString().equals(e2.toString());
//    }
}
