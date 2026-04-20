#!/bin/bash
# TV Monkey Test Script - Random D-pad events for Android TV

TARGET_IP="10.113.45.16"
PACKAGE="com.example.netflixtv"
DURATION=300
DELAY=0.5

echo "Starting TV Monkey Test"
echo "Target: $TARGET_IP"
echo "Package: $PACKAGE"
echo "Duration: ${DURATION}s"
echo ""

KEYS=(19 20 21 22 23 4)
KEY_NAMES=("UP" "DOWN" "LEFT" "RIGHT" "CENTER" "BACK")

start_time=$(date +%s)
count=0

while true; do
    current_time=$(date +%s)
    elapsed=$((current_time - start_time))

    if [ $elapsed -ge $DURATION ]; then
        echo ""
        echo "Test completed! Events sent: $count"
        break
    fi

    rand_idx=$((RANDOM % ${#KEYS[@]}))
    keycode=${KEYS[$rand_idx]}
    keyname=${KEY_NAMES[$rand_idx]}

    adb -s $TARGET_IP shell input keyevent $keycode 2>/dev/null

    echo -ne "Elapsed: ${elapsed}s | Event $count: KEYCODE_$keyname (keycode=$keycode)    \r"

    sleep $DELAY
    count=$((count + 1))
done

echo ""
echo "Monkey test finished"
