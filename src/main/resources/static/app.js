let stompClient = null;
let ChatMessageUl = null;

function ChatWriteMessage(form) {

    form.content.value = form.content.value.trim();

    stompClient.send(`/pub/live/chats/coin/sendMessage`, {}, JSON.stringify({message: form.content.value}));

    form.content.value = '';
    form.content.focus();
}

function drawMessage(body) {
        const newItem = document.createElement("li");
        newItem.textContent = `${body.senderName} : ${body.message}`;

        ChatMessageUl.appendChild(newItem);
}

function connect() {
    var socket = new SockJS('/ws-stomp');
    stompClient = Stomp.over(socket);

    const headers = {
        'X-CSRF-TOKEN': token,
    };

    stompClient.connect(headers, function (frame) {
        console.log('Connected: ' + frame);

        stompClient.subscribe(`/sub/live/chats/coin`, function (data) {
            console.log(data.body);
            const body = JSON.parse(data.body);
            drawMessage(body);
        });
    });
}

document.addEventListener("DOMContentLoaded", function() {
    ChatMessageUl = document.getElementById("chat__message-ul");
    connect();
});