"use client"

import React, {useEffect, useState} from "react";
import downloadStyle from "@/app/(afterLogin)/request/result/download/_component/downloadTable.module.css";
import globalTableStyle from "@/css/globalTable.module.css";
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
import {FilterGroup, Query} from "@/model/Query";
import {Report} from "@/model/Report";
import {getReportFiles} from "@/app/(afterLogin)/request/result/download/_api/getReportFiles";
import {Status} from "@/model/Status";
import DatePickerRangeBox from "@/app/_component/DatePickerRangeBox";
import {CallAlertDialog} from "@/app/_component/dialog/CallAlertDialog";

interface RequestWithSelected extends Request {
    isSelected?: boolean;
}
const selectBoxOptions: SelectBoxOption[] = [
    { table: "user", column: "name", name: "User Name" },
    { table: "organization", column: "name", name: "Institution" },
    { table: "sample", column: "barcode", name: "Registration ID" },
    { table: "service", column: "name", name: "Service" },
    { table: "patient", column: "name", name: "Patient(s) Name" },
    { table: "patient", column: "serial", name: "MRN" },
    { table: "request", column: "status", name: "Status" }
];
const defaultSearch: Query = {size:10, page:1}
const deliveredFilter: Filter = {
    table: "request",
    column: "status",
    operator: "=",
    value: Status.DELIVERED.valueOf()
}
const completedFilter: Filter = {
    table: "request",
    column: "status",
    operator: "=",
    value: Status.COMPLETED.valueOf()
}

export default function DownloadTable() {
    const [requestData, setRequestData] = useState<RequestWithSelected[]>([]);
    const isSelectedAll = requestData && requestData.length > 0 ? requestData.every((row) => row.isSelected) : false;
    const [totalPage, setTotalPage] = useState<number>(0);
    const [selectedOption, setSelectedOption] = useState<SelectBoxOption>(selectBoxOptions[0]);
    const [search, setSearch] = useState<Query>(defaultSearch);
    const [updateSearch, setUpdateSearch] = useState<Query>({});
    const [searchFilter, setSearchFilter] = useState<Filter | undefined>(undefined);
    const [orderDateFilter, setOrderDateFilter] = useState<FilterGroup>()
    const showAlert = CallAlertDialog();

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
            setRequestData([]);
            setTotalPage(0);
        }
    }

    useEffect(() => {
        const updatedSearch = {
            ...search,
            filter_groups: [
                ...(orderDateFilter ? [orderDateFilter] : []),
                {
                    condition_type: "OR",
                    filters: [
                        deliveredFilter,
                        completedFilter,
                    ],
                },
                {
                    filters: [
                        ...(searchFilter ? [searchFilter] : [])
                    ]
                }
            ],
        };
        setUpdateSearch(updatedSearch)
        fetchData(updatedSearch);
    }, [search,searchFilter,orderDateFilter]);

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
                fetchData(updateSearch);
            } else {
                showAlert("Download failed")
            }
        } catch (error) {
            showAlert("error");
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
                fetchData(updateSearch);
            } else {
                showAlert("Download failed")
            }
        } catch (error) {
            console.error("Failed to fetch multi download file:", error);
        }
    }

    return (
        <div className={globalTableStyle.container}>
            <div className={globalTableStyle.formGroupRight}>
                <BlueButton name={"Batch Download"} onClick={handleBatchDownloadClick}/>
            </div>
            <div className={globalTableStyle.formGroupBetween}>
                <div>
                    <DatePickerRangeBox
                        label={"from-to"}
                        onChange={(from, to) => {
                            setOrderDateFilter(
                                from && to ? {
                                    filters: [
                                        {
                                            table: "request",
                                            column: "specified_at",
                                            value: from?.toLocaleDateString('en-CA'),
                                            operator: ">="
                                        },
                                        {
                                            table: "request",
                                            column: "specified_at",
                                            value: to.toLocaleDateString('en-CA'),
                                            operator: "<="
                                        }
                                    ]
                                } as FilterGroup : undefined
                            )
                        }}
                    />
                </div>
                <div className={downloadStyle.filterContainerLeft}>
                    <SelectBox
                        width={"200px"}
                        value={selectedOption.name}
                        options={selectBoxOptions}
                        label={"filter"}
                        onChange={(option) => {
                            setSelectedOption(option);
                        }}
                    />
                    <InputBox label={"search"} onChange={(value) => {
                        setSearchFilter(
                            value && value.trim() !== ""
                                ? {
                                    table: selectedOption.table,
                                    column: selectedOption.column,
                                    operator: "LIKE",
                                    value: value
                                } as Filter
                                : undefined
                        );
                    }}></InputBox>
                </div>
            </div>
            <section className={globalTableStyle.tableContainer}>
                <table className={globalTableStyle.table}>
                    <thead>
                    <tr>
                        <th>
                            <label form="agree" className={globalTableStyle.checkbox}>
                                <input
                                    type="checkbox"
                                    checked={isSelectedAll}
                                    onChange={() => handleSelectAll(!isSelectedAll)}
                                    className={globalTableStyle.checkbox}
                                />
                                <span className={globalTableStyle.checkmark}></span>
                            </label>
                        </th>
                        <th>Specified At<br/>(DD-MM-YYYY)</th>
                        <th>User Name</th>
                        <th>Institution</th>
                        <th>Registration ID</th>
                        <th>Service</th>
                        <th>Patient(s) Name</th>
                        <th>MRN</th>
                        <th>Report Date<br/>(YYYY/MM/DD)</th>
                        <th>Status</th>
                        <th>Report Download</th>
                    </tr>
                    </thead>
                    <tbody>
                    {requestData.map((request, rowIndex) => (
                        <tr key={request!.sample!.barcode! + request!.service!.id!}>
                            <td>
                                <label form="agree" className={globalTableStyle.checkbox}>
                                    <input
                                        type="checkbox"
                                        checked={request.isSelected || false}
                                        onChange={() => handleSelectChange(rowIndex, !request.isSelected)}
                                        className={globalTableStyle.checkbox}
                                    />
                                    <span className={globalTableStyle.checkmark}></span>
                                </label>
                            </td>
                            <td>{request.specified_at ? new Date(request.specified_at).toLocaleDateString('en-GB').replace(/\//g, '-') : ''}</td>
                            <td>{request.user?.name}</td>
                            <td>{request.sample?.patient?.organization?.name}</td>
                            <td>{request.sample?.barcode}</td>
                            <td>{request.service?.name}</td>
                            <td>{request.sample?.patient?.name}</td>
                            <td>{request.sample?.patient?.serial}</td>
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
            <div className={globalTableStyle.pagination}>
                <span>items per page:</span>
                <div className={globalTableStyle.select}>
                    <select onChange={handlePageSizeChange}>
                        <option value="10">10</option>
                        <option value="20">20</option>
                        <option value="50">50</option>
                    </select>
                </div>
                <span> 1-{totalPage} of {search.page} </span>
                <button
                    className={globalTableStyle.pageButton}
                    disabled={search.page === 1}
                    onClick={() => handlePageChange((search.page ?? 1) - 1)}
                ><FontAwesomeIcon icon={faAngleLeft}/>
                </button>
                <button
                    className={globalTableStyle.pageButton}
                    disabled={search.page === totalPage}
                    onClick={() => handlePageChange((search.page ?? 1) + 1)}
                ><FontAwesomeIcon icon={faAngleRight}/>
                </button>
            </div>
        </div>
    );
}