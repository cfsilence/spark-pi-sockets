package gpio

import com.pi4j.io.gpio.GpioController
import com.pi4j.io.gpio.GpioFactory
import com.pi4j.wiringpi.GpioUtil

@Singleton
class GpioHandler {
    GpioController gpio
    Boolean init=false

    def init(){
        if( !init ) {
            GpioUtil.enableNonPrivilegedAccess()
            gpio = GpioFactory.getInstance()
            this.init = true
        }
    }
}
