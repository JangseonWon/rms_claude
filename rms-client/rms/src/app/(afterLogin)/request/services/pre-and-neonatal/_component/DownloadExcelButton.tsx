'use client';

import style from './downloadExcelButton.module.css';
import {faDownload} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import React from "react";
import {useOpenAlertDialog, useSetMessageAlertDialog} from "@/store/useAlertDialogStore";
import {
    fetchServiceSampleFileDownload
} from "@/app/(afterLogin)/request/services/pre-and-neonatal/_api/fetchServiceSampleFileDownload";
import {putRequest} from "@/app/(afterLogin)/request/services/pre-and-neonatal/_api/putRequest";

export default function DownloadExcelButton() {
    const setShowAlertDialog = useOpenAlertDialog();
    const setMessage = useSetMessageAlertDialog();

    const handleDownloadClick = async (serviceName: string) => {
        const response = await fetchServiceSampleFileDownload(serviceName);

        if (response.ok) {
            const blob = await response.blob();
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = `${serviceName}_form.xlsx`;
            document.body.appendChild(a);
            a.click();
            a.remove();
            window.URL.revokeObjectURL(url);
        } else {
            setShowAlertDialog(true);
            setMessage('다운로드 에러 발생 했습니다.');
        }
    }

    return (
        <>
            <button className={style.download} onClick={() => handleDownloadClick('pre&neonatal')}>
                Download Excel&nbsp;
                <FontAwesomeIcon className={style.downloadIcon} icon={faDownload} />
            </button>
        </>
    );
}