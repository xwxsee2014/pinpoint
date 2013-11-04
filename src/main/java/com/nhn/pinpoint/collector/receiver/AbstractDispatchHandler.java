package com.nhn.pinpoint.collector.receiver;

import com.nhn.pinpoint.collector.handler.Handler;
import com.nhn.pinpoint.collector.handler.RequestResponseHandler;
import com.nhn.pinpoint.collector.handler.SimpleHandler;
import com.nhn.pinpoint.collector.util.AcceptedTimeService;
import org.apache.thrift.TBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author emeroad
 */
public abstract class AbstractDispatchHandler implements DispatchHandler {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AcceptedTimeService acceptedTimeService;

    public AbstractDispatchHandler() {
    }


    @Override
    public TBase dispatch(TBase<?, ?> tBase, byte[] packet, int offset, int length) {
        // accepted time 마크
        acceptedTimeService.accept();
        // TODO 수정시 dispatch table은 자동으로 바뀌게 변경해도 될듯하다.
        SimpleHandler simpleHandler = getSimpleHandler(tBase);
        if (simpleHandler != null) {
            if (logger.isTraceEnabled()) {
                logger.trace("simpleHandler name:{}", simpleHandler.getClass().getName());
            }
            simpleHandler.handler(tBase);
            return null;
        }

        Handler handler = getHandler(tBase);
        if (handler != null) {
            if (logger.isTraceEnabled()) {
                logger.trace("handler name:{}", handler.getClass().getName());
            }
            handler.handler(tBase, packet, offset, length);
            return null;
        }

        RequestResponseHandler requestResponseHandler = getRequestResponseHandler(tBase);
        if (requestResponseHandler != null) {
            if (logger.isTraceEnabled()) {
                logger.trace("requestResponseHandler name:{}", requestResponseHandler.getClass().getName());
            }
            return requestResponseHandler.handler(tBase);
        }

        throw new UnsupportedOperationException("Handler not found. Unknown type of data received. tBase=" + tBase);
    }

    Handler getHandler(TBase<?, ?> tBase) {
        return null;
    }


    RequestResponseHandler getRequestResponseHandler(TBase<?, ?> tBase) {
        return null;
    }


    SimpleHandler getSimpleHandler(TBase<?, ?> tBase) {
        return null;
    }
}
