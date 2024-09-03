import React from 'react';
import {utils, writeFile} from 'xlsx';
import {Extensions} from "@/model/ServiceExtensionAndSampleType";
import style from './downloadExcelButton.module.css'
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faDownload} from "@fortawesome/free-solid-svg-icons";
import {usePathname} from "next/navigation";

interface DownloadExcelButtonProps {
    extensions: Extensions[];
}

export default function DownloadExcelButton({ extensions }: DownloadExcelButtonProps) {
    const pathname = usePathname();
    const pathSegments = pathname.split('/');
    const serviceId = decodeURIComponent(pathSegments[pathSegments.length - 2]);

    const handleDownload = () => {
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
        const today = new Date();
        const year = today.getFullYear();
        const month = String(today.getMonth() + 1).padStart(2, '0');
        const day = String(today.getDate()).padStart(2, '0');
        const formattedDate = `${year}_${month}_${day}`;

        const worksheet = utils.aoa_to_sheet([headers]);
        const workbook = utils.book_new();
        utils.book_append_sheet(workbook, worksheet, "Sheet1");

        writeFile(workbook, `${serviceId}_${formattedDate}.xlsx`);
    };

    return (
        <button className={style.download} onClick={handleDownload}>
            Download Excel&nbsp;
            <FontAwesomeIcon className={style.downloadIcon} icon={faDownload} />
        </button>
    );
};

