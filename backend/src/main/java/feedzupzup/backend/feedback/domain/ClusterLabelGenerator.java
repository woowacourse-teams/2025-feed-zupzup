package feedzupzup.backend.feedback.domain;

import java.util.List;

public interface ClusterLabelGenerator {

    String generate(List<String> clusterContents);
}
