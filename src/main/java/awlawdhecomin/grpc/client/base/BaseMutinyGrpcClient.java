package awlawdhecomin.grpc.client.base;

import io.grpc.StatusRuntimeException;
import io.smallrye.mutiny.Uni;
import org.jboss.logging.Logger;


public class BaseMutinyGrpcClient {

    private static final Logger LOG = Logger.getLogger(BaseMutinyGrpcClient.class);
    protected final String serviceName;

    public BaseMutinyGrpcClient(String serviceName) {
        if (serviceName == null) {
            throw new IllegalArgumentException("serviceName cannot be null");
        }
        this.serviceName = serviceName;
    }

    /**
     * Executes a gRPC request represented as a Uni, handling errors uniformly.
     * In case of a failure, logs the error and recovers with a null value.
     */
    protected <T> Uni<T> executeGrpcRequest(Uni<T> grpcCall) {
        return grpcCall
                .onFailure(StatusRuntimeException.class)
                .invoke(e -> handleError((StatusRuntimeException) e))
                // Recover with a fallback value (here, null) if a StatusRuntimeException occurs
                .onFailure(StatusRuntimeException.class)
                .recoverWithItem(throwable -> null);
    }

    private void handleError(StatusRuntimeException e) {
        switch (e.getStatus().getCode()) {
            case UNAVAILABLE:
                LOG.errorf("gRPC service '%s' unavailable. Check if '%s' is running.", serviceName, serviceName);
                break;
            case DEADLINE_EXCEEDED:
                LOG.errorf("gRPC request to '%s' timed out.", serviceName);
                break;
            case INTERNAL:
                LOG.errorf("gRPC internal error in '%s'. Ensure it is a valid gRPC server.", serviceName);
                break;
            default:
                LOG.errorf("gRPC call to '%s' failed: %s", serviceName, e.getStatus().getDescription());
        }
    }
}
