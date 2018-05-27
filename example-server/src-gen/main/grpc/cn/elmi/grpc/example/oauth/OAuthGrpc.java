package cn.elmi.grpc.example.oauth;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 */
@javax.annotation.Generated(
        value = "by gRPC proto compiler (version 1.12.0)",
        comments = "Source: OAuth.proto")
public final class OAuthGrpc {

    private OAuthGrpc() {
    }

    public static final String SERVICE_NAME = "oauth.OAuth";

    // Static method descriptors that strictly reflect the proto.
    @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
    @java.lang.Deprecated // Use {@link #getGetAccessTokenMethod()} instead.
    public static final io.grpc.MethodDescriptor<cn.elmi.grpc.example.oauth.OAuthRequest,
            cn.elmi.grpc.example.oauth.OAuthResponse> METHOD_GET_ACCESS_TOKEN = getGetAccessTokenMethodHelper();

    private static volatile io.grpc.MethodDescriptor<cn.elmi.grpc.example.oauth.OAuthRequest,
            cn.elmi.grpc.example.oauth.OAuthResponse> getGetAccessTokenMethod;

    @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
    public static io.grpc.MethodDescriptor<cn.elmi.grpc.example.oauth.OAuthRequest,
            cn.elmi.grpc.example.oauth.OAuthResponse> getGetAccessTokenMethod() {
        return getGetAccessTokenMethodHelper();
    }

    private static io.grpc.MethodDescriptor<cn.elmi.grpc.example.oauth.OAuthRequest,
            cn.elmi.grpc.example.oauth.OAuthResponse> getGetAccessTokenMethodHelper() {
        io.grpc.MethodDescriptor<cn.elmi.grpc.example.oauth.OAuthRequest, cn.elmi.grpc.example.oauth.OAuthResponse> getGetAccessTokenMethod;
        if ((getGetAccessTokenMethod = OAuthGrpc.getGetAccessTokenMethod) == null) {
            synchronized (OAuthGrpc.class) {
                if ((getGetAccessTokenMethod = OAuthGrpc.getGetAccessTokenMethod) == null) {
                    OAuthGrpc.getGetAccessTokenMethod = getGetAccessTokenMethod =
                            io.grpc.MethodDescriptor.<cn.elmi.grpc.example.oauth.OAuthRequest, cn.elmi.grpc.example.oauth.OAuthResponse>newBuilder()
                                    .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                                    .setFullMethodName(generateFullMethodName(
                                            "oauth.OAuth", "getAccessToken"))
                                    .setSampledToLocalTracing(true)
                                    .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                                            cn.elmi.grpc.example.oauth.OAuthRequest.getDefaultInstance()))
                                    .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                                            cn.elmi.grpc.example.oauth.OAuthResponse.getDefaultInstance()))
                                    .setSchemaDescriptor(new OAuthMethodDescriptorSupplier("getAccessToken"))
                                    .build();
                }
            }
        }
        return getGetAccessTokenMethod;
    }

    /**
     * Creates a new async stub that supports all call types for the service
     */
    public static OAuthStub newStub(io.grpc.Channel channel) {
        return new OAuthStub(channel);
    }

    /**
     * Creates a new blocking-style stub that supports unary and streaming output calls on the service
     */
    public static OAuthBlockingStub newBlockingStub(
            io.grpc.Channel channel) {
        return new OAuthBlockingStub(channel);
    }

    /**
     * Creates a new ListenableFuture-style stub that supports unary calls on the service
     */
    public static OAuthFutureStub newFutureStub(
            io.grpc.Channel channel) {
        return new OAuthFutureStub(channel);
    }

    /**
     */
    public static abstract class OAuthImplBase implements io.grpc.BindableService {

        /**
         */
        public void getAccessToken(cn.elmi.grpc.example.oauth.OAuthRequest request,
                                   io.grpc.stub.StreamObserver<cn.elmi.grpc.example.oauth.OAuthResponse> responseObserver) {
            asyncUnimplementedUnaryCall(getGetAccessTokenMethodHelper(), responseObserver);
        }

        @java.lang.Override
        public final io.grpc.ServerServiceDefinition bindService() {
            return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
                    .addMethod(
                            getGetAccessTokenMethodHelper(),
                            asyncUnaryCall(
                                    new MethodHandlers<
                                            cn.elmi.grpc.example.oauth.OAuthRequest,
                                            cn.elmi.grpc.example.oauth.OAuthResponse>(
                                            this, METHODID_GET_ACCESS_TOKEN)))
                    .build();
        }
    }

    /**
     */
    public static final class OAuthStub extends io.grpc.stub.AbstractStub<OAuthStub> {
        private OAuthStub(io.grpc.Channel channel) {
            super(channel);
        }

        private OAuthStub(io.grpc.Channel channel,
                          io.grpc.CallOptions callOptions) {
            super(channel, callOptions);
        }

        @java.lang.Override
        protected OAuthStub build(io.grpc.Channel channel,
                                  io.grpc.CallOptions callOptions) {
            return new OAuthStub(channel, callOptions);
        }

        /**
         */
        public void getAccessToken(cn.elmi.grpc.example.oauth.OAuthRequest request,
                                   io.grpc.stub.StreamObserver<cn.elmi.grpc.example.oauth.OAuthResponse> responseObserver) {
            asyncUnaryCall(
                    getChannel().newCall(getGetAccessTokenMethodHelper(), getCallOptions()), request, responseObserver);
        }
    }

    /**
     */
    public static final class OAuthBlockingStub extends io.grpc.stub.AbstractStub<OAuthBlockingStub> {
        private OAuthBlockingStub(io.grpc.Channel channel) {
            super(channel);
        }

        private OAuthBlockingStub(io.grpc.Channel channel,
                                  io.grpc.CallOptions callOptions) {
            super(channel, callOptions);
        }

        @java.lang.Override
        protected OAuthBlockingStub build(io.grpc.Channel channel,
                                          io.grpc.CallOptions callOptions) {
            return new OAuthBlockingStub(channel, callOptions);
        }

        /**
         */
        public cn.elmi.grpc.example.oauth.OAuthResponse getAccessToken(cn.elmi.grpc.example.oauth.OAuthRequest request) {
            return blockingUnaryCall(
                    getChannel(), getGetAccessTokenMethodHelper(), getCallOptions(), request);
        }
    }

    /**
     */
    public static final class OAuthFutureStub extends io.grpc.stub.AbstractStub<OAuthFutureStub> {
        private OAuthFutureStub(io.grpc.Channel channel) {
            super(channel);
        }

        private OAuthFutureStub(io.grpc.Channel channel,
                                io.grpc.CallOptions callOptions) {
            super(channel, callOptions);
        }

        @java.lang.Override
        protected OAuthFutureStub build(io.grpc.Channel channel,
                                        io.grpc.CallOptions callOptions) {
            return new OAuthFutureStub(channel, callOptions);
        }

        /**
         */
        public com.google.common.util.concurrent.ListenableFuture<cn.elmi.grpc.example.oauth.OAuthResponse> getAccessToken(
                cn.elmi.grpc.example.oauth.OAuthRequest request) {
            return futureUnaryCall(
                    getChannel().newCall(getGetAccessTokenMethodHelper(), getCallOptions()), request);
        }
    }

    private static final int METHODID_GET_ACCESS_TOKEN = 0;

    private static final class MethodHandlers<Req, Resp> implements
            io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
            io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
            io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
            io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
        private final OAuthImplBase serviceImpl;
        private final int methodId;

        MethodHandlers(OAuthImplBase serviceImpl, int methodId) {
            this.serviceImpl = serviceImpl;
            this.methodId = methodId;
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("unchecked")
        public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
            switch (methodId) {
                case METHODID_GET_ACCESS_TOKEN:
                    serviceImpl.getAccessToken((cn.elmi.grpc.example.oauth.OAuthRequest) request,
                            (io.grpc.stub.StreamObserver<cn.elmi.grpc.example.oauth.OAuthResponse>) responseObserver);
                    break;
                default:
                    throw new AssertionError();
            }
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("unchecked")
        public io.grpc.stub.StreamObserver<Req> invoke(
                io.grpc.stub.StreamObserver<Resp> responseObserver) {
            switch (methodId) {
                default:
                    throw new AssertionError();
            }
        }
    }

    private static abstract class OAuthBaseDescriptorSupplier
            implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
        OAuthBaseDescriptorSupplier() {
        }

        @java.lang.Override
        public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
            return cn.elmi.grpc.example.oauth.GrpcOAuth.getDescriptor();
        }

        @java.lang.Override
        public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
            return getFileDescriptor().findServiceByName("OAuth");
        }
    }

    private static final class OAuthFileDescriptorSupplier
            extends OAuthBaseDescriptorSupplier {
        OAuthFileDescriptorSupplier() {
        }
    }

    private static final class OAuthMethodDescriptorSupplier
            extends OAuthBaseDescriptorSupplier
            implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
        private final String methodName;

        OAuthMethodDescriptorSupplier(String methodName) {
            this.methodName = methodName;
        }

        @java.lang.Override
        public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
            return getServiceDescriptor().findMethodByName(methodName);
        }
    }

    private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

    public static io.grpc.ServiceDescriptor getServiceDescriptor() {
        io.grpc.ServiceDescriptor result = serviceDescriptor;
        if (result == null) {
            synchronized (OAuthGrpc.class) {
                result = serviceDescriptor;
                if (result == null) {
                    serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
                            .setSchemaDescriptor(new OAuthFileDescriptorSupplier())
                            .addMethod(getGetAccessTokenMethodHelper())
                            .build();
                }
            }
        }
        return result;
    }
}
