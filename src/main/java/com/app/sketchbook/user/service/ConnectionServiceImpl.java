package com.app.sketchbook.user.service;

import com.app.sketchbook.user.entity.ConnectionLog;
import com.app.sketchbook.user.entity.SketchUser;
import com.app.sketchbook.user.repository.ConnectionLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

//작업자 : 홍제기
@Log
@Service
@RequiredArgsConstructor
public class ConnectionServiceImpl implements ConnectionLogService{

    private final ConnectionLogRepository connectionLogRepository;
    
    //접속 기록을 DB에 저장
    @Override
    public void insertConnection(HttpServletRequest request, SketchUser user) {
        var connectionLog = new ConnectionLog();

        connectionLog.setUser(user);
        connectionLog.setConnectedTime(new Date());
        connectionLog.setBrowser(getBrowser(request.getHeader("User-Agent")));
        String ip = getIp(request);
        connectionLog.setIp(ip);
        
        // 접속 위치 조회
        RestTemplate rest = new RestTemplate();
        String result = rest.getForObject("http://ip-api.com/csv/"+ip+"?fields=country", String.class);

        connectionLog.setRegion(result);

        connectionLogRepository.save(connectionLog);
    }
    
    //사용자의 모든 로그를 조회, 페이지네이션 적용
    @Override
    public Page<ConnectionLog> findAllLogsByUser(int page, SketchUser user) {
        PageRequest pageRequest = PageRequest.of(page,10);
        return connectionLogRepository.findAllByUserOrderByConnectedTimeDesc(pageRequest, user);
    }
    
    // 접속 브라우저 조회
    private String getBrowser(String agent){
        // 브라우져 구분
        String browser = null;
        if (agent != null) {
            if (agent.contains("Trident")) {
                browser = "MSIE";
            } else if (agent.contains("Chrome")) {
                browser = "Chrome";
            } else if (agent.contains("Opera")) {
                browser = "Opera";
            } else if (agent.contains("iPhone") && agent.contains("Mobile")) {
                browser = "iPhone";
            } else if (agent.contains("Android") && agent.contains("Mobile")) {
                browser = "Android";
            }
        }
        log.info("Result: Browser: "+browser);

        return browser;
    }
    
    // 접속 IP 조회
    private String getIp(HttpServletRequest request){
        String[] headers = {"X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR"};

        String ip = null;

        for(String header : headers) {
            if(ip==null){
                ip = request.getHeader(header);
            }
        }

        if (ip == null) {
            ip = request.getRemoteAddr();
        }

        // 로컬일 경우 공인 IP 획득
        if(ip.equals("0:0:0:0:0:0:0:1") || ip.equals("127.0.0.1") || ip.startsWith("192.168")){
            try{
                RestTemplate rest = new RestTemplate();
                ip = rest.getForObject("https://checkip.amazonaws.com/", String.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        log.info("Result : IP Address : "+ip);

        return ip;
    }
}
