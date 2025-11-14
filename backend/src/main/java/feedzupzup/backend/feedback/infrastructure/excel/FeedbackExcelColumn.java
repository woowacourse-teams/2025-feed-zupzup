package feedzupzup.backend.feedback.infrastructure.excel;

public enum FeedbackExcelColumn {

    FEEDBACK_NUMBER(0, "피드백 번호"),
    CONTENT(1, "피드백 내용"),
    CATEGORY(2, "카테고리"),
    IMAGE(3, "첨부 이미지"),
    LIKE_COUNT(4, "좋아요 수"),
    IS_SECRET(5, "비밀글 여부"),
    STATUS(6, "처리 상태"),
    COMMENT(7, "관리자 답변"),
    USER_NAME(8, "작성자 닉네임"),
    POSTED_AT(9, "작성 일시"),
    ;

    private final int columnIndex;
    private final String headerName;

    FeedbackExcelColumn(int columnIndex, String headerName) {
        this.columnIndex = columnIndex;
        this.headerName = headerName;
    }

    public int columnIndex() {
        return columnIndex;
    }

    public String headerName() {
        return headerName;
    }
}
