resource "aws_sqs_queue" "sqs_main" {
  for_each = toset(var.queues)

  name          = each.key
  delay_seconds = 90

  redrive_policy = jsonencode({
    deadLetterTargetArn = aws_sqs_queue.ddl[each.key].arn
    maxReceiveCount     = 4
  })

  tags = {
    Enviroment = var.env
  }

}

resource "aws_sqs_queue_redrive_allow_policy" "redrive_ddl_policy" {
  for_each = toset(var.queues)

  queue_url = aws_sqs_queue.ddl[each.key].id

  redrive_allow_policy = jsonencode({
    redrivePermission = "byQueue",
    sourceQueueArns   = [aws_sqs_queue.sqs_main[each.key].arn]
  })
}

resource "aws_sqs_queue" "ddl" {
  for_each = toset(var.queues)

  name = "${each.key}-ddl"
}
