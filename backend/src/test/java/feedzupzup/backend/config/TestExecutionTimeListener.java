package feedzupzup.backend.config;

import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;

public class TestExecutionTimeListener implements TestExecutionListener {

    private Long startTime;
    private static Long totalTime = 0L;

    @Override
    public void beforeTestClass(TestContext testContext) {
        startTime = System.currentTimeMillis();
    }

    @Override
    public void afterTestClass(TestContext testContext) {
        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;
        System.out.println("[" + testContext.getTestClass().getSimpleName() + "] 컨텍스트 로딩을 포함한 테스트 실행 시간: " + executionTime + "ms");

        totalTime += executionTime;
        System.out.println(">>> 지금까지 걸린 누적 시간: " + totalTime + "ms (" + (totalTime / 1000.0) + "초)");
    }
}