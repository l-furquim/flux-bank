package com.fluxbank.wallet_service.infrastructure.scheduler;

import com.fluxbank.wallet_service.infrastructure.persistence.adapter.WalletLimitAdapter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class WalletLimitResetScheduler {

    private final WalletLimitAdapter adapter;

    public WalletLimitResetScheduler(WalletLimitAdapter adapter) {
        this.adapter = adapter;
    }

    @Scheduled(cron = "0 20 12 * * *")
    public void resetAllWalletsDailyLimits(){
        this.adapter.resetDailyLimits();
    }

    @Scheduled(cron = "0 0 12 1 * *")
    public void resetAllWalletsMonthlyLimits(){
        this.adapter.resetMonthlyLimits();
    }


}
