data "archive_file" "fraud_lambda_zip" {
  type        = "zip"
  source_dir  = "${path.module}/../fraud-lambda-function"
  output_path = "${path.module}/fraud-lambda.zip"
}

resource "aws_iam_role" "lambda_exec_role" {
  name = "lambda_exec_fraud_detector"
  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action    = "sts:AssumeRole"
        Effect    = "Allow"
        Principal = { Service = "lambda.amazonaws.com" }
      }
    ]
  })
}

resource "aws_iam_role_policy_attachment" "lambda_logs_attach" {
  role       = aws_iam_role.lambda_exec_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole"
}

resource "aws_lambda_function" "fraud_detector" {
  function_name    = var.lambda_name
  filename         = data.archive_file.fraud_lambda_zip.output_path
  source_code_hash = data.archive_file.fraud_lambda_zip.output_base64sha256
  handler          = var.lambda_handler
  runtime          = var.lambda_runtime
  role             = aws_iam_role.lambda_exec_role.arn
  timeout          = var.lambda_timeout
  memory_size      = var.lambda_memory_size

  tags = {
    enviroment = var.env
  }
}
