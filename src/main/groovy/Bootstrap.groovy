import com.pi4j.io.gpio.PinPullResistance
import com.pi4j.io.gpio.PinState
import com.pi4j.io.gpio.RaspiPin
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent
import com.pi4j.io.gpio.event.GpioPinListenerDigital
import gpio.GpioHandler
import groovy.json.JsonOutput
import org.eclipse.jetty.websocket.api.Session
import spark.Spark

import static spark.Spark.*

class Bootstrap {
    static List<Session> users = [];

    static void main(String[] args) {
        Spark.staticFileLocation('/static')
        webSocket("/socket", WebSocketHandler.class)
        init()

        GpioHandler.instance.init()

        def ledPin = RaspiPin.getPinByAddress(0)
        def buttonPin = RaspiPin.getPinByAddress(2)
        def led = GpioHandler.instance.gpio.provisionDigitalOutputPin(ledPin)

        def button = GpioHandler.instance.gpio.provisionDigitalInputPin(buttonPin, PinPullResistance.PULL_UP)
        button.setShutdownOptions(true)

        button.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                // if the button state is HIGH, set the led state to LOW
                led.setState(event.getState() == PinState.HIGH ? PinState.LOW : PinState.HIGH)
                println(" --> GPIO PIN STATE CHANGE: " + event.getPin() + " = " + event.getState());
                broadcastMessage('GPIO', [event: event, message: " --> GPIO PIN STATE CHANGE: " + event.getPin() + " = " + event.getState()])
            }
        })

    }

    //Sends a message to all users
    static void broadcastMessage(String sender, Object message) {
        users.findAll{ Session it -> it.isOpen() }.each { Session it ->
            try {
                def msg = JsonOutput.toJson([message: message])
                it.getRemote().sendString(String.valueOf(msg))
            } catch (Exception e) {
                e.printStackTrace()
            }
        }
    }
}