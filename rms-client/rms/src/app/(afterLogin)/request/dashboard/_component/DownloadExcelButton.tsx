'use client';

import {faDownload} from "@fortawesome/free-solid-svg-icons";
import React from "react";
import * as XLSX from 'xlsx';
import {format} from "date-fns";
import {Query} from "@/model/Query";
import {postRequests} from "@/app/(afterLogin)/request/dashboard/_api/postRequests";
import type {Request} from "@/model/Request";
import {CallAlertDialog} from "@/app/_component/dialog/CallAlertDialog";
import BlueButton from "@/app/_component/BlueButton";

interface DownloadExcelButtonProps {
    search: Query;
}

export default function DownloadExcelButton({ search }: DownloadExcelButtonProps) {
    const showAlert = CallAlertDialog();

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

    const downloadExcel = async () => {
        const requestData = await fetchData(search);

        if (requestData.length === 0) {
            showAlert("No data available for download");
            return;
        }

        const headers = [
            "Order Date\n(YYYY-MM-DD)",
            "Registration ID",
            "User Name",
            "Institution",
            "Service",
            "Patient(s) Name",
            "MRN",
            "Patient BOD",
            "Current Status",
        ];

        const data = requestData.map(row => ({
            "Order Date\n(YYYY-MM-DD)": row.create_at ? format(new Date(row.create_at), "yyyy-MM-dd") : '-',
            "Registration ID": row.sample!.barcode,
            "User Name": row.sample!.patient!.organization!.user!.id,
            "Institution": row.sample!.patient!.organization!.id,
            "Service": row.service!.name,
            "Patient(s) Name": row.sample!.patient!.name,
            "MRN": row.sample!.patient!.serial,
            "Patient BOD": row.sample?.patient ?
                `${row.sample.patient.birth_year}-${row.sample.patient.birth_month}-${row.sample.patient.birth_day}` : '-',
            "Current Status": row.status === 'UNCONFIRMED_ORDER' ? 'PENDING_APPROVAL' : row.status === 'COMPLETED_ORDER' ? 'APPROVAL' : row.status
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

        const fileName = `Dashboard_${format(new Date(), 'yyMMdd')}.xlsx`;
        XLSX.writeFile(workbook, fileName);
    }

    return (
        <BlueButton
            name={'Download Excel'}
            onClick={downloadExcel}
            icon={faDownload}
        />
    );
}
