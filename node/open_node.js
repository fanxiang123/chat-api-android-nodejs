var WebSocketServer = require('websocket').server;
var http = require('http');
const https = require('https');


const options = {
    hostname: 'api.openai.com',
    port: 443,
    path: '/v1/chat/completions',
    method: 'POST',
    headers: {
        Accept: 'application/json, text/plain, */*',
        'Authorization': 'Bearer You-Key',
        'Content-Type': 'application/json',
        'OpenAI-Event-Stream': 'true'
    },
    responseType: 'stream'
};

// 创建http服务器
var server = http.createServer(function (request, response) {
    console.log((new Date()) + ' Received request for ' + request.url);
    response.writeHead(404);
    response.end();
});

// 监听端口
server.listen(8080, function () {
    console.log((new Date()) + ' Server is listening on port 8080');
});

// 创建WebSocket服务器
var wsServer = new WebSocketServer({
    httpServer: server
});

// 监听WebSocket连接事件
wsServer.on('request', function (request) {
    var connection = request.accept(null, request.origin);
    console.log((new Date()) + ' Connection accepted.');

    // 监听WebSocket消息事件
    connection.on('message', function (message) {
        // 可能会出现错误的代码
        console.log('messg====: ' + message.utf8Data);





        const req = https.request(options, (res) => {


            //            let data = '';

            res.on('data', (chunk) => {
                onnection.sendUTF(chunk);

            });
            res.on('end', () => {
                console.log(`Response:==========`)

            });
        });

        req.on('error', (error) => {
            console.error(error);
        });


        const msg = JSON.parse(message.utf8Data);
        const messages = [];
        for (let i = 0; i < msg.length; i++) {
            const item = msg[i];
            messages.push({ "content": item, "role": "user" })
        }

        //   data: '{"model":"gpt-3.5-turbo","stream":true,"messages":[{"role":"user","content":"Hello"}]}',

        var write = { "messages": messages, "model": "gpt-3.5-turbo", "stream": true }
        console.error(JSON.stringify(write));

        //    req.write(JSON.stringify(message.utf8Data));
        req.write(JSON.stringify(write));
        req.end();





    });

    // 监听WebSocket关闭事件
    connection.on('close', function (reasonCode, description) {
        console.log((new Date()) + ' Peer ' + connection.remoteAddress + ' disconnected.');
    });
});
