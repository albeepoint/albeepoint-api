package com.albee.albeepoint.api.common.controller;
 
import lombok.RequiredArgsConstructor;
//  import org.apache.commons.beanutils.BeanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.albee.albeepoint.api.common.dto.ResponseDto;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
 


@RestController
@EnableWebMvc
@RequiredArgsConstructor
@RequestMapping(value = "/test", produces="application/json;charset=UTF-8")
public class TestController {

    public static final Logger log = LogManager.getLogger(TestController.class);

    @PostMapping("/hello")
    public ResponseEntity<ResponseDto<String>> hello(
            @RequestBody String dom
    ) throws InvocationTargetException, IllegalAccessException {
        List<String> userIdList = new ArrayList<>();
        userIdList.add("1");
        userIdList.add("2");
        userIdList.add("3");

        List<ObjCpTestUser> userList = new ArrayList<>();
        userList.add(new ObjCpTestUser(1, "name1", new ArrayList<>(Arrays.asList("r1", "r2", "r3"))));
        userList.add(new ObjCpTestUser(2, "name2", new ArrayList<>(Arrays.asList("r1", "r2", "r3"))));
        userList.add(new ObjCpTestUser(3, "name3", new ArrayList<>(Arrays.asList("r1", "r2", "r3"))));

        ObjCpTestOrg objCpTestOrg = new ObjCpTestOrg();
        objCpTestOrg.setOrgCd("test");
        objCpTestOrg.setOrgName("testName");
        objCpTestOrg.setUserIdList(userIdList);
        objCpTestOrg.setUserList(userList);

        ObjCpTestTargetOrg objCpTestTargetOrg = new ObjCpTestTargetOrg();

        // ObjCpTestOrg objCpTestTargetOrg = new ObjCpTestOrg();
        // BeanUtils.copyProperties(objCpTestOrg, objCpTestTargetOrg);
        ObjectCopyUtil.copyProperties(objCpTestOrg, objCpTestTargetOrg);

        // BeanUtils.copyProperties(objCpTestTargetOrg, objCpTestOrg);

        // log.info(objCpTestTargetOrg.getUserList().get(0).);

        return ResponseEntity.ok().body(new ResponseDto<>(okCd, okMsg, objCpTestTargetOrg.toString()));
    }

}
