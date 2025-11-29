package com.fluxbank.transaction_service.scheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fluxbank.transaction_service.model.ScheduledTransaction;
import com.fluxbank.transaction_service.model.events.TransactionEvent;
import com.fluxbank.transaction_service.model.exceptions.ScheduleException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.scheduler.SchedulerClient;
import software.amazon.awssdk.services.scheduler.model.CreateScheduleRequest;
import software.amazon.awssdk.services.scheduler.model.FlexibleTimeWindow;
import software.amazon.awssdk.services.scheduler.model.FlexibleTimeWindowMode;
import software.amazon.awssdk.services.scheduler.model.Target;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
public class PixScheduleService {

    private final SchedulerClient schedulerClient;
    private final ObjectMapper mapper;


    public PixScheduleService(SchedulerClient schedulerClient, ObjectMapper mapper) {
        this.schedulerClient = schedulerClient;
        this.mapper = mapper;
    }

    @Value("${aws.scheduler.role-arn}")
    private String schedulerRoleArn;

    @Value("${aws.scheduler.schedule-group}")
    private String scheduleGroup;

    @Value("${aws.scheduler.target-queue-arn}")
    private String targetQueueArn;

    public void schedulePixTransaction(
            ScheduledTransaction transaction,
            TransactionEvent event
    ) {
        try {
            String scheduleName = "pix".concat(transaction.getId().toString());
            String scheduleExpression = formatScheduleExpression(transaction.getScheduledTime());

            String node = mapper.writeValueAsString(event);

            CreateScheduleRequest scheduleRequest = CreateScheduleRequest.builder()
                    .name(scheduleName)
                    .groupName(scheduleGroup)
                    .scheduleExpression(scheduleExpression)
                    .scheduleExpressionTimezone("America/Sao_Paulo")
                    .flexibleTimeWindow(FlexibleTimeWindow.builder()
                            .mode(FlexibleTimeWindowMode.OFF)
                            .build())
                    .target(Target.builder()
                            .arn(targetQueueArn)
                            .roleArn(schedulerRoleArn)
                            .input(node)
                            .build())
                    .build();

            schedulerClient.createSchedule(scheduleRequest);

        } catch (Exception e) {
            log.error("Error while scheduling a PIX transaction: {}", e.getMessage());
            throw new ScheduleException("Error while scheduling the transaction: " + e.getMessage());
        }
    }

    private String formatScheduleExpression(LocalDateTime scheduledDate) {
        // Formato: at(2025-01-25T15:30:00)
        return String.format("at(%s)",
                scheduledDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }


}
