package com.albee.albeepoint.api.member.service;
 
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.albee.albeepoint.api.common.constant.EnumCommon.EncTarget;
import com.albee.albeepoint.api.common.constant.ErrorCode; 
import com.albee.albeepoint.api.common.dto.ResultListDto;
import com.albee.albeepoint.api.common.service.EncInfoService;
import com.albee.albeepoint.api.member.constant.EnumMember;
import com.albee.albeepoint.api.member.dto.MemberMstDto;
import com.albee.albeepoint.api.member.dto.MemberPidDto;
import com.albee.albeepoint.api.member.dto.MemberPidReqDto;
import com.albee.albeepoint.api.member.dto.MemberPidSearchDto;
import com.albee.albeepoint.api.member.dto.MemberSearchDto;
import com.albee.albeepoint.api.org.dto.OrgSearchDto;
import com.albee.albeepoint.api.org.service.OrgMstService;
import com.albee.albeepoint.api.util.EncUtil;
import com.albee.albeepoint.api.util.VdUtil;
import com.albee.albeepoint.mapper.base.t_enc_info.TEncInfo; 
import com.albee.albeepoint.mapper.base.t_member_pid.TMemberPid;
import com.albee.albeepoint.mapper.base.t_member_pid.TMemberPidExample;
import com.albee.albeepoint.mapper.base.t_member_pid.TMemberPidMapper;
import com.albee.albeepoint.mapper.base.t_org_mst.TOrgMst;
import com.albee.albeepoint.mapper.member.MemberPidMapper; 
 
import java.time.LocalDateTime;
import java.util.List;
 
@Log4j2
@RequiredArgsConstructor
@Service
@Transactional
public class MemberPidService {
    @Autowired
    private TMemberPidMapper tMbrPidMapper;

    @Autowired
    private MemberPidMapper mbrPidMapper;

    @Autowired
    private MemberMstService mbrService;

    @Autowired
    private EncInfoService encInfoService;

    @Autowired
    private OrgMstService orgService;

    /* 회원식별자 등록 */
    public void regMemberPid(MemberPidReqDto mbrPidDto) {
        TOrgMst orgEty = orgService.getOrgMstWec(new OrgSearchDto(mbrPidDto.getOrgCd()));
        VdUtil.emptyEc(orgEty, ErrorCode.BIZ_ERR_001032, "기관코드 없음");

        TMemberPid memberPidEty = new TMemberPid(); 
        BeanUtils.copyProperties(mbrPidDto, memberPidEty);
        memberPidEty.setOrgNo(orgEty.getOrgNo());

        VdUtil.emptyEc(mbrPidDto.getMemberId(), ErrorCode.BIZ_ERR_001001, "회원ID 미입력");
        VdUtil.emptyEc(mbrPidDto.getPid(), ErrorCode.BIZ_ERR_001076, "회원식별자 미입력");

        MemberMstDto memberDto = this.mbrService.getMemberMstWec(
            new MemberSearchDto(mbrPidDto.getOrgCd(), mbrPidDto.getMemberId()));
        VdUtil.emptyEc(memberDto, ErrorCode.BIZ_ERR_001105, "회원 없음");
        memberPidEty.setMemberNo(memberDto.getMemberNo());

        TMemberPidExample tMemberPidExample = new TMemberPidExample(); 
        tMemberPidExample.createCriteria()
            .andOrgNoEqualTo(memberPidEty.getOrgNo())
            .andMemberNoEqualTo(memberPidEty.getMemberNo());
        VdUtil.ec(this.tMbrPidMapper.countByExample(tMemberPidExample) > 0, ErrorCode.BIZ_ERR_001075, "기등록 회원식별자");

        BeanUtils.copyProperties(mbrPidDto, memberPidEty);

        TEncInfo encInfo = encInfoService.getEncInfo(EncTarget.ORG.getCode(), String.valueOf(orgEty.getOrgNo()));
        VdUtil.emptyEc(encInfo, ErrorCode.BIZ_ERR_001033, "암호화 정보 없음");

        String encStr = EncUtil.encryptAes256(mbrPidDto.getPid(), encInfo.getEncKey());
        log.debug("입력값(평문) : " + mbrPidDto.getPid());
        log.debug("암호화 결과 : " + encStr);
        log.debug("복호화 결과 : " + EncUtil.decryptAes256(encStr, encInfo.getEncKey()));

        memberPidEty.setPidEnc(encStr);
        memberPidEty.setPidStsCd(EnumMember.PidSts.NORMAL.getCode());
        memberPidEty.setJoinDt(LocalDateTime.now());
        memberPidEty.setPidNo(mbrPidMapper.selectSeqMemberPidNo());
 
        VdUtil.ec(this.tMbrPidMapper.insert(memberPidEty) <= 0, ErrorCode.BIZ_ERR_001141, "회원식별자 등록 실패");
    }

    /* 회원식별자 수정 */
    public void modMemberPid(MemberPidDto mbrPidDto) {
        TOrgMst orgEty = orgService.getOrgMstWec(new OrgSearchDto(mbrPidDto.getOrgCd()));
        mbrPidDto.setOrgNo(orgEty.getOrgNo());

        VdUtil.emptyEc(mbrPidDto.getMemberId(), ErrorCode.BIZ_ERR_001001, "회원ID 미입력");
        VdUtil.emptyEc(mbrPidDto.getPid(), ErrorCode.BIZ_ERR_001076, "회원식별자 미입력");

        MemberMstDto memberDto = mbrService.getMemberMstWec(
            new MemberSearchDto(mbrPidDto.getOrgCd(), mbrPidDto.getMemberId()));
        mbrPidDto.setMemberNo(memberDto.getMemberNo());

        MemberPidDto oldMemberPid = this.getMemberPidWec(new MemberPidSearchDto(mbrPidDto));
        mbrPidDto.setPidNo(oldMemberPid.getPidNo());

        TMemberPid memberPidEty = new TMemberPid();
        BeanUtils.copyProperties(mbrPidDto, memberPidEty);

        TEncInfo encInfo = encInfoService.getEncInfo(EncTarget.ORG.getCode(), String.valueOf(orgEty.getOrgNo()));
        VdUtil.emptyEc(encInfo, ErrorCode.BIZ_ERR_001033, "암호화 정보 없음");

        String encStr = EncUtil.encryptAes256(mbrPidDto.getPid(), encInfo.getEncKey());
        log.debug("입력값(평문) : " + mbrPidDto.getPid());
        log.debug("암호화 결과 : " + encStr);
        log.debug("복호화 결과 : " + EncUtil.decryptAes256(encStr, encInfo.getEncKey()));

        memberPidEty.setPidEnc(encStr);
        memberPidEty.setPidStsCd(EnumMember.PidSts.NORMAL.getCode());
        memberPidEty.setJoinDt(LocalDateTime.now());
        memberPidEty.setPidNo(mbrPidMapper.selectSeqMemberPidNo());
 
        VdUtil.ec(this.tMbrPidMapper.updateByPrimaryKeySelective(memberPidEty) <= 0, 
            ErrorCode.BIZ_ERR_001144, "회원식별자 수정 실패");
    }

    /* 회원식별자 단건 조회 */
    public MemberPidDto getMemberPid(MemberPidSearchDto dom) {
        return mbrPidMapper.selectMemberPid(dom);
    }

    public MemberPidDto getMemberPidWec(MemberPidSearchDto mbrPidSearch) {
        MemberPidDto mbrPidDto = this.getMemberPid(mbrPidSearch);
        VdUtil.emptyEc(mbrPidDto, ErrorCode.BIZ_ERR_001105, "회원식별자 없음");
        return mbrPidDto;
    }

    /* 회원식별자 목록 조회 */
    public ResultListDto<MemberPidDto> getMemberPidList(MemberPidSearchDto dom) {
        ResultListDto<MemberPidDto> result = new ResultListDto<>();
        Long totalCnt = mbrPidMapper.selectMemberPidListTotalCnt(dom);
        result.setTotalCnt(totalCnt != null ? totalCnt : 0L);
        if(result.getTotalCnt() > 0){
            List<MemberPidDto> list = this.mbrPidMapper.selectMemberPidList(dom);
            result.setList(list);
            result.setPageCnt(list != null && list.size() > 0 ? list.size() : 0);
        }
        return result;
    }
}
