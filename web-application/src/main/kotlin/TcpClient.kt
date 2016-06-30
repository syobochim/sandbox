import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket

/**
 * @author syobochim
 */

fun main(args: Array<String>) {
    Socket("localhost", 80).use { socket ->

        val output : OutputStream = socket.outputStream
        FileInputStream("./web-application/src/main/resources/client_send.txt").use { fileInput ->
            generateSequence {
                fileInput.read()
            }.takeWhile {
                it != -1
            }. forEach {
                output.write(it)
            }
        }
        // 終了を示すため、ゼロを送信
//        output.write(0)

        val input : InputStream = socket.inputStream
        FileOutputStream("./web-application/src/main/resources/client_recv.txt").use { fileOutput ->
            generateSequence {
                input.read()
            }.takeWhile {
                it != -1
            }.forEach {
                fileOutput.write(it)
            }
        }

    }

}
