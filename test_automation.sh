#!/bin/bash
# Netflix Clone TV Automation Test

DEVICE="192.168.137.48"
PKG="com.tvlive.app"

echo "=== Netflix Clone TV Automation Test ==="
echo ""

# Test 1: Check if app is installed
echo "[Test 1] Checking if app is installed..."
if adb -s $DEVICE shell pm list packages | grep -q $PKG; then
    echo "✓ App is installed"
else
    echo "✗ App is NOT installed"
    exit 1
fi

# Test 2: Launch app
echo "[Test 2] Launching app..."
adb -s $DEVICE shell am start -n $PKG/.MainActivity
sleep 3
if adb -s $DEVICE shell "dumpsys activity activities | grep $PKG" | grep -q "mResumedActivity"; then
    echo "✓ App launched successfully"
else
    echo "✗ App failed to launch"
fi

# Test 3: Check HomeScreen content
echo "[Test 3] Taking screenshot of HomeScreen..."
adb -s $DEVICE exec-out screencap -p > /vol1/1000/projects/tvlive/test_screenshots/homescreen.png
echo "✓ Screenshot saved to test_screenshots/homescreen.png"

# Test 4: Simulate D-pad navigation (press Down)
echo "[Test 4] Testing D-pad navigation (Down)..."
adb -s $DEVICE shell input keyevent KEYCODE_DPAD_DOWN
sleep 1
echo "✓ D-pad Down sent"

# Test 5: Simulate D-pad navigation (press Right)
echo "[Test 5] Testing D-pad navigation (Right)..."
adb -s $DEVICE shell input keyevent KEYCODE_DPAD_RIGHT
sleep 1
echo "✓ D-pad Right sent"

# Test 6: Take screenshot after navigation
echo "[Test 6] Taking screenshot after navigation..."
adb -s $DEVICE exec-out screencap -p > /vol1/1000/projects/tvlive/test_screenshots/after_nav.png
echo "✓ Screenshot saved"

# Test 7: Press Enter to select
echo "[Test 7] Testing item selection (Enter)..."
adb -s $DEVICE shell input keyevent KEYCODE_DPAD_CENTER
sleep 3

# Test 8: Take screenshot of DetailScreen
echo "[Test 8] Taking screenshot of DetailScreen..."
adb -s $DEVICE exec-out screencap -p > /vol1/1000/projects/tvlive/test_screenshots/detail.png
echo "✓ DetailScreen screenshot saved"

# Test 9: Navigate to Player (press Down then Enter)
echo "[Test 9] Testing Play button..."
adb -s $DEVICE shell input keyevent KEYCODE_DPAD_DOWN
sleep 1
adb -s $DEVICE shell input keyevent KEYCODE_DPAD_CENTER
sleep 3

# Test 10: Take screenshot of Player
echo "[Test 10] Taking screenshot of Player..."
adb -s $DEVICE exec-out screencap -p > /vol1/1000/projects/tvlive/test_screenshots/player.png
echo "✓ Player screenshot saved"

# Test 11: Test back navigation
echo "[Test 11] Testing back navigation..."
adb -s $DEVICE shell input keyevent KEYCODE_BACK
sleep 2
adb -s $DEVICE shell input keyevent KEYCODE_BACK
sleep 2

echo ""
echo "=== All Tests Complete ==="
echo "Screenshots saved in: /vol1/1000/projects/tvlive/test_screenshots/"
