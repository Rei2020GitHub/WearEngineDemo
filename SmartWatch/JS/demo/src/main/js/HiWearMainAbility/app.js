import hilog from '@ohos.hilog';

const DOMAIN = 0x0000;

export default {
    onCreate() {
        hilog.info(DOMAIN, 'testTag', '%{public}s', 'Application onCreate');
    },
    onDestroy() {
        hilog.info(DOMAIN, 'testTag', '%{public}s', 'Application onDestroy');
    },
}