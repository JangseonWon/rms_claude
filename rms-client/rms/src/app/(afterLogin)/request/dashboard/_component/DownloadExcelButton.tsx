'use client';

import style from '@/app/(afterLogin)/request/dashboard/_component/downloadExcelButton.module.css';
import {faDownload} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import React from "react";
import * as XLSX from 'xlsx';
import {format} from "date-fns";
import {Query} from "@/model/Query";
import {postRequests} from "@/app/(afterLogin)/request/dashboard/_api/postRequests";
import type {Request} from "@/model/Request";
import {Organization} from "@/model/Organization";

interface DownloadExcelButtonProps {
    search: Query;
    status: string;
}

interface ExcelRow {
    "Registration ID": string;
    "User Name": string;
    "Institution": string;
    "Service": string;
    "Patient(s) Name": string;
    "MRN": string;
    "Patient BOD": string;
    "Current Status": string;
    "Order Date": string;
}

export default function DownloadExcelButton({ search, status }: DownloadExcelButtonProps) {
    const fetchData = async (search: Query) => {
        const response = await postRequests(search)
        if (response.ok) {
            const responseData = await response.json();
            return responseData as Request[];
        } else {
            return [];
        }
    };

    const downloadExcel = async () => {
        const requestData = await fetchData(search);

        if (requestData.length === 0) {
            alert("No data available for download");
            return;
        }

        const headers = [
            "Registration ID",
            "User Name",
            "Institution",
            "Service",
            "Patient(s) Name",
            "MRN",
            "Patient BOD",
            "Current Status",
            "Order Date"
        ];

        const data = requestData.map(row => ({
            "Registration ID": row.sample!.barcode,
            "User Name": row.sample!.patient!.organization!.user!.id,
            "Institution": row.sample!.patient!.organization!.id,
            "Service": row.service!.name,
            "Patient(s) Name": row.sample!.patient!.name,
            "MRN": row.sample!.patient!.serial,
            "Patient BOD": row.sample?.patient ?
                formatDate(row.sample.patient.birth_year, row.sample.patient.birth_month, row.sample.patient.birth_day) : '-',
            "Current Status": row.status,
            "Order Date": row.create_at ? format(new Date(row.create_at), "yyyy-MMM-dd") : '-'
        }));

        const worksheet = XLSX.utils.json_to_sheet(data, { header: headers });
        const workbook = XLSX.utils.book_new();
        XLSX.utils.book_append_sheet(workbook, worksheet, "Requests");

        worksheet['!cols'] = headers.map(header => {
            const maxLength = Math.max(
                header.length,
                ...data.map(row => row[header as keyof ExcelRow]?.toString().length || 0)
            );
            return {wch: maxLength + 2};
        });

        const fileName = `dashboard_${status}_${format(new Date(), 'yyyy-MM-dd')}.xlsx`;
        XLSX.writeFile(workbook, fileName);
    }

    const formatDate = (year: number | undefined, month: number | undefined, day: number | undefined) => {
        const parts = [];

        if (year !== undefined) {
            parts.push(year.toString());
        }
        if (month !== undefined) {
            parts.push(format(new Date(year ?? 0, month - 1, 1), "MMM"));
        }
        if (day !== undefined) {
            parts.push(day.toString());
        }

        return parts.length > 0 ? parts.join('-') : '-';
    };

    return (
        <button
            className={style.download}
            onClick={downloadExcel}
        >
            Download Excel&nbsp;
            <FontAwesomeIcon className={style.downloadIcon} icon={faDownload} />
        </button>
    );
}
