package com.fluxbank.wallet_service.application.dto;

import java.util.List;

public record GetWalletLimitsResponse(
        List<LimitInformationDto> limits
) {
}
