import router from '@system.router';

export default {
    data: {
        imageBackgroundFrames: [
            {
                src: 'common/image/loading/loading-0.png',
            },
            {
                src: 'common/image/loading/loading-1.png',
            },
            {
                src: 'common/image/loading/loading-2.png',
            },
            {
                src: 'common/image/loading/loading-3.png',
            },
            {
                src: 'common/image/loading/loading-4.png',
            },
            {
                src: 'common/image/loading/loading-5.png',
            },
            {
                src: 'common/image/loading/loading-6.png',
            },
            {
                src: 'common/image/loading/loading-7.png',
            },
            {
                src: 'common/image/loading/loading-8.png',
            },
            {
                src: 'common/image/loading/loading-9.png',
            },
            {
                src: 'common/image/loading/loading-10.png',
            },
            {
                src: 'common/image/loading/loading-11.png',
            },
            {
                src: 'common/image/loading/loading-12.png',
            },
            {
                src: 'common/image/loading/loading-13.png',
            },
            {
                src: 'common/image/loading/loading-14.png',
            },
        ],
        imageSrc1: '',
        imageSrc2: '',
        imageSrc3: '',
        imageSrc4: '',
        imageSrc5: '',
        imageSrc6: '',
        imageSrc7: '',
        imageSrc8: '',
        imageSrc9: '',
        imageSrc10: '',
        imageSrc11: '',
        imageSrc12: '',
        imageSrc13: '',
        imageSrc14: '',
        imageSrc15: '',
        imageSrc16: '',
        imageSrc17: '',
        imageSrc18: '',
        imageSrc19: '',
        imageSrc20: '',
        imageSrc21: '',
        imageSrc22: '',
        imageSrc23: '',
        imageSrc24: '',
        imageSrc25: '',
        imageSrc26: '',
        imageSrc27: '',
        imageSrc28: '',
        imageSrc29: '',
        imageSrc30: '',
        imageSrc31: '',
        imageSrc32: '',
        imageSrc33: '',
        imageSrc34: '',
        imageSrc35: '',
        imageSrc36: '',
        imageSrc37: '',
        imageSrc38: '',
        imageSrc39: '',
        imageSrc40: '',
        imageSrc41: '',
        imageSrc42: '',
        imageSrc43: '',
        imageSrc44: '',
        imageSrc45: '',
        imageSrc46: '',
        imageSrc47: '',
        imageSrc48: '',
        imageSrc49: '',
        imageSrc50: '',
        imageSrc51: '',
        imageSrc52: '',
        imageSrc53: '',
        imageSrc54: '',
        imageSrc55: '',
        imageSrc56: '',
        imageSrc57: '',
        imageSrc58: '',
        imageSrc59: '',
        imageSrc60: '',
        imageSrc61: '',
        imageSrc62: '',
        imageSrc63: '',
        imageSrc64: '',
        imageSrc65: '',
        imageSrc66: '',
        imageSrc67: '',
        imageSrc68: '',
        imageSrc69: '',
        imageSrc70: '',
        imageSrc71: '',
        imageSrc72: '',
        imageSrc73: '',
        imageSrc74: '',
        imageSrc75: '',
        imageSrc76: '',
        imageSrc77: '',
        imageSrc78: '',
        imageSrc79: '',
        imageSrc80: '',
        message: '',
        loading: true,
        drawTimeout: 0,
    },
    onInit() {
        this.message = this.$t('strings.loading');
    },
    async onShow() {
        this.$refs.animator.start();

        this.drawTimeout = setTimeout(()=>{
            this.imageSrc1 = 'common/image/image.jpg';
            this.imageSrc2 = 'common/image/image.jpg';
            this.imageSrc3 = 'common/image/image.jpg';
            this.imageSrc4 = 'common/image/image.jpg';
            this.imageSrc5 = 'common/image/image.jpg';
            this.imageSrc6 = 'common/image/image.jpg';
            this.imageSrc7 = 'common/image/image.jpg';
            this.imageSrc8 = 'common/image/image.jpg';
            this.imageSrc9 = 'common/image/image.jpg';
            this.imageSrc10 = 'common/image/image.jpg';
            this.imageSrc11 = 'common/image/image.jpg';
            this.imageSrc12 = 'common/image/image.jpg';
            this.imageSrc13 = 'common/image/image.jpg';
            this.imageSrc14 = 'common/image/image.jpg';
            this.imageSrc15 = 'common/image/image.jpg';
            this.imageSrc16 = 'common/image/image.jpg';
            this.imageSrc17 = 'common/image/image.jpg';
            this.imageSrc18 = 'common/image/image.jpg';
            this.imageSrc19 = 'common/image/image.jpg';
            this.imageSrc20 = 'common/image/image.jpg';
            this.imageSrc21 = 'common/image/image.jpg';
            this.imageSrc22 = 'common/image/image.jpg';
            this.imageSrc23 = 'common/image/image.jpg';
            this.imageSrc24 = 'common/image/image.jpg';
            this.imageSrc25 = 'common/image/image.jpg';
            this.imageSrc26 = 'common/image/image.jpg';
            this.imageSrc27 = 'common/image/image.jpg';
            this.imageSrc28 = 'common/image/image.jpg';
            this.imageSrc29 = 'common/image/image.jpg';
            this.imageSrc30 = 'common/image/image.jpg';
            this.imageSrc31 = 'common/image/image.jpg';
            this.imageSrc32 = 'common/image/image.jpg';
            this.imageSrc33 = 'common/image/image.jpg';
            this.imageSrc34 = 'common/image/image.jpg';
            this.imageSrc35 = 'common/image/image.jpg';
            this.imageSrc36 = 'common/image/image.jpg';
            this.imageSrc37 = 'common/image/image.jpg';
            this.imageSrc38 = 'common/image/image.jpg';
            this.imageSrc39 = 'common/image/image.jpg';
            this.imageSrc40 = 'common/image/image.jpg';
            this.imageSrc41 = 'common/image/image.jpg';
            this.imageSrc42 = 'common/image/image.jpg';
            this.imageSrc43 = 'common/image/image.jpg';
            this.imageSrc44 = 'common/image/image.jpg';
            this.imageSrc45 = 'common/image/image.jpg';
            this.imageSrc46 = 'common/image/image.jpg';
            this.imageSrc47 = 'common/image/image.jpg';
            this.imageSrc48 = 'common/image/image.jpg';
            this.imageSrc49 = 'common/image/image.jpg';
            this.imageSrc50 = 'common/image/image.jpg';
            this.imageSrc51 = 'common/image/image.jpg';
            this.imageSrc52 = 'common/image/image.jpg';
            this.imageSrc53 = 'common/image/image.jpg';
            this.imageSrc54 = 'common/image/image.jpg';
            this.imageSrc55 = 'common/image/image.jpg';
            this.imageSrc56 = 'common/image/image.jpg';
            this.imageSrc57 = 'common/image/image.jpg';
            this.imageSrc58 = 'common/image/image.jpg';
            this.imageSrc59 = 'common/image/image.jpg';
            this.imageSrc60 = 'common/image/image.jpg';
            this.imageSrc61 = 'common/image/image.jpg';
            this.imageSrc62 = 'common/image/image.jpg';
            this.imageSrc63 = 'common/image/image.jpg';
            this.imageSrc64 = 'common/image/image.jpg';
            this.imageSrc65 = 'common/image/image.jpg';
            this.imageSrc66 = 'common/image/image.jpg';
            this.imageSrc67 = 'common/image/image.jpg';
            this.imageSrc68 = 'common/image/image.jpg';
            this.imageSrc69 = 'common/image/image.jpg';
            this.imageSrc70 = 'common/image/image.jpg';
            this.imageSrc71 = 'common/image/image.jpg';
            this.imageSrc72 = 'common/image/image.jpg';
            this.imageSrc73 = 'common/image/image.jpg';
            this.imageSrc74 = 'common/image/image.jpg';
            this.imageSrc75 = 'common/image/image.jpg';
            this.imageSrc76 = 'common/image/image.jpg';
            this.imageSrc77 = 'common/image/image.jpg';
            this.imageSrc78 = 'common/image/image.jpg';
            this.imageSrc79 = 'common/image/image.jpg';
            this.imageSrc80 = 'common/image/image.jpg';

            this.loading = false;
            this.$refs.animator.stop();
        }, 1);
    },
    async onHide() {
        this.$refs.animator.stop();
        clearTimeout(this.drawTimeout);
    },
    onDestroy() {
    },
    // 画面のスワイプイベント
    onSwipe(event) {
        // 下スワイプ
        if (event.direction === 'up') {
            router.replace({
                uri: 'pages/index/index'
            })
        }
    },
    dragstartfunc(e) {
        this.start = e.globalX
    },
    dragendfunc(e) {
        var temp = e.globalX
        if(temp - this.start > 0) {
        }
    },
};
