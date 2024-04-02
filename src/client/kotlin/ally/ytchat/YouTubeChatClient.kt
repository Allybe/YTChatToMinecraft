package ally.ytchat

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer
import net.fabricmc.api.ClientModInitializer
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.text.Text
import org.slf4j.LoggerFactory
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.InetSocketAddress


object YouTubeChatClient : ClientModInitializer {
	private val logger = LoggerFactory.getLogger("youtube-chat")
	private val server: HttpServer = HttpServer.create(InetSocketAddress(8000), 0)

	override fun onInitializeClient() {
		server.createContext("/message", MessageHandler())
		server.executor = null
		server.start()
	}


	internal class MessageHandler : HttpHandler {
		@Throws(IOException::class)
		override fun handle(request: HttpExchange) {
			val player: ClientPlayerEntity = MinecraftClient.getInstance().player ?: return

			val isr = InputStreamReader(request.requestBody, "utf-8")
			val br = BufferedReader(isr)

			var b: Int
			val buf = StringBuilder()
			while ((br.read().also { b = it }) != -1) {
				buf.append(b.toChar())
			}

			br.close()
			isr.close()

			player.sendMessage(Text.of(buf.toString()))

			request.sendResponseHeaders(200, 0);
		}
	}
}