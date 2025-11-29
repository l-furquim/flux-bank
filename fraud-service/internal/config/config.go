package config

import (
	"context"
	"log"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/config"
)

func loadCredentials() aws.Config {
	cfg, err := config.LoadDefaultConfig(context.TODO(),
		config.WithRegion("us-east-1"),
	)

	if err != nil {
		log.Fatalf("Could not load the AWS credentials: %v", err)
	}

	return cfg
}
