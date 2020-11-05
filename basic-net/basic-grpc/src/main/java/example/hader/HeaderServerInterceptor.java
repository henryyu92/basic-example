package example.hader;

import com.google.common.annotations.VisibleForTesting;
import io.grpc.*;

import java.util.logging.Logger;

public class HeaderServerInterceptor implements ServerInterceptor {

    private static final Logger logger = Logger.getLogger(HeaderServerInterceptor.class.getName());

    @VisibleForTesting
    static final Metadata.Key<String> CUSTOM_HEADER_KEY =
            Metadata.Key.of("custom_server_header_key", Metadata.ASCII_STRING_MARSHALLER);

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {

        logger.info("header received from client:" + headers);

        return next.startCall(new ForwardingServerCall.SimpleForwardingServerCall<ReqT, RespT>(call) {

            @Override
            public void sendHeaders(Metadata headers) {
                headers.put(CUSTOM_HEADER_KEY, "customRespondValue");
                super.sendHeaders(headers);
            }
        }, headers);
    }
}
