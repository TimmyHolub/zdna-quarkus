package awlawdhecomin.api.grpc;

import awlawdhecomin.grpc.client.backend.BackendGrpcClient;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/backend")
public class BackendResource {

    @Inject
    BackendGrpcClient grpcClient;

    @GET
    @Path("/{name}")
    @Produces(MediaType.TEXT_PLAIN)
    public Uni<String> hello(String name) {
        return grpcClient.sendHello(name);
    }
}