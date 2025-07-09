package com.fluxbank.wallet_service.domain.strategy.factories;

import com.fluxbank.wallet_service.domain.enums.LimitType;
import com.fluxbank.wallet_service.domain.strategy.WalletLimitResetStrategy;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class WalletLimitResetStrategyFactory {

    private final Map<LimitType, WalletLimitResetStrategy> strategyMap = new EnumMap<>(LimitType.class);

    public WalletLimitResetStrategyFactory(List<WalletLimitResetStrategy> strategies) {
        for (WalletLimitResetStrategy strategy : strategies) {
            strategyMap.put(strategy.getSupportedType(), strategy);
        }
    }

    public WalletLimitResetStrategy getStrategy(LimitType type) {
        return strategyMap.get(type);
    }

}
