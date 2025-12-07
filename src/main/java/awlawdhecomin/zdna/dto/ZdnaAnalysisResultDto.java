package awlawdhecomin.zdna.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ZdnaAnalysisResultDto {
    private Long id;

    private int position;
    private int length;

    private String sequence;

    private double zdnaGCRichness;
    private double zdnaGTRichness;

    private float score;
    private float scorePerc;
}

