package awlawdhecomin.grpc.server;

import awlawdhecomin.zdna.ZdnaAnalyser;
import awlawdhecomin.zdna.dto.ZdnaAnalysisInputDto;
import awlawdhecomin.zdna.dto.ZdnaAnalysisResultDto;
import com.google.protobuf.Int64Value;
import cz.mendelu.dnaAnalyser.grpc.analysis.ZdnaAnalysisInput;
import cz.mendelu.dnaAnalyser.grpc.analysis.ZdnaAnalysisResponse;
import cz.mendelu.dnaAnalyser.grpc.analysis.ZdnaAnalysisResult;
import cz.mendelu.dnaAnalyser.grpc.analysis.ZdnaAnalysisService;
import io.quarkus.grpc.GrpcService;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.util.List;

@GrpcService
public class ZdnaAnalysisServiceImpl implements ZdnaAnalysisService {

    private static final Logger LOG = Logger.getLogger(ZdnaAnalysisServiceImpl.class);

    @Inject
    ZdnaAnalyser zdnaAnalyser;

    @Override
    public Uni<ZdnaAnalysisResponse> analyzeZdna(ZdnaAnalysisInput request) {
        LOG.infof("Received Zdna analysis input: %s", request.getSequenceDataPlain());

        ZdnaAnalysisInputDto inputDto = toZdnaAnalysisInputDto(request);
        List<ZdnaAnalysisResult> results = zdnaAnalyser.getResults(inputDto)
                .stream()
                .map(this::toZdnaAnalysisResult)
                .toList();

        ZdnaAnalysisResponse response = ZdnaAnalysisResponse.newBuilder()
                .addAllResults(results)
                .build();

        return Uni.createFrom().item(response);
    }

    private ZdnaAnalysisResult toZdnaAnalysisResult(ZdnaAnalysisResultDto zdnaAnalysisResultDto) {
        ZdnaAnalysisResult.Builder builder = ZdnaAnalysisResult.newBuilder()
                .setPosition(zdnaAnalysisResultDto.getPosition())
                .setLength(zdnaAnalysisResultDto.getLength())
                .setSequence(zdnaAnalysisResultDto.getSequence())
                .setZdnaGCRichness(zdnaAnalysisResultDto.getZdnaGCRichness())
                .setZdnaGTRichness(zdnaAnalysisResultDto.getZdnaGTRichness())
                .setScore(zdnaAnalysisResultDto.getScore())
                .setScorePerc(zdnaAnalysisResultDto.getScorePerc());

        Long id = zdnaAnalysisResultDto.getId();
        if (id != null) {
            builder.setId(Int64Value.of(id));
        }

        return builder.build();
    }

    private ZdnaAnalysisInputDto toZdnaAnalysisInputDto(ZdnaAnalysisInput zdnaAnalysisInput) {
        return ZdnaAnalysisInputDto.builder()
                .sequenceDataPlain(zdnaAnalysisInput.getSequenceDataPlain())
                .minSequenceSize(zdnaAnalysisInput.getMinSequenceSize())
                .threshold(zdnaAnalysisInput.getThreshold())
                .gc(zdnaAnalysisInput.getGc())
                .gtac(zdnaAnalysisInput.getGtac())
                .at(zdnaAnalysisInput.getAt())
                .build();
    }
}
