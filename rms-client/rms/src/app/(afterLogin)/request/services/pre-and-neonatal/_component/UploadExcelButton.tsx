import style from "@/app/(afterLogin)/request/services/pre-and-neonatal/_component/uploadExcelButton.module.css";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import React, {ChangeEvent} from "react";
import {faUpload} from "@fortawesome/free-solid-svg-icons/faUpload";
import * as XLSX from "xlsx";

type UploadExcelButtonProps = {
    onFileUpload: (data: any[][]) => void;
};

export default function UploadExcelButton({ onFileUpload }: UploadExcelButtonProps) {
    const handleFileChange = (e: ChangeEvent<HTMLInputElement>) => {
        const file = e.target.files?.[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = (event) => {
                const data = new Uint8Array(event.target?.result as ArrayBuffer);
                const workbook = XLSX.read(data, { type: 'array' });
                const sheetName = workbook.SheetNames[0];
                const worksheet = workbook.Sheets[sheetName];
                const jsonData: (string | number)[][] = XLSX.utils.sheet_to_json(worksheet, { header: 1 });
                onFileUpload(jsonData);
            };
            reader.readAsArrayBuffer(file);
        }
        e.target.value = '';
    };

    const handleUploadClick = () => {
        document.getElementById('excelFileInput')?.click();
    };

    return (
        <>
            <input
                type="file"
                id="excelFileInput"
                accept=".xlsx, .xls"
                style={{ display: 'none' }}
                onChange={handleFileChange}
            />
            <button
                className={style.upload}
                onClick={handleUploadClick}>
                Upload Excel&nbsp;
                <FontAwesomeIcon className={style.downloadIcon} icon={faUpload} />
            </button>
        </>
    );
}