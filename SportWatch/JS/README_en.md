# Huawei Wear Engine Sample Project (Lite Wearable)
## Development Environment
* DevEco Studio 5.1.0 Release
## Test Environment
### Lite Wearable
| Item | Contents |
| --- | --- |
| Device Name | HUAWEI WATCH ATM-6D2 |
| Model Name | HUAWEI WATCH ATM |
| Model Number | ATM-B29 |
## Notes on Wear Engine (Lite Wearable) Implementation
* The Wear Engine SDK uses callbacks in some parts. When displaying values ​​received via callbacks on a JS page, the values ​​may not be reflected unless you add ".bind(this)" after the callback function.
* Images are usually in formats such as jpg or png, but Lite Wearable cannot display received jpg or png images. Images must be converted to bin format before being sent to Lite Wearable. For information on the bin format, please refer to the BinDemo project in this sample.
## References
* Wearable App Development (JS) (https://developer.huawei.com/consumer/en/doc/connectivity-Guides/fitnesswatch-dev-0000001051423561)
* Lite Smart Wearables (https://developer.huawei.com/consumer/en/doc/best-practices/bpta-lite-wearable-guide)
* Wear Engine Reference (https://developer.huawei.com/consumer/en/doc/connectivity-References/api-description-0000001111724474)