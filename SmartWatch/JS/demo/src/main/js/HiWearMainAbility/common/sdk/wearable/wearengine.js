/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

import fs from '@ohos.file.fs';
import featureAbility from '@ohos.ability.featureAbility';

const context = featureAbility.getContext();
const wearEngineNapi = globalThis.requireNapi('watch.wear_engine_js_so');

const WearEngineConst = {
  RESULT_CODE_SUCCESS: 0,
  WEARENGINE_DEFAULT_API_LEVEL: 1,
  DELAY_FILEAPP_VALUE: 'internal://app/'
};
const ErrorCode = {
  ERROR_CODE_INTERNAL_ERROR: 12,
  MSG_ERROR_PING_OTHER: 203,
  MSG_ERROR_PING_PHONE_APP_NOT_EXIST: 204,
  MSG_ERROR_PING_PHONE_APP_NOT_RUNNING: 205,
  MSG_ERROR_SEND_FAIL: 206,
  MSG_ERROR_SEND_SUCCESS: 207
};
const MessageType = {
  MESSAGE_TYPE_DATA: 0,
  MESSAGE_TYPE_FILE: 1
};
const isFunction = obj => {
  return typeof obj === 'function' && typeof obj.nodeType !== 'number';
};
const P2pClient = (function () {
  let peerPkgName;

  let peerFingerPrint;

  let isTransferring = false;

  function P2pClient() {
  }

  /**
   * It is used to set an app package name on the peer device.
   *
   * @param peerPkgName App package name on the peer device, which is case sensitive.
   */
  P2pClient.prototype.setPeerPkgName = async function(peerPkgName) {
    this.peerPkgName = peerPkgName;
  };

  /**
   * It is used to set the fingerprint information on the peer device.
   *
   * @param fingerPrint Fingerprint information on the peer device.
   */
  P2pClient.prototype.setPeerFingerPrint = async function(fingerPrint) {
    this.peerFingerPrint = fingerPrint;
  };

  /**
   * It is used to check whether the specified app has been installed on the peer device.
   * If the app has not been installed on the peer device, result code 204 is returned.
   * If the app has been installed on the peer device, result code 205 is returned.
   *
   * @param pingCallback Callback of the ping message. onSuccess: Callback function for API call success.
   * onFailure: Callback function for failed API calls. onPingResult: Callback function for API call result.
   */
  P2pClient.prototype.ping = async function(pingCallback) {
    let successCode = {
      data: 'ERROR_CODE_P2P_PHONE_APP_EXIST',
      code: ErrorCode.MSG_ERROR_PING_PHONE_APP_NOT_RUNNING
    };

    let notInstallCode = {
      data: 'ERROR_CODE_P2P_PHONE_APP_NOT_EXIST',
      code: ErrorCode.MSG_ERROR_PING_PHONE_APP_NOT_EXIST
    };

    let failCode = {
      data: 'ERROR_CODE_P2P_OTHER_ERROR',
      code: ErrorCode.MSG_ERROR_PING_OTHER
    };

    let successCallBack = function () {
      isFunction(pingCallback.onSuccess) && pingCallback.onSuccess();
      isFunction(pingCallback.onPingResult) && pingCallback.onPingResult(successCode);
    };

    let failCallBack = function (code) {
      if (code === ErrorCode.MSG_ERROR_PING_PHONE_APP_NOT_EXIST) {
        isFunction(pingCallback.onFailure) && pingCallback.onFailure();
        isFunction(pingCallback.onPingResult) && pingCallback.onPingResult(notInstallCode);
      } else {
        isFunction(pingCallback.onFailure) && pingCallback.onFailure();
        isFunction(pingCallback.onPingResult) && pingCallback.onPingResult(failCode);
      }
    };

    let remoteAppInfo = {
      bundleName: this.peerPkgName,
      fingerprint: this.peerFingerPrint
    };

    let resultCode = wearEngineNapi.ping(remoteAppInfo);
    if (resultCode === ErrorCode.MSG_ERROR_PING_PHONE_APP_NOT_RUNNING) {
      successCallBack();
      console.info('ping success.');
    } else {
      failCallBack(resultCode);
      console.error('ping failed.');
    }
  };

  /**
   * It is used to register and listen for the messages and files sent from the peer device.
   * The received files are stored in the private directory of the app 'internal://app/'.
   *
   * @param receiver Callback of registering for receiving messages.
   * onSuccess: Callback function called when the registration is successful.
   * onFailure: Callback function called when the registration fails.
   * onReceiveMessage: Callback function returned when the registration is successful.
   */
  P2pClient.prototype.registerReceiver = async function(receiver) {
    if (!receiver) {
      return;
    }

    let successCallBack = function () {
      isFunction(receiver.onSuccess) && receiver.onSuccess();
    };

    let failCallBack = function () {
      isFunction(receiver.onFailure) && receiver.onFailure();
    };

    let remoteAppInfo = {
      bundleName: this.peerPkgName,
      fingerprint: this.peerFingerPrint
    };

    let filesDir = await context.getFilesDir();

    let messageCallBack = function (data) {
      if (data) {
        isFunction(receiver.onReceiveMessage) && receiver.onReceiveMessage(data);
      }
    };

    let receiverCallback = async function (data) {
      if (!data) {
        return;
      }
      if (data.messageType === 'text') {
        let messageInfo = data.messageInfo;
        if (messageInfo) {
          messageCallBack(messageInfo.content);
        }
        return;
      }
      if (data.messageType === 'file') {
        let fileObj = await getFileObj(data.fileInfo);
        messageCallBack(fileObj);
        return;
      }
    };

    let resultCode = wearEngineNapi.registerReceiver(remoteAppInfo, filesDir, receiverCallback);
    if (resultCode === WearEngineConst.RESULT_CODE_SUCCESS) {
      successCallBack();
      console.info('registerReceiver success.');
    } else {
      failCallBack();
      console.error('registerReceiver failed.');
    }
  };

  async function getFileObj(fileInfo) {
    if (!fileInfo || !fileInfo.fileName || fileInfo.fileName.includes('..')) {
      console.error('getFileObj param is empty..');
      return undefined;
    }
    try {
      let fileObj = {
        isFileType: true,
        name: WearEngineConst.DELAY_FILEAPP_VALUE + fileInfo.fileName,
        mode: '',
        mode2: ''
        };
      return fileObj;
    } catch (err) {
        console.error(`getFileObj err code:${err.code}, message:${err.message}`);
    }
    return undefined;
  }


  /**
   * It is used to send messages or files to the peer device.
   *
   * @param message Messages sent.
   * @param sendCallback Callback for sending messages. onSuccess: Callback function for API call success.
   * onFailure: Callback function for failed API calls. onSendResult: Callback function called after a message is sent.
   * onSendProgress: Callback function called when the message is being sent.
   */
  P2pClient.prototype.send = async function(message, sendCallback) {
    if (!message || !sendCallback) {
      console.warn('send params is empty.');
      return;
    }
    console.info('send.');
    let successCallBack = function () {
      let successCode = {
        data: 'ERROR_CODE_COMM_SUCCESS',
        code: ErrorCode.MSG_ERROR_SEND_SUCCESS
      };

      isFunction(sendCallback.onSuccess) && sendCallback.onSuccess();
      isFunction(sendCallback.onSendResult) && sendCallback.onSendResult(successCode);
      isFunction(sendCallback.onSendProgress) && sendCallback.onSendProgress('100%');
    };

    let failCallBack = function () {
      let failCode = {
        data: 'ERROR_CODE_COMM_FAILED',
        code: ErrorCode.MSG_ERROR_SEND_FAIL
      };

      isFunction(sendCallback.onFailure) && sendCallback.onFailure();
      isFunction(sendCallback.onSendResult) && sendCallback.onSendResult(failCode);
      isFunction(sendCallback.onSendProgress) && sendCallback.onSendProgress('0%');
    };

    let remoteAppInfo = {
      bundleName: this.peerPkgName,
      fingerprint: this.peerFingerPrint
    };

    if (message.getType() === MessageType.MESSAGE_TYPE_DATA) {
      sendMessage(message, remoteAppInfo, successCallBack, failCallBack);
    } else {
      sendFile(message, remoteAppInfo, sendCallback, successCallBack, failCallBack);
    }
  };

  async function sendMessage(message, remoteAppInfo, successCallBack, failCallBack) {
    console.info('send message.');

    let messageStr = message.getData();
    let messageInfo = { content: messageStr };
    let resultCode = wearEngineNapi.sendMessage(remoteAppInfo, messageInfo);
    if (resultCode === ErrorCode.MSG_ERROR_SEND_SUCCESS) {
      successCallBack();
      console.info('send message success.');
    } else {
      failCallBack();
      console.error('send message failed.');
    }
  }

  async function sendFile(message, remoteAppInfo, sendCallback, successCallBack, failCallBack) {
    console.info('send file.');

    if (isTransferring) {
      console.error('send file failed,there is already a file being sent.');
      failCallBack();
      return;
    }
    isTransferring = true;

    if (!message.getFile() || !message.getFile().fileName) {
      callbackResponse(false, 'send file failed, file is null', successCallBack, failCallBack);
      return;
    }

    let progressCallBack = function (data) {
      isFunction(sendCallback.onSendProgress) && sendCallback.onSendProgress(data + '%');
    };

    let fileInfo = await getFileParams(message.getFile());
    if (!fileInfo) {
      callbackResponse(false, 'send file failed, fileInfo is null', successCallBack, failCallBack);
      return;
    }
    let sendFileCallback = function (data) {
      if (!data) {
        return;
      }
      if (data.code === ErrorCode.MSG_ERROR_SEND_SUCCESS) {
        callbackResponse(true, 'sendFile success.', successCallBack, failCallBack);
        return;
      }
      if (data.code === ErrorCode.MSG_ERROR_SEND_FAIL) {
        callbackResponse(false, 'sendFile failed.', successCallBack, failCallBack);
        return;
      }
      if (data.progress) {
        progressCallBack(data.progress);
        return;
      }
    };
    let resultCode = wearEngineNapi.sendFile(remoteAppInfo, fileInfo, sendFileCallback);
    if (resultCode !== WearEngineConst.RESULT_CODE_SUCCESS) {
      callbackResponse(false, 'sendFile failed, resultCode is ' + JSON.stringify(resultCode), successCallBack,
        failCallBack);
    }
  }

  async function getFilePath(name) {
    if (!name) {
      console.error('getFilePath name is empty');
      return undefined;
    }
    let fileName = name.trim()
      .substring(name.indexOf(WearEngineConst.DELAY_FILEAPP_VALUE) + WearEngineConst.DELAY_FILEAPP_VALUE.length);
    let filePath = await context.getFilesDir() + '/' + fileName;
    return filePath;
  }

  async function getFileParams(file) {
    if (!file) {
      console.warn('getFileParams file is empty');
      return undefined;
    }

    try {
      let filePath = await getFilePath(file.fileContent?.name);
      let isAccess = fs.accessSync(filePath);
      let fileSize = isAccess ? fs.statSync(filePath).size : 0;
      if (isAccess && fileSize > 0) {
        console.info('getFileParams file is exit , fileSize:' + fileSize);
        return { filePath: filePath };
      }
      console.warn('getFileParams file no  exit');
    } catch (err) {
      console.warn('getFileParams error code:' + err.code + ';message:' + err.message);
    }
    return undefined;
  }

  function callbackResponse(isSuccess, info, successCallBack, failCallBack) {
    isTransferring = false;
    if (isSuccess) {
      console.info(info);
      successCallBack();
      return;
    }
    console.error(info);
    failCallBack();
  }

  /**
   * It is used to deregister the function of receiving messages or files from the peer device.
   * You are advised to use this call during or before the onDestroy life cycle of an app to release server resources.
   *
   * @param receiver Callback for stopping receiving messages.
   * onSuccess: Callback function called when a message is successfully received.
   */
  P2pClient.prototype.unregisterReceiver = async function(receiver) {
    let remoteAppInfo = {
      bundleName: this.peerPkgName,
      fingerprint: this.peerFingerPrint
    };
    let resultCode = wearEngineNapi.unregisterReceiver(remoteAppInfo);
    if (resultCode === WearEngineConst.RESULT_CODE_SUCCESS) {
      isFunction(receiver.onSuccess) && receiver.onSuccess();
      console.info('unregisterReceiver success.');
    } else {
      isFunction(receiver.onFailure) && receiver.onFailure();
      console.error('unregisterReceiver failed.');
    }
  };

  return P2pClient;
})();

const Builder = (function () {
  let messageInfo;

  function Builder() {
  }

  Builder.prototype.setDescription = function (description) {
    this.messageInfo = description;
    this.messageType = MessageType.MESSAGE_TYPE_DATA;
  };

  Builder.prototype.setPayload = function (data) {
    if (!data) {
      return;
    }
    if (typeof data === 'object' && data.name) {
      this.messageType = MessageType.MESSAGE_TYPE_FILE;
      this.setFilePlayload(data);
    } else {
      this.messageType = MessageType.MESSAGE_TYPE_DATA;
      this.setBufferPlayload(data);
    }
  };

  Builder.prototype.setBufferPlayload = function (data) {
    this.messageInfo = String.fromCharCode.apply(null, new Uint16Array(data));
  };

  Builder.prototype.setFilePlayload = function (data) {
    let that = this;
    if (isEmpty(data)) {
      return;
    }

    let fileInfoArray = data.name.split('/');
    let fileName = fileInfoArray[fileInfoArray.length - 1];
    if (isEmpty(fileName)) {
      return;
    }

    let fileInfo = {
      fileContent: data,
      fileName: fileName
    };
    that.messageInfo = fileInfo;
  };

  Builder.prototype.setReceivedInfo = function (type, data) {
    this.messageType = type;
    this.messageInfo = data;
  };

  function isEmpty(obj) {
    return typeof obj === 'undefined' || obj === null || obj === '' || obj === ' ';
  }

  return Builder;
})();

const Message = (function () {
  let builder = new Builder();

  function Message() {
  }

  Message.prototype.describeContents = function () {
    return this.builder.messageInfo;
  };

  Message.prototype.getData = function () {
    return this.builder.messageInfo;
  };
  Message.prototype.getDescription = function () {
    return this.builder.messageInfo;
  };

  Message.prototype.getFile = function () {
    if (this.builder.messageType === MessageType.MESSAGE_TYPE_FILE) {
      return this.builder.messageInfo;
    }
    return null;
  };

  Message.prototype.getType = function () {
    return this.builder.messageType;
  };
  return Message;
})();

const PeerDeviceClient = (function () {
  function PeerDeviceClient() {
  }

  // Obtain the peer device object.
  PeerDeviceClient.prototype.getPeerDevice = async function (receiver) {
    let successCallBack = function (data) {
      isFunction(receiver.onSuccess) && receiver.onSuccess(data);
      console.info('getPeerDevice success.');
    };
    let failCallBack = function (data) {
      isFunction(receiver.onFailure) && receiver.onFailure(data);
      console.info('getPeerDevice fail.');
    };

    let resultObj = wearEngineNapi.getPeerDevice();
    if (!resultObj) {
      console.error('getPeerDevice resultObj is null.');
      failCallBack(ErrorCode.ERROR_CODE_INTERNAL_ERROR);
      return;
    }

    if (resultObj.errorCode === 0) {
      successCallBack(resultObj);
      console.info('getPeerDevice success.');
    } else {
      failCallBack(resultObj.errorCode);
      console.error('getPeerDevice failed.');
    }
  };
  return PeerDeviceClient;
})();

/**
 * Get wear engine API level
 *
 * callback.onSuccess({
 *   apiLevel: number
 * })
 * callback.onFailure(code: number, msg: string)
 */
function getWearEngineAplLevel(callback) {
  if (!callback) {
    console.error('getWearEngineAplLevel callback is null');
    return;
  }

  try {
    let resultObj = wearEngineNapi.getApiLevel();
    if (!resultObj) {
      console.error('getWearEngineAplLevel resultObj is null.');
      callback.onFailure(ErrorCode.ERROR_CODE_INTERNAL_ERROR, 'Internal error');
      return;
    }

    if (resultObj.errorCode === 0) {
      console.info('getWearEngineAplLevel success:' + JSON.stringify(resultObj.data));
      callback.onSuccess(resultObj.data);
    } else {
      console.error('getWearEngineAplLevel fail code:' + resultObj.errorCode);
      callback.onFailure(ErrorCode.ERROR_CODE_INTERNAL_ERROR, 'Internal error');
    }
  } catch (error) {
    callback.onSuccess({
      apiLevel: WearEngineConst.WEARENGINE_DEFAULT_API_LEVEL
    });
    console.info('getWearEngineAplLevel error:' + error.message);
  }
}

export { P2pClient, PeerDeviceClient, Message, Builder, getWearEngineAplLevel };
