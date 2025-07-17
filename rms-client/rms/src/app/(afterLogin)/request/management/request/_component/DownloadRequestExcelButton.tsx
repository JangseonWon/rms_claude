'use client';

import {faDownload} from "@fortawesome/free-solid-svg-icons";
import React from "react";
import * as XLSX from 'xlsx';
import {format} from "date-fns";
import {Query} from "@/model/Query";
import type {Request} from "@/model/Request";
import {CallAlertDialog} from "@/app/_component/dialog/CallAlertDialog";
import {postRequests} from "@/app/(afterLogin)/request/management/request/_api/postRequests";
import {formatDateLocal, getStringDateFromComponents} from "@/app/_component/DateUtil";
import BlueButton from "@/app/_component/BlueButton";

interface DownloadExcelButtonProps {
    search: Query;
}

export default function DownloadRequestExcelButton({search}: DownloadExcelButtonProps) {
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

    const downloadExcel = async () => {
        const requestData: Request[] = await fetchData(search);

        if (requestData.length === 0) {
            showAlert("No data available for download");
            return;
        }

        const headers = [
            "의뢰날짜\n(YYYY-MM-DD)",
            "거래처명",
            "의뢰기관",
            "의뢰번호",
            "환자명",
            "생년월일\n(YYYY-MM-DD)",
            "성별",
            "담당의",
            "검체채취일\n(YYYY-MM-DD)",
            "의뢰명",
            "MRN",
            "의뢰코드",
            "배송업체",
            "운송번호",
            "상태",
            "취소된의뢰",
            "취소시간",
            "림스재검사유",
            "림스재검요청시간"
        ];

        const data = requestData.map(row => ({
            "의뢰날짜\n(YYYY-MM-DD)": row.create_at ? format(new Date(row.create_at), "yyyy-MM-dd") : '-',
            "거래처명": row.user?.name,
            "의뢰기관": row.sample?.patient?.organization?.name,
            "의뢰번호": row.sample?.barcode,
            "환자명": row.sample?.patient?.name,
            "생년월일\n(YYYY-MM-DD)": getStringDateFromComponents(row.sample?.patient?.birth_year, row.sample?.patient?.birth_month, row.sample?.patient?.birth_day),
            "성별": row.sample?.patient?.sex,
            "담당의": row.physician,
            "검체채취일\n(YYYY-MM-DD)": row.sample?.sampling_on ? formatDateLocal(new Date(row.sample.sampling_on)) : '-',
            "의뢰명": row.service?.name,
            "MRN": row.sample?.patient?.serial,
            "의뢰코드": row.service?.id,
            "배송업체": row.courier_company,
            "운송번호": row.awb_number,
            "상태": row.status === 'UNCONFIRMED_ORDER' ? 'PENDING_APPROVAL' : row.status === 'COMPLETED_ORDER' ? 'APPROVAL' : row.status,
            "취소된의뢰": row.is_cancel,
            "취소시간": row.cancel_at,
            "림스재검사유": row.lims_resample_reason,
            "림스재검요청시간": row.lims_resample_at
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

        const fileName = `RequestManagement_${format(new Date(), "yyMMdd")}.xlsx`;

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
