let stompClient = null;

//// ✅ Thymeleaf variables safely injected
//let riderEmail = /*[[${riderEmail}]]*/ 'rider@example.com';
//let driverEmail = /*[[${driverEmail}]]*/ 'driver@example.com';
//let sessionJwt = /*[[${sessionJwt}]]*/ 'dummyToken';

let riderEmail = window.riderData.riderEmail;
let driverEmail = window.riderData.driverEmail;
let sessionJwt = window.riderData.token;


if (!riderEmail || !driverEmail || !sessionJwt) {
    console.error("❌ Thymeleaf variables missing:", {riderEmail, driverEmail, sessionJwt});
}

// Connect to WebSocket
function connectWebSocket() {
    const socket = new SockJS('/ws?token=' + sessionJwt);
    stompClient = Stomp.over(socket);

    stompClient.connect({}, function (frame) {
        console.log("✅ WebSocket Connected:", frame);

        // Subscribe to private messages
        stompClient.subscribe("/user/queue/messages", function (message) {
            const msg = JSON.parse(message.body);
            const type = msg.sender === riderEmail ? 'outgoing' : 'incoming';
            showMessage(msg.content, type);
        });
    }, function (error) {
        console.error("❌ WebSocket Connection Error:", error);
        setTimeout(connectWebSocket, 3000); // retry
    });
}

connectWebSocket();

// Send chat message
function sendMessage() {
    const messageInput = document.getElementById("messageInput");
    const content = messageInput.value.trim();
    if (!content || !stompClient) return;

    const chatMessage = {
        sender: riderEmail,
        recipient: driverEmail,
        content: content,
        timestamp: new Date()
    };

    stompClient.send("/app/chat", {}, JSON.stringify(chatMessage));
    showMessage(content, 'outgoing');
    messageInput.value = "";
}

// Press Enter to send
document.addEventListener("DOMContentLoaded", () => {
    const input = document.getElementById("messageInput");
    input.addEventListener("keydown", function (e) {
        if (e.key === "Enter") {
            e.preventDefault();
            sendMessage();
        }
    });
});

// Display message
function showMessage(text, type) {
    const chatBox = document.getElementById("chatBox") || document.getElementById("chat-box");
    const messageDiv = document.createElement("div");

    if (type === 'outgoing') {
        messageDiv.className = "text-right";
        messageDiv.innerHTML = `<span class='inline-block bg-blue-600 text-white px-4 py-2 rounded-2xl max-w-xs break-words'>${text}</span>`;
    } else {
        messageDiv.className = "text-left";
        messageDiv.innerHTML = `<span class='inline-block bg-gray-200 text-gray-900 px-4 py-2 rounded-2xl max-w-xs break-words'>${text}</span>`;
    }

    chatBox.appendChild(messageDiv);
    chatBox.scrollTop = chatBox.scrollHeight;
}
