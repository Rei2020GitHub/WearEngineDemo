import router from '@system.router'
import brightness from '@system.brightness';
import { fullCharTotal, halfCharTotal } from '../../common/util';

export default {
    data: {
        title: "",
        messageItemHeight: "600",
        messageList: [],
        buttonMessage: ''
    },
    onInit() {
        this.title = this.$t('strings.info_title');
        this.messageList.push({
            message: this.$t('strings.info_message')
        });

        // テキスト要素の高さを動的に計算する
        this.messageItemHeight = ((fullCharTotal(this.$t('strings.info_message')) / 9) + (halfCharTotal(this.$t('strings.info_message')) / 20)) * (32 + 8 + 8);

        this.buttonMessage = this.$t('strings.back');
    },
    onShow() {
        brightness.setKeepScreenOn(this.setKeepScreenOnOptions(true));
    },
    onHide() {
        brightness.setKeepScreenOn(this.setKeepScreenOnOptions(false));
    },
    onClickBack() {
        router.replace({
            uri: 'pages/index/index'
        })
    },
};
