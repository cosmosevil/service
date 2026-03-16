package com.support.dto;

import com.support.domain.ClaimStatus;
import java.math.BigDecimal;

public class ClaimSummaryDto {
    private ClaimStatus status;
    private long count;
    private BigDecimal totalApprovedAmount;

    public ClaimStatus getStatus() { return status; }
    public void setStatus(ClaimStatus status) { this.status = status; }
    public long getCount() { return count; }
    public void setCount(long count) { this.count = count; }
    public BigDecimal getTotalApprovedAmount() { return totalApprovedAmount; }
    public void setTotalApprovedAmount(BigDecimal totalApprovedAmount) { this.totalApprovedAmount = totalApprovedAmount; }
}
