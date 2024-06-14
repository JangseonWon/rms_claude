'use client';

import style from './downloadExcelButton.module.css';
import {faDownload} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import React from "react";
import {useOpenAlertDialog, useSetMessageAlertDialog} from "@/store/useAlertDialogStore";

export default function DownloadExcelButton() {
    const setShowAlertDialog = useOpenAlertDialog();
    const setMessage = useSetMessageAlertDialog();

    const handleDownloadClick = () => {
        setShowAlertDialog(true);
        setMessage('다운로드 받을 파일이 없습니다.')
    }

    return (
        <>
            <button
                className={style.download}
                onClick={handleDownloadClick}
            >
                Download Excel&nbsp;
                <FontAwesomeIcon className={style.downloadIcon} icon={faDownload} />
            </button>
        </>
    );
}