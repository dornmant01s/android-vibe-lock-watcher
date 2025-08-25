# VibeLockWatcher

VibeLockWatcher provides continuous vibration feedback when the device is unlocked and stops when the screen is turned off or the lock screen appears. This tactile cue helps users who rely on vibration to know the device's lock state without needing to see the screen.

## Accessibility service usage
The service runs in the foreground and listens for screen on/off and user-present events solely to trigger vibration for accessibility purposes. It does not collect or share any personal data.

### Play Console declaration
When completing *App content > Accessibility* in Play Console, you can use the following description:

> VibeLockWatcher vibrates when the device is unlocked to give a tactile notification for users needing extra accessibility support. The app does not collect or transmit any user data and uses no accessibility features beyond this vibration feedback.

## Permissions
- `VIBRATE` – needed to produce vibration feedback.
- `FOREGROUND_SERVICE` – keeps the vibration service running reliably.
- `POST_NOTIFICATIONS` – required on Android 13+ to display the persistent notification.

No other special permissions are requested.
