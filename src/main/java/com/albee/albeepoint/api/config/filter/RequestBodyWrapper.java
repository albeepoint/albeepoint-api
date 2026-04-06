package com.albee.albeepoint.api.config.filter;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class RequestBodyWrapper extends HttpServletRequestWrapper {

    // 가로챈 데이터를 가공하여 담을 final 변수
    private final String requestOutBodyData;

    public static final Logger log = LogManager.getLogger(RequestBodyWrapper.class);

    public RequestBodyWrapper(HttpServletRequest request) throws IOException, DecoderException {
        super(request);

        String requestFilterInputBodyData = requestDataByte(request); // Request Data 가로채기
        requestOutBodyData = requestFilterInputBodyData;

        log.info("ApiFilter input 데이터: " + requestFilterInputBodyData);
        /*
        // 나중에 전문 암복호화 필요시 코딩
        String decodeTemp = requestBodyDecode(requestHashData); // Reqeust Data Hex 디코드

        log.info("인코딩 데이터: " + requestHashData);
        log.info("디코딩 데이터: " + decodeTemp);

        requestFilterOutputBodyData = decodeTemp;
         */
        log.info("ApiFilter output 데이터: " + requestOutBodyData);
    }

    @Override
    public ServletInputStream getInputStream() {
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(requestOutBodyData.getBytes(StandardCharsets.UTF_8));
        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener listener) {
            }

            @Override
            public int read() {
                return byteArrayInputStream.read();
            }
        };
    }

    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(this.getInputStream()));
    }

    //==request Body 가로채기==//
    private String requestDataByte(HttpServletRequest request) throws IOException {
        byte[] rawData = new byte[128];
        InputStream inputStream = request.getInputStream();
        rawData = IOUtils.toByteArray(inputStream);
        return new String(rawData);
    }

    //==request Body Hex 디코딩==//
    // 나중에 전문 암복호화 필요시 코딩
    private String requestBodyDecode(String requestHashData) throws DecoderException {
        return HexDecodeToString(requestHashData);
    }

    //==Request Data Decode (Hex Decode)==//
    public static String HexDecodeToString(String encodeText) throws DecoderException {
        return new String(Hex.decodeHex(encodeText.toCharArray()));
    }
}
