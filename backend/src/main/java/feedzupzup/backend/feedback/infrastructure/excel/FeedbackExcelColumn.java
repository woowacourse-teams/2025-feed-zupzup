package feedzupzup.backend.feedback.infrastructure.excel;

public enum FeedbackExcelColumn {

    FEEDBACK_NUMBER(0, "피드백 번호", 4000),
    CONTENT(1, "피드백 내용", 4000),
    CATEGORY(2, "카테고리", 4000),
    IMAGE(3, "첨부 이미지", 10000),
    LIKE_COUNT(4, "좋아요 수", 4000),
    IS_SECRET(5, "비밀글 여부", 4000),
    STATUS(6, "처리 상태", 4000),
    COMMENT(7, "관리자 답변", 4000),
    USER_NAME(8, "작성자 닉네임", 4000),
    POSTED_AT(9, "작성 일시", 6000),
    ;

    private final int columnIndex;
    private final String headerName;
    private final int columnWidth;

    FeedbackExcelColumn(int columnIndex, String headerName, int columnWidth) {
        this.columnIndex = columnIndex;
        this.headerName = headerName;
        this.columnWidth = columnWidth;
    }

    public int columnIndex() {
        return columnIndex;
    }

    public String headerName() {
        return headerName;
    }

    public int columnWidth() {
        return columnWidth;
    }
}
