package feedzupzup.backend.feedback.domain;

import feedzupzup.backend.organization.domain.Organization;
import java.io.OutputStream;
import java.util.List;

public interface FeedbackExcelExporter {

    void export(final Organization organization, final List<Feedback> feedbacks, final OutputStream outputStream);
}
