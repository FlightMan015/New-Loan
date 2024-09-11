package de.joonko.loan.config;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.logging.LoggingHandler;

import java.nio.charset.StandardCharsets;

public class HttpLJettyLoggingHandler extends LoggingHandler {
    public HttpLJettyLoggingHandler(Class<?> clazz) {
        super(clazz);
    }

    @Override
    protected String format(ChannelHandlerContext ctx, String event, Object arg) {
        if (arg instanceof ByteBuf) {
            ByteBuf msg = (ByteBuf) arg;
            return msg.toString(StandardCharsets.UTF_8);
        }
        return super.format(ctx, event, arg);
    }
}
