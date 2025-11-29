package models

import (
	"math/big"
	"time"
)

type TransactionEvent struct {
	id                   string
	eventType            string
	transactionId        string
	transactionType      string
	status               string
	amount               big.Float
	currency             string
	processingDurationMs int64
	timestamp            time.Time
	sourceService        string
}
