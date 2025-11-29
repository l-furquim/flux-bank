resource "aws_iam_role" "eventbridge_scheduler_role" {
  name = "fluxbank-eventbridge-scheduler-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Action = "sts:AssumeRole"
      Effect = "Allow"
      Principal = {
        Service = "scheduler.amazonaws.com"
      }
    }]
  })
}

resource "aws_iam_role_policy" "eventbridge_to_sqs" {
  name = "eventbridge-to-sqs-policy"
  role = aws_iam_role.eventbridge_scheduler_role.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Effect = "Allow"
      Action = [
        "sqs:SendMessage"
      ]
      Resource = aws_sqs_queue.sqs_main["transaction"].arn
    }]
  })
}

resource "aws_scheduler_schedule_group" "pix_schedules" {
  name = "fluxbank-pix-schedules"

  tags = {
    Environment = var.env
  }
}


