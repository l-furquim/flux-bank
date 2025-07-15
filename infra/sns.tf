resource "aws_sns_topic" "events" {
  name = "all-events-topic"
}

resource "aws_sns_topic" "sns_topics" {
  for_each = toset(var.topics)

  name = each.key
}

resource "aws_sns_topic_subscription" "per_service_event" {
  for_each = local.subscriptions

  topic_arn = aws_sns_topic.sns_topics[each.value.evt].arn
  protocol  = "sqs"
  endpoint  = aws_sqs_queue.sqs_main[each.value.svc].arn
}
