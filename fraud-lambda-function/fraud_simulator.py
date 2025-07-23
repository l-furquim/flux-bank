import json
import uuid
import random
from decimal import Decimal
from datetime import datetime, timezone


def generate_fraud_event(transaction):
    """
    Simula um evento de fraude baseado no evento de transação recebido.

    Args:
        transaction (dict): Um dicionário contendo os campos:
            - id (str)
            - type (str)
            - transactionId (str)
            - transactionType (str)
            - status (str)
            - amount (Decimal)
            - currency (str)
            - processingDurationMs (int)
            - timestamp (str, ISO format)
            - sourceService (str)

    Returns:
        dict: JSON referente a o evento de fraude.
    """
    fraud_probability = 0.1
    is_fraud = random.random() < fraud_probability

    return {
        "id": str(uuid.uuid4()),
        "type": "FRAUD_DETECTED" if is_fraud else "FRAUD_CLEARED",
        "transactionId": transaction.get("transactionId"),
        "transactionType": transaction.get("transactionType"),
        "status": "FLAGGED" if is_fraud else transaction.get("status"),
        "amount": str(transaction.get("amount")),
        "currency": transaction.get("currency"),
        "processingDurationMs": transaction.get("processingDurationMs"),
        "timestamp": datetime.now(timezone.utc).isoformat(),
        "sourceService": transaction.get("sourceService"),
    }


def lambda_handler(event, context):
    payload = None
    if event.get('body'):
        try:
            payload = json.loads(event['body'])
        except json.JSONDecodeError:
            return {
                "statusCode": 400,
                "body": json.dumps({"error": "Body inválido: não é JSON"})
            }
    else:
        payload = event

    transaction = payload

    try:
        transaction["amount"] = Decimal(str(transaction.get("amount")))
    except Exception:
        return {
            "statusCode": 400,
            "body": json.dumps({"error": "Campo 'amount' inválido"})
        }

    fraud_event = generate_fraud_event(transaction)

    return {
        "statusCode": 200,
        "body": json.dumps(fraud_event)
    }
