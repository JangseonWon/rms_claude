import React from 'react';
import {utils, writeFile} from 'xlsx';
import {Extensions} from "@/model/ServiceExtensionAndSampleType";
import style from './downloadExcelButton.module.css'
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faDownload} from "@fortawesome/free-solid-svg-icons";

interface DownloadExcelButtonProps {
    extensions: Extensions[];
}

export default function DownloadExcelButton({ extensions }: DownloadExcelButtonProps) {
    const handleDownload = () => {
        // 헤더 생성
        const headers = [
            "Registration Date (YYYY/MM/DD)",
            "Ward",
            "Patient Name",
            "Personal ID Number (YYYY/MM/DD)",
            "Gender (Male, Female)",
            "Physician",
            "Collection Date (YYYY/MM/DD)",
            "Chart Number",
            "Code",
            "Gestational Age",
            "Weight",
            "Fetuses (1 or 2)",
            "Quantity",
            "Notes",
            "Race (Genome Health Premium)",
            ...extensions.map(extension => extension.name),
        ];

        const worksheet = utils.aoa_to_sheet([headers]);
        const workbook = utils.book_new();
        utils.book_append_sheet(workbook, worksheet, "Sheet1");

        writeFile(workbook, "order_data.xlsx");
    };

    return (
        <button className={style.download} onClick={handleDownload}>
            Download Excel&nbsp;
            <FontAwesomeIcon className={style.downloadIcon} icon={faDownload} />
        </button>
    );
};

