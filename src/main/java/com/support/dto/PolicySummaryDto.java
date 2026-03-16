package com.support.dto;

import com.support.domain.PolicyStatus;

public class PolicySummaryDto {
    private PolicyStatus status;
    private long count;

    public PolicyStatus getStatus() { return status; }
    public void setStatus(PolicyStatus status) { this.status = status; }
    public long getCount() { return count; }
    public void setCount(long count) { this.count = count; }
}
