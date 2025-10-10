# Huawei Wear Engine Sample Project (Wearable)
## Development Environment
* DevEco Studio 5.1.0 Release
## Test Environment
### Wearable
| Item | Contents |
| --- | --- |
| Device Name | HUAWEI WATCH 5-25B |
| Model Name | HUAWEI WATCH 5 |
| Model Number | RTS-AL00 |
| HarmonyOS | 5.1.0 |
| Software Version | 5.1.0.205(SP1C900E101R1P100) |
| OpenHarmony Version | OpenHarmony 5.0.1 |
## Wear Engine (Wearable) Implementation Notes
* The Wear Engine SDK uses callbacks in some parts. When displaying values ​​received via callbacks on a JS page, the values ​​may not be reflected unless you add ".bind(this)" after the callback function.
* When the watch receives a file, the file path (internal://app/[filename]) is passed. However, when displaying the file as an image, you must specify an absolute path. For information on how to convert, see convertInternalToAbsolute() in util.js.
## References
* Wearable App Development (JS) (https://developer.huawei.com/consumer/en/doc/connectivity-Guides/fitnesswatch-dev-0000001051423561)
* Lite Smart Wearables (https://developer.huawei.com/consumer/en/doc/best-practices/bpta-lite-wearable-guide)
* Wear Engine Reference (https://developer.huawei.com/consumer/en/doc/connectivity-References/api-description-0000001111724474)