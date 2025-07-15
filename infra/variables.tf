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
    "transaction-completed"
  ]
}


variable "event_topics" {
  type = map(list(string))
  default = {
    fraud        = ["transaction-initiated"]
    transaction  = ["transaction-initiated", "fraud-check-completed"]
    notification = ["transaction-completed"]
    analytics    = ["*"]
  }
}
