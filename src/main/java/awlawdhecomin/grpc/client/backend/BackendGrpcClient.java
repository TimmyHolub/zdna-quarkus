package awlawdhecomin.grpc.client.backend;

import awlawdhecomin.grpc.client.base.BaseMutinyGrpcClient;
import cz.mendelu.dnaAnalyser.grpc.hello.HelloReply;
import cz.mendelu.dnaAnalyser.grpc.hello.HelloRequest;
import cz.mendelu.dnaAnalyser.grpc.hello.HelloService;
import io.quarkus.grpc.GrpcClient;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class BackendGrpcClient extends BaseMutinyGrpcClient {

    @GrpcClient("backend")
    HelloService helloService;

    public BackendGrpcClient() {
        super("backend");
    }

    /**
     * Sends a hello request using the gRPC client.
     * Returns a Uni that emits the reply message or "REQUEST FAILED" if something goes wrong.
     */
    public Uni<String> sendHello(String name) {
        HelloRequest request = HelloRequest.newBuilder().setMessage(name).build();
        return executeGrpcRequest(helloService.exchangeMessage(request))
                .onItem().ifNotNull().transform(HelloReply::getMessage)
                .onItem().ifNull().continueWith("REQUEST FAILED");
    }
}
