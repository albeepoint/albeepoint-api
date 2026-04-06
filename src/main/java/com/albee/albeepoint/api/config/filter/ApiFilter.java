package com.albee.albeepoint.api.config.filter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/*
init()
웹 컨테이너(톰캣)이 시작될 때 필터 최초 한 번 인스턴스 생성

doFilter()
클라이언트의 요청 시 전/후 처리
FilterChain을 통해 전달

public void destroy()
필터 인스턴스가 제거될 때 실행되는 메서드, 종료하는 기능
 */
@WebFilter(urlPatterns = "/*")
@Component
public class ApiFilter implements Filter {

    public static final Logger log = LogManager.getLogger(ApiFilter.class);

    /*
        - 필터 인스턴스 초기화
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("---필터 인스턴스 초기화---");
    }

    /*
        - 전/후 처리
        - Request, Response가 필터를 거칠 때 수행되는 메소드
        - chain.doFilter() 기점으로 request, response 나눠집니다.
     */@Override
public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
        throws IOException, ServletException {
    
    // 1. HTTP 타입으로 미리 캐스팅 (필터 내부에서 반복 사용하기 위함)
    HttpServletRequest httpRequest = (HttpServletRequest) request;
    HttpServletResponse httpResponse = (HttpServletResponse) response;

    try {
        // 2. 커스텀 래퍼 적용
        // RequestBodyWrapper 내부의 import도 반드시 jakarta.servlet으로 변경되어 있어야 합니다.
        RequestBodyWrapper wrappedRequest = new RequestBodyWrapper(httpRequest);
        String requestURI = wrappedRequest.getRequestURI();

        // 3. 로그 방식 개선 (문자열 더하기 대신 슬롯 방식 {} 사용 - 성능 최적화)
        log.info("--- Filter Request({}) ---", requestURI);

        // 4. 래퍼 객체를 체인에 전달
        chain.doFilter(wrappedRequest, httpResponse);

        log.info("--- Filter Response({}) ---", requestURI);

    } catch (Exception e) {
        // 5. 예외 로깅 강화 (StackTrace를 포함하여 에러 원인 파악 용이하게 수정)
        log.error("필터 처리 중 에러 발생: ", e);

        // 6. 응답 처리 개선
        String responseMessage = "요청 또는 응답 필터 처리 오류입니다.";
        byte[] data = responseMessage.getBytes(StandardCharsets.UTF_8); // "utf-8" 문자열 대신 상수 사용

        // 헤더 설정 (한글 깨짐 방지)
        httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400 상숫값 사용
        httpResponse.setContentType("text/plain; charset=utf-8");
        httpResponse.setCharacterEncoding("UTF-8");
        
        httpResponse.getOutputStream().write(data);
        httpResponse.flushBuffer();
    }
}

    /*
        - 필터 인스턴스 종료
     */
    @Override
    public void destroy() {
        log.info("---필터 인스턴스 종료---");
    }
}