package feedzupzup.backend.feedback.domain;

import feedzupzup.backend.organization.domain.Organization;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

public interface FeedbackExcelExporter {

    void export(final Organization organization, final List<Feedback> feedbacks, final HttpServletResponse response);
}
