package com.albee.albeepoint.api.util;
 
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.albee.albeepoint.api.common.dto.ResultListDto;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import lombok.extern.log4j.Log4j2;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

@Log4j2
public class ComUtil {

    // Vo -> IVo 로 복사
    public static Object votoivoCopy(Object source){
        if(source == null){
            return null;
        }

        String srcClsName = source.getClass().getName();
        String tgtClsName = srcClsName.substring(0, srcClsName.length() - 2) + "IVo";
        tgtClsName = tgtClsName.replace("com.albeepoint.api", "com.albeepoint.core");
        tgtClsName = tgtClsName.replace(".vo.", ".ivo.");
        Object target = null;

        try {
            target = ClassUtil.getObjectByClassType(Class.forName(tgtClsName));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        BeanUtils.copyProperties(source, target);
        return target;
    }

    // IVo -> Vo 로 복사
    public static Object ivotovoCopy(Object source){
        if(source == null){
            return null;
        }

        String srcClsName = source.getClass().getName();
        String tgtClsName = "";

        if(srcClsName.equals("java.util.ArrayList")){
            tgtClsName = srcClsName;
        }else{
            tgtClsName = srcClsName.substring(0, srcClsName.length() - 3) + "Vo";
            tgtClsName = tgtClsName.replace("com.albeepoint.core", "com.albeepoint.api");
            tgtClsName = tgtClsName.replace(".ivo.", ".vo.");
        }
        Object target = null;

        try {
            target = ClassUtil.getObjectByClassType(Class.forName(tgtClsName));
//            target = objectCopy(source, Class.forName(tgtClsName));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        BeanUtils.copyProperties(source, target);
        return target;
    }

    // 리스트 복사
    public static Object ivotovoListCopy(Object source){
        if(source == null || (((ArrayList)source).size() <= 0)){
            return null;
        }

        List<Object> sourceList = java.util.Arrays.asList(source); // ((ArrayList)source);
        List<Object> targetList = new ArrayList<>();

        if(VdUtil.isNotEmpty(sourceList)) {
            String tgtClsName = sourceList.get(0).getClass().getName().substring(0, sourceList.get(0).getClass().getName().length() - 3) + "Vo";
            tgtClsName = tgtClsName.replace("com.albeepoint.core", "com.albeepoint.api");
            tgtClsName = tgtClsName.replace(".ivo.", ".vo.");
            try {
                Object target = ClassUtil.getObjectByClassType(Class.forName(tgtClsName));
                targetList.add(target);
                for (Object obj : (ArrayList) source) {
                    targetList.add(objectCopy(obj, Class.forName(tgtClsName)));
                }
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        return targetList;
    }

    // 컨트롤러의 목록 조회 형식에 맞게 변환하여 복사
    public static ResultListDto ivotovoResultListCopy(Object source){
        ResultListDto<Object> sourceResultListDto = ((ResultListDto)source);

        List<Object> sourceList = ((ResultListDto)source).getList();
        List<Object> targetList = (ArrayList)ivotovoListCopy(sourceList);

        ResultListDto<Object> targetResultListDto = new ResultListDto<>();
        targetResultListDto.setTotalCnt(sourceResultListDto.getTotalCnt() != null ? sourceResultListDto.getTotalCnt() : null);
        targetResultListDto.setPageCnt(sourceResultListDto.getPageCnt() != null ? sourceResultListDto.getPageCnt() : null);
        // targetResultListDto.setCurrentPage(sourceResultListDto.getCurrentPage() != null ? sourceResultListDto.getCurrentPage() : null);
        targetResultListDto.setList(targetList != null ? targetList : null);

        return targetResultListDto;
    }

    // 오브젝트 복사
    public static Object objectCopy(Object source, Class targetClass){
        if(source == null){
            return null;
        }

        Object target = ClassUtil.getObjectByClassType(targetClass);

        BeanUtils.copyProperties(source, target);
        return target;
    }

    // 리스트 복사
    public static Object objectListCopy(Object source, Class targetClass){
        if(source == null || (((ArrayList)source).size() < 0)){
            return null;
        }

        Object target = ClassUtil.getObjectByClassType(targetClass);
        List<Object> targetList = new ArrayList<>();

        for(Object obj : (ArrayList)source){
            targetList.add(objectCopy(obj, targetClass));
        }

        return targetList;
    }

    // 컨트롤러의 목록 조회 형식에 맞게 변환하여 복사
    public static ResultListDto objectResultListCopy(Object source, Class targetClass){
        ResultListDto<Object> sourceResultListDto = ((ResultListDto)source);

        List<Object> sourceList = ((ResultListDto)source).getList();
        List<Object> targetList = (ArrayList)objectListCopy(sourceList, targetClass);

        ResultListDto<Object> targetResultListDto = new ResultListDto<>();
        targetResultListDto.setTotalCnt(sourceResultListDto.getTotalCnt() != null ? sourceResultListDto.getTotalCnt() : null);
        targetResultListDto.setPageCnt(sourceResultListDto.getPageCnt() != null ? sourceResultListDto.getPageCnt() : null);
        // targetResultListDto.setCurrentPage(sourceResultListDto.getCurrentPage() != null ? sourceResultListDto.getCurrentPage() : null);
        targetResultListDto.setList(targetList != null ? targetList : null);

        return targetResultListDto;
    }


    public static void objectCopy(Object source, Object target){
        if(source == null){
            return;
        }

        BeanUtils.copyProperties(source, target);
    }

    public static Object objectCopy(Object source){
        Object target;
        if(source == null){
            return null;
        }

        try {
            target = Class.forName(source.getClass().getName()).getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException |
                 ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        BeanUtils.copyProperties(source, target);

        return target;
    }



    public static String convToClockDigit(String val){
        val = val.trim().replaceAll("-", "").replaceAll(":", "");

        if (val.length() == 8) {
            return val.substring(0, 4) + "-" + val.substring(4, 6) + "-" + val.substring(6, 8);
        }

        if (val.length() == 14) {
            return val.substring(0, 4) + "-" + val.substring(4, 6) + "-" + val.substring(6, 8)
                    + val.substring(8, 10) + "-" + val.substring(10, 12) + "-" + val.substring(12, 14);
        }

        return val;
    }

    public static Long calcPtByPurchaseAmt(long purchaseAmt, double rate){
        Long calcPt = 0L;
        calcPt = Math.round(purchaseAmt * rate);
        return calcPt;
    }

    // 오브젝트내에 원하는 특정 멤버변수의 값을 특정 값으로 변환
    // 예를 들면 컨트롤러에 유입된 입력값 중에 memberId 가 있다면 자동으로 암호화하여 memberIdEnc를 채움
    public static void convObjectVarValue(Object orgObj, String varName, String convVal){
        try{
            for(Field field : orgObj.getClass().getDeclaredFields()){
                field.setAccessible(true);
                if(field.getType().getName().contains("java.util.List")){
                    for(Object subObj : (ArrayList)field.get(orgObj)){
                        convObjectVarValue(subObj, varName, convVal);
                    }
                }else{
                    if(varName.equals(field.getName())){
                        try{
                            orgObj.getClass().getField("memberIdEnc").set(orgObj, orgObj.getClass().getField(varName).get(orgObj).toString() + convVal);
                        }catch(NoSuchFieldException e){
                            continue;
                        }
                    }
                }
            }
        }catch(IllegalAccessException e){
            e.printStackTrace();
        }
    }

    // 오브젝트내에 원하는 특정 멤버변수의 값을 찾기
    public static Object findObjectVarValue(Object orgObj, String varName){
        try{
            for(Field field : orgObj.getClass().getDeclaredFields()){
                field.setAccessible(true);
                if(field.getType().getName().contains("java.util.List")){
                    for(Object subObj : (ArrayList)field.get(orgObj)){
                        findObjectVarValue(subObj, varName);
                    }
                }else{
                    if(varName.equals(field.getName())){
                        try{
                            return orgObj.getClass().getField(varName).get(orgObj);
                        }catch(NoSuchFieldException e){
                            continue;
                        }
                    }
                }
            }
        }catch(IllegalAccessException e){
            e.printStackTrace();
        }

        return null;
    }


    // 오브젝트내에 원하는 특정 멤버변수의 값을 찾기
    /*
        첫번째 파라미터(gsonMap)
            gsonMap = new Gson().fromJson(inputJson, Map.class); // inputJson : json 스트링
     */
    public static Object findGsonMapByVarValue(Map<String, Object> gsonMap, String varName){
        Object returnObj = null;

        for(Map.Entry entry : gsonMap.entrySet()){
            try{
                if(entry.getValue().getClass().getName().contains("java.util.ArrayList")){
                    for(Object subEntry : (ArrayList)entry.getValue()){
                        returnObj = findGsonMapByVarValue(new Gson().fromJson(new Gson().toJson(subEntry), Map.class), varName);
                        if(returnObj != null){
                            return returnObj;
                        }
                    }
                }else{
                    for(Object val : ((LinkedTreeMap) entry.getValue()).entrySet()){
                        if(varName.equals(((LinkedTreeMap.Entry) val).getKey().toString())){
                            returnObj = ((LinkedTreeMap.Entry) val).getValue().toString();
                            break;
                        }
                    }
                }
            }catch(NullPointerException e){
                continue;
            }
        }

        return returnObj;
    }

    public static String getUuid(){
        UUID uuid = UUID.randomUUID();

        // 2. 문자열로 변환 (예: 550e8400-e29b-41d4-a716-446655440000)
        String uuidString = uuid.toString(); 

        // 3. 하이픈(-) 제거 버전 (DB PK나 파일명으로 쓸 때 유용)
        String shortUuid = uuidString.replace("-", "");
        return shortUuid;
    }
}
