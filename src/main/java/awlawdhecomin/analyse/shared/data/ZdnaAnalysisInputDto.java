package awlawdhecomin.analyse.shared.data;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ZdnaAnalysisInputDto {
    private String sequenceDataPlain;
    private int minSequenceSize;
    private float threshold;
    private float gc;
    private float gtac;
    private float at;
}
