package org.androidtown.gcm.push;

/**
 * GCM 관련 정보 변수/상수 선언
 */
public class GCMInfo {
	
	/**
     * Project Id registered to use GCM.
     * 단말 등록을 위한 필요한 ID
     */
    public static final String PROJECT_ID = "992372736160";

    /**
     * Google API Key generated for service access
     * 서버 : 푸시 메시지 전송을 위해 필요한 KEY
     */
    public static final String GOOGLE_API_KEY = "AIzaSyD9K5_TNttazSbp2K3bA8ORhT1OwUYbGtQ";

    /**
     * Registration ID for this device
     * 단말 등록 후 수신한 등록 ID
     */
    public static String RegistrationId = "";
    
}
