package clevercloud;

import org.jboss.netty.channel.*;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;

import static org.jboss.netty.handler.codec.http.HttpResponseStatus.OK;
import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * @author Marc-Antoine Perennou<Marc-Antoine@Perennou.com>
 */

public class HTTPHandler extends SimpleChannelUpstreamHandler {

   @Override
   public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
      e.getChannel().write(new DefaultHttpResponse(HTTP_1_1, OK)).addListener(ChannelFutureListener.CLOSE);
   }

   @Override
   public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
   }
}
