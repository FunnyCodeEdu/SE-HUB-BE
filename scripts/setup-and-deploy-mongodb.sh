#!/bin/bash

# Complete script to setup MongoDB and deploy service
# This script combines MongoDB setup and service deployment

set -e

echo "🚀 Starting MongoDB setup and service deployment..."
echo ""

# Get the directory where this script is located
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

# Step 1: Setup MongoDB
echo "═══════════════════════════════════════════════════════════"
echo "Step 1: Setting up MongoDB"
echo "═══════════════════════════════════════════════════════════"
sudo bash "$SCRIPT_DIR/setup-mongodb.sh"

echo ""
echo "═══════════════════════════════════════════════════════════"
echo "Step 2: Updating .env and restarting service"
echo "═══════════════════════════════════════════════════════════"
bash "$SCRIPT_DIR/update-mongodb-env.sh"

echo ""
echo "═══════════════════════════════════════════════════════════"
echo "✅ All done! MongoDB is configured and service is restarted."
echo "═══════════════════════════════════════════════════════════"

