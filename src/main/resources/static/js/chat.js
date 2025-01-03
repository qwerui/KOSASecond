//작업자 : 홍제기
let historyFetched = false;
let isBottom = true;

let receiveQueue = [];
let start = 0;

let lastChatId = "";

let stompClient = null;

$(function () {
    $("#chatform").on('submit', (e) => e.preventDefault());
    $( "#disconnect" ).click(() => disconnect());
    $( "#send" ).click(() => sendChat());
    $("#chat").keypress((e) => {
        if(e.code == "Enter"){
            sendChat();
        }
    });

    // 스크롤 최하단 체크
    $("#content-frame").on("scroll", ()=>{
        const frame = $("#content-frame")[0];
        if(frame.scrollTop == frame.scrollHeight-frame.clientHeight){
            isBottom = true;
        } else {
            isBottom = false;
        }
    });
});

// 페이지 종료 전에 웹 소켓 닫기
$(window).on("beforeunload", function(){
    if(stompClient != null){
        disconnect();
    }
});

function setConnected(connected) {
    $("#disconnect").prop("disabled", !connected);
    $("#content").html("");
    historyFetched = false;
}

function connect() {
    // 웹 소켓 생성, localhost:8080 부분이 Spring 서버 IP
    stompClient = new StompJs.Client({
        //brokerURL: 'ws://localhost:8080/chat-socket',
        brokerURL: 'ws://sketchbook.com/chat-socket',
        connectHeaders: {
            room: $("#room").val() //채팅방 생성을 위한 파라미터
        }
    });

    // 웹 소켓 접속 이벤트
    stompClient.onConnect = (frame) => {

        setConnected(true);
        console.log('Connected: ' + frame);

        stompClient.subscribe('/topic/receive/'+$("#room").val(), (chat) => {
            if(historyFetched) {
                showMessage(JSON.parse(chat.body));
            } else {
                receiveQueue.push(JSON.parse(chat.body));
            }
        });

        stompClient.subscribe('/topic/history/'+$("#room").val(), (history) => {
            showRecentMessage(JSON.parse(history.body));
        });

        fetchPreviousMessages();
    };

    // 웹 소켓 에러 발생
    stompClient.onWebSocketError = (error) => {
            console.error('Error with websocket', error);
        };

    // STOMP 에러
    stompClient.onStompError = (frame) => {
            console.error('Broker reported error: ' + frame.headers['message']);
            console.error('Additional details: ' + frame.body);
        };

    // 웹 소켓 활성화
    stompClient.activate();
}

// 웹 소켓 종료 메소드
function disconnect() {
    fetch("http://localhost:8080/disconnect/"+$("#room").val()); // 종료 전에 접속 종료 시간 업데이트
    // 채팅 화면 숨김
    if($("#chat-container").css("display")=="block") {
        $("#chat-container").css("display", "none");
    }
    // 웹 소켓 비활성화
    stompClient.deactivate();
    setConnected(false);
    console.log("Disconnected");
}

function sendChat() {
    // 채팅 메시지 전송
    if($("#chat").val()){
        stompClient.publish({
                destination: "/app/send",
                body: JSON.stringify({
                    'room' : $("#room").val(),
                    'user' : $("#chatUser").val(),
                    'content' : $("#chat").val(),
                    'userid' : $("#userId").val()
            })
        });
        $("#chat").val("");
    }
}

function fetchPreviousMessages() {
    // 이전 채팅 목록 요청
      stompClient.publish({
        destination: "/app/history/"+$("#room").val()
      });
}

function showMessage(message) {

    // 마지막 채팅과 같은 채팅일 경우 무시
    if(lastChatId == message.id){
        return;
    }

    lastChatId = message.id;

    // 채팅 글 UI 출력
    if($("#chatUser").val()==message.user){
        $("#content").append("<div class=\"w-100 d-flex justify-content-end mb-2\"><div class=\"bg-warning-subtle p-1 rounded\">" + message.content + "</div></div>");
    } else {

        $("#content").append("<div class=\"w-100 d-flex justify-content-start mb-2\"><div class=\"bg-light p-1 rounded\">" + message.content + "</div></div>");
    }

    // 최하단일 경우 스크롤 고정
    if(isBottom) {
        const frame = $("#content-frame")[0];
        frame.scrollTop = frame.scrollHeight;
    }
}

function showRecentMessage(history) {
    history.forEach(item=>showMessage(item)); // 채팅 내역 출력
    receiveQueue.forEach(item=>showMessage(item)); // 채팅 로딩 중 수신내용 출력
    receiveQueue = [];
    historyFetched = true;
}

function openChat(room, opponent, userId, username, profile) {
    $("#room").val(room);
    $("#room-name").text(opponent);
    $("#chatUser").val(username);
    $("#userId").val(userId);
    // 채팅 알림 숨김
    $("#circle-"+room).css("display", "none");
    $("#chat-profile").attr("src", profile);

    // 채팅 활성화 시 이미 채팅 화면이 열려 있을 경우, 채팅방의 정보에 따라 접속 종료 혹은 유지
    if($("#chat-container").css("display")=="block") {
        if(room == $("#room").val()){
            console.log("already opened");
            return;
        } else {
            disconnect();
        }
    }
    $("#chat-container").css("display", "block");
    connect();
}