package com.btree.shared.job;

public record JobResult(int processed, int skipped, int failed) {

    public static JobResult of(final int processed, final int skipped, final int failed) {
        return new JobResult(processed, skipped, failed);
    }

    public static JobResult success(final int processed) {
        return new JobResult(processed, 0, 0);
    }

    public static JobResult empty() {
        return new JobResult(0, 0, 0);
    }

    public int total() {
        return processed + skipped + failed;
    }

    public boolean hasFailures() {
        return failed > 0;
    }

    @Override
    public String toString() {
        if (skipped > 0) {
            return "JobResult{processed=%d, skipped=%d, failed=%d, total=%d}"
                    .formatted(processed, skipped, failed, total());
        }
        return "JobResult{processed=%d, failed=%d, total=%d}"
                .formatted(processed, failed, total());
    }
}
