import router from '@system.router'
import { fullCharTotal, halfCharTotal, setWindowKeepScreenOn } from '../../common/util';

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
        this.messageItemHeight = ((fullCharTotal(this.$t('strings.info_message')) / 9) + (halfCharTotal(this.$t('strings.info_message')) / 20)) * (32 + 8);

        this.buttonMessage = this.$t('strings.back');
    },
    async onShow() {
        await setWindowKeepScreenOn(true);
    },
    async onHide() {
        await setWindowKeepScreenOn(false);
    },
    dragstartfunc(e) {
        this.start = e.globalX
    },
    dragendfunc(e) {
        var temp = e.globalX
        if(temp - this.start > 0) {
        }
    },
    onClickBack() {
        router.replace({
            uri: 'pages/index/index'
        })
    },
};
