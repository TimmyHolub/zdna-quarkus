package awlawdhecomin.grpc.server;

import awlawdhecomin.analyse.shared.data.ZdnaAnalyseResult;
import awlawdhecomin.analyse.shared.data.ZdnaAnalysisInputDto;
import awlawdhecomin.analyse.zdna.ZdnaAnalyser;
import com.google.protobuf.Int64Value;
import cz.mendelu.dnaAnalyser.grpc.analysisStreaming.*;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.quarkus.grpc.GrpcService;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.util.List;

@GrpcService
public class ZdnaAnalysiStreamingServiceImpl implements ZdnaAnalysisService {

    private static final Logger LOG = Logger.getLogger(ZdnaAnalysiStreamingServiceImpl.class);

    @Inject
    ZdnaAnalyser zdnaAnalyser;

    @Override
    public Uni<ZdnaAnalysisResponse> analyzeZdna(Multi<ZdnaAnalysisRequest> requests) {
        return requests.collect().asList().flatMap(list -> {
            if (list.isEmpty() || !list.getFirst().hasConfig()) {
                return Uni.createFrom().failure(new StatusRuntimeException(Status.INVALID_ARGUMENT.withDescription("First request must contain config")));
            }

            ZdnaAnalysisConfig config = list.getFirst().getConfig();

            StringBuilder sequence = new StringBuilder();
            for (int i = 1; i < list.size(); i++) {
                ZdnaAnalysisRequest req = list.get(i);
                if (!req.hasChunk()) {
                    return Uni.createFrom().failure(new StatusRuntimeException(Status.INVALID_ARGUMENT.withDescription("Subsequent requests must contain chunks")));
                }
                sequence.append(req.getChunk().getData());
            }

            LOG.infof("Received Zdna analysis input: sequence length %d, minSequenceSize %d", sequence.length(), config.getMinSequenceSize());

            ZdnaAnalysisInputDto inputDto = ZdnaAnalysisInputDto.builder()
                    .sequenceDataPlain(sequence.toString())
                    .minSequenceSize(config.getMinSequenceSize())
                    .threshold(config.getThreshold())
                    .gc(config.getGc())
                    .gtac(config.getGtac())
                    .at(config.getAt())
                    .build();

            List<ZdnaAnalyseResult> domainResults = zdnaAnalyser.getResults(inputDto);

            List<ZdnaAnalysisResult> protoResults = domainResults.stream()
                    .map(this::toZdnaAnalysisResult)
                    .toList();

            ZdnaAnalysisResponse response = ZdnaAnalysisResponse.newBuilder()
                    .addAllResults(protoResults)
                    .build();

            return Uni.createFrom().item(response);
        });
    }

    private ZdnaAnalysisResult toZdnaAnalysisResult(ZdnaAnalyseResult zdnaAnalyseResult) {
        ZdnaAnalysisResult.Builder builder = ZdnaAnalysisResult.newBuilder()
                .setPosition(zdnaAnalyseResult.getPosition())
                .setLength(zdnaAnalyseResult.getLength())
                .setSequence(zdnaAnalyseResult.getSequence())
                .setZdnaGCRichness(zdnaAnalyseResult.getZdnaGCRichness())
                .setZdnaGTRichness(zdnaAnalyseResult.getZdnaGTRichness())
                .setScore(zdnaAnalyseResult.getScore())
                .setScorePerc(zdnaAnalyseResult.getScorePerc());

        Long id = zdnaAnalyseResult.getId();
        if (id != null) {
            builder.setId(Int64Value.of(id));
        }

        return builder.build();
    }
}
