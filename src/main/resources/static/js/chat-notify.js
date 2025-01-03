//작업자 : 홍제기

// SSE 접속
//const sse = new EventSource("http://localhost:8080/connect-chat-notify");
const sse = new EventSource("http://sketchbook.com/connect-chat-notify");

// SSE 접속 확인 이벤트
sse.addEventListener('connect', (e) => {
	const { data: receivedConnectData } = e;
	console.log('connect event data: ',receivedConnectData);  // "connected!"
});

// SSE 채팅 알림 이벤트
sse.addEventListener('chat', (e) => {
	const { data: room } = e;
	console.log('received : ', room);
	if($("#room").val()!=room){
	    $("#circle-"+room).css("display", "block");
	}
});