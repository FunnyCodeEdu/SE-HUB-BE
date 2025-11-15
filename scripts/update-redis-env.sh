#!/bin/bash

# Script to update .env file with Redis configuration and restart service

set -e

REDIS_HOST="localhost"
REDIS_PORT="6379"
REDIS_PASSWORD="sehub@redis"

ENV_FILE="/var/www/SE-HUB-BE/.env"
DEPLOY_ENV_FILE="/opt/sehub/.env"

echo "📝 Updating Redis configuration in .env files..."

# Check if .env file exists
if [ ! -f "$ENV_FILE" ]; then
    echo "⚠️  .env file not found at $ENV_FILE"
    echo "📝 Creating new .env file..."
    touch "$ENV_FILE"
    chmod 600 "$ENV_FILE"
fi

# Update or add Redis configuration in source .env file
if grep -q "^REDIS_HOST=" "$ENV_FILE"; then
    sed -i "s|^REDIS_HOST=.*|REDIS_HOST=${REDIS_HOST}|" "$ENV_FILE"
else
    echo "REDIS_HOST=${REDIS_HOST}" >> "$ENV_FILE"
fi

if grep -q "^REDIS_PORT=" "$ENV_FILE"; then
    sed -i "s|^REDIS_PORT=.*|REDIS_PORT=${REDIS_PORT}|" "$ENV_FILE"
else
    echo "REDIS_PORT=${REDIS_PORT}" >> "$ENV_FILE"
fi

if grep -q "^REDIS_PASSWORD=" "$ENV_FILE"; then
    sed -i "s|^REDIS_PASSWORD=.*|REDIS_PASSWORD=${REDIS_PASSWORD}|" "$ENV_FILE"
else
    echo "REDIS_PASSWORD=${REDIS_PASSWORD}" >> "$ENV_FILE"
fi

echo "✅ Updated $ENV_FILE"

# Copy to deployment location if it exists
if [ -f "$DEPLOY_ENV_FILE" ]; then
    echo "📋 Copying .env to deployment location..."
    sudo cp "$ENV_FILE" "$DEPLOY_ENV_FILE"
    sudo chown root:root "$DEPLOY_ENV_FILE"
    sudo chmod 600 "$DEPLOY_ENV_FILE"
    echo "✅ Updated $DEPLOY_ENV_FILE"
fi

# Restart service if it exists
if systemctl list-units --type=service | grep -q "sehub.service"; then
    echo "🔄 Restarting sehub service..."
    sudo systemctl restart sehub
    
    sleep 5
    
    echo "📊 Service status:"
    sudo systemctl status sehub --no-pager | head -10
    
    if sudo systemctl is-active --quiet sehub; then
        echo "✅ Service restarted successfully!"
    else
        echo "❌ Error: Service is not running!"
        sudo systemctl status sehub --no-pager
        exit 1
    fi
else
    echo "⚠️  sehub service not found. Please restart manually if needed."
fi

echo ""
echo "✅ Redis configuration updated successfully!"
echo "📋 Redis settings:"
echo "   REDIS_HOST=${REDIS_HOST}"
echo "   REDIS_PORT=${REDIS_PORT}"
echo "   REDIS_PASSWORD=${REDIS_PASSWORD}"

