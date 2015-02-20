package clevercloud;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.handler.codec.http.HttpContentCompressor;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;

import static org.jboss.netty.channel.Channels.pipeline;

/**
 * @author Marc-Antoine Perennou<Marc-Antoine@Perennou.com>
 */

public class HTTPPipelineFactory implements ChannelPipelineFactory {

   public ChannelPipeline getPipeline() {
      ChannelPipeline pipeline = pipeline();
      pipeline.addLast("decoder", new HttpRequestDecoder());
      pipeline.addLast("encoder", new HttpResponseEncoder());
      pipeline.addLast("deflater", new HttpContentCompressor());
      pipeline.addLast("handler", new HTTPHandler());
      return pipeline;
   }
}
