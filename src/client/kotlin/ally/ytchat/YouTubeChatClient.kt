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
			val input = InputStreamReader(request.requestBody, "utf-8")
			val reader = BufferedReader(input)
			var byteIndex: Int
			val stringBuilder = StringBuilder()

			while ((reader.read().also { byteIndex = it }) != -1) {
				stringBuilder.append(byteIndex.toChar())
			}

			reader.close()
			input.close()

			val text = stringBuilder.toString().replace("\n", "").replace("\r", "")
			if (text == "") {
				return
			}
			player.sendMessage(Text.of(text))
			request.sendResponseHeaders(200, 0);
		}
	}
}