'use client';

import style from './downloadExcelByS3Button.module.css';
import {faDownload} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import React from "react";
import {useOpenAlertDialogB, useSetMessageAlertDialogB} from "@/store/useBeforeLoginAlertDialogStore";
import {fetchServiceSampleFileDownload} from "@/app/(afterLogin)/request/services/[service]/multi/_api/fetchServiceSampleFileDownload";

export default function DownloadExcelByS3Button() {
    const setShowAlertDialog = useOpenAlertDialogB();
    const setMessage = useSetMessageAlertDialogB();

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
            setMessage('A download error occurred.');
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