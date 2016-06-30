import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.ServerSocket
import java.net.Socket

/**
 * @author syobochim
 */
fun main(args: Array<String>) {

    ServerSocket(8001).use { server ->

        println("クライアントからの接続を待ちます。")
        val socket: Socket = server.accept()
        println("クライアント接続")

        // クライアントから受け取った内容をserver_receive.txtに出力
        val input: InputStream = socket.inputStream
        // クライアントは終了のマークをとして0を送付する
        FileOutputStream("./web-application/src/main/resources/server_receive.txt").use { output ->
            generateSequence() {
                input.read()
            }.takeWhile {
                it != 0
            }.forEach {
                output.write(it)
            }
        }

        // server_send.txtの内容をクライアントに送付
        val output: OutputStream = socket.outputStream
        FileInputStream("./web-application/src/main/resources/server_send.txt").use { input ->
            generateSequence(0) {
                input.read()
            }.takeWhile {
                it != -1
            }.forEach {
                output.write(it)
            }
        }

        socket.close()
        println("通信を終了しました。")
    }
}
