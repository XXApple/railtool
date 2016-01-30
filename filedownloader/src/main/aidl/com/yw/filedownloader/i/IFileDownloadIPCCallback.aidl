package com.yw.filedownloader.i;

import com.yw.filedownloader.model.FileDownloadTransferModel;

interface IFileDownloadIPCCallback {
    oneway void callback(in FileDownloadTransferModel transfer);
}
