#!/bin/bash

# Script to setup Redis on the server
# This script installs Redis, configures it with password, and enables it to start on boot

set -e

REDIS_HOST="localhost"
REDIS_PORT="6379"
REDIS_PASSWORD="sehub@redis"

echo "🔧 Setting up Redis..."

# Check if running as root
if [ "$EUID" -ne 0 ]; then 
    echo "❌ Please run as root (use sudo)"
    exit 1
fi

# Update package list
echo "📦 Updating package list..."
apt-get update

# Install Redis if not already installed
if ! command -v redis-server &> /dev/null; then
    echo "📥 Installing Redis..."
    apt-get install -y redis-server
else
    echo "✅ Redis is already installed"
fi

# Backup original redis.conf
if [ -f /etc/redis/redis.conf ]; then
    if [ ! -f /etc/redis/redis.conf.backup ]; then
        echo "💾 Backing up Redis configuration..."
        cp /etc/redis/redis.conf /etc/redis/redis.conf.backup
    fi
fi

# Configure Redis
echo "⚙️  Configuring Redis..."

# Set password in redis.conf
if grep -q "^requirepass" /etc/redis/redis.conf; then
    # Update existing password
    sed -i "s/^requirepass.*/requirepass ${REDIS_PASSWORD}/" /etc/redis/redis.conf
else
    # Add password if not exists
    echo "requirepass ${REDIS_PASSWORD}" >> /etc/redis/redis.conf
fi

# Bind to localhost only (for security)
if grep -q "^bind" /etc/redis/redis.conf; then
    sed -i "s/^bind.*/bind 127.0.0.1/" /etc/redis/redis.conf
else
    echo "bind 127.0.0.1" >> /etc/redis/redis.conf
fi

# Disable protected mode (since we're binding to localhost)
sed -i "s/^protected-mode yes/protected-mode no/" /etc/redis/redis.conf

# Enable Redis to start on boot
echo "🔄 Enabling Redis to start on boot..."
systemctl enable redis-server

# Restart Redis to apply changes
echo "🔄 Restarting Redis..."
systemctl restart redis-server

# Wait a moment for Redis to start
sleep 2

# Verify Redis is running
if systemctl is-active --quiet redis-server; then
    echo "✅ Redis is running"
else
    echo "❌ Error: Redis failed to start"
    systemctl status redis-server
    exit 1
fi

# Test Redis connection with password
echo "🧪 Testing Redis connection..."
if redis-cli -a "${REDIS_PASSWORD}" ping | grep -q "PONG"; then
    echo "✅ Redis connection test successful"
else
    echo "❌ Error: Redis connection test failed"
    exit 1
fi

echo ""
echo "✅ Redis setup completed successfully!"
echo "📋 Configuration:"
echo "   Host: ${REDIS_HOST}"
echo "   Port: ${REDIS_PORT}"
echo "   Password: ${REDIS_PASSWORD}"
echo ""
echo "💡 To connect to Redis: redis-cli -a ${REDIS_PASSWORD}"

