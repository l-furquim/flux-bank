variable "env" {
  type = string
}

variable "region" {
  type = string
}

variable "queues" {
  type = list(string)
  default = [
    "transaction",
    "user",
    "analytics",
    "wallet",
    "notification",
    "fraud"
  ]
}

variable "topics" {
  type = list(string)
  default = [
    "transaction-initiated",
    "fraud-check-completed",
    "transaction-completed",
    "transaction-failed",
    "pix-key-created",
    "limit-exceeded",
  ]
}


variable "event_topics" {
  type = map(list(string))
  default = {
    fraud        = ["transaction-initiated"]
    transaction  = ["fraud-check-completed"]
    notification = ["transaction-completed", "transaction-failed", "pix-key-created", "limit-exceeded"]
    analytics    = ["*"]
  }
}

variable "lambda_name" {
  type = string
}

variable "lambda_handler" {
  type    = string
  default = "fraud_simulator.lambda_handler"
}

variable "lambda_runtime" {
  type    = string
  default = "python3.9"
}

variable "lambda_timeout" {
  type    = number
  default = 10
}

variable "lambda_memory_size" {
  type    = number
  default = 128
}
