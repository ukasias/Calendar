package com.ukasias.android.calendar;

import java.text.SimpleDateFormat;

public class BasicInfo {
	/**
     * 외장 메모리 패스
     */
    public static String ExternalPath = "/sdcard/";

    /**
     * 외장 메모리 패스 체크 여부
     */
    public static boolean ExternalPathChecked = false;

	/**
	 * 미디어 포맷
	 */
	public static final String URI_MEDIA_FORMAT		= "content://media";

	/**
	 * 데이터 베이스 이름
	 */
	public static String DATABASE_NAME = "Calendar/schedule.db";



    /**
     * 날짜, 시간 포맷
     */
	public static SimpleDateFormat dateKorFormat
			= new SimpleDateFormat("yyyy년 MM월 dd일");

	public static SimpleDateFormat dateFormat
			= new SimpleDateFormat("yyyy-MM-dd 00:00:00");

	/**
	 * 인텐트 전달 시의 부가 정보
	 */
	public static final String KEY_SCHEDULE_MODE = "SCHEDULE_MODE";

	/**
	 * key KEY_SCHEDULE_MODE에 쓰일 상수.
	 */
	public static final String SCHEDULE_MODE_INSERT = "MODE_INSERT";
	public static final String SCHEDULE_MODE_VIEW = "MODE_VIEW";

	/**
	 * 인텐트 전달 시 한 item(schedule)에 대한 정보를 전달하기 위한 Key 값
	 */
	public static final String KEY_SCHEDULE_ID 			= "SCHEDULE_ID";
	public static final String KEY_SCHEDULE_DATE 		= "SCHEDULE_DATE";
	public static final String KEY_SCHEDULE_TITLE  	    = "SCHEDULE_TITLE";
	public static final String KEY_SCHEDULE_TEXT		= "SCHEDULE_TEXT";
	public static final String KEY_SCHEDULE_DATE_YEAR	= "SCHEDULE_DATE_YEAR";
	public static final String KEY_SCHEDULE_DATE_MONTH	= "SCHEDULE_DATE_MONTH";
	public static final String KEY_SCHEDULE_DATE_DAY	= "SCHEDULE_DATE_DAY";

	public static final String KEY_SCHEDULE_LIST		= "SCHEDULE_LIST";
	public static final String KEY_SCHEDULE_LIST_DAY	= "SCHEDULE_LIST_DAY";

	public static final String KEY_FRAGMENT_NUMBER		= "LAST_USED_FRAGMENT_NUMBER";

	/**
	 * ScheduleAcitivity에서의 Dialog를 띄울 때의 키 값
	 */
	public static final int CONFIRM_DELETE = 1001;
	public static final int CONFIRM_TITLE_INPUT = 1002;
	public static final int CONFIRM_CONTENTS_INPUT = 1003;

	public static final int REQ_INSERT_ACTIVITY = 2001;
	public static final int REQ_VIEW_ACTIVITY = 2002;

	public static final int SHOW_SCHEDULE = 3001;

	public static final int DATE_INFORMATION = 3 << 24;
}
