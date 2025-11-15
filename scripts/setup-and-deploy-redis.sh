#!/bin/bash

# Complete script to setup Redis and deploy service
# This script combines Redis setup and service deployment

set -e

echo "🚀 Starting Redis setup and service deployment..."
echo ""

# Get the directory where this script is located
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

# Step 1: Setup Redis
echo "═══════════════════════════════════════════════════════════"
echo "Step 1: Setting up Redis"
echo "═══════════════════════════════════════════════════════════"
sudo bash "$SCRIPT_DIR/setup-redis.sh"

echo ""
echo "═══════════════════════════════════════════════════════════"
echo "Step 2: Updating .env and restarting service"
echo "═══════════════════════════════════════════════════════════"
bash "$SCRIPT_DIR/update-redis-env.sh"

echo ""
echo "═══════════════════════════════════════════════════════════"
echo "✅ All done! Redis is configured and service is restarted."
echo "═══════════════════════════════════════════════════════════"

