output "sns_main_arn" {
  description = "ARNs de todos os topicos"
  value       = { for name, topic in aws_sns_topic.sns_topics : name => topic.arn }
}

output "sqs_main_arn" {
  description = "ARNs de todas as queues"
  value       = { for name, queue in aws_sqs_queue.sqs_main : name => queue.arn }
}

output "lambda_function_arn" {
  description = "ARN da função Lambda"
  value       = aws_lambda_function.fraud_detector.arn
}

output "transaction_queue_arn" {
  description = "ARN da fila de transações (usada também para transações agendadas)"
  value       = aws_sqs_queue.sqs_main["transaction"].arn
}

output "scheduler_role_arn" {
  description = "ARN da role do EventBridge Scheduler"
  value       = aws_iam_role.eventbridge_scheduler_role.arn
}