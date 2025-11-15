#!/bin/bash

# Script to update .env file with MongoDB configuration and restart service

set -e

MONGO_HOST="localhost"
MONGO_PORT="27017"
MONGO_DB="chat"
MONGO_URI="mongodb://${MONGO_HOST}:${MONGO_PORT}/${MONGO_DB}"

ENV_FILE="/var/www/SE-HUB-BE/.env"
DEPLOY_ENV_FILE="/opt/sehub/.env"

echo "📝 Updating MongoDB configuration in .env files..."

# Check if .env file exists
if [ ! -f "$ENV_FILE" ]; then
    echo "⚠️  .env file not found at $ENV_FILE"
    echo "📝 Creating new .env file..."
    touch "$ENV_FILE"
    chmod 600 "$ENV_FILE"
fi

# Update or add MongoDB configuration in source .env file
if grep -q "^MONGO_URI=" "$ENV_FILE"; then
    sed -i "s|^MONGO_URI=.*|MONGO_URI=${MONGO_URI}|" "$ENV_FILE"
else
    echo "MONGO_URI=${MONGO_URI}" >> "$ENV_FILE"
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
        
        # Check for MongoDB connection errors
        echo ""
        echo "🔍 Checking for MongoDB connection errors..."
        if sudo journalctl -u sehub -n 20 --no-pager | grep -i "mongodb\|mongo" | grep -i "error\|exception\|refused" > /dev/null; then
            echo "⚠️  Warning: MongoDB connection errors detected in logs"
            echo "   Please check: sudo journalctl -u sehub -n 50 --no-pager | grep -i mongo"
        else
            echo "✅ No MongoDB connection errors found"
        fi
    else
        echo "❌ Error: Service is not running!"
        sudo systemctl status sehub --no-pager
        exit 1
    fi
else
    echo "⚠️  sehub service not found. Please restart manually if needed."
fi

echo ""
echo "✅ MongoDB configuration updated successfully!"
echo "📋 MongoDB settings:"
echo "   MONGO_URI=${MONGO_URI}"

