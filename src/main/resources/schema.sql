-- Schema for rule_config table
CREATE TABLE IF NOT EXISTS rule_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    country VARCHAR(10) NOT NULL,
    currency VARCHAR(10) NOT NULL,
    rules_json CLOB NOT NULL,
    enabled BOOLEAN DEFAULT TRUE,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (country, currency)
);

-- Sample rule data for US/USD
INSERT INTO rule_config (country, currency, rules_json, enabled) VALUES
('US', 'USD', '{
  "rules": [
    {
      "code": "US_USD_001",
      "name": "US Dollar transaction must be >= 10",
      "status": "Enabled",
      "conditions": [
        {
          "field": "transaction.amount",
          "type": "number",
          "op": "GreaterOrEqual",
          "source": "static",
          "values": ["10"]
        }
      ]
    },
    {
      "code": "US_USD_002",
      "name": "US Dollar currency validation",
      "status": "Enabled",
      "conditions": [
        {
          "field": "transaction.currency",
          "type": "string",
          "op": "Equals",
          "source": "static",
          "values": ["USD"]
        }
      ]
    }
  ]
}', true);

-- Sample rule data for UK/GBP
INSERT INTO rule_config (country, currency, rules_json, enabled) VALUES
('UK', 'GBP', '{
  "rules": [
    {
      "code": "UK_GBP_001",
      "name": "UK Pound transaction must be >= 5",
      "status": "Enabled",
      "conditions": [
        {
          "field": "transaction.amount",
          "type": "number",
          "op": "GreaterOrEqual",
          "source": "static",
          "values": ["5"]
        }
      ]
    }
  ]
}', true);

-- Sample rule data for EU/EUR
INSERT INTO rule_config (country, currency, rules_json, enabled) VALUES
('EU', 'EUR', '{
  "rules": [
    {
      "code": "EU_EUR_001",
      "name": "Euro transaction amount validation",
      "status": "Enabled",
      "conditions": [
        {
          "field": "transaction.amount",
          "type": "number",
          "op": "Between",
          "source": "static",
          "values": ["1", "10000"]
        }
      ]
    },
    {
      "code": "EU_EUR_002",
      "name": "Euro message type check",
      "status": "Enabled",
      "conditions": [
        {
          "field": "transaction.messageType",
          "type": "string",
          "op": "In",
          "source": "static",
          "values": ["SEPA", "SWIFT"]
        }
      ]
    }
  ]
}', true);
