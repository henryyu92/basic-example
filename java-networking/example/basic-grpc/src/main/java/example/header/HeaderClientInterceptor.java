package example.header;

import com.google.common.annotations.VisibleForTesting;
import io.grpc.*;

import java.util.logging.Logger;

public class HeaderClientInterceptor implements ClientInterceptor {

    private static final Logger logger = Logger.getLogger(HeaderClientInterceptor.class.getName());

    @VisibleForTesting
    static final Metadata.Key<String> CUSTOM_HEADER_KEY =
            Metadata.Key.of("custom_client_header_key", Metadata.ASCII_STRING_MARSHALLER);


    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
            MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {

        return new ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(next.newCall(method, callOptions)) {

            @Override
            public void start(Listener<RespT> responseListener, Metadata headers) {
                headers.put(CUSTOM_HEADER_KEY, "customRequestValue");
                super.start(new ForwardingClientCallListener.SimpleForwardingClientCallListener<RespT>(responseListener) {
                    @Override
                    public void onHeaders(Metadata headers) {
                        logger.info("header received from server:" + headers);
                        super.onHeaders(headers);
                    }
                }, headers);
            }
        };
    }
}
