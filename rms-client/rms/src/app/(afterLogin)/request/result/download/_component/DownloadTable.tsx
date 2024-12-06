"use client"

import React, {useEffect, useState} from "react";
import downloadStyle from "@/app/(afterLogin)/request/result/download/_component/downloadTable.module.css";
import style from "@/css/globalTable.module.css";
import {faAngleLeft, faAngleRight, faFilePdf} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import type {Request} from "@/model/Request";
import {postRequests} from "@/app/(afterLogin)/request/result/download/_api/postRequests";
import {getReportFile} from "@/app/(afterLogin)/request/result/download/_api/getReportFile";
import SelectBox from "@/app/_component/SelectBox";
import InputBox from "@/app/_component/InputBox";
import BlueButton from "@/app/_component/BlueButton";
import {SelectBoxOption} from "@/model/SelectBoxOption";
import {Filter} from "@/model/Filter";
import {Query} from "@/model/Query";
import {Report} from "@/model/Report";
import {getReportFiles} from "@/app/(afterLogin)/request/result/download/_api/getReportFiles";

interface RequestWithSelected extends Request {
    isSelected?: boolean;
}
const selectBoxOptions: SelectBoxOption[] = [
    { table: "sample", column: "barcode", name: "Registration Number" },
    { table: "service", column: "name", name: "Service Name" },
    { table: "patient", column: "name", name: "Patient(s) Name" },
    { table: "patient", column: "serial", name: "MRN" },
    { table: "organization", column: "name", name: "Institution" },
    { table: "request", column: "status", name: "Status" },
];
const defaultFilter: Filter = {
    table: "request",
    column: "reported_at",
    operator: "IS NOT NULL",
    value: ""
}

export default function DownloadTable() {
    const [requestData, setRequestData] = useState<RequestWithSelected[]>([]);
    const isSelectedAll = requestData && requestData.length > 0 ? requestData.every((row) => row.isSelected) : false;
    const [totalPage, setTotalPage] = useState<number>(0);
    const [selectedOption, setSelectedOption] = useState<SelectBoxOption>(selectBoxOptions[0]);
    const [filter, setFilter] = useState<Filter>({
            table: selectBoxOptions[0].table!,
            column: selectBoxOptions[0].column!,
            operator: "LIKE",
            value: ""
    })
    const [search, setSearch] = useState<Query>({size:10, page:1, filter_groups:[{filters:[defaultFilter]}]});

    const handlePageChange = (newPageNumber: number) => {
        setSearch(prevPage =>({
            ...prevPage,
            page: newPageNumber
        }));
    };
    const handlePageSizeChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
        const newSize = parseInt(event.target.value);
        setSearch(prevSearch => ({
            ...prevSearch,
            size: newSize,
            page: 1
        }));
    };

    const handleSelectChange = (rowIndex: number, isSelected: boolean) => {
        setRequestData((prevData) => {
            const updatedData = [...prevData];
            updatedData[rowIndex].isSelected = isSelected;
            return updatedData;
        });
    };

    const handleSelectAll = (isSelected: boolean) => {
        setRequestData((prevData) =>
            prevData.map((row) => ({ ...row, isSelected }))
        );
    };

    const fetchData = async (search: Query) => {
        try {
            const response = await postRequests(search)
            const totalPage = parseInt(response.headers.get("X-Total-Page") || '0');
            const responseData = await response.json();
            setRequestData(responseData as Request[]);
            setTotalPage(totalPage)
        } catch(error) {
            console.error("Failed to fetch data:", error);
            setRequestData([]);
            setTotalPage(0);
        }
    }

    useEffect(() => {
        setSearch((prevSearch) => ({
            ...prevSearch,
            filter_groups: [
                {
                    filters: [
                        defaultFilter,
                        filter
                    ]
                }
            ]
        }));
    }, [filter]);

    useEffect(() => {
        fetchData(search)
    }, [search]);

    const handleDownloadOnClick = async (report: Report, request: Request) => {
        try {
            const response = await getReportFile(report.id!);
            if (response.ok) {
                const blob = await response.blob();
                const url = URL.createObjectURL(blob)
                const a = document.createElement('a');
                a.href = url;
                a.download = `${request.sample?.barcode || 'NA'}_${request.service?.id || 'NA'}.pdf`;
                document.body.appendChild(a);
                a.click();
                a.remove();
                URL.revokeObjectURL(url);
            } else {
                alert("Download failed")
            }
        } catch (error) {
            console.error("Failed to fetch download file:", error);
        }
    };

    const handleBatchDownloadClick = async () => {
        const selectedRequest: Request[] = requestData.filter(row => row.isSelected) as Request[]
        try {
            const response = await getReportFiles(selectedRequest);
            if (response.ok) {
                const blob = await response.blob();
                const url = URL.createObjectURL(blob);
                const a = document.createElement('a');
                const today = new Date().toISOString().split('T')[0];
                a.href = url;
                a.download = `GCGenome_reports_${today}.zip`;
                document.body.appendChild(a);
                a.click();
                document.body.removeChild(a);
                URL.revokeObjectURL(url);
                fetchData(search);
            } else {
                alert("Download failed")
            }
        } catch (error) {
            console.error("Failed to fetch multi download file:", error);
        }
    }

    return (
        <div className={style.container}>
            <section className={downloadStyle.filterContainer}>
                <div className={downloadStyle.filterContainerRight}>
                    <BlueButton name={"Batch Download"} onClick={handleBatchDownloadClick}/>
                </div>
                <div className={downloadStyle.filterContainerLeft}>
                    <SelectBox
                        width={"200px"}
                        value={selectedOption.name}
                        options={selectBoxOptions}
                        label={"filter"}
                        onChange={(option) => {
                            setSelectedOption(option);
                            setFilter(prev => ({
                                ...prev,
                                table: option.table!,
                                column: option.column!
                            }))
                        }}
                    />
                    <InputBox label={"search"} onChange={(value) => {
                        setFilter(prev => ({
                            ...prev,
                            value: value
                        }))
                    }}></InputBox>
                </div>
            </section>
            <section className={style.tableContainer}>
                <table className={style.table}>
                    <thead>
                    <tr>
                        <th>
                            <label form="agree" className={style.checkbox}>
                                <input
                                    type="checkbox"
                                    checked={isSelectedAll}
                                    onChange={() => handleSelectAll(!isSelectedAll)}
                                    className={style.checkbox}
                                />
                                <span className={style.checkmark}></span>
                            </label>
                        </th>
                        <th>Registration Number</th>
                        <th>Service</th>
                        <th>Patient(s) Name</th>
                        <th>MRN</th>
                        <th>Institution</th>
                        <th>Report out<br/>(YYYY/MM/DD)</th>
                        <th>Status</th>
                        <th>Report Download</th>
                    </tr>
                    </thead>
                    <tbody>
                    {requestData.map((request, rowIndex) => (
                        <tr key={request!.sample!.barcode! + request!.service!.id!}>
                            <td>
                                <label form="agree" className={style.checkbox}>
                                    <input
                                        type="checkbox"
                                        checked={request.isSelected || false}
                                        onChange={() => handleSelectChange(rowIndex, !request.isSelected)}
                                        className={style.checkbox}
                                    />
                                    <span className={style.checkmark}></span>
                                </label>
                            </td>
                            <td>{request.sample?.barcode}</td>
                            <td>{request.service?.name}</td>
                            <td>{request.sample?.patient?.name}</td>
                            <td>{request.sample?.patient?.serial}</td>
                            <td>{request.sample?.patient?.organization?.name}</td>
                            <td>{request.reported_at ? new Date(request.reported_at).toLocaleDateString() : '-'}</td>
                            <td>{request.status}</td>
                            <td>
                                {(request.reports as Report[])?.filter((report: Report) => report.type === 'PDF').map((report) => (
                                    <FontAwesomeIcon
                                        key={report.id}
                                        className={downloadStyle.downloadIcon}
                                        icon={faFilePdf}
                                        onClick={() => handleDownloadOnClick(report, request)}/>
                                ))}
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            </section>
            <div className={style.pagination}>
                <span>items per page:</span>
                <div className={style.select}>
                    <select onChange={handlePageSizeChange}>
                        <option value="10">10</option>
                        <option value="20">20</option>
                        <option value="50">50</option>
                    </select>
                </div>
                <span> 1-{totalPage} of {search.page} </span>
                <button
                    className={style.pageButton}
                    disabled={search.page === 1}
                    onClick={() => handlePageChange((search.page ?? 1) - 1)}
                ><FontAwesomeIcon icon={faAngleLeft}/>
                </button>
                <button
                    className={style.pageButton}
                    disabled={search.page === totalPage}
                    onClick={() => handlePageChange((search.page ?? 1) + 1)}
                ><FontAwesomeIcon icon={faAngleRight}/>
                </button>
            </div>
        </div>
    );
}