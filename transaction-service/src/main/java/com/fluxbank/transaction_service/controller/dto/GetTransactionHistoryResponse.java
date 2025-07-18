package com.fluxbank.transaction_service.controller.dto;

import java.util.List;

public record GetTransactionHistoryResponse(
    List<TransactionInfoDto> transactions
) {
}
