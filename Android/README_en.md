# Huawei Wear Engine Sample Project (Android)
## Development Environment
* Android Studio Giraffe | 2022.3.1 Patch 2
## Test Environment
### Android
| Item | Contents |
| --- | --- |
| Device Name | HUAWEI P30 lite |
| Model | MAR-LX2J |
## Wear Engine (Android) Implementation Notes
* When implementing Wear Engine, you must pass the correct package name and fingerprint to P2pClient. The fingerprint in particular is a bit confusing. The procedure for obtaining the fingerprint is written in Constant.kt, so please refer to that.
## References
* Wear Engine - Android Phone App Development (https://developer.huawei.com/consumer/en/doc/connectivity-Guides/phone-dev-0000001086797354)