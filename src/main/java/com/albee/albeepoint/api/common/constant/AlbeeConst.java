package com.albee.albeepoint.api.common.constant;


public final class AlbeeConst {
//    @Value("${spring.jackson.time-zone}")
//   	private static String timezone;

    public static final String defaultTimeZone = "Asia/Seoul";
//    public static final String defaultTimeZone = String.valueOf(timezone);

    public static final Integer timeZoneOffset = -9;

    public static final Integer timeZonePlusOffset = 9;

    public static final String timestampJsonPattern = "yyyy-MM-dd HH:mm:ss";

    public static final String orgCd = "ORG001";

    public static final String apiUserId = "API";

    public static final String okCd = "000000";

    public static final String okMsg = "SUCCESS";

    public static final String crlf = "\r\n";
}
