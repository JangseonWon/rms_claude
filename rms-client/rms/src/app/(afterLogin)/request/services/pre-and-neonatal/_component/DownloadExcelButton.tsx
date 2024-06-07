'use client';

import style from './downloadExcelButton.module.css';
import {faDownload} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import React, {useState} from "react";
import AlertDialog from "@/app/_component/AlertDialog";

export default function DownloadExcelButton() {
    const [showDialog, setShowDialog] = useState(false);

    const handleDownloadClick = () => {
        setShowDialog(true);
    }

    const handleCloseDialog = () => {
        setShowDialog(false);
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
            {showDialog && (
                <AlertDialog
                    icon= 'warning'
                    message="다운로드 받을 파일이 없습니다."
                    onClose={handleCloseDialog}
                />
            )}
        </>
    );
}