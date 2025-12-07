package awlawdhecomin.zdna;

import awlawdhecomin.common.sequence.nucleotide.Nucleotide;
import awlawdhecomin.common.sequence.stream.BufferedWindow;
import awlawdhecomin.common.sequence.stream.Window;
import awlawdhecomin.zdna.dto.ZdnaAnalysisInputDto;
import awlawdhecomin.zdna.dto.ZdnaAnalysisResultDto;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@ApplicationScoped
public class ZdnaAnalyser {

    private static final Pattern PATTERN_GC = Pattern.compile("(?=(GC|CG))");
    private static final Pattern PATTERN_GT = Pattern.compile("(?=(GT|TG))");

    public List<ZdnaAnalysisResultDto> getResults(ZdnaAnalysisInputDto inputDto) {
        // Prepare sequence window
        Window sequenceWindow = BufferedWindow.wrap(inputDto.getSequenceDataPlain());

        // Scoring parameters from input
        float gcScore = inputDto.getGc();
        float gtAcScore = inputDto.getGtac();
        float atScore = inputDto.getAt();
        float notPossibleZdnaScore = 0;

        Map<NucleotidePair, Float> subscoreMap = createSubscoreMapping(gcScore, gtAcScore, atScore);

        List<ZdnaAnalysisResultDto> results = new ArrayList<>();

        int i = 0;
        int len = 1;
        float score = 0;
        float tmpScore;

        while (i < (sequenceWindow.getSize())) {
            tmpScore = notPossibleZdnaScore;
            if (i < sequenceWindow.getSize() - 1) {
                Nucleotide now = sequenceWindow.get(i);
                Nucleotide next = sequenceWindow.get(i + 1);

                tmpScore = subscoreMap.getOrDefault(new NucleotidePair(now, next), notPossibleZdnaScore);
            }
            if (tmpScore > 0) {
                len++;
                score += tmpScore;
            } else {
                if (len >= inputDto.getMinSequenceSize()) {
                    float kvScore = score / 2;
                    float maxPossibleScore = ((len - 1) * Math.max(gcScore, Math.max(gtAcScore, atScore))) / 2;
                    float scorePerc = (kvScore / maxPossibleScore) * 100;

                    if (scorePerc >= inputDto.getThreshold()) {
                        int start = i - len + 2;

                        StringBuilder seqBuilder = new StringBuilder();
                        for (int k = 0; k < len; k++) {
                            seqBuilder.append((char) sequenceWindow.get(start + k - 1).toByte());
                        }
                        String seq = seqBuilder.toString();
                        int pcLen = len - 1;

                        double countGC = PATTERN_GC.matcher(seq).results().count();
                        double richnessGC = countGC / pcLen * 100;
                        double countGT = PATTERN_GT.matcher(seq).results().count();
                        double richnessGT = countGT / pcLen * 100;

                        ZdnaAnalysisResultDto result = ZdnaAnalysisResultDto.builder()
                                .id(null)
                                .position(start)
                                .length(len)
                                .sequence(seq)
                                .zdnaGCRichness(richnessGC)
                                .zdnaGTRichness(richnessGT)
                                .score(kvScore)
                                .scorePerc(scorePerc)
                                .build();

                        results.add(result);
                    }
                }
                // reset for next potential sequence
                len = 1;
                score = 0;
            }
            i++;
        }

        return results;
    }

    private Map<NucleotidePair, Float> createSubscoreMapping(
            float gcScore,
            float gtAcScore,
            float atScore
    ) {
        Map<NucleotidePair, Float> subscoreMap = new HashMap<>();
        // GC pairs
        subscoreMap.put(new NucleotidePair(Nucleotide.G, Nucleotide.C), gcScore);
        subscoreMap.put(new NucleotidePair(Nucleotide.C, Nucleotide.G), gcScore);
        // GT/AC pairs
        subscoreMap.put(new NucleotidePair(Nucleotide.G, Nucleotide.T), gtAcScore);
        subscoreMap.put(new NucleotidePair(Nucleotide.T, Nucleotide.G), gtAcScore);
        subscoreMap.put(new NucleotidePair(Nucleotide.A, Nucleotide.C), gtAcScore);
        subscoreMap.put(new NucleotidePair(Nucleotide.C, Nucleotide.A), gtAcScore);
        // AT pairs
        subscoreMap.put(new NucleotidePair(Nucleotide.A, Nucleotide.T), atScore);
        subscoreMap.put(new NucleotidePair(Nucleotide.T, Nucleotide.A), atScore);
        return subscoreMap;
    }

    private record NucleotidePair(Nucleotide first, Nucleotide second) {
    }
}
