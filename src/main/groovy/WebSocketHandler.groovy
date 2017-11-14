import org.eclipse.jetty.websocket.api.*
import org.eclipse.jetty.websocket.api.annotations.*

@WebSocket
class WebSocketHandler {

    @OnWebSocketConnect
    void onConnect(Session user) throws Exception {
        Bootstrap.users << user
        Bootstrap.broadcastMessage("Server",
                [
                        message: "connection established from ${user.remoteAddress}"
                ]
        )
    }

    @OnWebSocketClose
    void onClose(Session user, int statusCode, String reason) {
        Bootstrap.broadcastMessage("Server",
                [
                        message: "connection disconnected from ${user.remoteAddress}"
                ]
        )
        println Bootstrap.users
        Bootstrap.users.remove(user)
        println Bootstrap.users
    }

    @OnWebSocketMessage
    void onMessage(Session user, String message) {
        Bootstrap.broadcastMessage(user, [message: message])
    }

}