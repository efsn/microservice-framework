package cn.elmi.grpc.example.hello;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 */
@javax.annotation.Generated(
        value = "by gRPC proto compiler (version 1.9.1)",
        comments = "Source: Hello.proto")
public final class HelloGrpc {

    private HelloGrpc() {
    }

    public static final String SERVICE_NAME = "hello.Hello";

    // Static method descriptors that strictly reflect the proto.
    @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
    @java.lang.Deprecated // Use {@link #getSayMethod()} instead.
    public static final io.grpc.MethodDescriptor<cn.elmi.grpc.example.hello.HelloRequest,
            cn.elmi.grpc.example.hello.HelloResponse> METHOD_SAY = getSayMethod();

    private static volatile io.grpc.MethodDescriptor<cn.elmi.grpc.example.hello.HelloRequest,
            cn.elmi.grpc.example.hello.HelloResponse> getSayMethod;

    @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
    public static io.grpc.MethodDescriptor<cn.elmi.grpc.example.hello.HelloRequest,
            cn.elmi.grpc.example.hello.HelloResponse> getSayMethod() {
        io.grpc.MethodDescriptor<cn.elmi.grpc.example.hello.HelloRequest, cn.elmi.grpc.example.hello.HelloResponse> getSayMethod;
        if ((getSayMethod = HelloGrpc.getSayMethod) == null) {
            synchronized (HelloGrpc.class) {
                if ((getSayMethod = HelloGrpc.getSayMethod) == null) {
                    HelloGrpc.getSayMethod = getSayMethod =
                            io.grpc.MethodDescriptor.<cn.elmi.grpc.example.hello.HelloRequest, cn.elmi.grpc.example.hello.HelloResponse>newBuilder()
                                    .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                                    .setFullMethodName(generateFullMethodName(
                                            "hello.Hello", "say"))
                                    .setSampledToLocalTracing(true)
                                    .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                                            cn.elmi.grpc.example.hello.HelloRequest.getDefaultInstance()))
                                    .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                                            cn.elmi.grpc.example.hello.HelloResponse.getDefaultInstance()))
                                    .setSchemaDescriptor(new HelloMethodDescriptorSupplier("say"))
                                    .build();
                }
            }
        }
        return getSayMethod;
    }

    /**
     * Creates a new async stub that supports all call types for the service
     */
    public static HelloStub newStub(io.grpc.Channel channel) {
        return new HelloStub(channel);
    }

    /**
     * Creates a new blocking-style stub that supports unary and streaming output calls on the service
     */
    public static HelloBlockingStub newBlockingStub(
            io.grpc.Channel channel) {
        return new HelloBlockingStub(channel);
    }

    /**
     * Creates a new ListenableFuture-style stub that supports unary calls on the service
     */
    public static HelloFutureStub newFutureStub(
            io.grpc.Channel channel) {
        return new HelloFutureStub(channel);
    }

    /**
     */
    public static abstract class HelloImplBase implements io.grpc.BindableService {

        /**
         */
        public void say(cn.elmi.grpc.example.hello.HelloRequest request,
                        io.grpc.stub.StreamObserver<cn.elmi.grpc.example.hello.HelloResponse> responseObserver) {
            asyncUnimplementedUnaryCall(getSayMethod(), responseObserver);
        }

        @java.lang.Override
        public final io.grpc.ServerServiceDefinition bindService() {
            return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
                    .addMethod(
                            getSayMethod(),
                            asyncUnaryCall(
                                    new MethodHandlers<
                                            cn.elmi.grpc.example.hello.HelloRequest,
                                            cn.elmi.grpc.example.hello.HelloResponse>(
                                            this, METHODID_SAY)))
                    .build();
        }
    }

    /**
     */
    public static final class HelloStub extends io.grpc.stub.AbstractStub<HelloStub> {
        private HelloStub(io.grpc.Channel channel) {
            super(channel);
        }

        private HelloStub(io.grpc.Channel channel,
                          io.grpc.CallOptions callOptions) {
            super(channel, callOptions);
        }

        @java.lang.Override
        protected HelloStub build(io.grpc.Channel channel,
                                  io.grpc.CallOptions callOptions) {
            return new HelloStub(channel, callOptions);
        }

        /**
         */
        public void say(cn.elmi.grpc.example.hello.HelloRequest request,
                        io.grpc.stub.StreamObserver<cn.elmi.grpc.example.hello.HelloResponse> responseObserver) {
            asyncUnaryCall(
                    getChannel().newCall(getSayMethod(), getCallOptions()), request, responseObserver);
        }
    }

    /**
     */
    public static final class HelloBlockingStub extends io.grpc.stub.AbstractStub<HelloBlockingStub> {
        private HelloBlockingStub(io.grpc.Channel channel) {
            super(channel);
        }

        private HelloBlockingStub(io.grpc.Channel channel,
                                  io.grpc.CallOptions callOptions) {
            super(channel, callOptions);
        }

        @java.lang.Override
        protected HelloBlockingStub build(io.grpc.Channel channel,
                                          io.grpc.CallOptions callOptions) {
            return new HelloBlockingStub(channel, callOptions);
        }

        /**
         */
        public cn.elmi.grpc.example.hello.HelloResponse say(cn.elmi.grpc.example.hello.HelloRequest request) {
            return blockingUnaryCall(
                    getChannel(), getSayMethod(), getCallOptions(), request);
        }
    }

    /**
     */
    public static final class HelloFutureStub extends io.grpc.stub.AbstractStub<HelloFutureStub> {
        private HelloFutureStub(io.grpc.Channel channel) {
            super(channel);
        }

        private HelloFutureStub(io.grpc.Channel channel,
                                io.grpc.CallOptions callOptions) {
            super(channel, callOptions);
        }

        @java.lang.Override
        protected HelloFutureStub build(io.grpc.Channel channel,
                                        io.grpc.CallOptions callOptions) {
            return new HelloFutureStub(channel, callOptions);
        }

        /**
         */
        public com.google.common.util.concurrent.ListenableFuture<cn.elmi.grpc.example.hello.HelloResponse> say(
                cn.elmi.grpc.example.hello.HelloRequest request) {
            return futureUnaryCall(
                    getChannel().newCall(getSayMethod(), getCallOptions()), request);
        }
    }

    private static final int METHODID_SAY = 0;

    private static final class MethodHandlers<Req, Resp> implements
            io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
            io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
            io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
            io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
        private final HelloImplBase serviceImpl;
        private final int methodId;

        MethodHandlers(HelloImplBase serviceImpl, int methodId) {
            this.serviceImpl = serviceImpl;
            this.methodId = methodId;
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("unchecked")
        public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
            switch (methodId) {
                case METHODID_SAY:
                    serviceImpl.say((cn.elmi.grpc.example.hello.HelloRequest) request,
                            (io.grpc.stub.StreamObserver<cn.elmi.grpc.example.hello.HelloResponse>) responseObserver);
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

    private static abstract class HelloBaseDescriptorSupplier
            implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
        HelloBaseDescriptorSupplier() {
        }

        @java.lang.Override
        public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
            return cn.elmi.grpc.example.hello.GrpcHello.getDescriptor();
        }

        @java.lang.Override
        public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
            return getFileDescriptor().findServiceByName("Hello");
        }
    }

    private static final class HelloFileDescriptorSupplier
            extends HelloBaseDescriptorSupplier {
        HelloFileDescriptorSupplier() {
        }
    }

    private static final class HelloMethodDescriptorSupplier
            extends HelloBaseDescriptorSupplier
            implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
        private final String methodName;

        HelloMethodDescriptorSupplier(String methodName) {
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
            synchronized (HelloGrpc.class) {
                result = serviceDescriptor;
                if (result == null) {
                    serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
                            .setSchemaDescriptor(new HelloFileDescriptorSupplier())
                            .addMethod(getSayMethod())
                            .build();
                }
            }
        }
        return result;
    }
}
