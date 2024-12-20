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

interface DownloadExcelButtonProps {
    search: Query;
    status: string;
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

    const formatStatus = (status: string) => {
        return status
            .toLowerCase()
            .split('_')
            .map((word, index) => (index === 0 ? word.charAt(0).toUpperCase() + word.slice(1) : word))
            .join(' ');
    };

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

    const downloadExcel = async () => {
        const requestData = await fetchData(search);

        if (requestData.length === 0) {
            alert("No data available for download");
            return;
        }

        const headers = [
            "Order Date\n(DD-MM-YYYY)",
            "Registration ID",
            "User Name",
            "Institution",
            "Service",
            "Patient(s) Name",
            "MRN",
            "Patient BOD",
            "Current Status",
            "Report Date\n(DD-MM-YYYY)",
        ];

        const data = requestData.map(row => ({
            "Order Date\n(DD-MM-YYYY)": row.create_at ? format(new Date(row.create_at), "dd-MM-yyyy") : '-',
            "Registration ID": row.sample!.barcode,
            "User Name": row.sample!.patient!.organization!.user!.id,
            "Institution": row.sample!.patient!.organization!.id,
            "Service": row.service!.name,
            "Patient(s) Name": row.sample!.patient!.name,
            "MRN": row.sample!.patient!.serial,
            "Patient BOD": row.sample?.patient ?
                formatDate(row.sample.patient.birth_year, row.sample.patient.birth_month, row.sample.patient.birth_day) : '-',
            "Current Status": formatStatus(row.status!),
            "Report Date\n(DD-MM-YYYY)": row.report?.create_at && !isNaN(new Date(row.report.create_at).getTime()) ?
                format(new Date(row.report.create_at!), "dd-MM-yyyy") : '-'
        }));

        const worksheet = XLSX.utils.json_to_sheet(data, { header: headers });
        const workbook = XLSX.utils.book_new();
        XLSX.utils.book_append_sheet(workbook, worksheet, "Requests");

        worksheet["!cols"] = headers.map(header => ({
            wch: Math.max(
                header.replace("\n", "").length,
                ...data.map(row => row[header as keyof typeof data[0]]?.toString().length || 0)
            ) + 2,
        }));

        const headerCells = Object.keys(worksheet)
            .filter(cell => /^[A-Z]+1$/.test(cell));

        headerCells.forEach(cell => {
            if (worksheet[cell]) {
                worksheet[cell].s = {
                    alignment: {
                        wrapText: true,
                        vertical: "center",
                        horizontal: "center",
                    },
                };
            }
        });

        const fileName = `dashboard_${status}_${format(new Date(), 'yyyy-MM-dd')}.xlsx`;
        XLSX.writeFile(workbook, fileName);
    }

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
