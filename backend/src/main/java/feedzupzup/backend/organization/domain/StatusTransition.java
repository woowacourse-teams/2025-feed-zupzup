package feedzupzup.backend.organization.domain;

import lombok.Getter;

@Getter
public enum StatusTransition {

    WAITING_TO_CONFIRMED(0, 1, -1),
    CONFIRMED_TO_WAITING(0, -1, 1),
    CREATED_CONFIRMED(1, 1, 0),
    CREATED_WAITING(1, 0, 1),
    DELETED_CONFIRMED(-1, -1, 0),
    DELETED_WAITING(-1, 0, -1)
    ;

    private final long totalAmount;
    private final long confirmedAmount;
    private final long waitingAmount;

    StatusTransition(final long totalAmount, final long confirmedAmount, final long waitingAmount) {
        this.totalAmount = totalAmount;
        this.confirmedAmount = confirmedAmount;
        this.waitingAmount = waitingAmount;
    }
}
