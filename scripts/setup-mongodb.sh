#!/bin/bash

# Script to setup MongoDB on the server for chat module
# This script installs MongoDB, configures it, and enables it to start on boot

set -e

MONGO_HOST="localhost"
MONGO_PORT="27017"
MONGO_DB="chat"
MONGO_URI="mongodb://${MONGO_HOST}:${MONGO_PORT}/${MONGO_DB}"

echo "🔧 Setting up MongoDB for chat module..."

# Check if running as root
if [ "$EUID" -ne 0 ]; then 
    echo "❌ Please run as root (use sudo)"
    exit 1
fi

# Update package list
echo "📦 Updating package list..."
apt-get update

# Install MongoDB if not already installed
if ! command -v mongod &> /dev/null; then
    echo "📥 Installing MongoDB..."
    
    # Install MongoDB Community Edition
    # Add MongoDB GPG key
    curl -fsSL https://www.mongodb.org/static/pgp/server-7.0.asc | gpg -o /usr/share/keyrings/mongodb-server-7.0.gpg --dearmor
    
    # Add MongoDB repository
    echo "deb [ arch=amd64,arm64 signed-by=/usr/share/keyrings/mongodb-server-7.0.gpg ] https://repo.mongodb.org/apt/ubuntu jammy/mongodb-org/7.0 multiverse" | tee /etc/apt/sources.list.d/mongodb-org-7.0.list
    
    # Update package list again
    apt-get update
    
    # Install MongoDB
    apt-get install -y mongodb-org
    
    echo "✅ MongoDB installed"
else
    echo "✅ MongoDB is already installed"
fi

# Configure MongoDB
echo "⚙️  Configuring MongoDB..."

# Create MongoDB data directory if it doesn't exist
if [ ! -d /var/lib/mongodb ]; then
    mkdir -p /var/lib/mongodb
    chown mongodb:mongodb /var/lib/mongodb
fi

# Create MongoDB log directory if it doesn't exist
if [ ! -d /var/log/mongodb ]; then
    mkdir -p /var/log/mongodb
    chown mongodb:mongodb /var/log/mongodb
fi

# Backup original mongod.conf if exists
if [ -f /etc/mongod.conf ]; then
    if [ ! -f /etc/mongod.conf.backup ]; then
        echo "💾 Backing up MongoDB configuration..."
        cp /etc/mongod.conf /etc/mongod.conf.backup
    fi
fi

# Configure MongoDB to bind to localhost only (for security)
if [ -f /etc/mongod.conf ]; then
    # Update bindIp to localhost
    if grep -q "^net:" /etc/mongod.conf; then
        if grep -q "bindIp:" /etc/mongod.conf; then
            sed -i "s/^  bindIp:.*/  bindIp: 127.0.0.1/" /etc/mongod.conf
        else
            # Add bindIp under net section
            sed -i "/^net:/a \  bindIp: 127.0.0.1" /etc/mongod.conf
        fi
    else
        # Add net section with bindIp
        echo "" >> /etc/mongod.conf
        echo "net:" >> /etc/mongod.conf
        echo "  bindIp: 127.0.0.1" >> /etc/mongod.conf
    fi
fi

# Enable MongoDB to start on boot
echo "🔄 Enabling MongoDB to start on boot..."
systemctl enable mongod

# Start MongoDB
echo "🔄 Starting MongoDB..."
systemctl start mongod

# Wait a moment for MongoDB to start
sleep 3

# Verify MongoDB is running
if systemctl is-active --quiet mongod; then
    echo "✅ MongoDB is running"
else
    echo "❌ Error: MongoDB failed to start"
    systemctl status mongod
    exit 1
fi

# Test MongoDB connection
echo "🧪 Testing MongoDB connection..."
if mongosh --quiet --eval "db.adminCommand('ping')" > /dev/null 2>&1; then
    echo "✅ MongoDB connection test successful"
else
    # Try with legacy mongo client
    if command -v mongo &> /dev/null; then
        if mongo --quiet --eval "db.adminCommand('ping')" > /dev/null 2>&1; then
            echo "✅ MongoDB connection test successful (legacy client)"
        else
            echo "⚠️  Warning: MongoDB connection test failed, but service is running"
        fi
    else
        echo "⚠️  Warning: Could not test MongoDB connection (mongosh/mongo not found)"
        echo "   MongoDB service is running, connection should work"
    fi
fi

# Create database if it doesn't exist (optional, MongoDB creates databases automatically)
echo "📂 Ensuring database '${MONGO_DB}' exists..."
mongosh --quiet --eval "use ${MONGO_DB}; db.getName()" > /dev/null 2>&1 || true

echo ""
echo "✅ MongoDB setup completed successfully!"
echo "📋 Configuration:"
echo "   Host: ${MONGO_HOST}"
echo "   Port: ${MONGO_PORT}"
echo "   Database: ${MONGO_DB}"
echo "   URI: ${MONGO_URI}"
echo ""
echo "💡 To connect to MongoDB: mongosh"
echo "💡 To check MongoDB status: sudo systemctl status mongod"

