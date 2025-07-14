resource "aws_sns_topic" "sns_topics" {
  for_each = var.topics

  name = each.key
}
